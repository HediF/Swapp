package com.example.android.study;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class SearchSubjects extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner1;
    private Spinner spinner2;
    private BottomNavigationView navigationView;
    private Button matchme;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private MyAdapt customAdapter;
    private DatabaseReference dr;
    private String mysubject,yoursubject;
    private ListView userslist;
    private String otheruserid;
    ArrayList<Displayuser> arrayList = new ArrayList<Displayuser>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_subjects);
        spinner1 = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Subjects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(this);
        userslist = findViewById(R.id.userslist);
        spinner2.setOnItemSelectedListener(this);


        firebaseAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference().child("users");
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        if (firebaseAuth.getCurrentUser() == null) {
            //start login activity
            finish();
            startActivity(new Intent(this, Login.class));

        }
        navigationView = findViewById(R.id.bottomnavigation);
        navigationView.setOnNavigationItemSelectedListener(navListener);
        matchme = findViewById(R.id.matchme);
        matchme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.clear();

                 mysubject = spinner2.getSelectedItem().toString().trim();
                 yoursubject = spinner1.getSelectedItem().toString().trim();
                savesubjects();

                showUsers();


            }


        });

        userslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView tselectedFromList = view.findViewById(R.id.displayusername);
                final String selectedFromList = tselectedFromList.getText().toString();

                String[] choices = {"Send message", "Show location"};
                 AlertDialog.Builder builder = new AlertDialog.Builder(SearchSubjects.this,AlertDialog.THEME_HOLO_DARK);
                builder.setTitle("Choose an action");
                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                       if (which == 1){
                            dr.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                        String name = datas.child("Name").getValue().toString();
                                        if (name.equals(selectedFromList)) {
                                            otheruserid = datas.getKey();

                                            String address = datas.child("Location").getValue().toString();
                                            Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address));
                                            startActivity(searchAddress);
                                        }
                                    }
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                       }      if (which==0){
                           dr.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   for (DataSnapshot datas : dataSnapshot.getChildren()){
                                       String otheruserid = datas.getKey();
                                       String name = datas.child("Name").getValue().toString();


                                       if (name.equals(selectedFromList)){
                                           Intent i = new Intent(SearchSubjects.this,SendMessage.class);
                                            i.putExtra("otheruserid",otheruserid);
                                           startActivity(i);


                                       }

                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });
                            }
                    }





                });
                builder.show();

            }
        });


    }



    private void savesubjects() {
     String mysubject = spinner2.getSelectedItem().toString().trim();
     String yoursubject = spinner1.getSelectedItem().toString().trim();
     databaseReference.child("Mysubject").setValue(mysubject);
     databaseReference.child("Yoursubject").setValue(yoursubject);
     }

    private void showUsers() {



        userslist.setAdapter(null);

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 arrayList = new ArrayList<Displayuser>();

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    String name = datas.child("Name").getValue().toString();
                    String image = datas.child("Profilepicture").getValue().toString().trim();
                    String id = datas.getKey();
                    String userxmysubject = datas.child("Mysubject").getValue().toString();
                    String userxyoursubject = datas.child("Yoursubject").getValue().toString();
                    if (mysubject.equals(userxyoursubject) && yoursubject.equals(userxmysubject) && !id.equals(user.getUid()) ) {

                        arrayList.add(new Displayuser(name,image));
                        customAdapter = new MyAdapt(SearchSubjects.this, arrayList);
                        userslist.setAdapter(customAdapter);




                    }
                }




            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {

                        case R.id.homeprofile:
                            startActivity(new Intent(SearchSubjects.this,profileActivity.class));
                            break;



                        case R.id.search:
                            break;
                        case R.id.messages:
                            startActivity(new Intent(SearchSubjects.this,Messages.class));
                            break;





                    }
                    return true;
                }
            };
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
                startActivity(new Intent(SearchSubjects.this, Login.class));
            case R.id.item2:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
