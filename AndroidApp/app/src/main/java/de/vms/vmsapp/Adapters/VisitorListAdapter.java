package de.vms.vmsapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.vms.vmsapp.Models.Visitor;
import de.vms.vmsapp.R;

public class VisitorListAdapter extends ArrayAdapter<Visitor> {
    private ArrayList<Visitor> visitors;
    private Button actionButton;
    private Button deleteButton;

    public VisitorListAdapter(Context context, ArrayList<Visitor> visitors) {
        super(context, 0, visitors);
        this.visitors = visitors;

        Log.v("visitors size adapter", "" + visitors.size());
        for (int i = 0; i < visitors.size(); i++) {
            System.out.println("visitor loop adpater " + visitors.get(i).getName() + " " + visitors.get(i).getId() + visitors.get(i));
        }
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get data item for this position
        Visitor visitor = this.visitors.get(position);
        Log.v("visitor adapter", visitor.getName() + " " + visitor.getId() + visitor);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.visitor_list_item, parent, false);
        }

        // data population view lookup
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView companyTextView = (TextView) convertView.findViewById(R.id.companyTextView);
        TextView hostTextView = (TextView) convertView.findViewById(R.id.hostTextView);
        actionButton = (Button) convertView.findViewById(R.id.actionButton);
        deleteButton = (Button) convertView.findViewById(R.id.deleteButton);

        // populate data to text view
        nameTextView.setText(visitor.getName());
        companyTextView.setText(visitor.getCompany().getName());
        hostTextView.setText(visitor.getMeeting().getUser().getName() + " >> " + visitor.getMeeting().getRoom().getName());

        // visitor has not checked in yet
        if (!visitor.isChecked_In()) {
            // action button s> check in
            actionButton.setBackground(getContext().getDrawable(R.drawable.ic_add));
            // delete button -> enable
            deleteButton.setEnabled(true);
        } else {
            // action button -> check out
            actionButton.setBackground(getContext().getDrawable(R.drawable.ic_minus));
            // delete button -> disable
            deleteButton.setEnabled(false);
        }

        // visitor has already checked out
        if (visitor.isChecked_Out()) {
            // action button -> disable
            actionButton.setEnabled(false);
            // delete button -> disable
            deleteButton.setEnabled(false);
        }

        // return completed view to render on screen
        return convertView;
    }

    public Visitor getItem(int position) {
        return this.visitors.get(position);
    }

}
