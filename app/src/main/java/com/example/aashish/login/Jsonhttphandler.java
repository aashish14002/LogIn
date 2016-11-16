package com.example.aashish.login;

import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Aashish on 16-11-2016.
 */

public class Jsonhttphandler extends JsonHttpResponseHandler {


    private String[] res={"",""};
    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        try {
            //Log.d(TAG,"SUCCESS");
            //JSONArray resp = response.getJSONArray(arr);
            //JSONObject jsonobj = resp.getJSONObject(0);
            if(response.getString("status")!=null)
            {
                res[0] =response.getString("userid");
                //Log.d(TAG,res[0]);
                res[1] ="token";//jsonobj.getString("token");
            }

        } catch (JSONException e) {

        }

        }



    @Override
    public void onFailure(int statusCode,Header[] headers, Throwable throwable,JSONObject response) {

    }

    public String[] getRes()
    {
        return res;
    }
}
