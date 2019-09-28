package de.vms.vmsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScanInHomeFragmentActivity extends AppCompatActivity {

    public static TextView tv_result;
    private Button btn_scan;

    private static final String TAG = "ScanInHomeFragmentActiv";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_in_home_fragment);

        Log.d(TAG, "onCreate: created");

        tv_result = (TextView) findViewById(R.id.tv_result11);

        btn_scan = (Button) findViewById(R.id.btn_scan11);

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanInHomeFragmentActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
    }

}
