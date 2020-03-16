package de.vms.vmsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
