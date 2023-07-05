package com.SlonopartI.weatherapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SlonopartI.weatherapp.R;

import java.io.InputStream;


public class ForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String[] forecasts;
    private Activity activity;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false))
        {};
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(forecasts.length==3){
            if(position>0)return;
            TextView textView=holder.itemView.findViewById(R.id.textView);
            TextView textView2=holder.itemView.findViewById(R.id.textView2);
            TextView textView3=holder.itemView.findViewById(R.id.textView3);
            ImageView imageView=holder.itemView.findViewById(R.id.imageView);
            textView.setText(forecasts[0]);
            textView2.setText(forecasts[1]+"C");
            textView3.setText("");
            try {
                InputStream stream=activity.getAssets().open(forecasts[2]);
                Drawable drawable=Drawable.createFromStream(stream,null);
                imageView.setImageDrawable(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            if(position>= forecasts.length/4)return;
            TextView textView=holder.itemView.findViewById(R.id.textView);
            TextView textView2=holder.itemView.findViewById(R.id.textView2);
            TextView textView3=holder.itemView.findViewById(R.id.textView3);
            ImageView imageView=holder.itemView.findViewById(R.id.imageView);

            textView.setText(forecasts[position*4].replace("\"",""));
            textView2.setText("макс: "+forecasts[position*4+1]+"C");
            textView3.setText("мин: "+forecasts[position*4+2]+"C");
            try {
                InputStream stream=activity.getAssets().open(forecasts[position*4+3]);
                Drawable drawable=Drawable.createFromStream(stream,null);
                imageView.setImageDrawable(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if(forecasts.length==3)return 1;
        else return forecasts.length/4;
    }

    public void setForecasts(String[] forecasts) {
        this.forecasts = forecasts;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String[] getForecasts() {
        return forecasts;
    }

    public Activity getActivity() { return activity; }
}
