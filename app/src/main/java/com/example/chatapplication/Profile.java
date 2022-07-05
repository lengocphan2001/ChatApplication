package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    Button btnUpLoad, logOut, changePassword;
    ImageView imgUser;
    FirebaseAuth mAuth;
    Uri imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        imgUser = findViewById(R.id.proFileImage);
        btnUpLoad = findViewById(R.id.btnUploadPhoto);
        changePassword = findViewById(R.id.btnChangePassword);
        logOut = findViewById(R.id.btnLogOut);
        btnUpLoad.setOnClickListener(this);
        logOut.setOnClickListener(this);
        imgUser.setOnClickListener(this);
        changePassword.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUploadPhoto: {
                uploadImage();
                break;
            }
            case R.id.btnLogOut: {
                mAuth.signOut();
                Intent signIn = new Intent(Profile.this, LoginActivity.class);
                startActivity(signIn);
                break;
            }
            case R.id.proFileImage: {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
                break;
            }
            case R.id.btnChangePassword:{
                Intent intent = new Intent(Profile.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                updateProfilePicture(task.getResult().toString());
                            }
                        }
                    });
                    progressDialog.dismiss();
                    Toast.makeText(Profile.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(Profile.this, "Upload fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfilePicture(String toString) {
        FirebaseDatabase.getInstance().getReference("Users/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() +
                "/imgProfile").setValue(toString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            getImageInImageView();
        }
    }

    private void getImageInImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgUser.setImageBitmap(bitmap);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}