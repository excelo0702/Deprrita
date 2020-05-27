package com.example.chhots.category_view.Courses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.VideoModel;
import com.example.chhots.category_view.routine.RoutineModel;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import static android.view.View.GONE;

public class upload_course extends AppCompatActivity {


    private Button choosebtn;
    private Button uploadBtn;
    private Button Done;
    private EditText video_title;
    private EditText video_sequence;
    private Uri videouri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    final String time = System.currentTimeMillis()+"";
    ImageView image;
    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int PICK_VIDEO_REQUEST = 1;
    private ProgressBar progress_seekBar,progress_seekBar2;

    //exoplayer implementation
    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;



    private static final String TAG = "Upload_Course";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_course);

        init();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });




        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FullScreen();

            }
        });


        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVideo();
            }
        });

       uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosebtn.setEnabled(false);
                uploadBtn.setEnabled(false);
                uploadVideo();
            }
        });

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCourse();
            }
        });


    }

    private void CreateCourse() {
        if(mImageUri!=null)
        {
            final StorageReference reference = storageReference.child("CoursesThumbnail").child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            reference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG,mImageUri.toString());
                                            CourseThumbnail thumbnail = new CourseThumbnail("course Name",time,uri.toString(),user.getUid());
                                            databaseReference.child("CoursesThumbnail").child(time).setValue(thumbnail)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "Course Created", Toast.LENGTH_SHORT).show();
                                                            onBackPressed();
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
                            progress_seekBar2.setProgress((int)progress);
                        }
                    });
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void chooseVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("2323232","111111");
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            videouri = data.getData();
            initializePlayer();
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(image);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    private String getfilterExt(Uri videoUri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }


    private void uploadVideo()
    {
        if(videouri!=null && video_sequence!=null && video_title!=null)
        {
            final String title = video_title.getText().toString();
            final String sequence = video_sequence.getText().toString();
            final String time2 = System.currentTimeMillis()+"";
            final StorageReference reference = storageReference.child("Course").child(time+"courseName").child(sequence+"."+title+getfilterExt(videouri));
            reference.putFile(videouri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String courseId = time;
                                            RoutineModel model = new RoutineModel(video_title.getText().toString(),video_sequence.getText().toString(),user.getUid(),time,uri.toString());
                                            databaseReference.child("Courses").child(courseId).child(sequence+title).setValue(model);
                                  //          VideoModel video_model = new VideoModel(user.getUid(),title,"category","description","videoURL","thumbnail","ContestID","courseId","price","videoId","0","0","0","sun_category");
                                    //        databaseReference.child("VIDEOS").child(time2).setValue(video_model);
                                            Toast.makeText(getApplicationContext(),"uploaded",Toast.LENGTH_SHORT).show();

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
                            progress_seekBar.setProgress((int)progress);
                        }
                    });
        }
        uploadBtn.setEnabled(true);
        choosebtn.setEnabled(true);
    }


    private void FullScreen() {
        if(fullScreen)
        {
            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_fullscreen_black_24dp));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int)( 300 * getApplicationContext().getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);


            upload_course.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            upload_course.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (((AppCompatActivity)upload_course.this).getSupportActionBar()!=null)
                ((AppCompatActivity)upload_course.this).getSupportActionBar().show();
            playerView.setBackgroundColor(Color.parseColor("#000000"));

            choosebtn.setVisibility(View.VISIBLE);
            video_title.setVisibility(View.VISIBLE);
            video_sequence.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);
            Done.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            progress_seekBar.setVisibility(View.VISIBLE);
            progress_seekBar2.setVisibility(View.VISIBLE);

            fullScreen=false;


        }
        else{

            choosebtn.setVisibility(View.GONE);
            video_title.setVisibility(View.GONE);
            video_sequence.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.GONE);
            Done.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
            progress_seekBar.setVisibility(View.GONE);
            progress_seekBar2.setVisibility(View.GONE);

            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_fullscreen_black_24dp));

            upload_course.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
            );

            if(getSupportActionBar() != null){
                getSupportActionBar().hide();
            }
            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_fullscreen_black_24dp));
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            upload_course.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            if(getSupportActionBar() != null){
                getSupportActionBar().hide();
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
            params.height = params.MATCH_PARENT;
            params.width = (int)( (params.height*4)/3);
            playerView.setBackgroundColor(Color.parseColor("#000000"));
            playerView.setLayoutParams(params);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);


            fullScreen = true;
        }
    }


    private void initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext());
        playerView.setPlayer(player);

        MediaSource mediaSource = buildMediaSource(videouri);

        player.setPlayWhenReady(playWhenReady);
        //  player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getApplicationContext(), "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }





    private void init() {
        choosebtn = findViewById(R.id.choose_video_course);
        video_title = findViewById(R.id.video_title_course);
        video_sequence =findViewById(R.id.video_sequence_course);
        uploadBtn = findViewById(R.id.upload_video_course);
        Done = findViewById(R.id.done);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        image = findViewById(R.id.upload_course_image);
        progress_seekBar = findViewById(R.id.progress_bar_upload_course);
        progress_seekBar2 = findViewById(R.id.progress_bar_upload_course_image);

        playerView = findViewById(R.id.video_course);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        playerView.setPadding(5,0,5,0);


    }
}
