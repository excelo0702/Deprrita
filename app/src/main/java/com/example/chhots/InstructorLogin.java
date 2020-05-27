package com.example.chhots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InstructorLogin extends AppCompatActivity {



    TextInputLayout email_login_ttl,password_login_ttl;
    EditText email_login,password_login;
    Button loginBtn;
    SignInButton Gbtn;
    TextView register,forgotpassword;
    private static final int RC_SIGN_IN = 234;
    private FirebaseAuth auth;
    private DatabaseReference database;

    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginLogin";
    private static final String EMAIL = "email";
    LoginButton loginButton;
    CallbackManager callbackManager;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_login);

        email_login_ttl = findViewById(R.id.email_instructor_login_TIL);
        password_login_ttl = findViewById(R.id.password_instructor_login_TIL);
        email_login = findViewById(R.id.email_instructor_login);
        password_login = findViewById(R.id.password_instructor_login);
        loginBtn = findViewById(R.id.login_instructor_btn);
        register = findViewById(R.id.signup_instructor);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email,password;
                email = email_login.getText().toString();
                password = password_login.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    email_login_ttl.setFocusable(true);
                    email_login_ttl.setError("Enter Email Address");
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    password_login_ttl.setFocusable(true);
                    password_login_ttl.setError("Enter Password");

                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Checking you are instructor....",Toast.LENGTH_SHORT).show();

                                    database.child("InstructorInfo").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists())
                                            {
                                                Intent intent = new Intent(InstructorLogin.this,MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(),"Create Account",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Logoooo",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InstructorLogin.this,Signup.class));

            }
        });



    }
}
