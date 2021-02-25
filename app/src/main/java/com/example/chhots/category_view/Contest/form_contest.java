package com.example.chhots.category_view.Contest;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.PaymentListener;
import com.example.chhots.R;
import com.example.chhots.Models.InstructorInfoModel;
import com.example.chhots.onBackPressed;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.razorpay.Checkout;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class form_contest extends Fragment implements onBackPressed, PaymentListener {


    public form_contest() {
        // Required empty public constructor
    }


    private TextView register,info;
    private String contestId,imageUrl;
    private EditText userName,userEmail;
    private ImageView image_1,image_2;
    private FirebaseUser user;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private MediaController mediaController;
    private Button choose_video;
    private Uri videouri;
    private ProgressBar progress_seekBar;


    //exoplayer implementation
    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_contest, container, false);
        Bundle bundle = this.getArguments();
        contestId= bundle.getString("contestId");
        imageUrl = bundle.getString("imageUrl");
        init(view);
        fetchUserInfo();
        userEmail.setText(user.getEmail());
        Picasso.get().load(Uri.parse(imageUrl)).into(image_1);
        Picasso.get().load(Uri.parse(imageUrl)).into(image_2);


        playerView = view.findViewById(R.id.form_contest_videoView);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        playerView.setPadding(5,0,5,0);

        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FullScreen();
                //      Intent intent = new Intent(getContext(), FloatingWidgetService.class);
                //    intent.putExtra("videoUri",videouri.toString());
                //  getActivity().startService(intent);

            }
        });

        choose_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVideo();
            }
        });

        register = (TextView)view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPayment("Tanish","Tanish","0723237826","https://s3.amazonaws.com/rzp-mobile/images/rzp.png","4000");
                register.setEnabled(false);
                choose_video.setEnabled(false);
                Toast.makeText(getContext(),"Start uploading",Toast.LENGTH_LONG).show();

            }
        });
        return view;
    }


    private void fetchUserInfo()
    {
        databaseReference.child(getString(R.string.InstructorInfo)).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                userName.setText( model.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    private String getfilterExt(Uri videoUri)
    {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }


    private void init(View v)
    {
        info = v.findViewById(R.id.contest_description);
        userName = v.findViewById(R.id.userName);
        userEmail = v.findViewById(R.id.userEmail);
        image_1 = v.findViewById(R.id.image_1_contest);
        image_2 = v.findViewById(R.id.image_2_contest);
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("");
        choose_video = v.findViewById(R.id.choose_video_form);
        progress_seekBar = v.findViewById(R.id.progress_bar_upload_contest_video);
    }
/*
    private void RegisterUser()
    {
        if(videouri!=null&&userEmail!=null && userName!=null)
        {
            final String user_name = userName.getText().toString();
            final String user_email = userEmail.getText().toString();
            final StorageReference reference = storageReference.child("ContestVideos").child(contestId).child(user.getUid()+getfilterExt(videouri));
            final String videoId = System.currentTimeMillis()+"";
            reference.putFile(videouri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FormContestModel model = new FormContestModel(user_name,contestId,uri.toString());
                                            databaseReference.child("ContestVideos").child(contestId).child(user.getUid()).setValue(model);
                                            Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_LONG).show();


                                            VideoModel mode = new VideoModel(user.getUid(),"User Name","Contest","",uri.toString(),imageUrl,contestId,"NONE","",videoId,"0","0","0","CONTEST");
                                            databaseReference.child("VIDEOS").child(videoId).setValue(mode);
                                            databaseReference.child("contest").child(contestId).child(user.getUid()).setValue(mode);


                                            register.setEnabled(true);
                                            choose_video.setEnabled(true);
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
                            double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getBytesTransferred();
                            progress_seekBar.setProgress((int)progress);
                        }
                    });

            register.setEnabled(true);
            choose_video.setEnabled(true);
        }
        register.setEnabled(true);
        choose_video.setEnabled(true);


    }

*/

    private void FullScreen() {
        if(fullScreen)
        {
            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_fullscreen_black_24dp));
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
                ((AppCompatActivity)getActivity()).getSupportActionBar().show();

            //Changed to unspecified from Portrait
            ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int)( 330 * getContext().getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
            fullScreen = false;
        }
        else{
            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen_black_24dp));

            ((AppCompatActivity)getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
            );

            if(((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
            }
            // Changed to unspecified from landscape
            ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            ((AppCompatActivity)getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.setMargins(0,0,0,0);
            params.height = params.MATCH_PARENT;

            playerView.setLayoutParams(params);

            View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
            BottomnavBar.setVisibility(GONE);



            View NavBar = getActivity().findViewById(R.id.nav_view);
            NavBar.setVisibility(GONE);

            fullScreen = true;
        }
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

    @Override
    public void onBackPressed() {
        player.release();
    }


    public void startPayment(String merchant,String desc,String order,String imageUrl,String amoun) {


        String merchantName = merchant;
        String description = desc;
        String orderId = order;
        String image = imageUrl;
        String amount = amoun;

        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();


        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.ic_launcher_background);

        /**
         * Reference to current activity
         */
        final Activity activity = getActivity();

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", merchantName);

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", desc);
            options.put("image", image);
            //    options.put("order_id", "order_9A33XWu170gUtm");
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", amount);

            checkout.open(activity, options);
        } catch(Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            Log.e("gggggg", "Error in starting Razorpay Checkout", e);
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        try{
            String time = System.currentTimeMillis()+"";
           // UserClass model = new UserClass(routineId,time);
         //   mDatabaseReference.child("USERS").child(user.getUid()).child("routines").child(routineId).setValue(model);
            //RegisterUser();
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

        }

    }
    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getContext(),"Nop Fragment",Toast.LENGTH_SHORT).show();
    }




}
