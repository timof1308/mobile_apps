package de.vms.vmsapp;

// SOURCES: https://www.androidtutorialonline.com/android-qr-code-scanner/
// https://demonuts.com/scan-barcode-qrcode/

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import org.json.JSONException;

import java.io.IOException;

import de.vms.vmsapp.Adapters.RoomListAdapter;
import de.vms.vmsapp.Models.Room;
import de.vms.vmsapp.Models.Visitor;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.CAMERA;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final String TAG = "ScanActivity";

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.M) {
            if(checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(ScanActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//        mScannerView.startCamera();          // Start camera on resume
//    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    public void handleResult(Result rawResult) {

        final String result = rawResult.getText();
        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScannerView.resumeCameraPreview(ScanActivity.this);
            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                startActivity(browserIntent);
            }
        });
        HomeFragment.tv_result.setText(rawResult.getText()); //setzt Text in Home
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();

        checkIn(getVisitorId(result));
    }

    public int getVisitorId(String json) {
        JSONObject jo = null;
        int visitorId = 0;
        try {
            jo = new JSONObject(json);
            visitorId = jo.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return visitorId;
    }

    public void checkIn(int visitorId) {
        
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjEsImlkIjoxLCJuYW1lIjoiQWRtaW4iLCJlbWFpbCI6InZtcy53d2kxN3NjYUBnbWFpbC5jb20iLCJwYXNzd29yZCI6ImQ5YjVmNThmMGIzODE5ODI5Mzk3MTg2NWExNDA3NGY1OWViYTNlODI1OTViZWNiZTg2YWU1MWYxZDlmMWY2NWUiLCJyb2xlIjoxLCJ0b2tlbiI6bnVsbCwiaWF0IjoxNTgyMjc3ODM1fQ.U9k0Oykk3rGBRKgQpuc7xgSFSeWaUzk9p3dDMCqVDro")
                        .url("http://35.223.244.220/api/visitors/" + visitorId + "/check_in")
                        .build();

                // run request
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response.body().string());

                    // return response as string to "onPostExecute"
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    // LOG response
                    Log.d("rooms", s);
//                    try {
//                        // pass to function to create List View elements and render view
//                        //loadIntoListView(s);
//                        Log.d(TAG, "onPostExecute: checkIn method executed");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        };
        asyncTask.execute();
    }
}
