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

package com.theta360.clouduploadv2.settingdata;

import com.theta360.clouduploadv2.httpserver.AndroidWebServer;

/**
 * Define the data type of the setting that the user decides.
 * <p>
 * If the bit rate is auto, AUTO_BITRATE will be entered.
 */
public class SettingData {

    private int noOperationTimeoutMinute;  // No operation timeout seconds
    private String status;  // Status
    private boolean isUploadRaw;  // Flag on whether to upload raw image
    private boolean isUploadMovie;  // Flag on whether to upload video
    private boolean isDeleteUploadedFile;  // Flag on whether to delete uploaded file

    /**
     * Constructor
     */
    public SettingData() {
        this.noOperationTimeoutMinute = AndroidWebServer.TIMEOUT_DEFAULT_MINUTE;
        this.status = "";
        this.isUploadRaw = false;
        this.isUploadMovie = false;
        this.isDeleteUploadedFile = false;
    }

    /**
     * Get no operation timeout seconds
     *
     * @return No operation timeout seconds
     */
    public int getNoOperationTimeoutMinute() {
        return this.noOperationTimeoutMinute;
    }

    /**
     * Set no operation timeout seconds
     *
     * @param noOperationTimeoutMinute No operation timeout seconds
     */
    public void setNoOperationTimeoutMinute(int noOperationTimeoutMinute) {
        this.noOperationTimeoutMinute = noOperationTimeoutMinute;
    }

    /**
     * Get status
     *
     * @return status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Set status
     *
     * @param status status
     */
    public void setStatus(String status) {
        if (status != null)
            this.status = status;
    }

    /**
     * Get the flag on whether to raw images
     *
     * @return flag
     */
    public boolean getIsUploadRaw() {
        return this.isUploadRaw;
    }

    /**
     * Get the flag on whether to upload video
     *
     * @return flag
     */
    public boolean getIsUploadMovie() {
        return this.isUploadMovie;
    }

    /**
     * Set the flag on whether to upload raw images
     *
     * @param isUploadRaw flag
     */
    public void setIsUploadRaw(int isUploadRaw) { this.isUploadRaw = isUploadRaw == 1; }


    /**
     * Set the flag on whether to upload video
     *
     * @param isUploadMovie flag
     */
    public void setIsUploadMovie(int isUploadMovie) {
        this.isUploadMovie = isUploadMovie == 1;
    }

    /**
     * Get the flag on whether to delete uploaded file
     *
     * @return flag
     */
    public boolean getIsDeleteUploadedFile() {
        return this.isDeleteUploadedFile;
    }

    /**
     * Set the flag on whether to delete uploaded file
     *
     * @param isDeleteUploadedFile flag
     */
    public void setIsDeleteUploadedFile(int isDeleteUploadedFile) {
        this.isDeleteUploadedFile = isDeleteUploadedFile == 1;
    }
}
