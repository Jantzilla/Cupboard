package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> quantities = new ArrayList<>();
    private ArrayList<String> units = new ArrayList<>();

    public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent, ArrayList<String> recipes, ArrayList<String> quantities, ArrayList<String> units) {
        mContext = applicationContext;

        if(recipes != null) {
            names = recipes;
            this.quantities = quantities;
            this.units = units;
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        final long identityToken = Binder.clearCallingIdentity();

        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION) {
            return null;
        }

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item);
        rv.setTextViewText(R.id.ingredientsListItem, names.get(position));
        rv.setTextViewText(R.id.quantityListItem, quantities.get(position));
        rv.setTextViewText(R.id.unitListItem, units.get(position));

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
