package com.example.chhots.User_Profile;


import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chhots.MainActivity;
import com.example.chhots.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class userprofile extends Fragment {


    public userprofile() {
        // Required empty public constructor
    }
    FirebaseUser user;
    TextView user_name;
    FirebaseAuth firebaseAuth;
    ImageView imageView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView editProfileLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        user_name = (TextView)view.findViewById(R.id.user_name);
        imageView = view.findViewById(R.id.imageView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.user_account_details);
        editProfileLink = view.findViewById(R.id.edit_profile_link);

        editProfileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.drawer_layout,new edit_profile(),"1");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.drawer_layout,new user_account(),"1");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        Query query = databaseReference.child("users").orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("23561","accha");


                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String image = ""+ds.child("mImageUrl").getValue();
                    Log.d("22231",image);
                    try{
                        if(image.equals(""))
                        {
                            Picasso.get().load(R.drawable.image).into(imageView);
                        }
                        else {
                            Picasso.get().load(image).resize(50,50).into(imageView);
                        }
                    }
                    catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        user_name.setText(user.getEmail());

        return view;
    }

    private void checkUserStatus()
    {
        if(user!=null)
        {

        }
        else
        {
            startActivity(new Intent(getContext(), MainActivity.class));
        }
    }

    @Override
    public void onStart() {
        checkUserStatus();
        super.onStart();
    }
}
