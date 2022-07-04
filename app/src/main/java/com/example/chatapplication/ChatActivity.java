package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    ArrayList<MessagesList> messagesListLists = new ArrayList<>();
    FirebaseAuth mAuth;
    CircleImageView imgUser;
    TextView txtUserName;
    ListView messageDisplay;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        txtUserName = findViewById(R.id.txtMessages);
        imgUser = findViewById(R.id.imgUser);
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, Profile.class);
                startActivity(intent);
            }
        });
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
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
                    Picasso.get().load(image).into(imgUser);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            HashMap<Pair<String, String>, String> storeLastMessage = new HashMap<>();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
            dbRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    storeLastMessage.clear();
                    String email = firebaseUser.getEmail();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        ModelChat modelChat = ds.getValue(ModelChat.class);
                        assert (modelChat != null);
                        String receiverEmail = modelChat.getReceiver();
                        Log.i("name", receiverEmail);
                        storeLastMessage.put(new Pair<String, String>(email, receiverEmail), modelChat.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Log.i("number of last message", String.valueOf(storeLastMessage.size()));
            txtUserName.setText(firebaseUser.getDisplayName());
            messageDisplay = findViewById(R.id.recyclerMessages);
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
                            String lastMess = "Chưa có tin nhắn";
                            if (storeLastMessage.containsKey(new Pair<String, String>(email, emailRoommate)))
                                lastMess = storeLastMessage.get(new Pair<String, String>(email, emailRoommate));
                            MessagesList messagesList = new MessagesList(emailRoommate, getName, lastMess, getProFilePic, 0);
                            messagesListLists.add(messagesList);
                        }
                    }
                    messageDisplay.setAdapter(new MessagesAdapter(messagesListLists, ChatActivity.this));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
}