package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chatapplication.chat.ChatAdapter;
import com.example.chatapplication.message.MessagesAdapter;
import com.example.chatapplication.message.MessagesList;
import com.example.chatapplication.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    ArrayList<MessagesList> messagesListLists = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    CircleImageView imgUser;
    TextView txtUserName;
    ListView messageDisplay;
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        txtUserName = findViewById(R.id.txtMessages);
        imgUser = findViewById(R.id.imgUser);
        messageDisplay = findViewById(R.id.recyclerMessages);
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, Profile.class);
                startActivity(intent);
            }
        });
        if (firebaseUser == null){
            Intent signIn = new Intent(ChatActivity.this, LoginActivity.class);
            startActivity(signIn);
        }else {
            String uid = firebaseUser.getUid();
            databaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("userName").getValue(String.class);
                    txtUserName.setText(name);
                    String image = snapshot.child("imgProfile").getValue(String.class);
                    assert image != null;
                    if (!image.equals("@drawable/img"))
                        Picasso.get().load(image).into(imgUser);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            HashMap<Pair<String, String>, String> storeLastMessage = new HashMap<>();
            HashMap<Pair<String, String>, String> timeLastMess = new HashMap<>();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
            dbRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    storeLastMessage.clear();
                    String email = firebaseUser.getEmail();
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        ModelChat modelChat = ds.getValue(ModelChat.class);
                        assert (modelChat != null);
                        String receiverEmail = modelChat.getReceiver();
                        if (modelChat.getSender().equals(email)) {
                            storeLastMessage.put(new Pair<String, String>(email, receiverEmail), modelChat.getMessage());
                            timeLastMess.put(new Pair<String, String>(email, receiverEmail), modelChat.getTime());
                        }
                        if (modelChat.getReceiver().equals(email)) {
                            storeLastMessage.put(new Pair<String, String>(email, modelChat.getSender()), modelChat.getMessage());
                            timeLastMess.put(new Pair<String, String>(email, modelChat.getSender()), modelChat.getTime());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            txtUserName.setText(firebaseUser.getDisplayName());
            messageDisplay = findViewById(R.id.recyclerMessages);
            updateUI(storeLastMessage, timeLastMess);
            messageDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent a = new Intent(ChatActivity.this, ChatRoom.class);
                    MessagesList user = messagesListLists.get(position);
                    String nameRoommate = user.getName();
                    String email = user.getEmail();
                    String imageURL = user.getProfilePic();
                    a.putExtra("nameRoommate", nameRoommate);
                    a.putExtra("email", email);
                    a.putExtra("imageURL", imageURL);
                    startActivity(a);
                }
            });
        }
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//        .setSmallIcon(R.drawable.notification_icon)
//        .setContentTitle("My notification")
//        .setContentText("Much longer text that cannot fit one line...")
//        .setStyle(new NotificationCompat.BigTextStyle()
//                .bigText("Much longer text that cannot fit one line..."))
//        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
    public void updateUI(HashMap<Pair<String, String>, String> storeLastMessage, HashMap<Pair<String, String>, String> timeLastMess){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesListLists.clear();
                String email = firebaseUser.getEmail();
                for (DataSnapshot dataSnapshot : snapshot.child("Users").getChildren()) {
                    assert email != null;
                    if (!email.equals(dataSnapshot.child("email").getValue(String.class))) {
                        String emailRoommate = dataSnapshot.child("email").getValue(String.class);
                        String getName = dataSnapshot.child("userName").getValue(String.class);
                        String getProFilePic = dataSnapshot.child("imgProfile").getValue(String.class);
                        String lastMess = "Chưa có tin nhắn nào";
                        String timeStamp = "";
                        if (storeLastMessage.containsKey(new Pair<String, String>(email, emailRoommate))) {
                            lastMess = storeLastMessage.get(new Pair<String, String>(email, emailRoommate));
                            timeStamp = timeLastMess.get(new Pair<String, String>(email, emailRoommate));
                        }
                        if (storeLastMessage.containsKey(new Pair<String, String>(emailRoommate, email))) {
                            lastMess = storeLastMessage.get(new Pair<String, String>(emailRoommate, email));
                            timeStamp = timeLastMess.get(new Pair<String, String>(emailRoommate, email));
                        }
                        MessagesList messagesList = new MessagesList(emailRoommate, getName, lastMess, getProFilePic, timeStamp);
                        messagesListLists.add(messagesList);
                    }
                }
//                Collections.sort(messagesListLists, new Comparator<MessagesList>(){
//                    public int compare(MessagesList s1, MessagesList s2) {
//                        if (!s1.getTime().isEmpty() && !s2.getTime().isEmpty())
//                            return s1.getTime().compareToIgnoreCase(s2.getTime());
//                        else return -1;
//                    }
//                });
                messageDisplay.setAdapter(new MessagesAdapter(messagesListLists, ChatActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            Intent signIn = new Intent(ChatActivity.this, LoginActivity.class);
            startActivity(signIn);
        }
    }
//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        //
        //// notificationId is a unique int for each notification that you must define
        //notificationManager.notify(notificationId, mBuilder.build());
//    }
}