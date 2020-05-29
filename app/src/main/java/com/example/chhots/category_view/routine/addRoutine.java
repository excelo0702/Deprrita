package com.example.chhots.category_view.routine;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.R;
import com.example.chhots.InstructorInfoModel;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.view.View.GONE;
import static com.paytm.pgsdk.easypay.manager.PaytmAssist.getContext;

public class addRoutine extends AppCompatActivity {

    private Button choose_video,upload_video,upload_routine;
    private EditText video_title,sequence_no,description,routine_Name;
    private TextView seqNo;
    private ImageView thumbnail;
    private Spinner spinner;
    private Uri videouri,mImageUri;
    private ProgressBar progress_seekbar1,progressBar2;
    private static final String TAG = "Upload_Routine";
    final String time = System.currentTimeMillis()+"";
    public StringBuffer category=new StringBuffer("0000000000000000000");
    String level;

    int flag=0;
    PopupWindow mPopupWindow;
    RelativeLayout relativeLayout;

    private boolean classical1=false,breaking1 = false,locking1=false,krump1=false,popping1=false,house1=false,waacking1=false;
    private boolean hiphop1=false,bharatnatyam1=false,kathak1=false,kuchipudi1=false,manipuri1=false,afro1=false,ballet1=false,contemporary1=false,dance_hall1=false,jazz1=false,litefeet1=false;


    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;
    private StorageReference storageReference;
    String userName,userImage;
    private TextView choose_category,selected_categories;
    String categories ="";

    //exoplayer implementation
    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;

    private static final int PICK_IMAGE_REQUEST = 2;
    HashMap<String,String> mm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);


        init();
        fetchUserInfo();
        mm = new HashMap<>();

        choose_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVideo();
            }
        });

        upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_video.setEnabled(false);
                upload_routine.setEnabled(false);

                uploadVideo();
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.level_list,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        choose_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseCategory();
            }
        });

        thumbnail.setOnClickListener(new View.OnClickListener() {
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

        upload_routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadRoutine();
            }
        });


    }

    private void ChooseCategory()
    {
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_category,null);


        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );


        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }
        Button submit = customView.findViewById(R.id.submit_category);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(String value : mm.keySet())
                {
                    categories+=value+"  ";
                }
                selected_categories.setText(categories);
                selected_categories.setVisibility(View.VISIBLE);
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(relativeLayout, Gravity.CENTER,0,0);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    Toast.makeText(getApplicationContext(),category,Toast.LENGTH_SHORT).show();






                    mPopupWindow.dismiss();
                }
                return false;
            }
        });



        final RadioButton classical = customView.findViewById(R.id.routine_category_classical);
        if(classical1)
        {
            classical.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
            category.setCharAt(0,'0');
        }
        classical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!classical1)
                {
                    classical1=true;
                    category.setCharAt(0,'1');
                    mm.put("Classical","Classical");
                    classical.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    classical1=false;
                    mm.remove("Classical");
                    classical.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(0,'0');
                }
            }
        });


        final RadioButton breaking = customView.findViewById(R.id.routine_category_breaking);
        if(breaking1)
        {
            breaking.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            breaking.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        breaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!breaking1)
                {
                    breaking1=true;
                    mm.put("Breaking","Breaking");
                    category.setCharAt(1,'1');
                    breaking.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    breaking1=false;
                    mm.remove("Breaking");
                    breaking.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(1,'0');
                }
            }
        });


        final RadioButton krump = customView.findViewById(R.id.routine_category_krump);
        if(krump1)
        {
            krump.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            krump.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();

        }
        krump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!krump1)
                {
                    krump1=true;
                    mm.put("Krump","Krump");
                    category.setCharAt(2,'1');
                    krump.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    krump1=false;
                    mm.remove("Krump");
                    krump.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(2,'0');
                }
            }
        });

        final RadioButton locking = customView.findViewById(R.id.routine_category_locking);
        if(locking1)
        {
            locking.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            locking.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        locking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!locking1)
                {
                    locking1=true;
                    mm.put("Locking","Locking");
                    category.setCharAt(3,'1');
                    locking.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    locking1=false;
                    mm.remove("Locking");
                    locking.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(3,'0');
                }
            }
        });

        final RadioButton popping = customView.findViewById(R.id.routine_category_popping);
        if(popping1)
        {
            popping.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            popping.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        popping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!popping1)
                {
                    popping1=true;
                    mm.put("Popping","Popping");
                    category.setCharAt(4,'1');
                    popping.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    popping1=false;
                    mm.remove("Popping");
                    popping.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(4,'0');
                }
            }
        });

        final RadioButton house = customView.findViewById(R.id.routine_category_house);
        if(house1)
        {
            house.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            house.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        house.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!house1)
                {
                    house1=true;
                    mm.put("House","House");
                    category.setCharAt(5,'1');
                    house.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    house1=false;
                    mm.remove("House");
                    house.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(5,'0');
                }
            }
        });

        final RadioButton waacking = customView.findViewById(R.id.routine_category_waacking);
        if(waacking1)
        {
            waacking.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            waacking.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        waacking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!waacking1)
                {
                    waacking1=true;
                    category.setCharAt(6,'1');
                    waacking.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    waacking1=false;
                    waacking.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(6,'0');
                }
            }
        });


        final RadioButton hiphop = customView.findViewById(R.id.routine_category_hip_hop);
        if(hiphop1)
        {
            hiphop.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            hiphop.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        hiphop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hiphop1)
                {
                    hiphop1=true;
                    category.setCharAt(7,'1');
                    hiphop.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    hiphop1=false;
                    hiphop.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(7,'0');
                }
            }
        });

        final RadioButton bharatnatyam = customView.findViewById(R.id.routine_category_bharatnatyam);
        if(bharatnatyam1)
        {
            bharatnatyam.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            bharatnatyam.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        bharatnatyam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bharatnatyam1)
                {
                    bharatnatyam1=true;
                    category.setCharAt(8,'1');
                    bharatnatyam.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    bharatnatyam1=false;
                    bharatnatyam.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(8,'0');
                }
            }
        });


        final RadioButton kathak = customView.findViewById(R.id.routine_category_kathak);
        if(bharatnatyam1)
        {
            bharatnatyam.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            bharatnatyam.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        bharatnatyam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bharatnatyam1)
                {
                    bharatnatyam1=true;
                    category.setCharAt(9,'1');
                    bharatnatyam.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    bharatnatyam1=false;
                    bharatnatyam.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(9,'0');
                }
            }
        });

        final RadioButton kuchipudi = customView.findViewById(R.id.routine_category_kuchipudi);
        if(kuchipudi1)
        {
            kuchipudi.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            kuchipudi.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        kuchipudi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!kuchipudi1)
                {
                    kuchipudi1=true;
                    category.setCharAt(10,'1');
                    kuchipudi.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    kuchipudi1=false;
                    kuchipudi.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(10,'0');
                }
            }
        });

        final RadioButton manipuri = customView.findViewById(R.id.routine_category_manipuri);
        if(manipuri1)
        {
            manipuri.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            manipuri.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        manipuri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!manipuri1)
                {
                    manipuri1=true;
                    category.setCharAt(11,'1');
                    manipuri.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    manipuri1=false;
                    manipuri.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(11,'0');
                }
            }
        });


        final RadioButton afro = customView.findViewById(R.id.routine_category_afro);
        if(afro1)
        {
            afro.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            afro.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        afro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!afro1)
                {
                    afro1=true;
                    category.setCharAt(12,'1');
                    afro.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    afro1=false;
                    afro.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(12,'0');
                }
            }
        });


        final RadioButton ballet = customView.findViewById(R.id.routine_category_ballet);

        if(ballet1)
        {
            ballet.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            ballet.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        ballet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ballet1)
                {
                    ballet1=true;
                    category.setCharAt(13,'1');
                    ballet.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ballet1=false;
                    ballet.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(13,'0');
                }
            }
        });


        final RadioButton contemporary = customView.findViewById(R.id.routine_category_contemporary);
        if(contemporary1)
        {
            contemporary.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            contemporary.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        contemporary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!contemporary1)
                {
                    contemporary1=true;
                    category.setCharAt(14,'1');
                    contemporary.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    contemporary1=false;
                    contemporary.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(14,'0');
                }
            }
        });


        final RadioButton dance_hall = customView.findViewById(R.id.routine_category_dance_hall);
        if(dance_hall1)
        {
            dance_hall.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            dance_hall.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        dance_hall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dance_hall1)
                {
                    dance_hall1=true;
                    category.setCharAt(15,'1');
                    dance_hall.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dance_hall1=false;
                    dance_hall.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(15,'0');
                }
            }
        });


        final RadioButton jazz = customView.findViewById(R.id.routine_category_jazz);
        if(jazz1)
        {
            jazz.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            jazz.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        jazz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!jazz1)
                {
                    jazz1=true;
                    category.setCharAt(16,'1');
                    jazz.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    jazz1=false;
                    jazz.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(16,'0');
                }
            }
        });



        final RadioButton litefeet = customView.findViewById(R.id.routine_category_litefeet);
        if(litefeet1)
        {
            litefeet.setBackgroundColor(Color.parseColor("#88111111"));
            Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
        }
        else
        {
            litefeet.setBackgroundColor(Color.parseColor("#0D111111"));
            Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
        }
        litefeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!litefeet1)
                {
                    litefeet1=true;
                    category.setCharAt(17,'1');
                    litefeet.setBackgroundColor(Color.parseColor("#88111111"));
                    Toast.makeText(getApplicationContext(),"checkde",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    litefeet1=false;
                    litefeet.setBackgroundColor(Color.parseColor("#0D111111"));
                    Toast.makeText(getApplicationContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                    category.setCharAt(17,'0');
                }
            }
        });

















    }



    private void fetchUserInfo()
    {
        mDatabaseReference.child(getString(R.string.InstructorInfo)).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                userName = model.getUserName();
                userImage = model.getUserImageurl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadRoutine() {
        if(flag==1 && mImageUri !=null)
        {

            final String routineName = routine_Name.getText().toString();


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    level = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            final StorageReference reference = storageReference.child("ROUTINE_THUMBNAILS").child(time+"."+getfilterExt(mImageUri));
            reference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            RoutineThumbnailModel model = new RoutineThumbnailModel(routineName,userName,"0",level,time,uri.toString(),user.getUid(),category.toString());
                                            mDatabaseReference.child("ROUTINE_THUMBNAIL").child(time).setValue(model)
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
                            progressBar2.setProgress((int)progress);
                        }
                    });
        }
        else
        {

        }
    }


    private void uploadVideo()
    {
        if(videouri!=null)
        {
            final String title = video_title.getText().toString();
            final String sequenceNo = sequence_no.getText().toString();
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
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
                                        String routineId = time;
                                        RoutineModel model = new RoutineModel(title,sequenceNo,user.getUid(),routineId,uri.toString());
                                        mDatabaseReference.child("ROUTINEVIDEOS").child(time).child(sequenceNo+title+getfilterExt(videouri)).setValue(model);
                                        flag=1;
                                        Toast.makeText(getApplicationContext(),"Upload Next Video",Toast.LENGTH_SHORT).show();
                                        progress_seekbar1.setProgress(0);
                                        upload_routine.setEnabled(true);
                                        upload_video.setEnabled(true);

                                    }
                                });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    upload_routine.setEnabled(true);
                    upload_video.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Upload Failed  " +e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progress_seekbar1.setProgress((int)progress);
                }
            });
        }
    }




    @SuppressLint("WrongViewCast")
    private void init() {
        choose_video = findViewById(R.id.choose_routine_video);
        upload_video =findViewById(R.id.upload_routine_video);
        upload_routine = findViewById(R.id.upload_routine);
        video_title = findViewById(R.id.routine_video_title);
        sequence_no = findViewById(R.id.routine_sequence_no);
        seqNo = findViewById(R.id.text);
        description = findViewById(R.id.routine_description);
        thumbnail = findViewById(R.id.choose_thumbnail);
        spinner = findViewById(R.id.routine_level_spinner);
        progress_seekbar1 = findViewById(R.id.progress_bar_upload_video);
        progressBar2 = findViewById(R.id.progress_bar_upload_routine);
        routine_Name = findViewById(R.id.routine_name);
        choose_category = findViewById(R.id.choose_category);
        relativeLayout = findViewById(R.id.routine_course);
        selected_categories = findViewById(R.id.selected_categories);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


        playerView = findViewById(R.id.routine_course_video);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setPadding(5,0,5,0);



    }


    private void FullScreen() {
        if(fullScreen)
        {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

            addRoutine.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (getSupportActionBar()!=null)
                getSupportActionBar().show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int)( 330 * getContext().getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);



            choose_video.setVisibility(View.VISIBLE);
            upload_routine.setVisibility(View.VISIBLE);
            upload_video.setVisibility(View.VISIBLE);
            sequence_no.setVisibility(View.VISIBLE);
            seqNo.setVisibility(View.VISIBLE);
            thumbnail.setVisibility(View.VISIBLE);
            progress_seekbar1.setVisibility(View.VISIBLE);
            progress_seekbar1.setVisibility(View.VISIBLE);
            routine_Name.setVisibility(View.VISIBLE);
            choose_category.setVisibility(View.VISIBLE);

            fullScreen = false;
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
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

            choose_video.setVisibility(GONE);
            upload_routine.setVisibility(GONE);
            upload_video.setVisibility(GONE);
            sequence_no.setVisibility(GONE);
            seqNo.setVisibility(GONE);
            thumbnail.setVisibility(GONE);
progress_seekbar1.setVisibility(GONE);
progress_seekbar1.setVisibility(GONE);
routine_Name.setVisibility(GONE);
choose_category.setVisibility(GONE);
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
        startActivityForResult(intent,1);
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
        }

    }

    private void initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext());
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

    private String getfilterExt(Uri videoUri)
    {
        ContentResolver contentResolver =addRoutine.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }


    @Override
    public void onBackPressed() {
     //   player.release();
        super.onBackPressed();
    }


}
