package com.example.chhots;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class SignUpNextScreen extends AppCompatActivity {

    private ImageView userImage_signup;
    private EditText user_name_signup,user_profession_signup;
    private Spinner user_dancer_level;
    private RadioButton r1,r2,r3,r4,r5,r6;
    private Button finalsignUp;
    ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseReference;

    private String Semail,Spassword,profession,level,name,user_name;
    private int R1=0,R2=0,R3=0,R4=0,R5=0,R6=0;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri mImageUri;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_next_screen);

        init();

        Intent intent = getIntent();
        Semail = intent.getStringExtra("email");
        Spassword = intent.getStringExtra("password");


        userImage_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        finalsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalsignUp.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);//min secs millisecs
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        SignUpNextScreen.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                finalsignUp.setEnabled(true);

                            }
                        });
                    }
                }).start();

                profession = user_profession_signup.getText().toString();

                user_dancer_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        level = adapterView.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        level = "Beignner";
                    }
                });
                if (profession == null) {
                    Toast.makeText(getApplicationContext(), "Users Profession cant be null", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mImageUri==null)
                    return;
                name = user_profession_signup.getText().toString();
                if (name == null) {
                    Toast.makeText(getApplicationContext(), "UserName cant be null", Toast.LENGTH_SHORT).show();
                    return;
                }
                    auth.createUserWithEmailAndPassword(Semail, Spassword)
                            .addOnCompleteListener(SignUpNextScreen.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(SignUpNextScreen.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignUpNextScreen.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        final StorageReference reference = storageReference.child("UserProfileImage").child(auth.getCurrentUser().getUid()+getfilterExt(mImageUri));

                                        reference.putFile(mImageUri)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        reference.getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    InstructorInfoModel model = new InstructorInfoModel(auth.getCurrentUser().getUid(), Semail, profession, level, uri.toString(),name,"","beginner", R1, R2, R3, R4, R5, R6);
                                                                    mDatabaseReference.child("InstructorInfo").child(auth.getCurrentUser().getUid()).setValue(model);
                                                                    startActivity(new Intent(SignUpNextScreen.this, MainActivity.class));
                                                                }
                                                            });
                                                    }
                                                });

                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

            }




    });
    }

        @Override
        protected void onResume() {
            super.onResume();
            progressBar.setVisibility(View.GONE);
        }

    private String getfilterExt(Uri videoUri)
    {
        ContentResolver contentResolver = SignUpNextScreen.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }

    private void init()
    {
        userImage_signup = findViewById(R.id.userImage_signup);
        user_name_signup = findViewById(R.id.user_name_signup);
        user_profession_signup = findViewById(R.id.user_profession_signup);
        user_dancer_level = findViewById(R.id.user_dancer_level);
        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);
        r3 = findViewById(R.id.r3);
        r4 = findViewById(R.id.r4);
        r5 = findViewById(R.id.r5);
        r6 = findViewById(R.id.r6);
        finalsignUp = findViewById(R.id.signup_next_submit);
        progressBar =findViewById(R.id.progressBar_signup_next);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.r1:
                if (checked)
                    R1=1;
                else
                    R1=0;
                    break;
            case R.id.r2:
                if (checked)
                    R2=1;
                else
                    R2=0;
                break;
            case R.id.r3:
                if (checked)
                    R2=1;
                else
                    R3=0;
                break;
            case R.id.r4:
                if (checked)
                    R4=1;
                else
                    R4=0;
                break;
            case R.id.r5:
                if (checked)
                    R5=1;
                else
                    R5=0;
                break;
            case R.id.r6:
                if (checked)
                    R6=1;
                else
                    R6=0;
                break;

        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("2323232","111111");

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).placeholder(R.mipmap.ic_logo).into(userImage_signup);
        }

    }



}
