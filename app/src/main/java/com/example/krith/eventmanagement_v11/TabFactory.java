package com.example.krith.eventmanagement_v11;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

/**
 * Created by Krith on 03-04-2015.
 */
public class TabFactory implements TabHost.TabContentFactory {

    private final Context mContext;

    public TabFactory(Context context) {
        mContext = context;
    }

    public View createTabContent(String tag) {
        View v = new View(mContext);
        v.setMinimumWidth(0);
        v.setMinimumHeight(0);
        return v;
    }
}
