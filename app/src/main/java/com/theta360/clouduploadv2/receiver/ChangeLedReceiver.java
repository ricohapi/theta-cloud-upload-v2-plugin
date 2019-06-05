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

package com.theta360.clouduploadv2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receive broadcast of LED change
 */
public class ChangeLedReceiver extends BroadcastReceiver {
    public static final String CHANGE_STARTUP_LED = "com.theta360.clouduploadv2.change-startup-led";
    public static final String CHANGE_READY_LED = "com.theta360.clouduploadv2.change-ready-led";
    public static final String CHANGE_TRANSFERRING_LED = "com.theta360.clouduploadv2.change-transferring-led";
    public static final String CHANGE_STOP_TRANSFERRING_LED = "com.theta360.clouduploadv2.change-stop-transferring-led";
    public static final String CHANGE_TRANSFERRING_STATUS_LED = "com.theta360.clouduploadv2.change-transferring-status-led";
    public static final String CHANGE_ERROR_LED = "com.theta360.clouduploadv2.change-error-led";
    public static final String TRANSFERRING_CURRENT_NUMBER = "transferring-current-number";
    public static final String TRANSFERRING_ALL_NUMBER = "transferring-all-number";
    public static final String ERROR_CODE = "error-code";

    private Callback mCallback;

    public ChangeLedReceiver(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        switch (action) {
            case CHANGE_STARTUP_LED:
                mCallback.callStartupCallback();
                break;
            case CHANGE_READY_LED:
                mCallback.callReadyCallback();
                break;
            case CHANGE_TRANSFERRING_LED:
                mCallback.callTransferringCallback();
                break;
            case CHANGE_STOP_TRANSFERRING_LED:
                mCallback.callStopTransferringCallback();
                break;
            case CHANGE_TRANSFERRING_STATUS_LED:
                int currentNumber = intent.getIntExtra(TRANSFERRING_CURRENT_NUMBER, 0);
                int allNumber = intent.getIntExtra(TRANSFERRING_ALL_NUMBER, 0);
                mCallback.callTransferringStatusCallback(currentNumber, allNumber);
                break;
            case CHANGE_ERROR_LED:
                String errorCode = intent.getStringExtra(ERROR_CODE);
                mCallback.callErrorCallback(errorCode);
                break;
            default:
                break;
        }
    }

    public interface Callback {
        void callStartupCallback();

        void callReadyCallback();

        void callTransferringCallback();

        void callStopTransferringCallback();

        void callTransferringStatusCallback(int currentNumber, int allNumber);

        void callErrorCallback(String errorCode);
    }
}
