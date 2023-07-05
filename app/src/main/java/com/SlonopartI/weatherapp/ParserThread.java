package com.SlonopartI.weatherapp;


import android.os.Bundle;
import android.os.Message;

import com.SlonopartI.weatherapp.adapter.ForecastAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.net.ssl.HttpsURLConnection;

public class ParserThread extends Thread {
    private final int length;
    private final String town;
    private final String request;
    String[] forecasts;

    public ParserThread(int length, String town, String request){
        this.length=length;
        this.town=town;
        forecasts=new String[length];
        this.request = request;
    }
    @Override
    public void run() {
        AtomicReference<AtomicReferenceArray<String>> array = new AtomicReference<>(new AtomicReferenceArray<>(new String[]{null}));
        String apiKey=BuildConfig.API_KEY;
        try {
            URL url;
            if(request.equals("Прогноз")) url= new URL("https://api.weatherapi.com/v1/forecast.json?key="+apiKey+"&q=" + town + "&days=3&aqi=no&alerts=no");
            else url= new URL("https://api.weatherapi.com/v1/current.json?key="+apiKey+"&q=" + town + "&aqi=no");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                line = line.replaceAll("\\{", "\n");
                line = line.replaceAll("\\]", "\n");
                line = line.replaceAll("\\}", "\n");
                line = line.replaceAll(",", "\n");
                line = line.replaceAll(" ","");
                array.set(new AtomicReferenceArray<>(line.split("\n")));
                reader.close();
                if(request.equals("Прогноз")) {
                    if (array.get().get(0) != null) {
                        int daysCount = 0;
                        for (int i = 0; i < array.get().length(); i++) {
                            if (daysCount == length / 4) break;
                            if (array.get().get(i).equals("\"forecastday\":[")) {
                                int dateindex = 0;
                                for (int j = i; j < array.get().length(); j++) {
                                    if (daysCount == length / 4) break;
                                    if (array.get().get(j).startsWith("\"date\":")) {
                                        forecasts[dateindex] = array.get().get(j).replace("\"date\":", "");
                                        dateindex += 4;
                                    }
                                    if (array.get().get(j).startsWith("\"day\":")) {
                                        int count = 0;
                                        for (; j < array.get().length(); j++) {
                                            if (count == 3) {
                                                daysCount++;
                                                break;
                                            }
                                            if (array.get().get(j).startsWith("\"maxtemp_c\":")) {
                                                forecasts[dateindex - 3] = array.get().get(j).replace("\"maxtemp_c\":", "");
                                                count++;
                                            }
                                            if (array.get().get(j).startsWith("\"mintemp_c\":")) {
                                                forecasts[dateindex - 2] = array.get().get(j).replace("\"mintemp_c\":", "");
                                                count++;
                                            }
                                            if (array.get().get(j).startsWith("\"icon\":")) {
                                                forecasts[dateindex - 1] = array.get().get(j)
                                                        .replace("\"icon\":\"//cdn.weatherapi.com/weather/64x64/", "")
                                                        .replace("\"", "");
                                                count++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    for(int i=0;i<array.get().length();i++){
                        if(array.get().get(i).startsWith("\"last_updated\":")){
                            forecasts[0] = array.get().get(i).replace("\"last_updated\":","").substring(1,11);
                        }
                        if(array.get().get(i).startsWith("\"temp_c\":")){
                            forecasts[1] = array.get().get(i).replace("\"temp_c\":","");
                        }
                        if(array.get().get(i).startsWith("\"icon\":")){
                            forecasts[2] = array.get().get(i).replace("\"icon\":\"//cdn.weatherapi.com/weather/64x64/","")
                                    .replace("\"","");
                        }
                    }
                }
                Bundle bundle=new Bundle();
                bundle.putStringArray("forecasts",forecasts);
                Message msg=MainActivity.handler.obtainMessage();
                msg.setData(bundle);
                msg.arg1=0;
                MainActivity.handler.sendMessage(msg);
            }
            else{
                Message msg=MainActivity.handler.obtainMessage();
                msg.arg1=-1;
                MainActivity.handler.sendMessage(msg);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}