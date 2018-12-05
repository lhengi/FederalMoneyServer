// FederalMoneyServer.aidl
package com.example.lhengi.federalmoneyserver;

// Declare any non-default types here with import statements

interface FederalMoneyServer {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     int[] dailyCash(int aYear, int aMonth, int aDay, int aNumber);
     int[] monthlyAvgCash(int aYear);


}
