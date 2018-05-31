package com.creativesourceapps.android.cupboard;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

public class MyWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ArrayList<String> recipe = intent.getStringArrayListExtra("bundle");
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent, recipe);
    }
}