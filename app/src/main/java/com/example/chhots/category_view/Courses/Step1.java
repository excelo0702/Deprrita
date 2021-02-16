package com.example.chhots.category_view.Courses;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.chhots.R;
import com.example.chhots.category_view.routine.RoutineModel;
import com.example.chhots.category_view.routine.Step2AddFragment;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Step1 extends Fragment {

    private Button step2,choose_vdo,upload_vdo;
    private EditText vdo_title,vdo_seq_no,vdo_description;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private Uri videouri;
    private PopupWindow mPopupWindow;

    private StorageReference storageReference;

    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;

    final String courseId = System.currentTimeMillis()+"";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step1, container, false);

        init(view);

        step2.setEnabled(false);
        step2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releasePlayer();
                setFragment(new Step2());
            }
        });

        choose_vdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVideo();
            }
        });

        upload_vdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                choose_vdo.setEnabled(false);
                upload_vdo.setEnabled(false);
                step2.setEnabled(false);
                uploadVideo();
            }
        });


        return view;
    }


    private void OnComplete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);
        builder.setMessage("Uploaded");
        builder.setCancelable(true);
        View customView = getLayoutInflater().inflate(R.layout.raw_upload_complete,null);
        builder.setView(customView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button nextStep = customView.findViewById(R.id.done_upload_next);
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releasePlayer();
                alertDialog.cancel();
                setFragment(new Step2());
            }
        });
        Button next = customView.findViewById(R.id.done_upload);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    private void uploadVideo() {
        if(videouri!=null &&vdo_seq_no!=null && vdo_description!=null && vdo_title!=null)
        {
            final String title = vdo_title.getText().toString();
            final String sequence = vdo_seq_no.getText().toString();
            final StorageReference reference = storageReference.child("Course").child(courseId+"courseName")
                    .child(sequence+"."+title+getFileExtension(videouri));
            reference.putFile(videouri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            RoutineModel model = new RoutineModel(title,sequence,user.getUid(),courseId,uri.toString(),vdo_description.getText().toString());
                                            databaseReference.child("Course").child(courseId).child(sequence+title).setValue(model);

                                            OnComplete();
                                            Toast.makeText(getContext(),"uploaded",Toast.LENGTH_SHORT).show();
                                            upload_vdo.setEnabled(true);
                                            choose_vdo.setEnabled(true);
                                            vdo_description.setText("");
                                            vdo_seq_no.setText("");
                                            vdo_title.setText("");
                                            releasePlayer();
                                            step2.setEnabled(true);
                                        }
                                    });
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

    private void init(View view) {
        choose_vdo = view.findViewById(R.id.choose_video_course);
        upload_vdo = view.findViewById(R.id.upload_video_course);
        step2 = view.findViewById(R.id.next_course);
        relativeLayout = view.findViewById(R.id.step1);

        vdo_title = view.findViewById(R.id.video_title_course);
        vdo_seq_no = view.findViewById(R.id.video_sequence_course);
        vdo_description = view.findViewById(R.id.video_description_course);
        progressBar = view.findViewById(R.id.progress_bar_upload_course);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        playerView = view.findViewById(R.id.video_course);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setPadding(5,0,5,0);
    }

    private void setFragment(Fragment fragment) {

        Bundle bundle = new Bundle();
        bundle.putString("courseId",courseId);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.course_vvv,fragment);
        fragmentTransaction.commit();

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

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }





    /*
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

*/

    private void initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(getContext());
        playerView.setPlayer(player);

        MediaSource mediaSource = buildMediaSource(videouri);

        player.setPlayWhenReady(playWhenReady);
        //  player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getContext(), "exoplayer-codelab");
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





}