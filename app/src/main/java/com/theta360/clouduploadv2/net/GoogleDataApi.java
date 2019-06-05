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

import com.theta360.clouduploadv2.httpserver.ExtensionType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;

import javax.net.ssl.HttpsURLConnection;

/**
 * Google Photos authentication class
 */
public class GoogleDataApi extends UploadPhotoApi {
    private final String TAG = "GoogleDataApi";
    private final String REDIRECT_URL = "verification_url";
    private final String DEVICE_CODE = "device_code";
    private final String USER_CODE = "user_code";
    private final String EXPIRES_IN = "expires_in";
    private final String INTERVAL = "interval";
    private final String ACCESS_TOKEN = "access_token";
    private final String REFRESH_TOKEN = "refresh_token";

    public GoogleDataApi(Context context) {
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
        return UploadPhotoApiFactory.GOOGLE_PHOTO;
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
                getProperty("GOOGLE_USERINFO_SCOPE") + " " + getProperty("GOOGLE_PHOTO_SCOPE");
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
        String url = MessageFormat.format(getProperty("GOOGLE_UPLOAD_FILE_URL"), getAccessToken());
        uploadFileTask.execute(url);
    }

    @Override
    protected HttpsURLConnection setupConnection(HttpsURLConnection connection) throws Exception {
        File file = new File(getUploadDataPath());
        byte[] byteArray = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(byteArray);
        fis.close();
        if (ExtensionType.isJPG(getUploadDataName())) {
            connection.addRequestProperty("Content-Type", "image/jpeg");
            connection.addRequestProperty("Slug", getUploadDataName());
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(byteArray);
            }
        } else {
            StringBuilder body = new StringBuilder();
            body.append("Media multipart posting\n");
            body.append("--").append(BOUNDARY).append("\n");
            body.append("Content-Type: application/atom+xml\n");
            body.append("\n");
            body.append("<entry xmlns='http://www.w3.org/2005/Atom'>\n");
            body.append("<title>").append(getUploadDataName()).append("</title>\n");
            body.append("<summary></summary>\n");
            body.append("<category scheme=\"http://schemas.google.com/g/2005#kind\"\n");
            body.append("term=\"http://schemas.google.com/photos/2007#photo\"/>\n");
            body.append("</entry>\n");
            body.append("--").append(BOUNDARY).append("\n");
            body.append("Content-Type: video/mp4\n");
            body.append("\n");

            StringBuilder bodyEnd = new StringBuilder();
            bodyEnd.append("\n--").append(BOUNDARY).append("--");

            byte[] bodyBytes = body.toString().getBytes();
            byte[] bodyEndBytes = bodyEnd.toString().getBytes();
            long contentLength = bodyBytes.length + byteArray.length + bodyEndBytes.length;

            connection.setRequestProperty("Content-Type", "multipart/related; boundary=" + BOUNDARY + "");
            connection.setRequestProperty("Content-Length", String.valueOf(contentLength));
            connection.setRequestProperty("MIME-version", "1.0");
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(bodyBytes);
                wr.write(byteArray);
                wr.write(bodyEndBytes);
            }
        }
        connection.connect();
        return connection;
    }
}
