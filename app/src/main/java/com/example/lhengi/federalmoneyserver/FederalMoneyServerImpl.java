package com.example.lhengi.federalmoneyserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.NetworkOnMainThreadException;
import android.util.Log;


import com.example.lhengi.federalmoneyserver.*;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class FederalMoneyServerImpl extends Service {

    protected static final String TAG = "FederalMoneyServer";

    private static final String initialURL = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=";




    private final FederalMoneyServer.Stub mBinder = new FederalMoneyServer.Stub() {

        public int[] monthlyAvgCash(int aYear)
        {

            System.out.println("$$$$$$$$$$$$$$$$$");
            String query = "SELECT open_today FROM t1 WHERE year = "+aYear+" AND month = 1";
            System.out.println(query);
            String urlQuery = initialURL+ URLEncoder.encode(query);
            System.out.println(urlQuery);
            String data = "";
            HttpURLConnection httpURLConnection = null;
            try{
                httpURLConnection = (HttpURLConnection) new URL(urlQuery).openConnection();
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                data = readStream(in);
            }catch (MalformedURLException exception){Log.e(TAG,"Malformedurl expection");}
            catch (IOException e){Log.e(TAG,"IOException");}
            catch (NetworkOnMainThreadException e){Log.e(TAG,"Network on main thread");}
            finally {
                if(null != httpURLConnection)
                    httpURLConnection.disconnect();
            }

            System.out.println("Data is coming**********\n\n\n"+data+"&&&&&\n\n\n");




            int[] returnVar = {666666666};



        return returnVar;
        }

        public int[] dailyCash(int aYear, int aMonth, int aDay, int aNumber) {
            return new int[1];
        }

    };

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        // StringBuffer is a thread-safe String that can also be changed
        StringBuffer data = new StringBuffer("");
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return data.toString();
    }



    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("&&&&&&&&&&&&&&&&&& Binding !!!!!!!!!!!!!!!!!1");
        return mBinder;
    }
}


