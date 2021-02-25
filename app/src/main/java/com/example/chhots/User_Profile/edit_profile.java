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

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chhots.BuildConfig;
import com.example.chhots.LoadingDialog;
import com.example.chhots.Models.InstructorInfoModel;
import com.example.chhots.MainActivity;
import com.example.chhots.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class edit_profile extends Fragment {


    private ImageView userImage_signup,cover_photo,choose_cover_photo,save_cover_photo;
    private EditText user_name_signup,user_profession_signup,phoneE,statusE,aboutE;
    private Spinner user_dancer_level;
    private RadioButton r1,r2,r3,r4,r5,r6;
    private Button finalsignUp;
    ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseReference;

    private String Semail,Spassword,profession,level,name,user_name;
    private int R1=0,R2=0,R3=0,R4=0,R5=0,R6=0;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri mImageUri,mCoverUri;
    private StorageReference storageReference;

    Spinner spinner;
    StringBuffer category=new StringBuffer("00000000000000000000");


    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private StorageTask mUploadTask;

    InstructorInfoModel model;
    int flag=0;
    LoadingDialog loadingDialog;



    public edit_profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        init(view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        choose_cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=0;
                save_cover_photo.setVisibility(View.VISIBLE);
                openFileChooser();
            }
        });

        save_cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadCoverPhoto();
            }
        });

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

    private void uploadCoverPhoto() {
        loadingDialog.startLoadingDialog();
        final StorageReference reference = storageReference.child("InstructorCoverPhoto").child(auth.getCurrentUser().getUid()+getfilterExt(mCoverUri));
        reference.putFile(mCoverUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        loadingDialog.DismissDialog();
                                      //  UserInfoModel mode = new UserInfoModel(auth.getCurrentUser().getUid(),model.getUserName(),uri.toString());
                                        //if(mode!=null)
                                          //  mDatabaseReference.child("InstructorCoverPhoto").child(auth.getCurrentUser().getUid()).setValue(mode);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.DismissDialog();
                                    }
                                });
                    }
                });

    }

    private void UpdateProfile() {
        if(mImageUri==null)
        {

            InstructorInfoModel mode = new InstructorInfoModel(auth.getCurrentUser().getUid(),user_name_signup.getText().toString(),model.getUserEmail(), user_profession_signup.getText().toString(),phoneE.getText().toString().trim(), statusE.getText().toString(),aboutE.getText().toString(),level, mImageUri.toString(),model.getPoints(),model.getBadge(),String.valueOf(category),model.getEarn());
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
                                            InstructorInfoModel mode = new InstructorInfoModel(auth.getCurrentUser().getUid(),user_name_signup.getText().toString(),model.getUserEmail(), user_profession_signup.getText().toString(),phoneE.getText().toString().trim(), statusE.getText().toString(),aboutE.getText().toString(),level, uri.toString(),model.getPoints(),model.getBadge(),String.valueOf(category),model.getEarn());
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
        databaseReference.child(getString(R.string.InstructorInfo)).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("pop pop ",dataSnapshot.getValue()+" pop ");
                model = dataSnapshot.getValue(InstructorInfoModel.class);
                user_name_signup.setText( model.getUserName());
                Picasso.get().load(Uri.parse(model.getUserImageurl())).into(userImage_signup);
                user_profession_signup.setText(model.getUserProfession());
                statusE.setText(model.getUserStatus());
                phoneE.setText(model.getUserPhone());
                aboutE.setText(model.getUserAbout());
                databaseReference.child("InstructorCoverPhoto").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        InstructorInfoModel mode = dataSnapshot.getValue(InstructorInfoModel.class);
                    //    if(mode!=null)
                      //      Picasso.get().load(Uri.parse(mode.getCoverPhoto())).into(cover_photo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        String interest = model.getInterest();
  //      String temp = interest.substring(0,3);
        //put check on radio Button

    }

    private void init(View view) {
        userImage_signup = view.findViewById(R.id.userImage_signupp);
        user_name_signup = view.findViewById(R.id.user_name_signupp);
        user_profession_signup = view.findViewById(R.id.user_profession_signupp);
        phoneE = view.findViewById(R.id.user_phone_no);
        statusE = view.findViewById(R.id.user_status);
        aboutE = view.findViewById(R.id.user_about);
        choose_cover_photo = view.findViewById(R.id.pencil_cover_photo);
        cover_photo = view.findViewById(R.id.cover_photo);
        save_cover_photo = view.findViewById(R.id.save_cover_photo);

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



    public void onRadioButtonClick(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.r11:
                if (checked) {
                    category.setCharAt(0, '1');
                    category.setCharAt(1, '1');
                    category.setCharAt(2, '1');
                }
                else
                {
                    category.setCharAt(0, '0');
                    category.setCharAt(1, '0');
                    category.setCharAt(2, '0');
                    break;
                }
            case R.id.r22:
                if (checked) {
                    category.setCharAt(3, '1');
                    category.setCharAt(4, '1');
                    category.setCharAt(5, '1');
                }
                else
                {
                    category.setCharAt(3, '0');
                    category.setCharAt(4, '0');
                    category.setCharAt(5, '0');
                    break;
                }
            case R.id.r33:
                if (checked) {
                    category.setCharAt(6, '1');
                    category.setCharAt(7, '1');
                    category.setCharAt(8, '1');
                }
                else
                {
                    category.setCharAt(6, '0');
                    category.setCharAt(7, '0');
                    category.setCharAt(8, '0');
                    break;
                }
            case R.id.r44:
                if (checked) {
                    category.setCharAt(9, '1');
                    category.setCharAt(10, '1');
                    category.setCharAt(11, '1');
                }
                else
                {
                    category.setCharAt(9, '0');
                    category.setCharAt(10, '0');
                    category.setCharAt(11, '0');
                    break;
                }
            case R.id.r55:
                if (checked) {
                    category.setCharAt(12, '1');
                    category.setCharAt(13, '1');
                    category.setCharAt(14, '1');
                }
                else
                {
                    category.setCharAt(12, '0');
                    category.setCharAt(13, '0');
                    category.setCharAt(14, '0');
                    break;
                }
            case R.id.r66:
                if (checked) {
                    category.setCharAt(15, '1');
                    category.setCharAt(16, '1');
                    category.setCharAt(17, '1');
                }
                else
                {
                    category.setCharAt(15, '0');
                    category.setCharAt(16, '0');
                    category.setCharAt(17, '0');
                    break;
                }

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
                && data != null && data.getData() != null) {if(flag==0)
        {
            mCoverUri = data.getData();
            Picasso.get().load(mCoverUri).placeholder(R.mipmap.ic_logo).into(cover_photo);
        }
        else
        {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).placeholder(R.mipmap.ic_logo).into(userImage_signup);
        }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                Toast.makeText(getContext(),"Logout",Toast.LENGTH_LONG).show();
                auth.signOut();
                Toast.makeText(getContext(), "logged out", Toast.LENGTH_SHORT).show();


                if(auth.getCurrentUser()==null)
                {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }

// this listener will be called when there is change in firebase user session
                FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user == null) {
                            // user auth state is changed - user is null
                            // launch login activity
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                };

                break;

            case R.id.delete_Account:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText(getContext(), "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), MainActivity.class));

                                    } else {
                                        Toast.makeText(getContext(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;

            case R.id.invite:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Smurfo");
                    String shareMessage= "\nHey Friend CheckOut This new cool app its called smurfo.\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
        }
        return true;
    }



}
