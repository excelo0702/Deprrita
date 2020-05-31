package com.example.chhots.User_Profile;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.InstructorInfoModel;
import com.example.chhots.MainActivity;
import com.example.chhots.R;
import com.example.chhots.SignUpNextScreen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class edit_profile extends Fragment {


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

    Spinner spinner;


    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private StorageTask mUploadTask;

    InstructorInfoModel model;



    public edit_profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        init(view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();



        fetchUserInfo();



        userImage_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        finalsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfile();
            }
        });


        return view;
    }

    private void UpdateProfile() {
        if(mImageUri==null)
        {

            InstructorInfoModel mode = new InstructorInfoModel(auth.getCurrentUser().getUid(), model.getUserEmail(), user_profession_signup.getText().toString(), level, model.getUserImageurl(),user_name_signup.getText().toString(),"",model.getBadge(), R1, R2, R3, R4, R5, R6);
            mDatabaseReference.child("InstructorInfo").child(auth.getCurrentUser().getUid()).setValue(mode);
        }
        else
        {

            final StorageReference reference = storageReference.child("UserProfileImage").child(auth.getCurrentUser().getUid()+getfilterExt(mImageUri));

            reference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            InstructorInfoModel mode = new InstructorInfoModel(auth.getCurrentUser().getUid(), model.getUserEmail(), user_profession_signup.getText().toString(), level, uri.toString(),user_name_signup.getText().toString(),"",model.getBadge(), R1, R2, R3, R4, R5, R6);
                                            mDatabaseReference.child("InstructorInfo").child(auth.getCurrentUser().getUid()).setValue(mode);
                                            Toast.makeText(getContext(),"Updated",Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    });

            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    private void fetchUserInfo()
    {
        databaseReference.child(getString(R.string.InstructorInfo)).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                model = dataSnapshot.getValue(InstructorInfoModel.class);
                user_name_signup.setText( model.getUserName());
                Picasso.get().load(Uri.parse(model.getUserImageurl())).into(userImage_signup);
                user_profession_signup.setText(model.getUserProfession());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void init(View view) {
        userImage_signup = view.findViewById(R.id.userImage_signupp);
        user_name_signup = view.findViewById(R.id.user_name_signupp);
        user_profession_signup = view.findViewById(R.id.user_profession_signupp);
        r1 = view.findViewById(R.id.r11);
        r2 = view.findViewById(R.id.r22);
        r3 = view.findViewById(R.id.r33);
        r4 = view.findViewById(R.id.r44);
        r5 = view.findViewById(R.id.r55);
        r6 = view.findViewById(R.id.r66);
        finalsignUp = view.findViewById(R.id.signup_next_submitt);
        progressBar = view.findViewById(R.id.progressBar_signup_nextt);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }


    private String getfilterExt(Uri videoUri)
    {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
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
