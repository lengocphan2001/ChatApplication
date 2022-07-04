package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    Button btnSignIn;
    TextView txtSignUp;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtSignUp = findViewById(R.id.txtLinkSignUp);
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class) );
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (email.isEmpty()){
                    edtEmail.setError("Không để trống mục này");
                    edtEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtEmail.setError("Địa chỉ email không hợp lệ!");
                    edtEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()){
                    edtPassword.setError("Không để trống mục này");
                    edtPassword.requestFocus();
                    return;
                }
                if (password.length() < 8){
                    edtPassword.setError("Mật khẩu dài hơn 8 kí tụ");
                    edtPassword.requestFocus();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent insideApp = new Intent(LoginActivity.this, ChatActivity.class);
                            startActivity(insideApp);
                        }else{
                            Toast.makeText(LoginActivity.this,"Đăng nhập không thành công, thử lại!", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
    }
}