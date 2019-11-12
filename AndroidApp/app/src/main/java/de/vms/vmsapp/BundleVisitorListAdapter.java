package de.vms.vmsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.vms.vmsapp.Models.Visitor;

public class BundleVisitorListAdapter extends ArrayAdapter<Visitor> {
    private ArrayList<Visitor> visitors;
    private Button deleteButton;

    public BundleVisitorListAdapter(Context context, ArrayList<Visitor> visitors) {
        super(context, 0, visitors);
        this.visitors = visitors;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get data item for this position
        Visitor visitor = this.visitors.get(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bundle_visitor_list_item, parent, false);
        }

        // data population view lookup
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView companyTextView = (TextView) convertView.findViewById(R.id.companyTextView);
        TextView telTextView = (TextView) convertView.findViewById(R.id.telTextView);
        deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
        // populate data to text view
        nameTextView.setText(visitor.getName());
        companyTextView.setText(visitor.getCompany().getName());
        telTextView.setText(visitor.getTel());

        // click event listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visitors.remove(position);
                Toast.makeText(getContext(), "Visitor has been removed", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });

        // return completed view to render on screen
        return convertView;
    }

    public void addVisitor (Visitor visitor) {
        this.visitors.add(visitor);
        notifyDataSetChanged();
    }

    public ArrayList<Visitor> getVisitors () {
        return this.visitors;
    }

    @Override
    public int getCount() {
        return this.visitors.size();
    }

    @Nullable
    @Override
    public Visitor getItem(int position) {
        return this.visitors.get(position);
    }
}
