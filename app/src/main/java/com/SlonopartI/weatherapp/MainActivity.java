package com.SlonopartI.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.SlonopartI.weatherapp.adapter.ForecastAdapter;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    static ParserThread thread=null;
    static MyHandler handler;
    static String[] forecasts=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView=findViewById(R.id.editTextTextPersonName);
        ForecastAdapter forecastAdapter=new ForecastAdapter();
        forecastAdapter.setActivity(this);
        handler=new MyHandler();
        Button button=findViewById(R.id.button);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new ForecastAdapter());
        button.setOnClickListener(v -> {
            if(thread!=null){
                Toast.makeText(this,"Приложение уже ищет прогноз",Toast.LENGTH_SHORT).show();
            }
            else{
                thread=new ParserThread(12,textView.getText().toString());
                thread.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ForecastAdapter forecastAdapter=new ForecastAdapter();
        forecastAdapter.setActivity(this);
        handler.setAdapter(forecastAdapter);
        handler.setView(findViewById(R.id.recyclerView));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(recyclerView.getAdapter()!=null&&((ForecastAdapter)recyclerView.getAdapter()).getForecasts()!=null){
            outState.putStringArray("forecasts",((ForecastAdapter)recyclerView.getAdapter()).getForecasts());
        }
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle inState){
        super.onRestoreInstanceState(inState);
        if(inState.get("forecasts")!=null){
            ForecastAdapter adapter=new ForecastAdapter();
            adapter.setForecasts(inState.getStringArray("forecasts"));
            adapter.setActivity(this);
            recyclerView=findViewById(R.id.recyclerView);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }
    public static class MyHandler extends Handler{
        private RecyclerView view;
        private ForecastAdapter adapter;
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            forecasts= (String[]) msg.getData().get("forecasts");
            adapter.setForecasts(forecasts);
            view.setAdapter(adapter);
            view.setLayoutManager(new LinearLayoutManager(adapter.getActivity()));
            thread=null;
        }

        public void setView(RecyclerView view) {
            this.view = view;
        }
        public void setAdapter(ForecastAdapter adapter){
            this.adapter=adapter;
        }
    }
}