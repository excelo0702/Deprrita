package com.example.chhots.category_view.routine;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.chhots.Models.RoutineModel;
import com.example.chhots.R;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class Step1AddRoutine extends Fragment {

    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;

    private Button choose_vdo,step2,upload_vdo;
    private EditText song_name,seq_no,description;
    private ProgressBar progressBar;
    private Uri videouri,mImageUri;
    int playerchoose=0;
    Switch sw1,sw2;
    PopupWindow mPopupWindow;
    String routineId;

    final String time = System.currentTimeMillis()+"";
    private FirebaseUser user;
    private DatabaseReference mDatabaseReference,databaseReference;
    private StorageReference storageReference;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_step1_add_routine, container, false);
        init(view);

        choose_vdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videouri=null;
                playerchoose=0;
                chooseVideo();
            }
        });

        upload_vdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_vdo.setEnabled(false);
                step2.setEnabled(false);
                choose_vdo.setEnabled(false);
                uploadVideo();
            }
        });

        step2.setEnabled(false);

        step2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releasePlayer();
                setFragment(new Step2AddFragment());
            }
        });

        return view;
    }

    private void init(View view) {
        playerView = view.findViewById(R.id.video_routine);
        choose_vdo = view.findViewById(R.id.choose_video_routine);
        song_name = view.findViewById(R.id.video_title_routine);
        seq_no = view.findViewById(R.id.video_sequence_routine);
        description = view.findViewById(R.id.video_description_routine);
        progressBar = view.findViewById(R.id.progress_bar_upload_routine);
        upload_vdo = view.findViewById(R.id.upload_video_routine);
        step2 = view.findViewById(R.id.next_course_routine);
        routineId = time;

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }

    private void setFragment(Fragment fragment) {

        Bundle bundle = new Bundle();
        bundle.putString("routineId",routineId);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.routine_add,fragment);
        fragmentTransaction.commit();
    }

    private void chooseVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private void OnComplete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);
        builder.setCancelable(true);
        View customView = getLayoutInflater().inflate(R.layout.raw_upload_complete,null);
        builder.setView(customView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button next = customView.findViewById(R.id.done_upload);
        Button nextStep = customView.findViewById(R.id.done_upload_next);
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releasePlayer();
                alertDialog.cancel();
                setFragment(new Step2AddFragment());
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    private void uploadVideo()
    {
        if(videouri!=null)
        {
            choose_vdo.setEnabled(false);
            final String title = song_name.getText().toString();
            final String sequenceNo = seq_no.getText().toString();
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            final StorageReference reference = storageReference.child("ROUTINEVIDEOS")
                    .child(time).child(sequenceNo+"."+title+getfilterExt(videouri));
            reference.putFile(videouri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            RoutineModel model = new RoutineModel(title,sequenceNo,user.getUid(),routineId,uri.toString(),description.getText().toString());
                                            mDatabaseReference.child("ROUTINEVIDEOS").child(time).child(sequenceNo+title+getfilterExt(videouri)).setValue(model);
                                            OnComplete();
                                            song_name.setText("");
                                            seq_no.setText("");
                                            description.setText("");
                                            Toast.makeText(getContext(),"Upload Next Video",Toast.LENGTH_SHORT).show();
                                            progressBar.setProgress(0);
                                            upload_vdo.setEnabled(true);
                                            choose_vdo.setEnabled(true);
                                            releasePlayer();
                                            step2.setEnabled(true);
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setProgress(0);
                    upload_vdo.setEnabled(true);
                    choose_vdo.setEnabled(true);
                    releasePlayer();
                    Toast.makeText(getContext(),"Upload Failed  " +e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int)progress);
                }
            });
        }
        else
        {
            upload_vdo.setEnabled(true);
            choose_vdo.setEnabled(true);
            Toast.makeText(getContext(),"Choose Video",Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("2323232","111111");
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            videouri = data.getData();
            initializePlayer();
        }


    }

    private void initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(getContext());
        playerView.setPlayer(player);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        MediaSource mediaSource = buildMediaSource(videouri);
        player.setPlayWhenReady(playWhenReady);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
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

    private String getfilterExt(Uri videoUri)
    {
        ContentResolver contentResolver =getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }

}