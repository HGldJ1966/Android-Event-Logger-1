package com.arkada38.eventlogger.Controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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

import com.arkada38.eventlogger.Controller.Tabs.TabEventsFragment;
import com.arkada38.eventlogger.Controller.Tabs.TabLogsFragment;
import com.arkada38.eventlogger.Controller.Tabs.TabResultsFragment;
import com.arkada38.eventlogger.Model.Profile.ItemProfileList;
import com.arkada38.eventlogger.Model.Profile.ItemProfileListItem;
import com.arkada38.eventlogger.Model.Profile.Profiles;
import com.arkada38.eventlogger.Model.Settings;
import com.arkada38.eventlogger.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Item {

    public static FragmentManager fragmentManager;
    public static Context context;
    LayoutInflater ltInflater;
    LinearLayout linearLayout;
    public TextView header, note, interval, quantity;
    public ItemProfileList pItem;
    Button action, action1, action2;
    String tag = "EventLogger";

    public Item(LayoutInflater ltInflater, LinearLayout linearLayout, final ItemProfileList pItem) {
        this.ltInflater = ltInflater;
        this.linearLayout = linearLayout;
        this.pItem = pItem;

        View view = pItem.type != 2 ? ltInflater.inflate(R.layout.item, null, false) : ltInflater.inflate(R.layout.switch_item, null, false);
        linearLayout.addView(view);

        header = (TextView) view.findViewById(R.id.header);
        note = (TextView) view.findViewById(R.id.note);
        interval = (TextView) view.findViewById(R.id.interval);
        quantity = (TextView) view.findViewById(R.id.quantity);
        action = (Button) view.findViewById(R.id.action);//Старт Стоп +
        action1 = (Button) view.findViewById(R.id.action1);//Сон
        action2 = (Button) view.findViewById(R.id.action2);//Бодрствование

        if (pItem.type == 1) {
            action.setText("+");
            if (pItem.item.size() > 0)
                setInterval(pItem.item.get(pItem.item.size() - 1).startDate, null);
            else
                interval.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
        }
        else if (pItem.type == 2) {
            action1.setText(pItem.header);
            action2.setText(pItem.alternativeHeader);
            interval.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
            if (pItem.item.size() > 0 && pItem.item.get(pItem.item.size() - 1).startDate != null && pItem.item.get(pItem.item.size() - 1).endDate == null) {
                interval.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(pItem.item.get(pItem.item.size() - 1).startDate));
                setInterval(pItem.item.get(pItem.item.size() - 1).startDate, null);
                interval.setVisibility(View.VISIBLE);
                note.setText(pItem.item.get(pItem.item.size() - 1).note);
                note.setVisibility(View.VISIBLE);

                action1.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                action1.setTextColor(ContextCompat.getColor(context, R.color.white));
                action2.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                action2.setTextColor(ContextCompat.getColor(context, R.color.blue));
            }
            if (pItem.item.size() > 0 && pItem.item.get(pItem.item.size() - 1).startDate != null && pItem.item.get(pItem.item.size() - 1).endDate != null) {
                setInterval(pItem.item.get(pItem.item.size() - 1).endDate, null);
                interval.setVisibility(View.VISIBLE);
                note.setVisibility(View.GONE);

                action1.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                action1.setTextColor(ContextCompat.getColor(context, R.color.blue));
                action2.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                action2.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        }
        else if (pItem.item.size() == 0){
            action.setText(R.string.start);
            interval.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
        }
        else if (pItem.item.get(pItem.item.size() - 1).endDate == null) {
            action.setText(R.string.stop);
            setInterval(pItem.item.get(pItem.item.size() - 1).startDate, null);
            note.setText(pItem.item.get(pItem.item.size() - 1).note);
        }
        else {
            action.setText(R.string.start);
            setInterval(pItem.item.get(pItem.item.size() - 1).startDate, pItem.item.get(pItem.item.size() - 1).endDate);
            note.setText(pItem.item.get(pItem.item.size() - 1).note);
        }

        if (pItem.type != 2) {
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pItem.type == 1) {
                        pItem.item.add(new ItemProfileListItem(new Date()));
                        setInterval(pItem.item.get(pItem.item.size() - 1).startDate, null);
                        interval.setVisibility(View.VISIBLE);
                        setQuantity(pItem);
                        TabLogsFragment.setData();
                        Profiles.saveProfiles();
                    } else {
                        TimeDialog pickerFragment = new TimeDialog();
                        pickerFragment.item = Item.this;
                        pickerFragment.view = v;
                        if (pItem.item.size() > 0)
                            pickerFragment.note = pItem.item.get(pItem.item.size() - 1).note;
                        pickerFragment.createDialog();
                    }
                }
            });
            action.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    contextCall(menu, v);
                }
            });
        }
        else {
            action1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pItem.item.size() == 0 || pItem.item.get(pItem.item.size() - 1).endDate instanceof Date) {
                        TimeDialog pickerFragment = new TimeDialog();
                        pickerFragment.item = Item.this;
                        pickerFragment.view = v;
                        pickerFragment.createDialog();
                    }
                }
            });
            action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pItem.item.size() > 0 && pItem.item.get(pItem.item.size() - 1).endDate == null) {
                        TimeDialog pickerFragment = new TimeDialog();
                        pickerFragment.item = Item.this;
                        pickerFragment.view = v;
                        pickerFragment.alternativeHeader = pItem.alternativeHeader;
                        pickerFragment.createDialog();
                    }
                }
            });

            action1.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    contextCall(menu, v);
                }
            });
            action2.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    contextCall(menu, v);
                }
            });
        }

        header.setText(pItem.header);
        setQuantity(pItem);

        ((TextView) view.findViewById(R.id.quantityTextView)).setText(Settings.activity.getString(R.string.quantity) + " " + Settings.getItems()[Settings.getPeriod()] + ":");

        view.findViewById(R.id.linearLayout).setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                contextCall(menu, v);
            }
        });
    }

    private void contextCall(ContextMenu menu, View v) {
        menu.setHeaderTitle(pItem.header);
        menu.clear();
        menu.add(0, v.getId(), 0, R.string.rename);
        menu.add(0, v.getId(), 1, R.string.delete);

        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getOrder() == 0) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings.activity);
                        LayoutInflater inflater = Settings.activity.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.input_dialog, null);
                        dialogBuilder.setView(dialogView);

                        final EditText header = (EditText) dialogView.findViewById(R.id.header);
                        final EditText alternativeHeader = (EditText) dialogView.findViewById(R.id.alternativeHeader);
                        header.setText(pItem.header);

                        if (pItem.type == 2) {
                            dialogView.findViewById(R.id.input_layout_alternative_header).setVisibility(View.VISIBLE);
                            alternativeHeader.setText(pItem.alternativeHeader);
                        }

                        dialogBuilder.setTitle(Settings.activity.getString(R.string.rename_event));
                        dialogBuilder.setPositiveButton(Settings.activity.getString(R.string.rename), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (header.getText().length() > 0 && (pItem.type != 2 || alternativeHeader.getText().length() > 0)) {
                                    boolean b = true;
                                    if (Profiles.profilesList.size() > 0 && !pItem.header.equals(header.getText().toString()))
                                        for (int i = 0; i < Profiles.profilesList.get(Profiles.itemIndex).item.size(); i++)
                                            if (Profiles.profilesList.get(Profiles.itemIndex).item.get(i).header.equals(header.getText().toString())) {
                                                b = false;
                                                Toast.makeText(Settings.activity, R.string.name_is_busy, Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                    if (b) {
                                        pItem.header = header.getText().toString();
                                        if (pItem.type == 2) pItem.alternativeHeader = alternativeHeader.getText().toString();
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
                    if (item.getOrder() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.activity);
                        builder.setMessage(Settings.activity.getString(R.string.delete_event) + " " + pItem.header + "?")
                                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        for (int i = 0; i < Profiles.profilesList.get(Profiles.itemIndex).item.size(); i++)
                                            if (Profiles.profilesList.get(Profiles.itemIndex).item.get(i) == pItem) {
                                                Profiles.profilesList.get(Profiles.itemIndex).item.remove(i);
                                                break;
                                            }

                                        TabEventsFragment.setData();
                                        TabLogsFragment.setData();
                                        TabResultsFragment.setData();
                                        Profiles.saveProfiles();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                        builder.create().show();
                    }
                    return false;
                }
            });
    }

    private void setQuantity(ItemProfileList pItem) {
        if (TabLogsFragment.fromDate != null) {
            int q = 0;
            for (int j = 0; j < pItem.item.size(); j++) {
                if (pItem.item.get(j).startDate.getTime() > TabLogsFragment.fromDate.getTime() && pItem.item.get(j).startDate.getTime() < new Date().getTime())
                    q++;
            }
            quantity.setText(String.format("%s", q));
        }
    }

    private void setInterval(Date startDate, Date stopDate) {
        String startDateString = new SimpleDateFormat("dd.MM.yyyy").format(startDate).equals(new SimpleDateFormat("dd.MM.yyyy").format(new Date())) ?
                new SimpleDateFormat("HH:mm").format(startDate) : new SimpleDateFormat("dd.MM.yyyy HH:mm").format(startDate);

        if (stopDate == null) interval.setText(startDateString);
        else {
            String stopDateString = new SimpleDateFormat("dd.MM.yyyy").format(stopDate).equals(new SimpleDateFormat("dd.MM.yyyy").format(new Date())) ?
                    new SimpleDateFormat("HH:mm").format(stopDate) : new SimpleDateFormat("dd.MM.yyyy HH:mm").format(stopDate);
            interval.setText(startDateString + " - " + stopDateString);
        }
    }

    private void click(Date date, String note, View v) {
        if (pItem.type != 2) {
            if (action.getText().equals(context.getResources().getString(R.string.start))) {
                pItem.item.add(new ItemProfileListItem());
                pItem.item.get(pItem.item.size() - 1).note = note;
                pItem.item.get(pItem.item.size() - 1).startDate = date;

                setInterval(pItem.item.get(pItem.item.size() - 1).startDate, null);
                interval.setVisibility(View.VISIBLE);
                this.note.setText(note);
                this.note.setVisibility(note != null ? View.VISIBLE : View.GONE);
                action.setText(R.string.stop);

                TabLogsFragment.setData();
            } else {
                if (pItem.item.get(pItem.item.size() - 1).startDate.getTime() > date.getTime()) {
                    pItem.item.get(pItem.item.size() - 1).endDate = pItem.item.get(pItem.item.size() - 1).startDate;
                    pItem.item.get(pItem.item.size() - 1).startDate = date;
                }
                else pItem.item.get(pItem.item.size() - 1).endDate = date;
                pItem.item.get(pItem.item.size() - 1).note = note;

                setInterval(pItem.item.get(pItem.item.size() - 1).startDate, pItem.item.get(pItem.item.size() - 1).endDate);
                interval.setVisibility(View.VISIBLE);
                this.note.setText(note);
                this.note.setVisibility(note != null ? View.VISIBLE : View.GONE);
                action.setText(R.string.start);

                TabLogsFragment.setData();
            }
        }
        else if (v.getId() == R.id.action1) {
            pItem.item.add(new ItemProfileListItem());
            pItem.item.get(pItem.item.size() - 1).note = note;
            pItem.item.get(pItem.item.size() - 1).startDate = date;

            setInterval(pItem.item.get(pItem.item.size() - 1).startDate, null);
            interval.setVisibility(View.VISIBLE);
            this.note.setText(note);
            this.note.setVisibility(View.VISIBLE);

            TabLogsFragment.setData();

            action1.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            action1.setTextColor(ContextCompat.getColor(context, R.color.white));
            action2.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            action2.setTextColor(ContextCompat.getColor(context, R.color.blue));
        }
        else {
            pItem.item.get(pItem.item.size() - 1).endDate = date;

            setInterval(pItem.item.get(pItem.item.size() - 1).endDate, null);
            interval.setVisibility(View.VISIBLE);
            this.note.setVisibility(View.GONE);

            TabLogsFragment.setData();

            action1.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            action1.setTextColor(ContextCompat.getColor(context, R.color.blue));
            action2.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            action2.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        setQuantity(pItem);
        TabLogsFragment.setData();
        TabResultsFragment.setData();

        Profiles.saveProfiles();
    }

    public static class TimeDialog implements View.OnClickListener {

        final String tag = "EventLogger";
        Date date;
        String note, alternativeHeader;
        public Item item;
        public View view;
        EditText noteText, dateText, timeText;
        Calendar c;

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dateText:
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, monthOfYear);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateDateText();
                        }

                    };
                    new DatePickerDialog(context, date, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                case R.id.timeText:
                    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute);
                            updateTimeText();
                        }

                    };
                    new TimePickerDialog(context, time, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                            DateFormat.is24HourFormat(Settings.activity)).show();
                    break;
            }
        }

        private void updateDateText() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            dateText.setText(sdf.format(c.getTime()));
            date = c.getTime();
        }

        private void updateTimeText() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            timeText.setText(sdf.format(c.getTime()));
            date = c.getTime();
        }

        public void createDialog() {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings.activity);
            LayoutInflater inflater = Settings.activity.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.create_event, null);
            dialogBuilder.setView(dialogView);

            dialogBuilder.setTitle(item.header.getText());

            date = new Date();

            noteText = (EditText) dialogView.findViewById(R.id.noteText);
            dateText = (EditText) dialogView.findViewById(R.id.dateText);
            timeText = (EditText) dialogView.findViewById(R.id.timeText);

            dateText.setFocusable(false);
            dateText.setClickable(true);
            timeText.setFocusable(false);
            timeText.setClickable(true);

            if (note == null && view.getId() == R.id.action2) {
                noteText.setVisibility(View.GONE);
                dialogBuilder.setTitle(alternativeHeader);
            }
            else if (note != null) noteText.setText(note);

            c = Calendar.getInstance();

            dateText.setOnClickListener(this);
            timeText.setOnClickListener(this);

            updateDateText();
            updateTimeText();

            dialogBuilder.setPositiveButton(Settings.activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    item.click(date, noteText.getText().toString(), view);
                }
            });
            dialogBuilder.setNegativeButton(Settings.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }
    }
}
