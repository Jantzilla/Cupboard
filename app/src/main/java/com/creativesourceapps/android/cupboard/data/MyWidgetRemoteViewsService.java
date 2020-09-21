package com.creativesourceapps.android.cupboard.data;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.creativesourceapps.android.cupboard.ui.MyWidgetRemoteViewsFactory;

import java.util.ArrayList;

public class MyWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ArrayList<String> names = intent.getStringArrayListExtra("names");
        ArrayList<String> quantities = intent.getStringArrayListExtra("quantities");
        ArrayList<String> units = intent.getStringArrayListExtra("units");
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), names, quantities, units);
    }
}