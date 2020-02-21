package de.vms.vmsapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.FormBody;


public class CreateRoomDialog extends AppCompatDialogFragment {

    EditText edit_roomname;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_create_room, null);

        //to display cursor, but doesn't work so far
        edit_roomname = (EditText) view.findViewById(R.id.edit_roomname);
        edit_roomname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == edit_roomname.getId()) {
                    edit_roomname.setCursorVisible(true);
                }
            }
        });


        builder.setView(view)
                .setTitle("Create Room")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createRoom(edit_roomname.getText().toString());
                        // @TODO: refresh rooms (getRooms())
                    }
                });
        return builder.create();
    }

    /**
     * Create a new room
     */
    private void createRoom(final String s) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("name", s)
                        .build();

                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjEsImlkIjoxLCJuYW1lIjoiQWRtaW4iLCJlbWFpbCI6InZtcy53d2kxN3NjYUBnbWFpbC5jb20iLCJwYXNzd29yZCI6ImQ5YjVmNThmMGIzODE5ODI5Mzk3MTg2NWExNDA3NGY1OWViYTNlODI1OTViZWNiZTg2YWU1MWYxZDlmMWY2NWUiLCJyb2xlIjoxLCJ0b2tlbiI6bnVsbCwiaWF0IjoxNTgyMjc3ODM1fQ.U9k0Oykk3rGBRKgQpuc7xgSFSeWaUzk9p3dDMCqVDro")
                        .url("http://35.223.244.220/api/rooms")
                        .post(formBody)
                        .build();

                // run request
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

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
                    Log.d("data", s);
//                    try {
//                        // pass to function to create List View elements and render view
//                        loadIntoListView(s);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        };

        asyncTask.execute();
    }


}


