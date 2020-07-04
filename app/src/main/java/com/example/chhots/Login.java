package com.example.chhots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONObject;

import java.util.Arrays;

import static com.example.chhots.BuildConfig.APPLICATION_ID;

public class Login extends AppCompatActivity {

    TextInputLayout email_login_ttl,password_login_ttl;
    EditText email_login,password_login;
    Button loginBtn;
    TextView register,forgotpassword;
    private static final int RC_SIGN_IN = 234;
    private FirebaseAuth auth;

    private static final String TAG = "LoginLogin";
    private static final String EMAIL = "email";
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    //    FacebookSdk.sdkInitialize(getApplicationContext());
      //  AppEventsLogger.activateApp(this);

        email_login_ttl = findViewById(R.id.email_login_TIL);
        password_login_ttl = findViewById(R.id.password_login_TIL);
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        loginBtn = findViewById(R.id.login_btn);
        register = findViewById(R.id.signup_text);
        forgotpassword= findViewById(R.id.forgot_password_text);
        auth = FirebaseAuth.getInstance();




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Signup.class));

            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email,password;
                email = email_login.getText().toString();
                password = password_login.getText().toString();
                loginBtn.setEnabled(false);
                if (TextUtils.isEmpty(email)) {
                    email_login_ttl.setFocusable(true);
                    email_login_ttl.setError("Enter Email Address");
                    loginBtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    password_login_ttl.setFocusable(true);
                    password_login_ttl.setError("Enter Password");
                    loginBtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Login",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    loginBtn.setEnabled(true);
                                    Login.this.finish();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });



    }

    private void showRecoverPasswordDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailEt = new EditText(this);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,0,10,0);
        emailEt.setHint("Email");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        emailEt.setLayoutParams(lp);
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(linearLayout);
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }




}
