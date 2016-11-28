package com.example.aashish.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Signup extends AppCompatActivity {

    private static final String TAG = Signup.class.getSimpleName();
    private static int RESULT_LOAD_IMAGE = 1;
    static final String S=TAG+".create";
    Bitmap bitmap;
    EditText name;
    EditText username;
    EditText age;
    EditText gender;
    EditText about;
    EditText password;
    EditText repassword;
    Button signupbutton;
    TextView loginlink;
    ImageView photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name=(EditText)findViewById(R.id.input_name);
        username=(EditText)findViewById(R.id.input_username);
        age=(EditText)findViewById(R.id.input_age);
        gender=(EditText)findViewById(R.id.input_gender);
        about=(EditText)findViewById(R.id.input_about);
        password=(EditText)findViewById(R.id.input_password);
        repassword=(EditText)findViewById(R.id.input_reEnterPassword);
        loginlink=(TextView)findViewById(R.id.link_login);
        signupbutton=(Button)findViewById(R.id.btn_signup);
        photo=(ImageView)findViewById(R.id.input_photo);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    private void logIntent(String[] d) {
        Intent intent=new Intent(getApplicationContext(),LoggedIn.class);
        intent.putExtra(S,d);
        startActivity(intent);
    }

    private void post(String url,final String arr,RequestParams params) {
        String[] r={"",""};
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        //create HTTP client
        AsyncHttpClient client = new AsyncHttpClient();
        Jsonhttphandler handler=new Jsonhttphandler();
        Log.d(TAG,params.toString());
        client.post(url, params, new JsonHttpResponseHandler(){



            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d(TAG,"entered on success");
                    if(response.getString("status")!=null)
                    {
                        Log.d(TAG,"success");
                        String u=response.getString("userid");
                        String t=response.getString("token");

                        logIntent(new  String[]{u,t,null});
                    }
                    else
                    {
                        Log.d(TAG,"not success");
                    }
                } catch (JSONException e) {
                    Log.d(TAG,"jsonexception");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(
                                    getApplicationContext(),
                                    "Something went wrong :(",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing())
                            progressDialog.hide();
                    }
                });

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, final JSONObject responseString) {
                Log.d(TAG,"failure");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        boolean error=true;

                        if(responseString!=null)
                        {
                            try {
                                JSONObject e=responseString.getJSONObject("err");
                                if(e!=null)
                                {
                                    if(e.getString("name")!=null && e.getString("name").equals("UserExistsError"))
                                    {
                                        error=false;
                                        Log.d(TAG,"failure : username exixts");
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Username already exixts!!",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                        username.setText("");
                                    }
                                }
                            }catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                        if(error){
                            Log.d(TAG,"failure "+error);
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Something went wrong +error:(",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                        if (progressDialog.isShowing())
                            progressDialog.hide();
                    }
                });
            }
        });


    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            Toast.makeText(getBaseContext(), "validate eror", Toast.LENGTH_SHORT).show();

            return;
        }



        final String Name = name.getText().toString();
        final String Username = username.getText().toString();
        String Age = age.getText().toString();
        String Password = password.getText().toString();
        String reEnterPassword = repassword.getText().toString();
        String About = about.getText().toString();
        String Gender = gender.getText().toString();
        final String photo;
        if(bitmap!=null)
            photo=BitMapToString(bitmap);
        else
            photo="";

        RequestParams params = new RequestParams();

        // set our JSON object
        params.put("username",Username );
        params.put("password", Password);
        params.put("name", Name);
        params.put("age", Age);
        params.put("aboutme", About);
        params.put("gender", Gender);
        params.put("photo",photo);
        params.put("isgmail","false");

        post("http://192.168.55.245:3000/users/register","register",params);



    }

    public boolean validate() {
        boolean valid = true;
        String Name = name.getText().toString();
        String Username= username.getText().toString();
        String Age = age.getText().toString();
        String Password = password.getText().toString();
        String About = about.getText().toString();
        String Gender = gender.getText().toString();
        String reEnterPassword = repassword.getText().toString();

        if (Name.isEmpty() || Name.length() < 3) {
            name.setError("at least 3 characters");
            Log.d(TAG,"at least 3 characters");
            valid = false;
        } else {
            name.setError(null);
        }

        if (Gender.isEmpty() || !(Gender.equalsIgnoreCase("m") || Gender.equalsIgnoreCase("f"))) {
            gender.setError("M or F");
            valid = false;
            Log.d(TAG,"gender");
        } else {
            gender.setError(null);
        }




        if (Username.isEmpty()) {
            username.setError("enter a valid username");
            valid = false;
            Log.d(TAG,"username");
        } else {
            username.setError(null);
        }

        if (Age.isEmpty() || Age.length()!=2) {
            age.setError("Enter Valid Age");
            valid = false;
            Log.d(TAG,"age");
        } else {
           age.setError(null);
        }

        if (Password.isEmpty() || Password.length() < 4 ) {
            password.setError("between 4 and 12 alphanumeric characters");
            valid = false;
            Log.d(TAG,"password");
        } else {
            password.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4  || !(reEnterPassword.equals(Password))) {
            repassword.setError("Password Do not match");
            valid = false;
            Log.d(TAG,"repassword");
        } else {
            repassword.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bitmap=BitmapFactory.decodeFile(picturePath);
            photo.setImageBitmap(bitmap);
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,20, baos);
        byte [] arr=baos.toByteArray();
        String result= Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    public Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}

