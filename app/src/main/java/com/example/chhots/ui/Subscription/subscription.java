package com.example.chhots.ui.Subscription;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.ChatBox.MessageModel;
import com.example.chhots.ChatBox.OnItemClickListener;
import com.example.chhots.Models.SubscriptionModel;
import com.example.chhots.PaymentListener;
import com.example.chhots.R;
import com.example.chhots.UserClass;
import com.example.chhots.Models.RoutineModel;
import com.example.chhots.ui.home.HomeFragment;
import com.example.chhots.Models.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class subscription extends Fragment implements PaymentListener {


    List<SubscriptionModel> list;
    SubscriptionAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = "RazorPay";


    String category,routineId,thumbnail,id,cat,planplan;
    FirebaseUser user;

    private DatabaseReference databaseReference;

    double viewIPAYL,viewIR,viewRC1,viewRC6,viewRC1Y,viewF1,viewF6,viewF1Y,viewPAYL,views;
    double viewTIR,viewTIC,viewTPAYL,viewTR1M,viewTR6M,viewTR1Y,viewTF1M,viewTF6M,viewTF1Y,viewTC1M,viewTC6M,viewTC1Y;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_subscription, container, false);
        init(root);

        Bundle bundle = getArguments();
        if(bundle!=null) {
            category = bundle.getString("category");
            if (category.equals("Routine")) {
                id = bundle.getString("Id");
                thumbnail = bundle.getString("thumbnail");
            } else if (category.equals("Course")) {
                id = bundle.getString("Id");
                thumbnail = bundle.getString("thumbnail");
            }
        }
        else
        {
            category="MainActivity";
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        list.add(new SubscriptionModel("Routine Subscription","Free For 3 days","For Rs 120,1 month Click here","For Rs 670,6 months Clickhere","For Rs 1280 1 year Click here","12000","67000","128000","000",routineId,thumbnail));
        list.add(new SubscriptionModel("Course Subscription","Free For 3 days","For Rs 350,1 month Click here","For Rs 2050,6 months Clickhere","For Rs 3430 1 year Click here","35000","205000","343000","000",routineId,thumbnail));
        list.add(new SubscriptionModel("Full Subscription","120Rs For 3 days","For Rs 450,1 month Click here","For Rs 2650,6 months Clickhere","For Rs 5240,1 year Click here","12000","45000","256000","524000",routineId,thumbnail));
        list.add(new SubscriptionModel("Individual","Free For 3 days","Routine","Course","Pay As You Learn",routineId));

        adapter = new SubscriptionAdapter(list, getContext(), getActivity(), new OnItemClickListener() {
            @Override
            public void onItemClick(MessageModel model) {

            }
            @Override
            public void onItemClick(RoutineModel model,int selected) {

            }

            @Override
            public void onItemClick(SubscriptionModel model, String plan, String price, int pos) {
                cat = "Individual";

                if(pos==0)
                {
                    cat="Routine";
                    planplan = plan;

                    if(plan.equals("Free"))
                    {
                        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        databaseReference.child("FreeRoutine").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getChildrenCount()==0)
                                {
                                    UserClass modelU = new UserClass(id,date,"Routine","Free");
                                    databaseReference.child("FreeRoutine").child(user.getUid()).setValue(modelU);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.drawer_layout,new HomeFragment()).commit();

                    }
                    else
                    {
                        startPayment("Tanish", "Tanish", "0723237826", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png", price);
                    }

                }
                else if(pos==1)
                {
                    cat="Course";
                    planplan = plan;

                    if(plan.equals("Free"))
                    {
                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        //Free For 3 days for Course
                        Toast.makeText(getContext(), "Free C", Toast.LENGTH_SHORT).show();
                        UserClass modelU = new UserClass(id,date,"Course","Free");
                        databaseReference.child("FreeCourse").child(user.getUid()).setValue(modelU);

                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.drawer_layout,new HomeFragment()).commit();
                    }
                    else
                    {
                        startPayment("Tanish", "Tanish", "0723237826", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png", price);
                    }

                }
                else if(pos==2)
                {
                    cat="Full";
                    planplan = plan;

                    if(plan.equals("Free"))
                    {
                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        //Full for 3 days
                        planplan="3day";
                        startPayment("Tanish", "Tanish", "0723237826", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png", "12000");
                    }
                    else
                    {
                        startPayment("Tanish", "Tanish", "0723237826", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png", price);
                    }
                }
                else if(pos==3)
                {
                    cat="Individual";
                }
                else {
                    if (category.equals("MainActivity") && pos == 3) {
                        Toast.makeText(getContext(), "Need to Select Routine or Course", Toast.LENGTH_SHORT).show();
                    } else {
                        planplan = plan;
                        Toast.makeText(getContext(), price, Toast.LENGTH_SHORT).show();
                        startPayment("Tanish", "Tanish", "0723237826", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png", price);
                    }
                }
            }

        });


        adapter.setData(list);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        return root;
    }

    private void init(View view) {
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.subscription_raw);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
    }


    private void setFragment(Fragment fragment) {
        Bundle bundle = new Bundle();

        if(category.equals("Course"))
        {
            bundle.putString("category",category);
            bundle.putString("courseId",id);
        }
        else
        {
            bundle.putString("category",category);
            bundle.putString("routineId",id);
        }
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout,fragment).commitAllowingStateLoss();
    }


    @Override
    public void onPaymentSuccess(String s) {
        try{
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            //TODO if he select individual

            //notification for instructor
            NotificationModel notify = new NotificationModel(date,user.getUid()+" Routine Purchase",user.getUid(),"description",thumbnail);
            Log.d("InstructorNotify",notify.getDate());

            //add views to instructor
            //if it is routine
            if(category.equals("Routine"))
            {
                if(cat.equals("Individual"))
                {
                    if(planplan.equals("Routine"))
                    {
                        UserClass model = new UserClass(id,date,"Routine","Individual");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).setValue(model);


                    }
                    else if(planplan.equals("PAYL"))
                    {

                        UserClass model = new UserClass(id,date,"PAYL","Individual");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).setValue(model);
                    }
                }
                else if(cat.equals("Routine"))
                {
                    if(planplan.equals("1month"))
                    {

                        UserClass model = new UserClass(id,date,"1month","Routine");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Routine").setValue(model);

                    }
                    else if(planplan.equals("6month"))
                    {

                        UserClass model = new UserClass(id,date,"6month","Routine");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Routine").setValue(model);

                    }
                    else if(planplan.equals("1year"))
                    {

                        UserClass model = new UserClass(id,date,"1year","Routine");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Routine").setValue(model);

                    }
                }
                else if(cat.equals("Full"))
                {
                    if(planplan.equals("3day"))
                    {
                        UserClass model = new UserClass(id,date,"3day","Full");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full").setValue(model);
                    }
                    else if(planplan.equals("1month"))
                    {
                        UserClass model = new UserClass(id,date,"1month","Full");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full").setValue(model);
                    }
                    else if(planplan.equals("6month"))
                    {
                        UserClass model = new UserClass(id,date,"6month","Full");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full").setValue(model);
                    }
                    else if(planplan.equals("1year"))
                    {
                        UserClass model = new UserClass(id,date,"1year","Full");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full").setValue(model);
                    }
                }
            }
            else if(category.equals("Course"))
            {
                if(cat.equals("Individual"))
                {
                    if(planplan.equals("Course"))
                    {

                        UserClass model = new UserClass(id,date,"Course","Individual");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).setValue(model);

                    }
                    else if(planplan.equals("PAYL"))
                    {

                        UserClass model = new UserClass(id,date,"PAYL","Individual");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).setValue(model);
                    }
                }
                else if(cat.equals("Course"))
                {

                    if(planplan.equals("1month"))
                    {

                        UserClass model = new UserClass(id,date,"1month","Course");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Course").setValue(model);

                    }
                    else if(planplan.equals("6month"))
                    {


                        UserClass model = new UserClass(id,date,"6month","Course");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Course").setValue(model);

                    }
                    else if(planplan.equals("1year"))
                    {

                        UserClass model = new UserClass(id,date,"1year","Course");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Course").setValue(model);
                    }
                }
                else if(cat.equals("Full"))
                {
                    if(planplan.equals("3day"))
                    {
                        UserClass model = new UserClass(id,date,"3day","Full");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full").setValue(model);
                    }
                    else if(planplan.equals("1month"))
                    {

                        UserClass model = new UserClass(id,date,"1month","Full");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full").setValue(model);

                    }
                    else if(planplan.equals("6month"))
                    {

                        UserClass model = new UserClass(id,date,"6month","Full");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full").setValue(model);

                    }
                    else if(planplan.equals("1year"))
                    {

                        UserClass model = new UserClass(id,date,"1year","Full");
                        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full").setValue(model);

                    }
                }

            }

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



    public void startPayment(String merchant,String desc,String order,String imageUrl,String amount) {



        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();


        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.bhr1);

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
            options.put("name", merchant);

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", desc);
            options.put("image", imageUrl);
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
            Log.e("mmmm", "Error in starting Razorpay Checkout", e);
        }
    }



}