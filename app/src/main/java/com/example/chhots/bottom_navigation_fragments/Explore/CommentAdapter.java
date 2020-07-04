package com.example.chhots.bottom_navigation_fragments.Explore;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.R;
import com.example.chhots.ui.Dashboard.dashboard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{


    private Context context;
    private List<CommentModel> list;
    private String videoUserID,videoId;
    private String TAG = "CommentAdapter";
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //userId of video


    public CommentAdapter(Context context, List<CommentModel> list, String videoUserID, String videoId) {
        this.context = context;
        this.list = list;
        this.videoUserID = videoUserID;
        this.videoId = videoId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG,"vbn");
        View view = LayoutInflater.from(context).inflate(R.layout.raw_comment_video,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"vddddbn");

        CommentModel model = list.get(position);

        holder.comment_user_name.setText(model.getUserName());
        holder.comment.setText(model.getComment());
        holder.comment_date.setText(model.getTime());
        Picasso.get().load(Uri.parse(list.get(position).getUserImage())).into(holder.image);
        holder.commentId = model.getCommentId();

        if(!user.getUid().equals(model.getUserId()) && !user.getUid().equals(videoUserID))
        {
            holder.three_dot.setVisibility(View.GONE);
        }


    }
    @Override
    public int getItemCount() {
        Log.d(TAG,"vbnssssss");

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView comment,comment_user_name,comment_date;
        private ImageView image,three_dot;
        private String commentId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG,"vaaabn");

            comment = itemView.findViewById(R.id.comment_text);
            comment_user_name = itemView.findViewById(R.id.comment_user);
            image = itemView.findViewById(R.id.comment_user_photo);
            comment_date = itemView.findViewById(R.id.comment_date);
            three_dot = itemView.findViewById(R.id.three_dot_comment);

            comment_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new dashboard();

                    Bundle bundle = new Bundle();
                    bundle.putString("category","UserView");
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.drawer_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });


            three_dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(three_dot);
                }
            });



        }


        private void showPopupMenu(View view)
        {
            PopupMenu popup = new PopupMenu(context, view, Gravity.END);
            MenuInflater inflater = popup.getMenuInflater();

            inflater.inflate(R.menu.delete_menu, popup.getMenu());

            //set menu item click listener here
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(getAdapterPosition()));
            popup.show();
        }

        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
            int position;

            /**
             * @param position
             */
            MyMenuItemClickListener(int position) {

                this.position = position;
            }

            /**
             * Click listener for popup menu items
             */

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("COMMENTS").child(videoId).child(commentId).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        if(position>0) {
                                            list.remove(position);
                                            notifyItemRemoved(position + 1);
                                            notifyItemRangeChanged(position + 1, list.size());
                                        }

                                        Toast.makeText(context,"Deleted ",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context,"Failed ",Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return true;
                }
                return false;
            }
        }



    }
}
