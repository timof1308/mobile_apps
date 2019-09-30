package de.vms.vmsapp;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardFragment extends Fragment {
    // UI elements
    private View view;
    private TextView activeValueTextView;
    private TextView planendValueTextView;
    private TextView totalValueTextView;
    private TextView companiesValueTextView;
    private EditText dateEditText;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // define view
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // define elements to manipulate
        activeValueTextView = (TextView) view.findViewById(R.id.activeValueTextView);
        planendValueTextView = (TextView) view.findViewById(R.id.planendValueTextView);
        totalValueTextView = (TextView) view.findViewById(R.id.totalValueTextView);
        companiesValueTextView = (TextView) view.findViewById(R.id.companiesValueTextView);
        dateEditText = (EditText) view.findViewById(R.id.dateEditText);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // load dashboard data
        getDashboardData();
        String date = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        getWeekData(date);
        getCompanyData();

        // set prefilled date for date select
        dateEditText.setText(date);

        // Date picker dialog
        DatePickerDialog.OnDateSetListener date_input = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        // open date picker dialog on text edit focus
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), R.style.DialogTheme, date_input, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

        // update text edit text
        dateEditText.setText(sdf.format(myCalendar.getTime()));
        // get data for new date
        getWeekData(sdf.format(myCalendar.getTime()));
    }

    public void getDashboardData() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjYsImlkIjo2LCJuYW1lIjoiVGVzdCBVc2VyIiwiZW1haWwiOiJ2bXMud3dpMTdzY2FAZ21haWwuY29tIiwicGFzc3dvcmQiOiI5MzdlOGQ1ZmJiNDhiZDQ5NDk1MzZjZDY1YjhkMzVjNDI2YjgwZDJmODMwYzVjMzA4ZTJjZGVjNDIyYWUyMjQ0Iiwicm9sZSI6MSwidG9rZW4iOm51bGwsImlhdCI6MTU2OTU5MjI0Mn0.R6bRJ21QNe-Er5GnakGQAY7YK1KPbN79gX67huhfzO4")
                        .url("http://35.184.56.207/api/dashboard")
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
                    Log.d("dashboard", s);
                    try {
                        // pass to function to display data and render view
                        loadDashboardData(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    public void loadDashboardData(String json_string) throws JSONException {
        // convert json string to json object
        JSONObject json = new JSONObject(json_string);
        activeValueTextView.setText(Integer.toString(json.getInt("active")));
        planendValueTextView.setText(Integer.toString(json.getInt("planned")));
        totalValueTextView.setText(Integer.toString(json.getInt("total")));
        companiesValueTextView.setText(Integer.toString(json.getInt("companies")));
    }

    public void getWeekData(String date) {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjYsImlkIjo2LCJuYW1lIjoiVGVzdCBVc2VyIiwiZW1haWwiOiJ2bXMud3dpMTdzY2FAZ21haWwuY29tIiwicGFzc3dvcmQiOiI5MzdlOGQ1ZmJiNDhiZDQ5NDk1MzZjZDY1YjhkMzVjNDI2YjgwZDJmODMwYzVjMzA4ZTJjZGVjNDIyYWUyMjQ0Iiwicm9sZSI6MSwidG9rZW4iOm51bGwsImlhdCI6MTU2OTU5MjI0Mn0.R6bRJ21QNe-Er5GnakGQAY7YK1KPbN79gX67huhfzO4")
                        .url("http://35.184.56.207/api/dashboard/week/" + date)
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

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    // LOG response
                    Log.d("week", s);
                    try {
                        // pass to function to display data and render view
                        loadWeekDataChart(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    public void getCompanyData() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                // prepare request
                // @TODO: get jwt from local storage
                Request request = new Request.Builder()
                        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtb2JpbGVfYXBwc19hcGkiLCJzdWIiOjYsImlkIjo2LCJuYW1lIjoiVGVzdCBVc2VyIiwiZW1haWwiOiJ2bXMud3dpMTdzY2FAZ21haWwuY29tIiwicGFzc3dvcmQiOiI5MzdlOGQ1ZmJiNDhiZDQ5NDk1MzZjZDY1YjhkMzVjNDI2YjgwZDJmODMwYzVjMzA4ZTJjZGVjNDIyYWUyMjQ0Iiwicm9sZSI6MSwidG9rZW4iOm51bGwsImlhdCI6MTU2OTU5MjI0Mn0.R6bRJ21QNe-Er5GnakGQAY7YK1KPbN79gX67huhfzO4")
                        .url("http://35.184.56.207/api/dashboard/companies")
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

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    // LOG response
                    Log.d("companies", s);
                    try {
                        // pass to function to display data and render view
                        loadCompanyDataChart(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();
    }

    public void loadWeekDataChart(String json) throws JSONException {
        // parse string to json
        JSONObject jsonObj = new JSONObject(json);

        // define column chart
        Cartesian cartesian = AnyChart.column();

        // fill data list from json
        List<DataEntry> data = new ArrayList<>();
        JSONArray keys = jsonObj.names();
        for (int i = 0; i < keys.length(); ++i) {
            String date_key = keys.getString(i);
            Integer count_value = jsonObj.getInt(date_key);
            data.add(new ValueDataEntry(date_key, count_value));
        }

        // get ui for chart
        AnyChartView anyChartView = (AnyChartView) view.findViewById(R.id.weekDataAnyChartView);
        // fill column chart with data
        Column column = cartesian.column(data);
        // column positioning
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");
        cartesian.animation(true);
        // set column chart title
        cartesian.title("Week's visitor count");
        // axis labels and scale
        cartesian.yScale().minimum(0d);
        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");
        // tooltip and hover animation / position
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        // x axis title
        cartesian.xAxis(0).title("Date");
        // y axis title
        cartesian.yAxis(0).title("Visitors");

        // draw chart
        anyChartView.setChart(cartesian);
        Log.d("column", "DONE");
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
    }

    public void loadCompanyDataChart(String json) throws JSONException {
        // parse string to json
        JSONArray jsonArray = new JSONArray(json);

        // fill data list from json
        List<DataEntry> data = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // get json object from array
            JSONObject obj = jsonArray.getJSONObject(i);
            data.add(new ValueDataEntry((String) obj.get("name"), (Integer) obj.get("count")));
        }

        // get ui for chart
        AnyChartView anyChartView = (AnyChartView) view.findViewById(R.id.companyDataAnyChartView);
        // define pie chart
        Pie pie = AnyChart.pie();
        // fill pie chart with data
        pie.data(data);
        // set pie chart title
        pie.title("Visitor count per company");
        // position labels outside of chart
        pie.labels().position("outside");
        // pie chart legend styles
        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Companies")
                .padding(0d, 0d, 10d, 0d);
        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        // draw chart
        anyChartView.setChart(pie);
        Log.d("pie", "DONE");
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
    }
}
