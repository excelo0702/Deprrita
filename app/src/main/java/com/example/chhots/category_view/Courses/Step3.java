package com.example.chhots.category_view.Courses;

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
import com.example.chhots.category_view.routine.routine;
import com.example.chhots.ui.Category.category;
import com.example.chhots.ui.Dashboard.PointModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class Step3 extends Fragment {

    private ImageView thumbnail;
    private ImageView choose_thumbnail_step3;
    private Spinner category_spinnerr;
    private EditText courseName,learn,description;
    private ProgressBar progressBar;
    private Button create;
    private String courseId,category="";
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri mImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        
        View view =  inflater.inflate(R.layout.fragment_step3, container, false);
        init(view);
        Bundle bundle = new Bundle();
        courseId = getArguments().getString("courseId");


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.category_list,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        category_spinnerr.setAdapter(adapter);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_thumbnail_step3.setEnabled(false);
                CreateCourse();
            }
        });



        category_spinnerr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("fffffuuuu",category+"  o o o o");
                category = adapterView.getItemAtPosition(i).toString();
                if(category.equals("Street"))
                {
                    category="1111111110000000000";
                    Toast.makeText(getContext(),category,Toast.LENGTH_SHORT).show();
                    Log.d("fffffuuuu",category+"  o o o o");
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
        MainActivity.fetchUserPoints();
        choose_thumbnail_step3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        return view;
                
    }

    private void init(View view) {
        thumbnail = view.findViewById(R.id.upload_course_image);
        choose_thumbnail_step3 = view.findViewById(R.id.choose_thumbnail_step3);
        category_spinnerr = view.findViewById(R.id.category_spinnerr);
        courseName = view.findViewById(R.id.course_name_step3);
        progressBar = view.findViewById(R.id.progress_bar_create_course);

        learn = view.findViewById(R.id.learn_course);
        description = view.findViewById(R.id.learn_course_description);
        create = view.findViewById(R.id.done);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();



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

    private void CreateCourse() {
        final String CourseName = courseName.getText().toString();
        final String learntext = learn.getText().toString();
        final String descriptiontext = description.getText().toString();

        Log.d("fffffuuuu",category+"  o o o op");

        if(mImageUri!=null && CourseName!=null && category!=null && learntext!=null && descriptiontext!=null)
        {
            final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            final StorageReference reference = storageReference.child("CoursesThumbnail").child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            reference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            CourseThumbnail thumbnail = new CourseThumbnail(CourseName,courseId,uri.toString(),user.getUid(),0,0.0,0,date,category,learntext,descriptiontext);
                                            databaseReference.child("CoursesThumbnail").child(courseId).setValue(thumbnail)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "Course Created", Toast.LENGTH_SHORT).show();
                                                            MainActivity.increaseUserPoints(35);
                                                            getFragmentManager().beginTransaction().replace(R.id.course_vvv,new courses()).commit();

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
    }
}