package com.example.chhots.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import com.example.chhots.R;
import com.example.chhots.category_view.Booking.booking;
import com.example.chhots.category_view.Contest.contest;
import com.example.chhots.category_view.Courses.courses;
import com.example.chhots.category_view.live;
import com.example.chhots.category_view.routine.routine;

import java.util.List;

public class Adapter extends PagerAdapter {

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.raw_home_item,container,false);



        TextView txt1,des1;
        ImageView img;
        txt1 = (TextView)view.findViewById(R.id.text);
        img = view.findViewById(R.id.viewpager_image);
        txt1.setText(models.get(position).getName());
        img.setImageResource(models.get(position).getImage());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(position)
                {
                    case 0:
                        setFragment(new live());
                        break;

                    case 1:
                        setFragment(new routine());
                        break;

                    case 2:
                        setFragment(new contest());
                        break;

                    case 3:
                        setFragment(new courses());
                        break;

                    case 4:
                        setFragment(new booking());
                        break;
                }

            }
        });

        container.addView(view,0);

        return view;
    }

    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
