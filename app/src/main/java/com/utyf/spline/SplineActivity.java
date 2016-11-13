package com.utyf.spline;

import android.app.Activity;
import android.os.Bundle;

public class SplineActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new RailView(this));
    }
}
