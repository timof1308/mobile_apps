package de.vms.vmsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import de.vms.vmsapp.Models.User;

public class ProfileFragment extends Fragment {
    // ui elements
    private View view;
    // api params
    private String URL;
    private String TOKEN;

    private TextView tv;
    private Button buttonProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);




    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // define view
        view = inflater.inflate(R.layout.fragment_profile, container, false);


        // get api url and token from shared pref
        SharedPreferences shared_pref = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        URL = shared_pref.getString("URL", null);
        TOKEN = shared_pref.getString("token", null);

        User user = null;
        try {
            user = JwtController.decodeJwt(TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        tv = (TextView) view.findViewById(R.id.profileMail);
        tv.setText(user.getEmail());

        buttonProfile = (Button) view.findViewById(R.id.buttonProfile);

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                SharedPreferences.Editor shared_pref_edit = shared_pref.edit();
                shared_pref_edit.putString("token", null);
                shared_pref_edit.apply();
                startActivity(intent);
            }
        });





        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        User user;

        try {
            user = JwtController.decodeJwt(TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ELEMENT.setText(user.getName());
        // ELEMENT.setText(user.getEmail());
    }
}
