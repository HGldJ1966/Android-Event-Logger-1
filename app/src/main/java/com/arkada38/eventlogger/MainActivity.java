package com.arkada38.eventlogger;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arkada38.eventlogger.Controller.AboutFragment;
import com.arkada38.eventlogger.Controller.Activation;
import com.arkada38.eventlogger.Controller.Item;
import com.arkada38.eventlogger.Controller.OtherFragment;
import com.arkada38.eventlogger.Controller.Tabs.TabLogsFragment;
import com.arkada38.eventlogger.Model.Profile.ItemProfileList;
import com.arkada38.eventlogger.Model.Profile.ProfileItemList;
import com.arkada38.eventlogger.Model.Profile.Profiles;
import com.arkada38.eventlogger.Controller.Tabs.TabFragment;
import com.arkada38.eventlogger.Model.Settings;

public class MainActivity extends AppCompatActivity{
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    android.support.v7.widget.Toolbar toolbar;
    String tag = "EventLogger";
    Menu m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Инициализация вкладок и бокового меню
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        mFragmentManager = getSupportFragmentManager();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.settings) {
                    toolbar.setTitle(R.string.other);
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new OtherFragment()).commit();
                    m.setGroupVisible(R.id.group_profile, false);
                }

                else if (menuItem.getItemId() == R.id.help) {
                    toolbar.setTitle(R.string.about);
                    FragmentTransaction FragmentTransaction = mFragmentManager.beginTransaction();
                    FragmentTransaction.replace(R.id.containerView, new AboutFragment()).commit();
                    m.setGroupVisible(R.id.group_profile, false);
                }

                else if (menuItem.getItemId() == R.id.add) {
                    if (Profiles.profilesList.size() < 1 || Settings.getAccess()) {
                        final ProfileItemList profileItemList = new ProfileItemList();

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings.activity);
                        LayoutInflater inflater = Settings.activity.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.input_dialog, null);
                        dialogBuilder.setView(dialogView);

                        final EditText header = (EditText) dialogView.findViewById(R.id.header);

                        dialogBuilder.setTitle(getString(R.string.add_profile));
                        dialogBuilder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (header.getText().length() > 0) {
                                    boolean b = true;
                                    for (int i = 0; i < Profiles.profilesList.size(); i++) {
                                        if (Profiles.profilesList.get(i).header.equals(header.getText().toString())) {
                                            b = false;
                                            Toast.makeText(Settings.activity, R.string.name_is_busy, Toast.LENGTH_LONG).show();
                                            break;
                                        }
                                    }
                                    if (b) {
                                        profileItemList.header = header.getText().toString();
                                        profileItemList.item.add(new ItemProfileList(getString(R.string.event)));
                                        Profiles.profilesList.add(profileItemList);

                                        toolbar.setTitle(getString(R.string.app_name) + " (" + header.getText().toString() + ")");
                                        initProfiles(mNavigationView.getMenu().getItem(0).getSubMenu());
                                        onMenuItemProfileClick(mNavigationView.getMenu().getItem(0).getSubMenu().getItem(Profiles.profilesList.size() - 1));
                                        Profiles.saveProfiles();
                                    }
                                } else
                                    Toast.makeText(Settings.activity, R.string.name_empty, Toast.LENGTH_LONG).show();
                            }
                        });
                        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                        AlertDialog b = dialogBuilder.create();
                        b.show();
                    }
                    else Activation.initMessage();
                }

                else {
                    onMenuItemProfileClick(menuItem);
                }

                return false;
            }

        });
        //endregion

        Settings.activity = this;
        Activation.a = new Activation();
        Item.fragmentManager = getSupportFragmentManager();
        Item.context = this;
        TabLogsFragment.fragmentManager = getSupportFragmentManager();
        Profiles.context = this;
        Profiles.initProfiles();
        SubMenu profilesMenu = mNavigationView.getMenu().getItem(0).getSubMenu();

        initProfiles(profilesMenu);

        if (Profiles.profilesList.size() > 0) onMenuItemProfileClick(profilesMenu.getItem(Profiles.itemIndex));
        else onMenuItemProfileClick(null);

        Activation.restored();

    }

    private void initProfiles(SubMenu profilesMenu) {
        profilesMenu.clear();
        for (int i = 0; i < Profiles.profilesList.size(); i++) {
            profilesMenu.add(Profiles.profilesList.get(i).header).setIcon(R.drawable.sent);
        }
    }

    private void onMenuItemProfileClick(MenuItem menuItem) {
        if (menuItem != null) {
            toolbar.setTitle(getString(R.string.app_name) + " (" + menuItem.getTitle().toString() + ")");
            Profiles.setItemIndex(menuItem.getTitle().toString());

            FragmentTransaction FragmentTransaction = mFragmentManager.beginTransaction();
            FragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
            if (m != null) m.setGroupVisible(R.id.group_profile, true);
        }
        else {
            toolbar.setTitle(R.string.other);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, new OtherFragment()).commit();
            if (m != null) m.setGroupVisible(R.id.group_profile, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        m = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.renameProfile:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.input_dialog, null);
                dialogBuilder.setView(dialogView);

                final EditText header = (EditText) dialogView.findViewById(R.id.header);
                header.setText(Profiles.profilesList.get(Profiles.itemIndex).header);

                dialogBuilder.setTitle(getString(R.string.rename_profile));
                dialogBuilder.setPositiveButton(getString(R.string.rename), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (header.getText().length() > 0) {
                            boolean b = true;
                            for (int i = 0; i < Profiles.profilesList.size(); i++) {
                                if (Profiles.profilesList.get(i).header.equals(header.getText().toString())) {
                                    b = false;
                                    Toast.makeText(Settings.activity, R.string.name_is_busy, Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
                            if (b) {
                                Profiles.profilesList.get(Profiles.itemIndex).header = header.getText().toString();
                                toolbar.setTitle(getString(R.string.app_name) + " (" + header.getText().toString() + ")");
                                initProfiles(mNavigationView.getMenu().getItem(0).getSubMenu());
                                Profiles.saveProfiles();
                            }
                        }
                        else Toast.makeText(Settings.activity, R.string.name_empty, Toast.LENGTH_LONG).show();
                    }
                });
                dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
                return true;
            case R.id.deleteProfile:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.delete_profile) + " " + Profiles.profilesList.get(Profiles.itemIndex).header + "?")
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Profiles.profilesList.remove(Profiles.itemIndex);
                                Profiles.itemIndex = 0;
                                Profiles.saveProfiles();

                                initProfiles(mNavigationView.getMenu().getItem(0).getSubMenu());
                                if (Profiles.profilesList.size() > 0) onMenuItemProfileClick(mNavigationView.getMenu().getItem(0).getSubMenu().getItem(Profiles.itemIndex));
                                else onMenuItemProfileClick(null);
                                //TODO При удалении всех профилей не обновляется боковое меню
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
                return true;
            case R.id.settings:
                toolbar.setTitle(R.string.other);
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new OtherFragment()).commit();
                m.setGroupVisible(R.id.group_profile, false);
                return true;
            case R.id.about:
                toolbar.setTitle(R.string.about);
                FragmentTransaction FragmentTransaction = mFragmentManager.beginTransaction();
                FragmentTransaction.replace(R.id.containerView, new AboutFragment()).commit();
                m.setGroupVisible(R.id.group_profile, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    @Override
    public void onResume() {
        Log.d(tag, "onResume");
        if (Settings.waitingPayment) {
            Activation.bp.purchase(this, "a");
            Settings.waitingPayment = false;
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (Activation.bp != null)
            Activation.bp.release();

        super.onDestroy();
    }
}