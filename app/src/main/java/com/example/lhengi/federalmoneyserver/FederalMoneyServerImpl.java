package com.example.lhengi.federalmoneyserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.NetworkOnMainThreadException;
import android.util.Log;


import com.example.lhengi.federalmoneyserver.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class FederalMoneyServerImpl extends Service {

    protected static final String TAG = "FederalMoneyServer";

    private static final String initialURL = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=";




    private final FederalMoneyServer.Stub mBinder = new FederalMoneyServer.Stub() {

        public int[] monthlyAvgCash(int aYear)
        {
            int[] returnArr = new int[12];
            for(int i = 0; i < 12; i++) {
                String query = "SELECT open_today FROM t1 WHERE year = " + aYear + " AND month = "+(i+1);
                //System.out.println(query);
                String urlQuery = initialURL + URLEncoder.encode(query);
                //System.out.println(urlQuery);
                String data = "";
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = (HttpURLConnection) new URL(urlQuery).openConnection();
                    InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                    data = readStream(in);
                } catch (MalformedURLException exception) {
                    Log.e(TAG, "Malformedurl expection");
                } catch (IOException e) {
                    Log.e(TAG, "IOException");
                } catch (NetworkOnMainThreadException e) {
                    Log.e(TAG, "Network on main thread");
                } finally {
                    if (null != httpURLConnection)
                        httpURLConnection.disconnect();
                }


                returnArr[i] = parseData(data,aYear,i+1);
            }






        return returnArr;
        }

        public int[] dailyCash(int aYear, int aMonth, int aDay, int aNumber)
        {
            int[] returnArr = new int[aNumber];



            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, aDay);
            cal.set(Calendar.MONTH, aMonth-1); // jan is 0
            cal.set(Calendar.YEAR, aYear);
            int iNum = aNumber;
            int startYear = aYear;
            String monthStartStr = (aMonth < 10)?"0"+(aMonth):""+(aMonth);
            String dayStartStr = (aDay < 10)?"0"+aDay:""+aDay;
            boolean startSet = false;
            //System.out.print(cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DAY_OF_MONTH)+"--WeekDay: "+cal.get(Calendar.DAY_OF_WEEK));
            if(cal.get(Calendar.DAY_OF_WEEK)<=6 && cal.get(Calendar.DAY_OF_WEEK) > 1) // sun is 0
            {
                System.out.println("SETTTTTTTTTTTTTTT");
                int curMonth = cal.get(Calendar.MONTH)+1;
                int curDay = cal.get(Calendar.DAY_OF_MONTH);
                startYear = cal.get(Calendar.YEAR);
                monthStartStr = (curMonth < 10)?"0"+curMonth:""+curMonth;
                dayStartStr = (curDay < 10)?"0"+curDay:""+curDay;
                startSet = true;
                iNum--;
            }

            for(int i = 0; i < iNum;)
            {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                System.out.print(cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DAY_OF_MONTH));

                if(cal.get(Calendar.DAY_OF_WEEK)<=6 && cal.get(Calendar.DAY_OF_WEEK) > 1)
                {
                    if (!startSet)
                    {
                        int curMonth = cal.get(Calendar.MONTH)+1;
                        int curDay = cal.get(Calendar.DAY_OF_MONTH);
                        startYear = cal.get(Calendar.YEAR);
                        monthStartStr = (curMonth < 10)?"0"+curMonth:""+curMonth;
                        dayStartStr = (curDay < 10)?"0"+curDay:""+curDay;
                        startSet = true;
                    }
                    System.out.println(" Yes");
                    i++;
                }
                else
                {
                    System.out.println();
                }
            }

            int endMonth = cal.get(Calendar.MONTH)+1;
            int endDay = cal.get(Calendar.DAY_OF_MONTH);
            String monthEndStr = ( endMonth < 10 )?"0"+endMonth:""+endMonth;
            String endDayStr = (endDay < 10)?"0"+endDay:""+endDay;


String query = "SELECT  \"date\", \"weekday\", \"account\", \"open_today\" FROM t1 WHERE (\"date\" >= '"+startYear+"-"+monthStartStr+"-"+dayStartStr+"' AND \"date\" <= '"
        +cal.get(Calendar.YEAR)+"-"+monthEndStr+"-"+endDayStr+"' AND \"weekday\" <> 'Saturday' AND \"weekday\" <> 'Sunday')";

            //String query = "SELECT \"date\", \"open_today\" FROM t1 WHERE (\"date\" >= '"+aYear+"-"+monthStartStr+"-"+dayStartStr+
             //       "' AND \"date\" <= '"+cal.get(Calendar.YEAR)+"-"+endMonth+"-"+endDay
             //       +"' )";// AND ( \"weekday\" = 'Monday' OR \"weekday\" = 'Tuesday' OR \"weekday\" = 'Wednesday' OR \"weekday\" = 'Thursday' OR \"weekday\" = 'Friday' ) " ;
            System.out.println(query);
            String urlQuery = initialURL + URLEncoder.encode(query);
            System.out.println(urlQuery);
            String data = "";
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL(urlQuery).openConnection();
                System.out.println("HERE after connect!!");
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                System.out.println("HERE after in!!");
                data = readStream(in);
                //System.out.println(data);
            } catch (MalformedURLException exception) {
                Log.e(TAG, "Malformedurl expection");
            } catch (IOException e) {
                Log.e(TAG, "IOException");
            } catch (NetworkOnMainThreadException e) {
                Log.e(TAG, "Network on main thread");
            } finally {
                if (null != httpURLConnection)
                    httpURLConnection.disconnect();
            }

            returnArr = parseDaily(data,aNumber);


            return returnArr;
        }

    };

    private int[] parseDaily(String data, int number)
    {
        //System.out.println(data);
        int[] returnArr = new int[number];
        data = data.replace("[","");
        data = data.replace("]","");
        data = data.replace("},","};");
        String[] dataArr = data.split(";");
        JSONObject jobj;
        HashMap<String,Integer> hashMap = new HashMap<>();
        for (String s : dataArr)
        {
            //System.out.println(s);

            try
            {
                jobj = new JSONObject(s);
                String currentDate = jobj.getString("date");
                int currentOpen = jobj.getInt("open_today");

                //System.out.println("Date: "+currentDate+" Open: "+currentOpen);

                if(hashMap.containsKey(currentDate))
                {
                    hashMap.put(currentDate,hashMap.get(currentDate) + currentOpen);
                    //System.out.println("At 2");
                }
                else
                {
                    hashMap.put(currentDate,currentOpen);
                    //System.out.println("At 3");
                }
            }catch (JSONException e){System.out.println("JSONException e");}


        }

        //System.out.println(hashMap.keySet().toString());
        String[] keyArr = new String[hashMap.keySet().size()];
        hashMap.keySet().toArray(keyArr);
        Arrays.sort(keyArr);

        System.out.println("Key length: "+keyArr.length +"  Number: "+number);
        for(int i = 0; i < keyArr.length;i++)
        {
            System.out.println(keyArr[i]);
            returnArr[i] = hashMap.get(keyArr[i]);
        }



        return returnArr;
    }

    private int parseData(String data,int year, int month)
    {
        YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();
        int totalMoney = 0;
        data = data.replace("[","");
        data = data.replace("]","");
        String[] dataArr = data.split(",");
        JSONObject jobj;
        for (String s : dataArr)
        {
            //System.out.println(s);
            try
            {
                jobj = new JSONObject(s);
                totalMoney += jobj.getInt("open_today");
            }catch (JSONException e){System.out.println("JSONException ");}

        }

        //System.out.println("Total money this month!!!!!!!!!: " + totalMoney);
        return totalMoney/daysInMonth;
    }

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


