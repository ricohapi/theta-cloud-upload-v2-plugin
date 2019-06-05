/**
 * Copyright 2019 Ricoh Company, Ltd.
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

package com.theta360.clouduploadv2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Provides HTTP connection to Theta Web API.
 * This class consists of methods that provide interface
 * to communicate with camera API.
 */
public class CameraConnector {
    private static final String TAG = "CameraConnector";
    private static final String IP = "127.0.0.1:8080";

    public CameraConnector() {
    }

    /**
     * Acquire Theta camera information
     *
     * @return Camera information.  If some errors occur, empty object.
     */
    public JSONObject getCameraInfo() {
        String responseString;
        InputStream rStream = null;
        JSONObject cInfo;
        HttpURLConnection cameraApi = createHttpConnection("GET", "/osc/info");

        if(cameraApi == null) {
            Log.e(TAG, "HttpConnection is null");
            return new JSONObject();
        }

        try {
            // send HTTP GET
            // this protocol has no input.
            cameraApi.connect();
            rStream = cameraApi.getInputStream();
            responseString = InputStreamToString(rStream);
        } catch (IOException e) {
            responseString= null;
            Log.e(TAG, "IOException of cameraApi");
        }  finally {
            if (rStream != null) {
                try {
                    rStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException on rStream close");
                }
            }
        }

        try {
            cInfo = new JSONObject(responseString);
        } catch (JSONException e) {
            cInfo= new JSONObject();
            e.printStackTrace();
            Log.e(TAG, "JSONException");
        }

        return cInfo;
    }


    /**
     * Generate HTTP URL to the Theta Web API.
     *
     * @param path Path of the Web API
     * @return URL
     */
    private String createUrl(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(IP);
        sb.append(path);

        return sb.toString();
    }

    /**
     * Generate HTTP connection to the Theta Web API.
     *
     * @param method Method of HTTP
     * @param path   Path of the Web API
     * @return HTTP Connection instance.  If some errors occur, null.
     */
    private HttpURLConnection createHttpConnection(String method, String path) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(createUrl(path));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);

            if (method.equals("GET")) {
                connection.setRequestMethod(method);
                connection.setDoOutput(false);
            } else {
                assert false : "Unsupported method: " + method;
		        connection= null;
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
            connection= null;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            connection= null;
        }

        return connection;
    }

    /**
     * Convert input stream to string
     *
     * @param is InputStream
     * @return String
     * @throws IOException IO error
     */
    private String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String lineData;
        while ((lineData = br.readLine()) != null) {
            sb.append(lineData);
        }
        br.close();
        return sb.toString();
    }

}
