package com.udacity.stockhawk.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;

/**
 * Created by abhi on 2/8/17.
 */

public class Utils {

    /**
     * check if internet is available
     * @param context context
     * @return true if internet is available
     */
    static public boolean isNetworkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     *
     * @param context context used to get shared preferences
     * @return location status integer type
     */
    @SuppressWarnings("ResourceType")
    static public @QuoteSyncJob.stockStatus int getStocksStatus(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(context.getString(R.string.pref_stock_key), QuoteSyncJob.STOCKS_UNKNOWN);
    }

    /**
     * reset stock status
     * @param context context
     */
    static public void resetStockStatus(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.pref_stock_key), QuoteSyncJob.STOCKS_UNKNOWN);
        editor.apply();
    }
}
