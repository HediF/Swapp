package com.example.android.study;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LoginButton fblogin;
    private Button signup;
    private EditText firstname, lastname, Email, Password;
    private CallbackManager callbackmanager;
    private TextView signin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fblogin = findViewById(R.id.fblogin);
        Email = findViewById(R.id.email);
        progressDialog = new ProgressDialog(this);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        signup =findViewById(R.id.signup);
        Password = findViewById(R.id.password);
        signin = findViewById(R.id.signin);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            //start profile activity
            finish();
            startActivity(new Intent(getApplicationContext(),profileActivity.class));
        }

        signin.setOnClickListener(this);
        signup.setOnClickListener(this);

        callbackmanager = CallbackManager.Factory.create();
        fblogin.setReadPermissions(Arrays.asList("email", "public profile"));
        fblogin.registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackmanager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokentracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                firstname.setText("");
                lastname.setText("");
                Email.setText("");
                Toast.makeText(MainActivity.this, "User logged out", Toast.LENGTH_LONG).show();

            } else loaduserprofile(currentAccessToken);
        }
    };

    private void loaduserprofile(AccessToken newaccesstoken) {
        GraphRequest request = GraphRequest.newMeRequest(newaccesstoken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    Email.setText(email);
                    firstname.setText(first_name);
                    lastname.setText(last_name);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void registerUser () {

        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            // email is empty
            Toast.makeText(this,"Please enter valid e-mail",Toast.LENGTH_LONG).show();
            //stop the function
           return;
        }
        if (TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            // stop the function
            return;
        }
        // at this point, email and password are both valid
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //user is successfully registered
                    //start next activity

                    Toast.makeText(MainActivity.this,"Registration successfull",Toast.LENGTH_LONG).show();
                    progressDialog.hide();

                        finish();
                        startActivity(new Intent(getApplicationContext(),profileActivity.class));
                    }

                else{
                    Toast.makeText(MainActivity.this,"Registration was not successfull",Toast.LENGTH_LONG).show();
                    progressDialog.hide();

                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v == signup) {
            registerUser();
        }
        if (v==signin){
            //login activity wird geladen
            startActivity(new Intent(this,Login.class));
        }

    }
}
