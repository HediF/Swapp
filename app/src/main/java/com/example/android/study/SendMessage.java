package com.example.android.study;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.List;


public class SendMessage extends AppCompatActivity {
    private ImageButton sendmessage;
    private TextView textmessage;
private Intent intent;
private FirebaseAuth firebaseAuth;
private DatabaseReference databaseReference;private DatabaseReference dr;
private FirebaseUser user;
private String otheruserid;
public RecyclerView recycler;
public MessageAdapter messageAdapter;
public List<Chat> mchat;
private DatabaseReference messagereference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        // setupActionBar();
         sendmessage = findViewById(R.id.sendbutton);
         textmessage = findViewById(R.id.messagetosend);

         recycler = findViewById(R.id.recycler);
         /*recycler.setHasFixedSize(true);*/
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
         linearLayoutManager.setStackFromEnd(true);
         recycler.setLayoutManager(linearLayoutManager);


        firebaseAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference().child("users");
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        intent = getIntent();
        otheruserid = intent.getStringExtra("otheruserid");


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(otheruserid);
        mchat = new ArrayList<>();
       reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                readMessages(user.getUid(), otheruserid );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = textmessage.getText().toString();
                if (!msg.equals("")){
                    Sendmessage(user.getUid(),otheruserid,msg);
                } else {
                    Toast.makeText(SendMessage.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                }
                if(recycler.getAdapter().getItemCount()==0)sendNotification(v);
                textmessage.setText("");
            }
        });

        recycler.setAdapter(messageAdapter);
    }









    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        ActionBar.LayoutParams lp1 = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customNav = LayoutInflater.from(this).inflate(R.layout.user_list_item, null);

        actionBar.setCustomView(customNav, lp1);
    }
    private void Sendmessage(String sender, String receiver, String message){

        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap <> ();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        databaseReference.child("Chats").push().setValue(hashMap); }
    private void readMessages(final String myid, final String userid) {


        messagereference = FirebaseDatabase.getInstance().getReference("Chats");
        messagereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String msg = snapshot.child("message").getValue().toString();
                    String reciever =  snapshot.child("receiver").getValue().toString();
                    String sender = snapshot.child("sender").getValue().toString();
                    Chat chat = new Chat(sender,reciever,msg);

                   if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(new Chat(sender,reciever,msg));
                    }

                }

                messageAdapter = new MessageAdapter(SendMessage.this, mchat);

                recycler.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void sendNotification(View view) {

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Swapp")
                        .setContentText("Hopefully you would get your item :)");


        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                mNotificationManager.notify(001, mBuilder.build());
    }
}









