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

package com.theta360.clouduploadv2.httpserver;

/**
 * Define the extension.
 */
public enum ExtensionType {
    JPG("image/jpeg"),
    RAW("image/x-adobe-dng"),
    MP4("video/mp4"),
    UNKNOWN("application/octet-stream");

    private String mimeType;

    ExtensionType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public static boolean isJPG(String path) {
        return path.endsWith(".JPG") || path.endsWith(".jpg") || path.endsWith(".jpeg");
    }

    public static boolean isRAW(String path) {
        return path.endsWith(".DNG") || path.endsWith(".dng");
    }

    public static boolean isMP4(String path) {
        return path.endsWith(".MP4") || path.endsWith(".mp4");
    }

    public static ExtensionType getType(String path) {
        if (isJPG(path)) {
            return JPG;
        } else if (isRAW(path)) {
            return RAW;
        } else if (isMP4(path)) {
            return MP4;
        } else {
            return UNKNOWN;
        }
    }
}