package com.example.chhots.category_view.routine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.InstructorLogin;
import com.example.chhots.LoadingDialog;
import com.example.chhots.Login;
import com.example.chhots.R;
import com.example.chhots.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    private static final String TAG = "NotificationViewAdapter";
    private List<RoutineThumbnailModel> list;
    private Context context;

    public SearchAdapter(List<RoutineThumbnailModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_search_item,parent,false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.search_text.setText(list.get(position).getTitle());
        holder.routineId = list.get(position).getRoutineId();
        holder.thumbnail = list.get(position).getRoutineThumbnail();
        holder.instructorId = list.get(position).getInstructorId();
    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    public void setData(List<RoutineThumbnailModel> list)
    {
        this.list =list;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{

        TextView search_text;
        String routineId,sub_category,thumbnail,userId,instructorId;
        FirebaseUser user;
        DatabaseReference mDatabaseReference;
        LoadingDialog loadingDialog;


        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            search_text = itemView.findViewById(R.id.search_text);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            loadingDialog = new LoadingDialog(((AppCompatActivity) context));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int p=0;
                    if(user==null)
                    {
                        Toast.makeText(context,"Login First",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, InstructorLogin.class);
                        context.startActivity(intent);
                    }
                    else
                        routinePurchase();

                }
            });




        }

        public void routinePurchase(){

            Fragment fragment = new routine_purchase();
            Bundle bundle = new Bundle();
            //  bundle.putString("category","Routine");
            bundle.putString("routineId",routineId);
            bundle.putString("userId",instructorId);
            //     bundle.putString("cat","Routine");
            //   bundle.putString("planplan","1month");
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.drawer_layout, fragment);
            fragmentTransaction.commit();

        }


    }
}
