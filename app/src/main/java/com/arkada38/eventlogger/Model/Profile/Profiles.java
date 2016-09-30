package com.arkada38.eventlogger.Model.Profile;

import android.content.Context;
import android.util.Log;

import com.arkada38.eventlogger.R;
import com.arkada38.eventlogger.Util.InternalStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Profiles {
    static public List<ProfileItemList> profilesList = new ArrayList<>();
    static public int itemIndex = 0;
    public static Context context;
    static String tag = "EventLogger";

    static public void initProfiles() {
        try {
            profilesList = (List<ProfileItemList>) InternalStorage.readObject(context, "profiles.dat");
        } catch (IOException | ClassNotFoundException e) {
            Log.e(tag, e.getMessage());
            setDefaultProfiles();
        }
    }

    static public void saveProfiles() {
        try {
            InternalStorage.writeObject(context, "profiles.dat", profilesList);
        } catch (IOException e) {
            Log.e(tag, e.getMessage());
        }
    }

    static public void setDefaultProfiles() {
        profilesList = new ArrayList<>();

        //region Младенец
        ProfileItemList profileItemList = new ProfileItemList();
        profileItemList.header = context.getString(R.string.baby);
        profileItemList.item.add(new ItemProfileList(context.getString(R.string.dream), context.getString(R.string.wake)));
        profileItemList.item.add(new ItemProfileList(context.getString(R.string.feeding)));
        profileItemList.item.add(new ItemProfileList(context.getString(R.string.pu_pu), 1));
        profileItemList.item.add(new ItemProfileList(context.getString(R.string.pi_pi), 1));
        profileItemList.item.add(new ItemProfileList(context.getString(R.string.walking)));

        profilesList.add(profileItemList);
        //endregion

        saveProfiles();
    }

    static public void setItemIndex(String title) {
        for (int i = 0; i < profilesList.size(); i++) {
            if (profilesList.get(i).header.equals(title)) {
                itemIndex = i;
                break;
            }
        }
    }
}
