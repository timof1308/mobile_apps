package de.vms.vmsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

import de.vms.vmsapp.Adapters.CompanySpinnerAdapter;
import de.vms.vmsapp.Models.Company;
import de.vms.vmsapp.Models.Visitor;

public class CreateVisitorDialog extends DialogFragment {
    private View view;
    private TextView cancelButton;
    private TextView okButton;
    private EditText visitorNameEditText;
    private EditText visitorEmailEditText;
    private EditText visitorTelEditText;
    private Spinner companySpinner;
    private Company company;
    private ArrayList<Company> companies;

    public static CreateVisitorDialog getInstanceFor(ArrayList<Company> companies) {
        CreateVisitorDialog cvd = new CreateVisitorDialog();
        cvd.companies = companies;
        return cvd;
    }

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

        // set spinner list items
        CompanySpinnerAdapter arrayAdapter = new CompanySpinnerAdapter(getActivity(), companies);
        companySpinner.setAdapter(arrayAdapter);

        // company spinner on select item event
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                company = (Company) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get data from input fields
                String name = visitorNameEditText.getText().toString();
                String email = visitorEmailEditText.getText().toString();
                String tel = visitorTelEditText.getText().toString();
                // create new visitor
                Visitor visitor = new Visitor();
                visitor.setName(name);
                visitor.setEmail(email);
                visitor.setTel(tel);
                visitor.setCompany(company);

                Log.d("DIALOG", "submit");
                // pass visitor to fragment
                sendResults(visitor);
            }
        });

        return view;
    }

    private void sendResults(Visitor visitor) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = MeetingBundleFragment.newIntent(visitor);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }
}
