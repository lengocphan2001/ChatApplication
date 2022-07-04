package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    TextView continueSingIn;
    EditText edtFullName, edtEmail, edtPassword;
    Button btnRegister;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        continueSingIn = findViewById(R.id.txtLinkSignIn);
        btnRegister = findViewById(R.id.btnRegister);
        edtFullName = findViewById(R.id.edtUser);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (fullName.isEmpty()){
                    edtFullName.setError("Không để trống mục này");
                    edtFullName.requestFocus();
                    return;
                }
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
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    User user = new User(fullName, email, password);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(RegisterActivity.this,"Đăng kí thành công!", Toast.LENGTH_LONG).show();
                                                        Intent signIn = new Intent(RegisterActivity.this, LoginActivity.class);
                                                        startActivity(signIn);
                                                    }else{
                                                        Toast.makeText(RegisterActivity.this,"Đăng kí không thành công, thử lại!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }else{
                                    Toast.makeText(RegisterActivity.this,"Đăng kí không thành công!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        continueSingIn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent singIn = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(singIn);
    }
}