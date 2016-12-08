package com.son.hinhnendep;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Le on 19-Mar-16.
 */
public class BaseActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    protected Toolbar activeToolBar(boolean menuBar){

        if(mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.mytoolbar);
            if(mToolbar != null){
                setSupportActionBar(mToolbar);
                if(menuBar)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        return mToolbar;

    }
}
