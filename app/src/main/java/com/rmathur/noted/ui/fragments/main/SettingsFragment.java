package com.rmathur.noted.ui.fragments.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.rmathur.noted.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingsFragment extends Fragment {

    @InjectView(R.id.lstSettings)
    ListView lstSettings;

    List<String> settingsList;

    String email;
    SharedPreferences sharedPreferences;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pref, container, false);
        ButterKnife.inject(this, view);

        lstSettings = (ListView) view.findViewById(R.id.lstSettings);
        settingsList = new ArrayList<String>();
        settingsList.add("Email");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, settingsList);
        lstSettings.setAdapter(arrayAdapter);

        lstSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(arrayAdapter.getItem(position));
            }
        });

        return view;
    }

    public void showDialog(String item) {
        switch (item) {
            case "Email":
                loadSettings();

                AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());

                alert.setTitle("Email");
                alert.setMessage("Enter your email address:");

                // Set an EditText view to get user input
                final EditText input = new EditText(this.getActivity());
                input.setText(email);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        saveEmail(value);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
        }
    }

    public void saveEmail(String email) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.commit();
    }

    public void loadSettings() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        email = sharedPreferences.getString("email", null);
    }
}