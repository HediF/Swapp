package com.example.android.study;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Messages extends AppCompatActivity {
    BottomNavigationView navigationView;
    private FirebaseUser user;
    Set setA ;
    private MyAdapt customAdapter;
    private ArrayList<Displayuser> arrayList;
    private DatabaseReference dr;
    private ListView userslist;
    private String otheruserid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        navigationView = findViewById(R.id.bottomnavigation);
        navigationView.setOnNavigationItemSelectedListener(navListener);
        user = FirebaseAuth.getInstance().getCurrentUser();
        setA = new HashSet();
        userslist = findViewById(R.id.userslist);
        DatabaseReference messagereference = FirebaseDatabase.getInstance().getReference("Chats");
        dr = FirebaseDatabase.getInstance().getReference().child("users");

        messagereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               setA.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    String reciever =  snapshot.child("receiver").getValue().toString();
                    String sender = snapshot.child("sender").getValue().toString();

                    if (user.getUid().equals(reciever) ) {
                        setA.add(sender);
                    }
                    if(user.getUid().equals(sender)){
                        setA.add(reciever);
                    }

                    showUsers(setA);

                }




            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView tselectedFromList = view.findViewById(R.id.displayusername);
                final String selectedFromList = tselectedFromList.getText().toString();

                DatabaseReference drx = FirebaseDatabase.getInstance().getReference().child("users");
                            drx.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                        String name = datas.child("Name").getValue().toString();

                                        if (name.equals(selectedFromList)){
                                            otheruserid = datas.getKey();
                                            Intent i = new Intent(getApplicationContext(),SendMessage.class);
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
        });
    }




    private void showUsers(final Set usercontact) {



        userslist.setAdapter(null);

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList = new ArrayList<Displayuser>();

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    String name = datas.child("Name").getValue().toString();
                    String image = datas.child("Profilepicture").getValue().toString().trim();
                    String id = datas.getKey();

                    if ( usercontact.contains(id) ) {

                        arrayList.add(new Displayuser(name,image));
                        customAdapter = new MyAdapt(Messages.this, arrayList);
                        userslist.setAdapter(customAdapter);


                    }
                }




            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {

                        case R.id.homeprofile:
                            startActivity(new Intent(Messages.this, profileActivity.class));
                            break;
                        case R.id.search:
                            startActivity(new Intent(Messages.this, SearchSubjects.class));
                            break;
                        case R.id.messages:
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
                startActivity(new Intent(Messages.this, Login.class));
            case R.id.item2:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
