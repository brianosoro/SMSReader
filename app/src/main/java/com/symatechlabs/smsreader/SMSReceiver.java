package com.symatechlabs.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//======================================================================
// SMS Reader App
//======================================================================
//-----------------------------------------------------
// Author   : Brian Osoro
// Company  : Symatech Labs Ltd
// Website  : www.symatechlabs.com
// Blog     : www.brianosoro.com
// Twitter  : @brayanosoro
// Email    : info@brianosoro.com / brianosoroinc@gmail.com
//-----------------------------------------------------

public class SMSReceiver extends BroadcastReceiver {

    Context context;
    String msisdn, message;
    public ConnectivityManager conMgr;
    public NetworkInfo netInfo;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        final Bundle bundle = intent.getExtras();
        try {

            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (Object pdus : pdusObj) {

                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus);
                    this.msisdn = message.getDisplayOriginatingAddress();
                    this.message = message.getDisplayMessageBody();

                    if(checkConnectivity()){
                        new sendToServer().execute();
                    }


                }
            }


        } catch (Exception e) {

        }

    }

    public boolean checkConnectivity() {

        this.conMgr = (ConnectivityManager)this.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        this.netInfo = this.conMgr.getActiveNetworkInfo();

        if (this.netInfo != null && this.netInfo.isConnectedOrConnecting() && this.netInfo.isAvailable()) {
            return true;
        } else {

            return false;
        }

    }


    class sendToServer extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObject;
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            FormBody.Builder formBuilder = new FormBody.Builder().add("msisdn", msisdn ).add("message", message);

            RequestBody formBody = formBuilder.build();
            Response response = null;
            Request request = null;

            try {

                request = new Request.Builder()
                        .url(MainActivity.BASE_URL)
                        .post(formBody)
                        .build();
            } catch (Exception e) {

            }


            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jsonObject = new JSONObject(response.body().string());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (jsonObject != null) {

            } else {


            }


        }
    }
}
