package de.vms.vmsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MeetingsFragment extends Fragment {
    private View view;
    private ListView listView;
    private Button newMeetingButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meetings, container, false);
        listView = (ListView) view.findViewById(R.id.meetingsListView);
        newMeetingButton = (Button) view.findViewById(R.id.newMeetingButton);

        getMeetings();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        newMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateMeetingBundle();
            }
        });
    }

    /**
     * open create meeting bundle fragment
     */
    private void openCreateMeetingBundle() {
        // New Fragment
        MeetingBundleFragment meeting_bundle_fragment = new MeetingBundleFragment();
        // Create new fragment and transaction
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, meeting_bundle_fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * get meetings for logged in user
     */
    private void getMeetings() {
        // @TODO: GET MEETINGS FOR USER
    }
}
