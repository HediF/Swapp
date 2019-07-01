package com.example.android.study;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText Email;
    private EditText password;
    private TextView resetpassword;
    private TextView signup;
    private Button login;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Email = findViewById(R.id.emaillogin);
        password = findViewById(R.id.passwordlogin);
        resetpassword = findViewById(R.id.resetpassword);
        signup = findViewById(R.id.signuplogin);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        resetpassword.setOnClickListener(this);
        signup.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            //start profile activity
            finish();
            startActivity(new Intent(getApplicationContext(),profileActivity.class));
        }

    }
    private void Userlogin() {
        String email = Email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            // email is empty
            Toast.makeText(this,"Please enter valid e-mail",Toast.LENGTH_LONG).show();
            //stop the function
            return;
        }
        if (TextUtils.isEmpty(pass)){
            //password is empty
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            // stop the function
            return;
        }
        // at this point, email and password are both valid
        progressDialog.setMessage("Connecting...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    //start the profile activity
                    finish();
                    startActivity(new Intent(getApplicationContext(),profileActivity.class));
                }
                else {
                    Toast.makeText(Login.this,"Login failed",Toast.LENGTH_LONG).show();
                }

            }
        });






    }

    @Override
    public void onClick(View v) {
        if (v==login){
            Userlogin();
        }
        if (v== signup){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
        if (v== resetpassword){
            //reset password
            startActivity(new Intent(Login.this,Resetpassword.class));
        }
    }
}
