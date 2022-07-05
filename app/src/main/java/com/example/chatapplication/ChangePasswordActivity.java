package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldPassWord, newPassWord, reNewPassWord;
    Button btnChangePassWord, btnCancel;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPassWord = findViewById(R.id.txtOldPassword);
        newPassWord = findViewById(R.id.txtNewPassword);
        reNewPassWord = findViewById(R.id.txtRenewPassword);
        btnChangePassWord = findViewById(R.id.btnChangePassword);
        btnCancel = findViewById(R.id.btnOut);
        btnChangePassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewPassWord();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }

    private void cancel() {
        finish();
    }

    private void setNewPassWord() {
        String txtOldPassWord = String.valueOf(oldPassWord.getText());
        String txtNewPassWord = String.valueOf(newPassWord.getText());
        String txtReNewPassWord = String.valueOf(reNewPassWord.getText());
        if (txtOldPassWord.length() < 8){
            newPassWord.setError("Password must be more than 8 characters");
            newPassWord.requestFocus();
            return;
        }
        if (txtNewPassWord.length() < 8){
            newPassWord.setError("Password must be more than 8 characters");
            newPassWord.requestFocus();
            return;
        }
        if (txtReNewPassWord.length() < 8){
            reNewPassWord.setError("Password must be more than 8 characters");
            reNewPassWord.requestFocus();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        String currentEmail = firebaseUser.getEmail();
        assert currentEmail != null;
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, txtOldPassWord);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseUser.updatePassword(txtNewPassWord)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePasswordActivity.this, "Password was changed successfully", Toast.LENGTH_SHORT).show();
                                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
                                                String uid = firebaseUser.getUid();
                                                dbRef.child(uid).child("password").setValue(txtNewPassWord);
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Authentication failed, wrong password?", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
        finish();
    }
}