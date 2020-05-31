package com.example.chhots.bottom_navigation_fragments.Explore;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private String TAG = "CommentAdapter";

    public CommentAdapter(Context context, List<CommentModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG,"vbn");
        View view = LayoutInflater.from(context).inflate(R.layout.raw_comment_video,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG,"vddddbn");

        CommentModel model = list.get(position);

        holder.comment_user_name.setText(model.getUserName());
        holder.comment.setText(model.getComment());
        holder.userVideoId = list.get(position).getUserId();
        Picasso.get().load(Uri.parse(list.get(position).getUserImage())).into(holder.image);

        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                list.remove(position);
                notifyDataSetChanged();
                return true;
            }
        });


    }
    @Override
    public int getItemCount() {
        Log.d(TAG,"vbnssssss");
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public TextView comment,comment_user_name;
        private ImageView image;
        RelativeLayout relativeLayout;
        String userVideoId,userId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG,"vaaabn");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();
            itemView.setOnCreateContextMenuListener(this);
            comment = itemView.findViewById(R.id.comment_text);
            comment_user_name = itemView.findViewById(R.id.comment_user);
            image = itemView.findViewById(R.id.comment_user_photo);
            relativeLayout = itemView.findViewById(R.id.comment_raw_view);

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



        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            if(userVideoId.equals(userId)) {

                MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Delete");
               // delete.setOnMenuItemClickListener(onDeleteMenu);
            }

        }

      /*  private final MenuItem.OnMenuItemClickListener onDeleteMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                switch (menuItem.getItemId()){
                    case 1:
                        databaseReference.child("ROUTINE_THUMBNAIL").child(routineId).removeValue();
                        databaseReference.child("ROUTINEVIDEOS").child(routineId).removeValue();
                        databaseReference.child("Instructor").child(userId).child(routineId).removeValue();

                        StorageReference ref = FirebaseStorage.getInstance().getReference("ROUTINEVIDEOS").child(routineId);
                        StorageReference ref2 = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context,"SuccessFully Deleted",Toast.LENGTH_SHORT).show();
                            }
                        });
                        ref2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context,"SuccessFully Deleted",Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

                return true;
            }
        };
*/

    }
}
