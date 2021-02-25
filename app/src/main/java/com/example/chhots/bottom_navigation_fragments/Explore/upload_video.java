package com.example.chhots.bottom_navigation_fragments.Explore;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chhots.MainActivity;
import com.example.chhots.Models.ExploreVideoModel;
import com.example.chhots.R;
import com.example.chhots.onBackPressed;
import com.example.chhots.ui.Dashboard.PointModel;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */

public class upload_video extends Fragment implements onBackPressed {

    private Button choosebtn;
    private Button uploadBtn;
    private EditText video_title;
    private EditText description;
    private ImageView thumbnail,pencil;
    private Uri videouri,mImageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressBar progressBar,progress_seekBar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "Upload_Video";
    RelativeLayout tt;
    Spinner spinner;

    //exoplayer implementation
    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;

    private String subCategory,descriptio;
    private static final int PICK_IMAGE_REQUEST = 2;
    int points=0;

    PointModel modelWeekly,modelOverAll;


    public upload_video() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_video, container, false);

        init(view);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.category_list,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Toast.makeText(getContext(),subCategory,Toast.LENGTH_LONG).show();


        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVideo();

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user==null)
                {
                    Toast.makeText(getContext(),"user cant be null",Toast.LENGTH_SHORT).show();

                }
                else if(videouri==null){
                    Toast.makeText(getContext(),"You didn't choose any video",Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadBtn.setEnabled(false);
                    choosebtn.setEnabled(false);
                    thumbnail.setEnabled(false);
                    uploadVideo();
                }
            }
        });

        pencil.setOnClickListener(new View.OnClickListener() {
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
        MainActivity.fetchUserPoints();


        return view;
    }

    private void init(View view) {
        choosebtn = (Button)view.findViewById(R.id.chhose_btn);
        uploadBtn = (Button)view.findViewById(R.id.upload_btn);
        video_title = view.findViewById(R.id.video_title);
        description = view.findViewById(R.id.descriptioin_upload_video);
        thumbnail = view.findViewById(R.id.upload_thumbnail);
        pencil = view.findViewById(R.id.pencil_explore);
        spinner =view.findViewById(R.id.category_spinner);

        progress_seekBar = view.findViewById(R.id.progress_bar);
        playerView = view.findViewById(R.id.video_view);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setPadding(0,0,0,0);
        playerView.setBackgroundColor(Color.parseColor("#000000"));


        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }


    private void FullScreen() {
        if(fullScreen)
        {
            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_fullscreen_black_24dp));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int)( 300 * getContext().getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);


            ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
                ((AppCompatActivity)getActivity()).getSupportActionBar().show();


            View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
            BottomnavBar.setVisibility(View.VISIBLE);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.VISIBLE);
            playerView.setBackgroundColor(Color.parseColor("#000000"));

            tt = getActivity().findViewById(R.id.tt);
            tt.setVisibility(View.VISIBLE);

            View NavBar = getActivity().findViewById(R.id.nav_view);
            NavBar.setVisibility(View.VISIBLE);

            choosebtn.setVisibility(View.VISIBLE);
            video_title.setVisibility(View.VISIBLE);
            thumbnail.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);

            fullScreen = false;
        }
        else{
            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen_black_24dp));
            ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            ((AppCompatActivity)getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            if(((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
            params.height = params.MATCH_PARENT;
            params.width = (int)( (params.height*4)/3);
            playerView.setBackgroundColor(Color.parseColor("#000000"));
            playerView.setLayoutParams(params);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);


            //  player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);


            View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
            BottomnavBar.setVisibility(GONE);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(GONE);

            tt = getActivity().findViewById(R.id.tt);
            tt.setVisibility(GONE);


            View NavBar = getActivity().findViewById(R.id.nav_view);
            NavBar.setVisibility(GONE);
            choosebtn.setVisibility(GONE);
            video_title.setVisibility(GONE);
            thumbnail.setVisibility(GONE);
            description.setVisibility(GONE);
            spinner.setVisibility(GONE);
            uploadBtn.setVisibility(GONE);

            fullScreen = true;
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
        getActivity().startActivityForResult(intent,1);
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
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            videouri = data.getData();
            initializePlayer();
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(thumbnail);
            if(videouri!=null){
                initializePlayer();
            }
        }

    }

    private void initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(getContext());
        playerView.setPlayer(player);

        MediaSource mediaSource = buildMediaSource(videouri);

        player.setPlayWhenReady(playWhenReady);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
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
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }

    private void uploadVideo()
    {
        if(videouri!=null )
        {
            final String title = video_title.getText().toString();
            final String sub_category;

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    descriptio = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if(title.equals(""))
            {
                Toast.makeText(getContext(),"Song Name can't be null",Toast.LENGTH_SHORT).show();
                uploadBtn.setEnabled(true);
                return;
            }

            if(mImageUri==null)
            {
                Toast.makeText(getContext(),"Choose thumbnail for dislpayng it",Toast.LENGTH_SHORT).show();
                uploadBtn.setEnabled(true);
                return;
            }
            final String videoId = System.currentTimeMillis()+"";
            final StorageReference reference = storageReference.child(getResources().getString(R.string.ExploreVideos)).child(user.getUid()).child(videoId+getfilterExt(videouri));
            reference.putFile(videouri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri videouri) {

                                    final StorageReference imageReference = storageReference.child(getResources().getString(R.string.ExploreThumbnail)).child(videoId+getfilterExt(mImageUri));
                                    imageReference.putFile(mImageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    imageReference.getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri imageuri) {

                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            progress_seekBar.setProgress(0);
                                                                        }
                                                                    },100);
                                                                    Toast.makeText(getContext(),"uploaded",Toast.LENGTH_LONG).show();
                                                                    Log.d("pop pop pop","wow wow wow");
                                                                    ExploreVideoModel model = new ExploreVideoModel(user.getUid(),imageuri.toString(),videouri.toString(),title,videoId,0,0,0);
                                                                    databaseReference.child(getResources().getString(R.string.ExploreVideos)).child(videoId).setValue(model);
                                                                    databaseReference.child(getResources().getString(R.string.UsersExploreVideos)).child(user.getUid()).child(videoId).setValue(model);
                                                                    MainActivity.increaseUserPoints(20);
                                                                    getFragmentManager().beginTransaction().replace(R.id.drawer_layout,new explore()).commit();

                                                                }
                                                            });
                                                }
                                            });
                                    uploadBtn.setEnabled(true);
                                    choosebtn.setEnabled(true);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),e.getMessage()+"Failed",Toast.LENGTH_SHORT).show();
                            uploadBtn.setEnabled(true);
                            choosebtn.setEnabled(true);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                            progress_seekBar.setProgress((int) progress);
                            Toast.makeText(getContext(),progress+"",Toast.LENGTH_SHORT).show();
                        }
                    });
            uploadBtn.setEnabled(true);
            choosebtn.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (player!=null)
        {
            player.release();
        }
    }

}