package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mSymbol;

    private static final int STOCKS_DATA_LOADER = 1;

//    @BindView(R.id.price)
    TextView price;
//    @BindView(R.id.absChange)
    TextView absChange;
//    @BindView(R.id.change)
    TextView perChange;
    TextView name;
    LineChart lineChart;


    //stock variables
    private String stock_symbol, history;
    private float stock_price, stock_absolute_change, stock_percentage_change;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSymbol = StockDetails.Symbol;


        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_sotck_details, container, false);
//        ButterKnife.bind(StockDetailsFragment.class, rootView);

        price = (TextView) rootView.findViewById(R.id.price);
        absChange = (TextView) rootView.findViewById(R.id.price_abs);
        perChange = (TextView) rootView.findViewById(R.id.price_per);
        name = (TextView) rootView.findViewById(R.id.stock_o_name);
        lineChart = (LineChart) rootView.findViewById(R.id.chart);


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(STOCKS_DATA_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mSymbol != null){
            return new CursorLoader(getActivity(),
                    Contract.Quote.makeUriForStock(mSymbol),
                    Contract.Quote.QUOTE_COLUMNS.toArray(new String[Contract.Quote.QUOTE_COLUMNS.size()]),
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null) {
            if (data.moveToFirst()) {
                do {
                    int numColumns = data.getColumnCount();

                    for (int i=0; i<numColumns; ++i) {
                        //System.out.println(data.getColumnName(i) + " == " + data.getString(i));
                        if(i == 1){
                            stock_symbol = data.getString(i);
                        }else if(i == 2){
                            stock_price = Float.parseFloat(data.getString(i));
                        }else if(i == 3){
                            stock_absolute_change = Float.parseFloat(data.getString(i));
                        }else if(i == 4){
                            stock_percentage_change = Float.parseFloat(data.getString(i));
                        }else if(i ==5){
                            history = data.getString(i);
                        }
                    }
                } while (data.moveToNext());
                setStockValues();

                //split the string into array
                String[] historyArray = history.split("\\n");
                List<Float> dates = new ArrayList<>();
                List<Float> price = new ArrayList<>();

                //put dates and values to hash map
                for(String s: historyArray){
                    String[] split = s.split(",");
                    dates.add(Float.valueOf(split[0]));
                    price.add(Float.valueOf(split[1]));
                }

                Collections.reverse(dates);
                Collections.reverse(price);

                List<Entry> datas = new ArrayList<>();
                for(int i = 0; i < price.size(); i++){
                    datas.add(new Entry(dates.get(i)-dates.get(0),price.get(i)));
                }

                Pair<Float, List<Entry>> chartEntry = new Pair<>(dates.get(0), datas);

                LineDataSet dataSet = new LineDataSet(datas, "");
                int color = R.color.material_red_700;
                dataSet.setColor(color);
                dataSet.setLineWidth(2f);

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);

                XAxis x = lineChart.getXAxis();
                x.setTextColor(color);
                x.setValueFormatter(new DateFormatter(chartEntry.first));
                x.setPosition(XAxis.XAxisPosition.BOTTOM);
                x.setAxisLineWidth(2f);
                x.setTextSize(13f);
                x.setAxisLineColor(color);


                YAxis y = lineChart.getAxisLeft();
                y.setTextColor(color);
                y.setAxisLineWidth(2f);
                y.setTextSize(13f);
                y.setAxisLineColor(color);
                y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

                YAxis y1 = lineChart.getAxisRight();
                y1.setEnabled(false);


                lineChart.setTouchEnabled(true);
                lineChart.setPinchZoom(true);
                lineChart.setScaleEnabled(true);
                lineChart.setDragEnabled(true);
                lineChart.getDescription().setEnabled(false);
                lineChart.setAutoScaleMinMaxEnabled(true);


            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setStockValues(){
        getActivity().setTitle(stock_symbol);
         price.setText(String.format("$%s", stock_price));
        absChange.setText(String.format("%s", stock_absolute_change));
        if(stock_percentage_change < 0){
            absChange.setTextColor(getResources().getColor(R.color.material_red_700));
            perChange.setTextColor(getResources().getColor(R.color.material_red_700));
        }else{
            absChange.setTextColor(getResources().getColor(R.color.material_green_700));
            perChange.setTextColor(getResources().getColor(R.color.material_green_700));
        }
        if(stock_absolute_change < 0){
            absChange.setTextColor(getResources().getColor(R.color.material_red_700));
            perChange.setTextColor(getResources().getColor(R.color.material_red_700));
        }else{
            absChange.setTextColor(getResources().getColor(R.color.material_green_700));
            perChange.setTextColor(getResources().getColor(R.color.material_green_700));
        }
        perChange.setText(String.format("%s", stock_percentage_change));
        name.setText(stock_symbol);
    }

    private class DateFormatter implements IAxisValueFormatter {

        private float floatDate;

        DateFormatter(Float first) {
            this.floatDate = first;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String dateString;

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yy", Locale.getDefault());
            System.out.println(value);
            date.setTime((long) (floatDate + value));
            dateString = dateFormat.format(date);
            return  dateString;
        }
    }
}
