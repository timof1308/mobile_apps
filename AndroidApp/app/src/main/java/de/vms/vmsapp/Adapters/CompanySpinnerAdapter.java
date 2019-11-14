package de.vms.vmsapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.vms.vmsapp.Models.Company;
import de.vms.vmsapp.R;

public class CompanySpinnerAdapter extends ArrayAdapter<Company> {
    private ArrayList<Company> companies;

    public CompanySpinnerAdapter(Context context, ArrayList<Company> companies) {
        super(context, android.R.layout.simple_list_item_1, companies);
        this.companies = companies;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get data item for this position
        Company company = getItem(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.company_spinner_item, parent, false);
        }

        // data population view lookup
        TextView nameTextView = (TextView) convertView.findViewById(R.id.companyNameTextView);
        // populate data to text view
        // @TODO: FIX SETTEXT TO DISPLAY ROOMNAME NOT OBJECT-TO-STRING METHOD
        nameTextView.setText(company.getName());

        // return completed view to render on screen
        return convertView;
    }

    /**
     * Specify to display correct text in dropdown list
     *
     * @param position    int
     * @param convertView View
     * @param parent      ViewGroup
     * @return view
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public Company getItem(int position) {
        return this.companies.get(position);
    }

    @Override
    public int getCount() {
        return this.companies.size();
    }
}
