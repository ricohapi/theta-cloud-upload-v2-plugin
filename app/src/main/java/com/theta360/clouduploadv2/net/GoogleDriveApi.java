/**
 * Copyright 2018 Ricoh Company, Ltd.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.theta360.clouduploadv2.net;

import android.content.Context;
import android.util.Log;

import com.theta360.clouduploadv2.httpserver.AndroidWebServer;
import com.theta360.clouduploadv2.httpserver.ExtensionType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.MessageFormat;

import javax.net.ssl.HttpsURLConnection;

/**
 * Google Drive authentication class
 */
public class GoogleDriveApi extends UploadPhotoApi {
    private final String TAG = "GoogleDriveApi";
    private final String REDIRECT_URL = "verification_url";
    private final String DEVICE_CODE = "device_code";
    private final String USER_CODE = "user_code";
    private final String EXPIRES_IN = "expires_in";
    private final String INTERVAL = "interval";
    private final String ACCESS_TOKEN = "access_token";
    private final String REFRESH_TOKEN = "refresh_token";
    private final int HTTP_RESUME_INCOMPLETE = 308;

    public GoogleDriveApi(Context context) {
        super(context);
        try {
            setClientId(props.getProperty("GOOGLE_CLIENT_ID"));
            setClientSecret(props.getProperty("GOOGLE_CLIENT_SECRET"));
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    @Override
    public String getApiType() {
        return UploadPhotoApiFactory.GOOGLE_DRIVE;
    }

    @Override
    public void setApiResult(JSONObject json) throws JSONException {
        if (json.has(REDIRECT_URL)) {
            setRedirectUrl(json.getString(REDIRECT_URL));
        }
        if (json.has(DEVICE_CODE)) {
            setDeviceCode(json.getString(DEVICE_CODE));
        }
        if (json.has(USER_CODE)) {
            setUserCode(json.getString(USER_CODE));
        }
        if (json.has(EXPIRES_IN)) {
            setExpiresIn(json.getInt(EXPIRES_IN));
        }
        if (json.has(INTERVAL)) {
            setInterval(json.getInt(INTERVAL));
        }
        if (json.has(ACCESS_TOKEN)) {
            setAccessToken(json.getString(ACCESS_TOKEN));
        }
        if (json.has(REFRESH_TOKEN)) {
            setRefreshToken(json.getString(REFRESH_TOKEN));
        }
    }

    @Override
    public void startRequestCode() {
        cancelRequestCode();
        requestCodeTask = new RequestCodeTask();
        String url = getProperty("GOOGLE_AUTHORIZATION_URL");
        String urlParams = "client_id=" + getClientId() + "&scope=" +
                getProperty("GOOGLE_USERINFO_SCOPE") + " " + getProperty("GOOGLE_DRIVE_SCOPE");
        requestCodeTask.execute(url, urlParams);
    }

    @Override
    public void startRequestToken() {
        cancelRequestToken();
        requestTokenTask = new RequestTokenTask();
        String url = getProperty("GOOGLE_GET_TOKEN_URL");
        String urlParams = "client_id=" + getClientId() +
                "&client_secret=" + getClientSecret() +
                "&grant_type=" + getProperty("GOOGLE_POLLING_GRANT_TYPE") +
                "&code=" + getDeviceCode();
        requestTokenTask.execute(url, urlParams);
    }

    @Override
    public void startRefreshToken() {
        cancelRefreshToken();
        refreshTokenTask = new RefreshTokenTask();
        String url = getProperty("GOOGLE_REFRESH_TOKEN_URL");
        String urlParams = "client_id=" + getClientId() +
                "&client_secret=" + getClientSecret() +
                "&grant_type=refresh_token" +
                "&refresh_token=" + getRefreshToken();
        refreshTokenTask.execute(url, urlParams);
    }

    @Override
    public void startRequestUserinfo() {
        cancelRequestUserinfo();
        requestUserinfoTask = new RequestUserinfoTask();
        String url = MessageFormat.format(getProperty("GOOGLE_USERINFO_URL"), getAccessToken());
        requestUserinfoTask.execute(url);
    }

    @Override
    public void startUploadFile() {
        cancelUploadFile();
        uploadFileTask = new UploadFileTask();
        uploadFileTask.execute(getProperty("GOOGLE_DRIVE_UPLOAD_FILE_URL"));
    }

    @Override
    protected HttpsURLConnection setupConnection(HttpsURLConnection connection) throws Exception {
        String mimeType = ExtensionType.getType(getUploadDataName()).getMimeType();
        File file = new File(getUploadDataPath());
        long fileContentLength = file.length();

        String location = getLocation(connection);
        if (location.isEmpty()) {
            return connection;
        }

        String contentLengthMaxMbString = props.getProperty("GOOGLE_DRIVE_CONTENT_LENGTH_MAX_MB", "200");
        long contentLengthMaxMb = Long.valueOf(contentLengthMaxMbString);
        contentLengthMaxMb = Math.max(Math.min(contentLengthMaxMb, 200), 1);
        long contentLengthMaxByte = contentLengthMaxMb * 1024L * 1024L;

        HttpsURLConnection resumableConnection = null;
        URL url = new URL(location);
        int responseCode = HTTP_RESUME_INCOMPLETE;
        long range_start = 0;
        long range_end = 0;

        while (responseCode == HTTP_RESUME_INCOMPLETE) {
            resumableConnection = (HttpsURLConnection) url.openConnection();
            resumableConnection.setRequestMethod("POST");
            resumableConnection.setReadTimeout(AndroidWebServer.UPLOAD_TIMEOUT_MSEC);
            resumableConnection.setConnectTimeout(AndroidWebServer.UPLOAD_TIMEOUT_MSEC);
            resumableConnection.setDoOutput(true);
            resumableConnection.setDoInput(true);

            if (range_start + contentLengthMaxByte < fileContentLength) {
                range_end = range_start + contentLengthMaxByte;
            } else {
                range_end = fileContentLength;
            }
            int contentLength = (int) (range_end - range_start);
            resumableConnection.setRequestProperty("Content-Length", String.valueOf(contentLength));
            resumableConnection.setRequestProperty("Content-Type", mimeType);
            resumableConnection.setRequestProperty("Content-Range", "bytes " + String.valueOf(range_start) + "-" + String.valueOf(range_end - 1) + "/" + String.valueOf(fileContentLength));

            byte[] byteArray = new byte[contentLength];
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(range_start);
            raf.read(byteArray, 0, contentLength);
            raf.close();

            try (DataOutputStream wr = new DataOutputStream(resumableConnection.getOutputStream())) {
                wr.write(byteArray);
            }

            responseCode = resumableConnection.getResponseCode();
            if (responseCode == HTTP_RESUME_INCOMPLETE) {
                String range = resumableConnection.getHeaderField("Range");
                range_start = Long.valueOf(range.split("-")[1]) + 1;
            }
        }
        return resumableConnection;
    }

    private String getLocation(HttpsURLConnection connection) throws Exception {
        String body = "{\"title\": \"" + getUploadDataName() + "\"}";
        File file = new File(getUploadDataPath());

        connection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
        connection.setRequestProperty("Content-Length", String.valueOf(body.getBytes().length));
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("X-Upload-Content-Type", ExtensionType.getType(getUploadDataName()).getMimeType());
        connection.setRequestProperty("X-Upload-Content-Length", String.valueOf(file.length()));

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.write(body.getBytes());
        }

        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            return connection.getHeaderField("Location");
        } else {
            return "";
        }
    }
}
