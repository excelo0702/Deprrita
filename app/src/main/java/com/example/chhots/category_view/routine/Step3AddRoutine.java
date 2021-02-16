package com.example.chhots.category_view.routine;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chhots.MainActivity;
import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.InstructorPackage.InstructorInfoModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

public class Step3AddRoutine extends Fragment {


    private ImageView thumbnail;
    private ImageView choose_thumbnail_step3;
    Spinner category_spinnerr;
    private EditText songName,learn,description;
    private ProgressBar progressBar;
    private Button create;
    private String routineId,category="1111111110000000000";
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri mImageUri;
    private String userName="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step3_add_routine, container, false);

        init(view);
        Bundle bundle = new Bundle();
        routineId = getArguments().getString("routineId");


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.category_list,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        category_spinnerr.setAdapter(adapter);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_thumbnail_step3.setEnabled(false);
                uploadRoutine();
            }
        });

        choose_thumbnail_step3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        fetchInstructorInfo();
        MainActivity.fetchUserPoints();
        return view;
    }


    private void init(View view) {
        thumbnail = view.findViewById(R.id.upload_routine_image);
        choose_thumbnail_step3 = view.findViewById(R.id.choose_thumbnail_step3_routine);
        category_spinnerr = view.findViewById(R.id.category_spinnerr_routine);
        songName = view.findViewById(R.id.routine_name_step3);
        progressBar = view.findViewById(R.id.progress_bar_create_routine);

        learn = view.findViewById(R.id.learn_routine);
        description = view.findViewById(R.id.learn_routine_description);
        create = view.findViewById(R.id.create_routine);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    private void uploadRoutine() {
        if(mImageUri !=null)
        {

            final String routineName = songName.getText().toString();

            category_spinnerr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    category = adapterView.getItemAtPosition(i).toString();
                    if(category.equals("Street"))
                    {
                        category="1111111110000000000";
                        Toast.makeText(getContext(),category,Toast.LENGTH_SHORT).show();
                    }
                    else if(category.equals("Classical"))
                    {
                        category="0000000001111100000";
                        Toast.makeText(getContext(),category,Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        category="0000000000000011111";
                        Toast.makeText(getContext(),category,Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    category="1111111110000000000";
                }
            });


            final StorageReference reference = storageReference.child("ROUTINE_THUMBNAILS").child(routineId+"."+getFileExtension(mImageUri));
            reference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            RoutineThumbnailModel model = new RoutineThumbnailModel(routineName,userName,"0",category,routineId,uri.toString(),user.getUid(),description.getText().toString(),learn.getText().toString(),category);
                                            databaseReference.child("ROUTINE_THUMBNAIL").child(routineId).setValue(model)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "Course Created", Toast.LENGTH_SHORT).show();
                                                            MainActivity.increaseUserPoints(30);
                                                             getFragmentManager().beginTransaction().replace(R.id.routine_add,new routine()).commit();

                                                        }
                                                    });
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressBar.setProgress((int)progress);
                        }
                    });
        }
        else
        {
            choose_thumbnail_step3.setEnabled(true);
        }
    }

    private void fetchInstructorInfo() {
        databaseReference.child(getResources().getString(R.string.InstructorInfo)).child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                        userName = model.getUserName();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("2323232","111111");

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(thumbnail);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }



}