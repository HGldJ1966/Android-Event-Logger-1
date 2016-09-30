package com.arkada38.eventlogger.Controller.Tabs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.arkada38.eventlogger.Controller.Activation;
import com.arkada38.eventlogger.Controller.Item;
import com.arkada38.eventlogger.Model.Profile.ItemProfileList;
import com.arkada38.eventlogger.Model.Profile.Profiles;
import com.arkada38.eventlogger.Model.Settings;
import com.arkada38.eventlogger.R;

public class TabEventsFragment extends Fragment {

    public static LinearLayout linearLayout;
    public static LayoutInflater ItInflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.events_layout, null);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        ItInflater = getLayoutInflater(savedInstanceState);

        setData();

        view.findViewById(R.id.addItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Profiles.profilesList.get(Profiles.itemIndex).item.size() < 5 || Settings.getAccess()) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings.activity);
                    LayoutInflater inflater = Settings.activity.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.add_event, null);
                    dialogBuilder.setView(dialogView);

                    final EditText header = (EditText) dialogView.findViewById(R.id.header);
                    final EditText alternativeHeader = (EditText) dialogView.findViewById(R.id.alternativeHeader);

                    final int[] type = {0};
                    final RadioButton type0 = (RadioButton) dialogView.findViewById(R.id.type0);
                    final RadioButton type1 = (RadioButton) dialogView.findViewById(R.id.type1);
                    final RadioButton type2 = (RadioButton) dialogView.findViewById(R.id.type2);

                    type0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            type[0] = 0;
                            dialogView.findViewById(R.id.input_layout_alternative_header).setVisibility(View.GONE);
                        }
                    });
                    type1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            type[0] = 1;
                            dialogView.findViewById(R.id.input_layout_alternative_header).setVisibility(View.GONE);
                        }
                    });
                    type2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            type[0] = 2;
                            dialogView.findViewById(R.id.input_layout_alternative_header).setVisibility(View.VISIBLE);
                        }
                    });

                    dialogBuilder.setTitle(Settings.activity.getString(R.string.add_event));
                    dialogBuilder.setPositiveButton(Settings.activity.getString(R.string.add), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (header.getText().length() > 0 && (alternativeHeader.getText().length() > 0 || type[0] != 2)) {
                                boolean b = true;
                                if (Profiles.profilesList.size() > 0)
                                    for (int i = 0; i < Profiles.profilesList.get(Profiles.itemIndex).item.size(); i++)
                                        if (Profiles.profilesList.get(Profiles.itemIndex).item.get(i).header.equals(header.getText().toString())) {
                                            b = false;
                                            Toast.makeText(Settings.activity, R.string.name_is_busy, Toast.LENGTH_LONG).show();
                                            break;
                                        }
                                if (b) {
                                    if (type[0] == 0)
                                        Profiles.profilesList.get(Profiles.itemIndex).item.add(new ItemProfileList(header.getText().toString()));
                                    if (type[0] == 1)
                                        Profiles.profilesList.get(Profiles.itemIndex).item.add(new ItemProfileList(header.getText().toString(), 1));
                                    if (type[0] == 2)
                                        Profiles.profilesList.get(Profiles.itemIndex).item.add(new ItemProfileList(header.getText().toString(), alternativeHeader.getText().toString()));

                                    TabEventsFragment.setData();
                                    TabLogsFragment.setData();
                                    TabResultsFragment.setData();
                                    Profiles.saveProfiles();
                                }
                            } else
                                Toast.makeText(Settings.activity, R.string.name_empty, Toast.LENGTH_LONG).show();
                        }
                    });
                    dialogBuilder.setNegativeButton(Settings.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    AlertDialog b = dialogBuilder.create();
                    b.show();
                }
                else Activation.initMessage();
            }
        });

        return view;
    }

    public static void setData() {
        linearLayout.removeAllViews();

        if (Profiles.profilesList.size() > 0)
            for (int i = 0; i < Profiles.profilesList.get(Profiles.itemIndex).item.size(); i++)
                new Item(ItInflater, linearLayout, Profiles.profilesList.get(Profiles.itemIndex).item.get(i));
    }
}
