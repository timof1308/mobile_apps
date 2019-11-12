package de.vms.vmsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import de.vms.vmsapp.Models.Company;

public class CreateVisitorDialog extends DialogFragment {
    private View view;
    private TextView cancelButton;
    private TextView okButton;
    private EditText visitorNameEditText;
    private EditText visitorEmailEditText;
    private EditText visitorTelEditText;
    private Spinner companySpinner;
    private Company company;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_create_visitor, container, false);

        cancelButton = (TextView) view.findViewById(R.id.actionCancelButton);
        okButton = (TextView) view.findViewById(R.id.actionOkButton);
        visitorNameEditText = (EditText) view.findViewById(R.id.nameEditText);
        visitorEmailEditText = (EditText) view.findViewById(R.id.emailEditText);
        visitorTelEditText = (EditText) view.findViewById(R.id.telEditText);
        companySpinner = (Spinner) view.findViewById(R.id.companySpinner);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // @TODO: get data form input
                Log.d("DIALOG", "submit");
                getDialog().dismiss();
            }
        });

        return view;
    }
}
