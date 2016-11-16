package com.example.aashish.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.AppCompatButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;



public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final String T = MainActivity.class.getSimpleName()+".tag";
    private static final int RC_SIGN_IN = 007;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private SignInButton btnSignIn;
    private AppCompatButton btn_login;
    private TextView link_signup;
    private EditText input_username;
    private EditText input_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);

        /*btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);*/
        input_username=(EditText)findViewById(R.id.input_username);
        input_password=(EditText)findViewById(R.id.input_password);
        link_signup=(TextView)findViewById(R.id.link_signup);
        btn_login=(AppCompatButton)findViewById(R.id.btn_login);

        btnSignIn.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        link_signup.setOnClickListener(this);
        //btnSignOut.setOnClickListener(this);
        //btnRevokeAccess.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        //Log.v("SCOPES:",gso.
    }

    private void logIntent(String[] d)
    {
        Intent intent=new Intent(getApplicationContext(),LoggedIn.class);
        intent.putExtra(T,d);
        startActivity(intent);
    }
    public boolean validate() {
        boolean valid = true;

        String username = input_username.getText().toString();
        String password = input_password.getText().toString();

        if (username.isEmpty() ) {
           input_username.setError("enter a valid username");
            valid = false;
        } else {
            input_username.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            input_password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            input_password.setError(null);
        }

        return valid;
    }

    private String[] post(String url,final String arr,RequestParams params)
    {
       //create HTTP client
        AsyncHttpClient client = new AsyncHttpClient();
        Jsonhttphandler handler=new Jsonhttphandler();
        Log.d(TAG,params.toString());
        client.post(url, params,handler );
        return handler.getRes();
    }
    private void logIn()
    {

        
        if (!validate()) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        String username=input_username.getText().toString();
        String password=input_password.getText().toString();
        

        RequestParams params = new RequestParams();

        // set our JSON object
        params.put("username",username );
        params.put("password", password);



        String r[]=post("http://192.168.55.245:3000/users/login","status",params);

        final  String userid = r[0];
        final String token = r[1];
        Log.d(TAG,"user:"+r[0]);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {


                        if(userid!=null && token!=null &&!userid.equals("") && !token.equals(""))
                        {
                            logIntent(new  String[]{userid,token,null});
                        }
                        else
                        {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Login failed",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                        
                        
                        progressDialog.dismiss();
                    }
                }, 3000);

    }




    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        //updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //updateUI(false);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            final String[] personPhotoUrl =new  String[1];
            try {
                personPhotoUrl[0] = acct.getPhotoUrl().toString();
            } catch (NullPointerException e) {
                personPhotoUrl[0]="";
            }

            String username = acct.getEmail();
            final List<String> p=new ArrayList();
            SimpleTarget target = new SimpleTarget<Bitmap>() {

                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    // do something with the bitmap
                    // for demonstration purposes, let's just set it to an ImageView

                    p.add(BitMapToString(bitmap));
                    Log.d(TAG,"bitmap  ");
                }

            };

            if(!personPhotoUrl[0].equals(""))
                Glide.with( getApplicationContext())
                    .load(personPhotoUrl[0])
                    .asBitmap()
                    .thumbnail(0.5f)
                    .into(target);
            Log.e(TAG, "Name: " + personName + ", username: " + username
                    + ", Image: " + personPhotoUrl[0]);
            RequestParams params = new RequestParams();

            // set our JSON object
            params.put("username",username );
            params.put("password", "");
            params.put("name", personName);
            params.put("age", "");
            params.put("aboutme", "");
            params.put("gender", "");
            if(!p.isEmpty())
                personPhotoUrl[0]=p.get(0);
            params.put("photo",personPhotoUrl[0]);


            String r[]=post("http://192.168.55.245:3000/users/register","status",params);
            final  String userid = r[0];
            final String token = r[1];
            if(userid!=null&&token!=null&&!userid.equals("") && !token.equals(""))
            {
                logIntent(new  String[]{userid,token,personPhotoUrl[0]});
            }
            else
            {
                Toast.makeText(
                        getApplicationContext(),
                        "Something went wrong :(",
                        Toast.LENGTH_LONG
                ).show();
            }

            /*txtName.setText(personName);
            txtEmail.setText(username);*/


            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;

            case R.id.btn_login:
                logIn();
                break;

            case R.id.link_signup:
                Intent intent=new Intent(getApplicationContext(),Signup.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] arr=baos.toByteArray();
        String result= Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }
   /* private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }*/


    interface Intet
    {
        public abstract int resultGet();
    }
}

