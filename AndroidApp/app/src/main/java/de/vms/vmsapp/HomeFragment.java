package de.vms.vmsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class HomeFragment extends AppCompatActivity { //Fragment, AppCompatActivity

    private static final String TAG = "HomeFragment";

    //Toolbar mToolbar;
    public static TextView tv_result;
    private Button btn_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
//        mToolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Log.d(TAG, "onCreate: created");

        tv_result = (TextView) findViewById(R.id.tv_result);

        btn_scan = (Button) findViewById(R.id.btn_scan);

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this, ScanActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //mToolbar.setTitle(R.string.app_name);
    }

}
