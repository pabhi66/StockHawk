package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import java.text.NumberFormat;
import java.util.Locale;

public class WidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor = null;

            @Override
            public void onCreate() {}

            @Override
            public void onDataSetChanged() {
                if (cursor != null)
                        cursor.close();

                final long clearIdentity = Binder.clearCallingIdentity();
                cursor = getContentResolver().query(Contract.Quote.URI, Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}), null, null, Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(clearIdentity);

            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getCount() {
                if(cursor != null)
                    return cursor.getCount();

                return 0;
            }

            @Override
            public long getItemId(int position) {
                if(cursor.moveToPosition(position))
                    return cursor.getLong(Contract.Quote.POSITION_ID);
                return position;

            }


            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }

                RemoteViews view = new RemoteViews(getPackageName(), R.layout.list_item_quote);

                view.setTextViewText(R.id.price, NumberFormat.getCurrencyInstance(Locale.US).format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));
                view.setTextViewText(R.id.symbol, cursor.getString(Contract.Quote.POSITION_SYMBOL));
                view.setTextViewText(R.id.change, NumberFormat.getPercentInstance(Locale.US).format(cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE)));

                if(cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE) < 0){
                    view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }else
                    view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);

                view.setOnClickFillInIntent(R.id.list_item_quote, new Intent().setData(Contract.Quote.makeUriForStock(cursor.getString(Contract.Quote.POSITION_SYMBOL))));

                return view;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
