package com.example.chhots.category_view.routine;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.chhots.Models.PreviewModel;
import com.example.chhots.R;
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

public class Step2AddFragment extends Fragment {


    private Button Step3,choose_prvw,upload_prvw;
    private String routineId;
    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;


    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private StorageReference storageReference;
    private Uri videouri;
    private PopupWindow mPopupWindow;

    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    boolean fullScreen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step2_add, container, false);


        init(view);
        Bundle bundle = getArguments();
        routineId = bundle.getString("routineId");

        Step3.setEnabled(false);

        Step3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releasePlayer();
                setFragment(new Step3AddRoutine());
            }
        });

        choose_prvw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVideo();
            }
        });

        upload_prvw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_prvw.setEnabled(false);
                upload_prvw.setEnabled(false);
                Step3.setEnabled(false);
                uploadPreview();
            }
        });

        return view;
    }


    private void init(View view) {
        Step3 = view.findViewById(R.id.next_step3_routine);
        choose_prvw = view.findViewById(R.id.choose_preview_routine);
        upload_prvw = view.findViewById(R.id.upload_preview_routine);
        progressBar = view.findViewById(R.id.progress_bar_upload_course_preview);
        relativeLayout = view.findViewById(R.id.step2_routine);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        playerView = view.findViewById(R.id.preview_video_routine);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setPadding(5,0,5,0);

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

    private void uploadPreview() {

        if(videouri!=null)
        {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            final StorageReference reference = storageReference.child("ROUTINE_PREVIEWS")
                    .child(routineId).child(routineId+getFileExtension(videouri));
            reference.putFile(videouri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            PreviewModel model = new PreviewModel(routineId,uri.toString(),user.getUid());
                                            databaseReference.child("ROUTINE_PREVIEWS").child(routineId).setValue(model);

                                            OnComplete();
                                            Toast.makeText(getContext(),"Upload Next Video",Toast.LENGTH_SHORT).show();
                                            progressBar.setProgress(0);
                                            choose_prvw.setEnabled(true);
                                            upload_prvw.setEnabled(true);
                                            Step3.setEnabled(true);

                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    choose_prvw.setEnabled(true);
                    upload_prvw.setEnabled(true);
                    Step3.setEnabled(true);
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

    private void OnComplete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);
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
                setFragment(new Step3AddRoutine());
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