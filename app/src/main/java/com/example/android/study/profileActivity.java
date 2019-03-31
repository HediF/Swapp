package com.example.android.study;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class profileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView save;
    private EditText Name;
    private EditText Location;
    private EditText Phone;
    private EditText Email;
    BottomNavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private ImageView profilepicture;
    private StorageReference storageReference;
    private Uri Filepath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        save = findViewById(R.id.save);
        Name = findViewById(R.id.nameprofile);

        Location = findViewById(R.id.location);
        Phone = findViewById(R.id.phone);
        Email = findViewById(R.id.emailprofile);
        navigationView = findViewById(R.id.bottomnavigation);
        profilepicture = findViewById(R.id.profilepicture);
         final String url = new String("http://storeprestamodules.com/media/catalog/product/cache/1/image/240x/9df78eab33525d08d6e5fb8d27136e95/u/s/users-256x256.png");


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        if (firebaseAuth.getCurrentUser() == null) {
            //start login activity
            finish();
            startActivity(new Intent(this, Login.class));

        }








        save.setOnClickListener(this);
        navigationView.setOnNavigationItemSelectedListener(navListener);
        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = "";
                if (dataSnapshot.child("Name").getValue() != null) {
                    name = dataSnapshot.child("Name").getValue().toString();
                }
                Name.setText(name);
                String location = "";
                if (dataSnapshot.child("Location").getValue() != null) {
                    location = dataSnapshot.child("Location").getValue().toString();
                }
                Location.setText(location);
                String phone = "";
                if (dataSnapshot.child("Phone").getValue() != null) {
                    phone = dataSnapshot.child("Phone").getValue().toString();
                }
                Phone.setText(phone);
                String email = "";
                if (dataSnapshot.child("Email").getValue() != null) {
                    email = dataSnapshot.child("Email").getValue().toString();
                }
                Email.setText(email);
                String profilepic = "";
                if (dataSnapshot.child("Profilepicture").getValue() != null) {
                  profilepic = dataSnapshot.child("Profilepicture").getValue().toString();
                    Glide.with(profileActivity.this).load(profilepic).into(profilepicture);

                }else{
                    databaseReference.child("Profilepicture").setValue(url);
                    profilepic = dataSnapshot.child("Profilepicture").getValue().toString();
                    Glide.with(profileActivity.this).load(profilepic).into(profilepicture);
                }




            }







            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void saveUser() {
        String name = Name.getText().toString().trim();
        String location = Location.getText().toString().trim();
        String phone = Phone.getText().toString().trim();
        String email = Email.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name, email, phone, location);
        user = firebaseAuth.getCurrentUser();

        databaseReference.child("Name").setValue(name);
        databaseReference.child("Location").setValue(location);
        databaseReference.child("Phone").setValue(phone);
        databaseReference.child("Email").setValue(email);


        Toast.makeText(profileActivity.this, "Informations saved successfully", Toast.LENGTH_SHORT).show();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {

                        case R.id.homeprofile:
                            break;
                        case R.id.search:
                            startActivity(new Intent(profileActivity.this, SearchSubjects.class));
                            break;
                        case R.id.messages:
                            startActivity(new Intent(profileActivity.this, Messages.class));


                    }
                    return true;
                }
            };


    @Override
    public void onClick(View v) {
        if (v == save) {
            saveUser();
            Email.setText(Email.getText());
            Phone.setText(Phone.getText());
            Name.setText(Name.getText());
            Location.setText(Location.getText());


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(profileActivity.this, Login.class));
            case R.id.item2:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Filepath);
                profilepicture.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadFile();

        }
    }

    private void uploadFile() {
        if (Filepath != null) {


            final StorageReference picture = storageReference.child("image.png"+user.getUid());
           final ProgressDialog pd = new ProgressDialog(profileActivity.this);

                picture.putFile(Filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Your picture has been successfully saved!", Toast.LENGTH_LONG).show();

                              String profilepictureurl = taskSnapshot.getDownloadUrl().toString();
                          databaseReference.child("Profilepicture").setValue(profilepictureurl);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();

                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            pd.setMessage("Loading");
                            pd.show();
                        }
                    });
        }
        else {
        }
    }
}
