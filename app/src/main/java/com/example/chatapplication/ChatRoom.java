package com.example.chatapplication;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatapplication.chat.ChatAdapter;
import com.example.chatapplication.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoom extends AppCompatActivity {
    TextView txtNameRoommate;
    CircleImageView imgRoommate;
    RecyclerView chatRecyclerView;
    ImageButton imageButtonSendMsg;
    ImageView imgBackIcon;
    EditText editTextSendMsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;
    String name, imageURL, email;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        txtNameRoommate = findViewById(R.id.txtChatRoommateAdapter);
        imgRoommate = findViewById(R.id.imgChatRoomImage);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        imageButtonSendMsg = findViewById(R.id.imgSendMessage);
        editTextSendMsg = findViewById(R.id.edtSendMessage);
        imgBackIcon = findViewById(R.id.imgBackIcon);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        userDbRef = firebaseDatabase.getReference("Users");
        Intent a = getIntent();
        name = a.getStringExtra("nameRoommate");
        email = a.getStringExtra("email");
        imageURL = a.getStringExtra("imageURL");
        Picasso.get().load(imageURL).into(imgRoommate);
        txtNameRoommate.setText(name);
        imgBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String image = "" + ds.child("imgProfile").getValue();
                    try{
                        Picasso.get().load(image).placeholder(R.drawable.ic_launcher_background).into(imgRoommate);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imageButtonSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextSendMsg.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(ChatRoom.this, "Không thể gửi tin nhắn trống", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }
            }
        });
        readMessage();
        // Tooi laf Le Ngoc Phan
    }

    private void readMessage() {
        List<ModelChat> list = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat modelChat = ds.getValue(ModelChat.class);
                    assert (modelChat != null);
                    if ((modelChat.getReceiver().equals(email) && modelChat.getSender().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()) ||
                            modelChat.getReceiver().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()) && modelChat.getSender().equals(email))){
                        list.add(modelChat);
                    }
                }
                chatAdapter = new ChatAdapter(ChatRoom.this, list, imageURL);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        Log.i("receiver", email);
        hashMap.put("sender", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());
        hashMap.put("receiver", email);
        hashMap.put("message", message);
        hashMap.put("time", String.valueOf(System.currentTimeMillis()));
        databaseReference.child("Chats").push().setValue(hashMap);
        editTextSendMsg.setText("");
    }
}