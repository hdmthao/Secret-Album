package com.bigocoding.secretalbum;

import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private BiometricAssistant mBiometricAssistant;
    private final String phrase = "Never forget tomorrow is a new day";
    private final String userId = "usr_5abcb05344d141e398f1489b159d5ae5";
    private final String contentLanguage = "en-US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBiometricAssistant = new BiometricAssistant("key_186c7a4a0d0f42e3a6adc06e61339d09", "tok_a4d34a9a28f44841bf083dfd6acbc2a0");
    }

    public void encapsulatedVoiceEnrollment(View view) {
        mBiometricAssistant.encapsulatedVoiceEnrollment(this, userId, contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVoiceEnrollment onSuccess Result : " + response.toString());
            }
    
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                System.out.println("encapsulatedVoiceEnrollment onFailure Result : " + errorResponse.toString());
            }
        });
    }

    public void checkResponse(JSONObject response) {
        try {
            if (response.getString("responseCode").equals("IFVD")
                    || response.getString("responseCode").equals("ACLR")
                    || response.getString("responseCode").equals("IFAD")
                    || response.getString("responseCode").equals("SRNR")
                    || response.getString("responseCode").equals("UNFD")
                    || response.getString("responseCode").equals("MISP")
                    || response.getString("responseCode").equals("DAID")
                    || response.getString("responseCode").equals("UNAC")
                    || response.getString("responseCode").equals("CLNE")
                    || response.getString("responseCode").equals("INCP")
                    || response.getString("responseCode").equals("NPFC")) {
//                Toast.makeText(this, "responseCode: " + response.getString("responseCode")
//                        + ", " + getString(com.voiceit.voiceit2.R.string.CHECK_CODE), Toast.LENGTH_LONG).show();
                Log.e("MainActivity","responseCode: " + response.getString("responseCode")
                        + ", " + getString(com.bigocoding.secretalbum.R.string.CHECK_CODE));
            }
        } catch (JSONException e) {
            Log.d("MainActivity","JSON exception : " + e.toString());
        }
    }
}