package com.example.lhengi.federalmoneyserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.lhengi.federalmoneyserver.*;

import java.util.ArrayList;
import java.util.List;


public class FederalMoneyServerImpl extends Service {


    private final FederalMoneyServer.Stub mBinder = new FederalMoneyServer.Stub() {

        public int[] monthlyAvgCash(int aYear) {



        return new int[1];
        }

        public int[] dailyCash(int aYear, int aMonth, int aDay, int aNumber) {
            return new int[1];
        }

    };



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}


