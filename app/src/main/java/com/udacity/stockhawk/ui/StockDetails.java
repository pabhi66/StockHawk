package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

public class StockDetails extends AppCompatActivity {

    static String Symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);


            //get intent argument
            Symbol = getIntent().getStringExtra(Intent.EXTRA_TEXT);


            //start the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_stock_details, new StockDetailsFragment())
                    .commit();
    }
}
