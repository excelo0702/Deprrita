package com.example.chhots.LetsGetStarted;

import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.chhots.R;
import com.example.chhots.ui.home.Model;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation.CornerType;

public class LGS_Adapter extends PagerAdapter {
    private List<LetsGetStartedModel> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public LGS_Adapter(List<LetsGetStartedModel> models, Context context) {
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
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.raw_lets_get_started_item,container,false);
        TextView welcome;
        TextView lgs;
        ImageView startimage;
        welcome=view.findViewById(R.id.welcome);
        lgs=view.findViewById(R.id.lets_get_started_description);
        startimage=view.findViewById(R.id.startimage);

        welcome.setText(models.get(position).getWelcome());
        lgs.setText(models.get(position).getDescription());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ViewOutlineProvider provider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int curveRadius = 24;
                    outline.setRoundRect(0, 0, view.getWidth(), (view.getHeight()+curveRadius), curveRadius);
                }
            };
            startimage.setOutlineProvider(provider);
            startimage.setClipToOutline(true);
        }
        Picasso.get().load(models.get(position).getStartimage()).transform(new RoundedCornersTransformation(100,100)).into(startimage);
        container.addView(view);


        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
