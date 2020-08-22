package com.bigocoding.secretalbum;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class BiometricAssistant {
    private static final String TAG = BiometricAssistant.class.getSimpleName();
    private static final String API_KEY = "key_186c7a4a0d0f42e3a6adc06e61339d09";
    private static final String API_TOKEN = "tok_a4d34a9a28f44841bf083dfd6acbc2a0";
    private static AsyncHttpClient mClient;
    private String BASE_URL = "https://api.voiceit.io";

    public BiometricAssistant(String apiKey, String apiToken) {
        mClient = new AsyncHttpClient();
        mClient.removeAllHeaders();
        mClient.setTimeout(30 * 1000);
        mClient.setBasicAuth(apiKey, apiToken);
        mClient.addHeader("platformId", "40");
        mClient.addHeader("platformVersion", BuildConfig.VERSION_NAME);
    }

    public void setURL(String url) {
        BASE_URL = url.replaceAll("\\s+","");
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public void deleteAllEnrollments(String userId, AsyncHttpResponseHandler responseHandler) {
        mClient.delete(getAbsoluteUrl("/enrollments/" + userId + "/all"), responseHandler);
    }

    public void createVoiceEnrollment(String userId, String contentLanguage, String phrase, File recording, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("contentLanguage", contentLanguage);
        params.put("phrase", phrase);
        try {
            params.put("recording", recording);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException: " + e.getMessage());
            responseHandler.sendFailureMessage(200, null, buildJSONFormatMessage().toString().getBytes(), new Throwable());
            return;
        }

        mClient.post(getAbsoluteUrl("/enrollments/voice"), params, responseHandler);
    }

    public void encapsulatedVoiceEnrollment(Activity activity, String userId, String contentLanguage, String phrase, final JsonHttpResponseHandler responseHandler) {
        Intent intent = new Intent(activity, VoiceEnrollment.class);
        Bundle bundle = new Bundle();
        bundle.putString("apiKey", API_KEY);
        bundle.putString("apiToken", API_TOKEN);
        bundle.putString("userId", userId);
        bundle.putString("contentLanguage", contentLanguage);
        bundle.putString("phrase", phrase);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);

        broadcastMessageHandler(activity, responseHandler);
    }

    private void broadcastMessageHandler(final Activity activity, final JsonHttpResponseHandler responseHandler) {
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            boolean broadcastTriggered = false;

            @Override
            public void onReceive(Context context, Intent intent) {
                if (!broadcastTriggered) {
                    broadcastTriggered = true;
                    String Response = intent.getStringExtra("Response");

                    if (intent.getAction().equals("assistant-success")) {
                        responseHandler.sendSuccessMessage(200, null, Response.getBytes());
                    }
                    if (intent.getAction().equals("assistant-failure")) {
                        responseHandler.sendFailureMessage(200, null, Response.getBytes(), new Throwable());
                    }
                }
            }
        };

        // Register observers (mMessageReceiver) to receive Intents with named actions
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("assistant-success");
        intentFilter.addAction("assistant-failure");
        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver, intentFilter);
    }

    private JSONObject buildJSONFormatMessage() {
        JSONObject json = new JSONObject();
        try {
            json.put("message", "Incorrectly formatted id argument. Check log output for more information");
        } catch (JSONException e) {
            Log.e(TAG,"JSON Exception : " + e.getMessage());
        }
        return json;
    }
}