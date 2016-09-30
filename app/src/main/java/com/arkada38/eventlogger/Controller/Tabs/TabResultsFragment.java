package com.arkada38.eventlogger.Controller.Tabs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arkada38.eventlogger.Model.Profile.ItemProfileList;
import com.arkada38.eventlogger.Model.Profile.ItemProfileListItem;
import com.arkada38.eventlogger.Model.Profile.Profiles;
import com.arkada38.eventlogger.Model.Settings;
import com.arkada38.eventlogger.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TabResultsFragment extends Fragment {

    static LinearLayout resultView;
    static LayoutInflater inflater;
    public static FragmentManager fragmentManager;
    static String tag = "EventLogger";
    static Button previous, period, next;
    static TextView periodDescription;
    static Date fromDate, toDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TabResultsFragment.inflater = inflater;
        View view = inflater.inflate(R.layout.scroll_layout,null);
        resultView = (LinearLayout) view.findViewById(R.id.View);
        previous = (Button) view.findViewById(R.id.previous);
        period = (Button) view.findViewById(R.id.period);
        next = (Button) view.findViewById(R.id.next);
        periodDescription = (TextView) view.findViewById(R.id.periodDescription);

        period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.periodIndex = 0;
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.activity);
                builder.setTitle(Settings.activity.getString(R.string.select_the_period));
                builder.setItems(Settings.getItems(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Settings.setPeriod(item);
                        setData();
                        TabEventsFragment.setData();
                        TabLogsFragment.setData();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        period.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(Settings.activity.getApplicationContext(), Settings.activity.getString(R.string.select_the_period), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.periodIndex++;
                setData();
                TabEventsFragment.setData();
                TabLogsFragment.setData();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.periodIndex--;
                Settings.periodIndex = Math.max(0, Settings.periodIndex);
                setData();
                TabEventsFragment.setData();
                TabLogsFragment.setData();
            }
        });

        Settings.periodIndex = 0;
        setData();

        return view;
    }

    public static void setData() {
        if (period != null) {
            period.setText(Settings.getItems()[Settings.getPeriod()]);
            periodDescription.setVisibility(View.VISIBLE);

            fromDate = new Date();
            toDate = new Date();

            switch (Settings.getPeriod()) {
                case 1:
                    fromDate = new Date(new Date().getTime() - (Settings.periodIndex + 1) * 7 * 24 * 60 * 60 * 1000);
                    toDate = new Date(new Date().getTime() - Settings.periodIndex * 7 * 24 * 60 * 60 * 1000);
                    break;
                case 2:
                    try {
                        fromDate = new Date(new Date().getTime() - Settings.periodIndex * 7 * 24 * 60 * 60 * 1000L);
                        fromDate = new SimpleDateFormat("dd.MM.yyyy").parse(new SimpleDateFormat("dd.MM.yyyy").format(fromDate));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                        calendar.setTime(fromDate);
                        int week = calendar.get(Calendar.DAY_OF_WEEK);
                        fromDate = new Date(fromDate.getTime() - 24 * 60 * 60 * 1000 * (week - 2));

                        if (Settings.periodIndex > 0) {
                            toDate = new Date(new Date().getTime() - (Settings.periodIndex - 1) * 7 * 24 * 60 * 60 * 1000L);
                            toDate = new SimpleDateFormat("dd.MM.yyyy").parse(new SimpleDateFormat("dd.MM.yyyy").format(toDate));
                            calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                            calendar.setTime(toDate);
                            week = calendar.get(Calendar.DAY_OF_WEEK);
                            toDate = new Date(toDate.getTime() - 24 * 60 * 60 * 1000 * (week - 2));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    fromDate = new Date(new Date().getTime() - (Settings.periodIndex + 1) * 24 * 60 * 60 * 1000);
                    toDate = new Date(new Date().getTime() - Settings.periodIndex * 24 * 60 * 60 * 1000);
                    break;
                case 4:
                    try {
                        fromDate = new Date(new Date().getTime() - Settings.periodIndex * 24 * 60 * 60 * 1000L);
                        fromDate = new SimpleDateFormat("dd.MM.yyyy").parse(new SimpleDateFormat("dd.MM.yyyy").format(fromDate));

                        if (Settings.periodIndex > 0) {
                            toDate = new Date(new Date().getTime() - (Settings.periodIndex - 1) * 24 * 60 * 60 * 1000L);
                            toDate = new SimpleDateFormat("dd.MM.yyyy").parse(new SimpleDateFormat("dd.MM.yyyy").format(toDate));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    fromDate = new Date(new Date().getTime() - (Settings.periodIndex + 1) * 60 * 60 * 1000);
                    toDate = new Date(new Date().getTime() - Settings.periodIndex * 60 * 60 * 1000);
                    break;
                default:
                    fromDate = new Date(0);
                    periodDescription.setVisibility(View.GONE);
            }

            periodDescription.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(fromDate) + " - " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(toDate));

            resultView.removeAllViews();

            if (Profiles.profilesList.size() > 0)
                for (int i = 0; i < Profiles.profilesList.get(Profiles.itemIndex).item.size(); i++) {
                    ItemProfileList item = Profiles.profilesList.get(Profiles.itemIndex).item.get(i);
                    View view = inflater.inflate(R.layout.result_item, null, false);
                    resultView.addView(view);

                    if (item.type == 1)
                        view.findViewById(R.id.continuityLayout).setVisibility(View.GONE);

                    ((TextView) view.findViewById(R.id.header)).setText(item.header);
                    if (item.item != null && item.item.size() > 0) {
                        int q = 0;
                        int c = 0;
                        for (int j = 0; j < item.item.size(); j++) {
                            if (item.item.get(j).startDate.getTime() > fromDate.getTime() && item.item.get(j).startDate.getTime() < toDate.getTime()) {
                                q++;
                                if (item.item.get(j).endDate != null)
                                    c += item.item.get(j).endDate.getTime() - item.item.get(j).startDate.getTime();
                                else
                                    c += new Date().getTime() - item.item.get(j).startDate.getTime();
                            }
                        }
                        ((TextView) view.findViewById(R.id.quantity)).setText("" + q);
                        ((TextView) view.findViewById(R.id.continuity)).setText(getContinuity(c));
                    }

                    if (item.type == 2) {
                        List<ItemProfileListItem> listItems = new ArrayList<>(Profiles.profilesList.get(Profiles.itemIndex).item.get(i).item);
                        Collections.sort(listItems, new ItemProfileListItemComparator());

                        View alternativeView = inflater.inflate(R.layout.result_item, null, false);
                        resultView.addView(alternativeView);

                        ((TextView) alternativeView.findViewById(R.id.header)).setText(item.alternativeHeader);
                        if (item.item != null && item.item.size() > 0) {
                            int q = 0;
                            int c = 0;
                            for (int j = 0; j < item.item.size(); j++) {
                                if (listItems.get(j).endDate != null && listItems.get(j).endDate.getTime() > fromDate.getTime() && listItems.get(j).endDate.getTime() < toDate.getTime()) {
                                    q++;
                                    if (j < listItems.size() - 1)
                                        c += listItems.get(j + 1).startDate.getTime() - listItems.get(j).endDate.getTime();
                                    else
                                        c += new Date().getTime() - listItems.get(j).endDate.getTime();
                                }
                            }
                            ((TextView) alternativeView.findViewById(R.id.quantity)).setText("" + q);
                            ((TextView) alternativeView.findViewById(R.id.continuity)).setText(getContinuity(c));
                        }
                    }
                }
        }
    }

    private static class ItemProfileListItemComparator implements Comparator<ItemProfileListItem> {
        @Override
        public int compare(ItemProfileListItem o1, ItemProfileListItem o2) {
            return o1.startDate.compareTo(o2.startDate);
        }
    }

    private static String getContinuity(int milliseconds) {
        long totalSecs = milliseconds / 1000;

        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        if (seconds != 0 || minutes != 0 || hours != 0)
            timeString = seconds + Settings.activity.getString(R.string.s);
        if (minutes != 0 || hours != 0)
            timeString = minutes + Settings.activity.getString(R.string.m) + timeString;
        if (hours != 0)
            timeString = hours + Settings.activity.getString(R.string.h) + timeString;

        return timeString;
    }
}
