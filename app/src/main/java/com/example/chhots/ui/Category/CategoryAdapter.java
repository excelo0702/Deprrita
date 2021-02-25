package com.example.chhots.ui.Category;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.Models.CategoryModel;
import com.example.chhots.R;
import com.example.chhots.category_view.Courses.AllCourse;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryView>{

    private List<CategoryModel> list;
    private Context context;

    public CategoryAdapter(List<CategoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_category_item,parent,false);
        return new CategoryView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryView holder, int position) {
        holder.img.setImageResource(list.get(position).getImage());
        holder.txt.setText(list.get(position).getText());
        holder.l = list.get(position).getL();
        holder.u = list.get(position).getU();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CategoryView extends RecyclerView.ViewHolder{

        ImageView img;
        TextView txt;
        int l,u;

        public CategoryView(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.category_image);
            txt = itemView.findViewById(R.id.category_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("category",txt.getText().toString());
                    bundle.putInt("lower",l);
                    bundle.putInt("upper",u);
                    Fragment fragment = new AllCourse();
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.drawer_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });


        }
    }

}
