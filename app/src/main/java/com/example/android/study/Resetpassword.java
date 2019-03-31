package com.example.android.study;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Resetpassword extends AppCompatActivity {
    private Button resetbutton;
    private EditText resetmail;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        resetbutton = findViewById(R.id.resetbutton);
        resetmail = findViewById(R.id.resetmail);
        firebaseAuth = FirebaseAuth.getInstance();
        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = resetmail.getText().toString().trim();
                if (TextUtils.isEmpty(useremail)){
                    Toast.makeText(Resetpassword.this,"Please enter your E-Mail",Toast.LENGTH_LONG).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Resetpassword.this,"Please check your E-Mail account",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Resetpassword.this,Login.class));

                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(Resetpassword.this,"An error occured: "+message,Toast.LENGTH_LONG).show();

                            }

                        }
                    });
                }

            }
        });

    }

}
