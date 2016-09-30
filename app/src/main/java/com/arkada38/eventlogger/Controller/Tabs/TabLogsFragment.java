package com.arkada38.eventlogger.Controller.Tabs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

public class TabLogsFragment extends Fragment {

    static LinearLayout logView;
    static LayoutInflater inflater;
    public static FragmentManager fragmentManager;
    static String tag = "EventLogger";
    static List<LogBase> log = new ArrayList<>();
    static Button previous, period, next;
    static TextView periodDescription;
    public static Date fromDate, toDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TabLogsFragment.inflater = inflater;
        View view = inflater.inflate(R.layout.scroll_layout,null);
        logView = (LinearLayout) view.findViewById(R.id.View);
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
                        TabResultsFragment.setData();
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
                TabResultsFragment.setData();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.periodIndex--;
                Settings.periodIndex = Math.max(0, Settings.periodIndex);
                setData();
                TabEventsFragment.setData();
                TabResultsFragment.setData();
            }
        });

        Settings.periodIndex = 0;
        setData();
        TabEventsFragment.setData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    public static void setData() {
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

        logView.removeAllViews();
        log = new ArrayList<>();

        if (Profiles.profilesList.size() > 0) {
            for (int i = 0; i < Profiles.profilesList.get(Profiles.itemIndex).item.size(); i++) {

                List<ItemProfileListItem> listItems = new ArrayList<>(Profiles.profilesList.get(Profiles.itemIndex).item.get(i).item);
                Collections.sort(listItems, new ItemProfileListItemComparator());

                for (int j = 0; j < listItems.size(); j++) {
                    ItemProfileListItem item = listItems.get(j);
                    if (item.startDate.getTime() > fromDate.getTime() && item.startDate.getTime() < toDate.getTime()) {
                        if (item.endDate == null) {
                            String note = item.note == null || item.note.length() == 0 ? "" : " (" + item.note + ") ";
                            log.add(new LogBase(Profiles.profilesList.get(Profiles.itemIndex).item.get(i).header,
                                    note, null, item.startDate, item.endDate, 0));
                        } else {
                            String note = item.note == null || item.note.length() == 0 ? "" : " (" + item.note + ") ";
                            log.add(new LogBase(Profiles.profilesList.get(Profiles.itemIndex).item.get(i).header,
                                    note, getContinuity(item.startDate, item.endDate), item.startDate, item.endDate, 0));
                        }
                    }

                    //Альтернативный заголовок
                    if (item.endDate != null && Profiles.profilesList.get(Profiles.itemIndex).item.get(i).type == 2 && item.endDate.getTime() > fromDate.getTime() && item.endDate.getTime() < toDate.getTime()) {
                        if (j < listItems.size() - 1) {
                            log.add(new LogBase(Profiles.profilesList.get(Profiles.itemIndex).item.get(i).alternativeHeader,
                                    "", getContinuity(item.endDate, listItems.get(j + 1).startDate), item.startDate, item.endDate, 1));
                        }
                        if (j == listItems.size() - 1) {
                            log.add(new LogBase(Profiles.profilesList.get(Profiles.itemIndex).item.get(i).alternativeHeader,
                                    "", null, item.endDate, null, 1));
                        }
                    }
                }
            }

            Collections.sort(log, new LogBaseComparator());
            for (int i = 0; i < log.size(); i++) {
                final View view = inflater.inflate(R.layout.log_item, null, false);
                logView.addView(view);

                ((TextView) view.findViewById(R.id.header)).setText(log.get(i).header);
                ((TextView) view.findViewById(R.id.interval)).setText(log.get(i).interval);

                if (log.get(i).note != null)
                    ((TextView) view.findViewById(R.id.note)).setText(log.get(i).note);
                else view.findViewById(R.id.note).setVisibility(View.GONE);

                if (log.get(i).continuity != null)
                    ((TextView) view.findViewById(R.id.continuity)).setText(log.get(i).continuity);
                else view.findViewById(R.id.continuity).setVisibility(View.GONE);

                view.setTag(log.get(i).startDate);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(tag, "tag " + v.getTag().toString());
                    }
                });
                final int finalI = i;
                final Object viewTag = view.getTag();
                if (log.get(i).type != 1)
                    view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            menu.setHeaderTitle(log.get(finalI).header);
                            menu.add(0, v.getId(), 0, R.string.change);
                            menu.add(0, v.getId(), 1, R.string.delete);

                            for (int i = 0; i < menu.size(); i++)
                                menu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        if (item.getOrder() == 0)
                                            changeItem(viewTag);
                                        if (item.getOrder() == 1)
                                            deleteItem(viewTag);
                                        return false;
                                    }
                                });
                        }
                    });
            }
        }
    }

    private static void changeItem(Object t) {
        for (int i = 0; i < Profiles.profilesList.get(Profiles.itemIndex).item.size(); i++) {
            for (int j = 0; j < Profiles.profilesList.get(Profiles.itemIndex).item.get(i).item.size(); j++) {
                Date date = Profiles.profilesList.get(Profiles.itemIndex).item.get(i).item.get(j).startDate;
                if (date == t) {
                    Log.d(tag, "Change " + date);
                    TimeChangeDialog pickerFragment = new TimeChangeDialog();
                    pickerFragment.item = Profiles.profilesList.get(Profiles.itemIndex).item.get(i).item.get(j);
                    pickerFragment.header = Profiles.profilesList.get(Profiles.itemIndex).item.get(i).header;
                    pickerFragment.type = Profiles.profilesList.get(Profiles.itemIndex).item.get(i).type;
                    pickerFragment.showTimeChangeDialog();
                    break;
                }
            }
        }
    }

    private static void deleteItem(Object t) {
        for (int i = 0; i < Profiles.profilesList.get(Profiles.itemIndex).item.size(); i++) {
            for (int j = 0; j < Profiles.profilesList.get(Profiles.itemIndex).item.get(i).item.size(); j++) {
                Date date = Profiles.profilesList.get(Profiles.itemIndex).item.get(i).item.get(j).startDate;
                if (date == t) {
                    for (int k = 0; k < log.size(); k ++) {
                        if (log.get(k).startDate == Profiles.profilesList.get(Profiles.itemIndex).item.get(i).item.get(j).startDate)
                            log.remove(k);
                    }
                    Profiles.profilesList.get(Profiles.itemIndex).item.get(i).item.remove(j);
                    setData();
                    TabResultsFragment.setData();
                    TabEventsFragment.setData();
                    Profiles.saveProfiles();
                    break;
                }
            }
        }
    }

    private static String getContinuity(Date startDate, Date endDate) {
        long totalSecs = (endDate.getTime() - startDate.getTime()) / 1000;

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

    private static class LogBase {
        public String interval, header, note, continuity;
        public Date startDate;
        public int type;

        public LogBase(String header, String note, String continuity, Date startDate, Date stopDate, int type) {
            this.header = header;
            this.note = note;
            this.continuity = continuity;
            this.startDate = startDate;
            this.type = type;

            String startDateString = new SimpleDateFormat("dd.MM.yyyy").format(startDate).equals(new SimpleDateFormat("dd.MM.yyyy").format(new Date())) ?
                    new SimpleDateFormat("HH:mm").format(startDate) : new SimpleDateFormat("dd.MM.yyyy HH:mm").format(startDate);

            if (stopDate == null) this.interval = startDateString;
            else {
                String stopDateString = new SimpleDateFormat("dd.MM.yyyy").format(stopDate).equals(new SimpleDateFormat("dd.MM.yyyy").format(new Date())) ?
                        new SimpleDateFormat("HH:mm").format(stopDate) : new SimpleDateFormat("dd.MM.yyyy HH:mm").format(stopDate);
                this.interval = startDateString + " - " + stopDateString;
            }
        }
    }

    //Сортировщик
    private static class LogBaseComparator implements Comparator<LogBase> {
        @Override
        public int compare(LogBase o1, LogBase o2) {
            return o1.startDate.compareTo(o2.startDate);
        }
    }

    private static class ItemProfileListItemComparator implements Comparator<ItemProfileListItem> {
        @Override
        public int compare(ItemProfileListItem o1, ItemProfileListItem o2) {
            return o1.startDate.compareTo(o2.startDate);
        }
    }

    public static class TimeChangeDialog implements View.OnClickListener {

        final String tag = "EventLogger";
        Date startDate, stopDate;
        public String header;
        public int type;
        public ItemProfileListItem item;
        EditText noteText, startDateText, stopDateText, startTimeText, stopTimeText;
        Calendar cStart, cStop;

        public void showTimeChangeDialog() {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings.activity);
            LayoutInflater inflater = Settings.activity.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.change_event, null);
            dialogBuilder.setView(dialogView);

            noteText = (EditText) dialogView.findViewById(R.id.noteText);
            startDateText = (EditText) dialogView.findViewById(R.id.startDateText);
            stopDateText = (EditText) dialogView.findViewById(R.id.stopDateText);
            startTimeText = (EditText) dialogView.findViewById(R.id.startTimeText);
            stopTimeText = (EditText) dialogView.findViewById(R.id.stopTimeText);

            startDateText.setFocusable(false);
            startDateText.setClickable(true);
            stopDateText.setFocusable(false);
            stopDateText.setClickable(true);
            startTimeText.setFocusable(false);
            startTimeText.setClickable(true);
            stopTimeText.setFocusable(false);
            stopTimeText.setClickable(true);

            startDate = item.startDate;
            stopDate = item.endDate;

            noteText.setText(item.note);
            if (type == 1) {
                noteText.setVisibility(View.GONE);
                dialogView.findViewById(R.id.stopDateLayout).setVisibility(View.GONE);
            }
            if (stopDate == null) dialogView.findViewById(R.id.stopDateLayout).setVisibility(View.GONE);

            cStart = Calendar.getInstance();
            if (startDate != null) cStart.setTime(startDate);
            cStop = Calendar.getInstance();
            if (stopDate != null) cStop.setTime(stopDate);

            startDateText.setOnClickListener(this);
            stopDateText.setOnClickListener(this);
            startTimeText.setOnClickListener(this);
            stopTimeText.setOnClickListener(this);

            updateDateText(startDateText, item.startDate);
            if (stopDate != null) updateDateText(stopDateText, item.endDate);
            updateTimeText(startTimeText, item.startDate);
            if (stopDate != null) updateTimeText(stopTimeText, item.endDate);

            dialogBuilder.setTitle(header);
            dialogBuilder.setPositiveButton(Settings.activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    item.note = noteText.getText().toString();
                    if (stopDate == null) item.startDate = startDate;
                    else {
                        if (startDate.getTime() < stopDate.getTime()) {
                            item.startDate = startDate;
                            item.endDate = stopDate;
                        }
                        else {
                            item.startDate = stopDate;
                            item.endDate = startDate;
                        }
                    }

                    setData();
                    TabEventsFragment.setData();
                    Profiles.saveProfiles();
                }
            });
            dialogBuilder.setNegativeButton(Settings.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startDateText:
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            cStart.set(Calendar.YEAR, year);
                            cStart.set(Calendar.MONTH, monthOfYear);
                            cStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateDateText(startDateText, cStart.getTime());
                        }

                    };
                    new DatePickerDialog(inflater.getContext(), date, cStart.get(Calendar.YEAR), cStart.get(Calendar.MONTH),
                            cStart.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                case R.id.startTimeText:
                    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            cStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            cStart.set(Calendar.MINUTE, minute);
                            updateTimeText(startTimeText, cStart.getTime());
                        }

                    };
                    new TimePickerDialog(inflater.getContext(), time, cStart.get(Calendar.HOUR_OF_DAY), cStart.get(Calendar.MINUTE),
                            DateFormat.is24HourFormat(Settings.activity)).show();
                    break;
                case R.id.stopDateText:
                    DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            cStop.set(Calendar.YEAR, year);
                            cStop.set(Calendar.MONTH, monthOfYear);
                            cStop.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateDateText(stopDateText, cStop.getTime());
                        }

                    };
                    new DatePickerDialog(inflater.getContext(), date1, cStop.get(Calendar.YEAR), cStop.get(Calendar.MONTH),
                            cStop.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                case R.id.stopTimeText:
                    TimePickerDialog.OnTimeSetListener time1 = new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            cStop.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            cStop.set(Calendar.MINUTE, minute);
                            updateTimeText(stopTimeText, cStop.getTime());
                        }

                    };
                    new TimePickerDialog(inflater.getContext(), time1, cStop.get(Calendar.HOUR_OF_DAY), cStop.get(Calendar.MINUTE),
                            DateFormat.is24HourFormat(Settings.activity)).show();
                    break;
            }
        }

        private void updateDateText(EditText text, Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            text.setText(sdf.format(date));
            if (text.getId() == R.id.startDateText) startDate = date;
            else stopDate = date;
        }

        private void updateTimeText(EditText text, Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            text.setText(sdf.format(date));
            if (text.getId() == R.id.startTimeText) startDate = date;
            else stopDate = date;
        }
    }
}
