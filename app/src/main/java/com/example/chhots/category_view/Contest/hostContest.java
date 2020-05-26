package com.example.chhots.category_view.Contest;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Calendar.CalendarModel;
import com.example.chhots.ui.notifications.NotificationModel;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class hostContest extends Fragment {


    public hostContest() {
        // Required empty public constructor
    }

    EditText contest_info;
    Button upload,select_date;
    ImageView contest_image;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private Uri mImageUri;
    private ProgressBar progress_seekBar;
    private PopupWindow mPopupWindow;
    String date_text;
    RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host_contest, container, false);
        init(view);


        contest_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.raw_calendar,null);

                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                Toast.makeText(getContext(),"pop up",Toast.LENGTH_SHORT).show();
                mPopupWindow.showAtLocation(relativeLayout, Gravity.CENTER,0,0);
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }
                Button submit = customView.findViewById(R.id.submit_date);

                final TextView date = customView.findViewById(R.id.date_view);
                CalendarView calendarView = customView.findViewById(R.id.calender);

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                        date_text = String.valueOf(year)+" "+String.valueOf(month)+" "+String.valueOf(day);
                        date.setText(date_text);
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        date_text = date_text;
                        mPopupWindow.dismiss();
                    }
                });
                select_date.setText(date_text);


            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadContest();
            }
        });

        return view;
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(contest_image);
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadContest()
    {
        final String info = contest_info.getText().toString();
        if(mImageUri!=null &&info!=null) {
            final String time = System.currentTimeMillis() + "";
            final StorageReference filereference = storageReference.child("Contest").child(time + "." + getFileExtension(mImageUri));
            filereference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HostModel model = new HostModel(info, uri.toString(),time,date_text);
                                    databaseReference.child("ContestThumbnail").child(time).setValue(model)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Contest Hosted", Toast.LENGTH_SHORT).show();
                                                    getFragmentManager().beginTransaction().replace(R.id.drawer_layout, new contest()).commit();
                                                }
                                            });

                                    NotificationModel notify = new NotificationModel(time,"category",user.getUid(),"description",uri.toString());
                                    databaseReference.child("NOTIFICATION").child(time).setValue(notify);

                                    CalendarModel calendar = new CalendarModel("Contest",date_text,info,info,uri.toString(),time);
                                    databaseReference.child("CALENDAR").child(date_text).child(time).setValue(calendar);

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
    }

    private void init(View v) {
        contest_info = v.findViewById(R.id.ContestInfo);
        upload = v.findViewById(R.id.contest_upload);
        contest_image = v.findViewById(R.id.contest_image);
        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("");
        storageReference = FirebaseStorage.getInstance().getReference();
        progress_seekBar = v.findViewById(R.id.progress_bar_creating_contest);
        select_date = v.findViewById(R.id.select_date);
        relativeLayout = v.findViewById(R.id.rrr);
    }

}
