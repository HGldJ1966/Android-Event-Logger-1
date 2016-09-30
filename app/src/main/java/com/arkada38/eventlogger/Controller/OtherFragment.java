package com.arkada38.eventlogger.Controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.arkada38.eventlogger.Model.Settings;
import com.arkada38.eventlogger.R;

public class OtherFragment extends Fragment implements View.OnClickListener {

    Button buttonFeedback, buttonEmail, buttonRestore;
    String tag = "EventLogger";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.other_layout, null);

        buttonFeedback = (Button) v.findViewById(R.id.buttonFeedback);
        buttonEmail = (Button) v.findViewById(R.id.buttonEmail);
        buttonRestore = (Button) v.findViewById(R.id.buttonRestore);

        buttonFeedback.setOnClickListener(this);
        buttonEmail.setOnClickListener(this);
        buttonRestore.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFeedback:
                Log.d(tag, "buttonFeedback");
                final String appPackageName = Settings.activity.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.buttonEmail:
                Log.d(tag, "buttonEmail");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","arkada38@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EventLogger");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send Email by EventLogger"));

                break;
            case R.id.buttonRestore:
                Log.d(tag, "buttonRestore");
                Activation.restored();
                if (Settings.getAccess())
                    Toast.makeText(Settings.activity, R.string.successfully_restored, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Settings.activity, R.string.fails_restored, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
