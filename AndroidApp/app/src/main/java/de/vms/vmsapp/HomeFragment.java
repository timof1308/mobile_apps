package de.vms.vmsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment { //Fragment, AppCompatActivity

    private static final String TAG = "HomeFragment";
    private View view;

    //Toolbar mToolbar;
    public static TextView tv_result;
    private Button btn_scan;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        tv_result = (TextView) view.findViewById(R.id.tv_result);

        btn_scan = (Button) view.findViewById(R.id.btn_scan);

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mToolbar.setTitle(R.string.app_name);
    }

}
