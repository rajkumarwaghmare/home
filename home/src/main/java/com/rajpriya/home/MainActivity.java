package com.rajpriya.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.gson.Gson;
import com.rajpriya.home.utils.StoredServices;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private ArrayList<String> mUrls = new ArrayList<String>();

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


                    mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


/*    @Override
    public void onNewItemAdded(String url) {
        mUrls.add(url);
        mStoredServices.getUrls().add(url);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String str = gson.toJson(mStoredServices, Services.class);
        sp.edit().putString(PREF_NEWLY_ADDED_SERVICES, str).commit();
    }*/
        @Override
    public void onNavigationDrawerItemSelected(int position) {
        //getSupportActionBar().hide();
/*        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(mUrls.get(position)))
                .commit();*/
            FragmentManager fragmentManager = getSupportFragmentManager();
        switch(position) {
            case 0:
                 fragmentManager.beginTransaction()
                        .replace(R.id.container, WebAppsFragment.newInstance(getString(R.string.drawer_item1)))
                        .commit();
                break;

            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new InstalledAppsFragment())
                        .commit();
                break;
             case 2:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AboutFragment.newInstance())
                        .commit();
                break;
            default:
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.drawer_item1);
                break;
            case 2:
                mTitle = getString(R.string.drawer_item2);
                break;
            case 3:
                mTitle = getString(R.string.drawer_item3);
                break;
            case 4:
                mTitle = getString(R.string.drawer_item4);
                break;
            case 5:
                mTitle = getString(R.string.drawer_item5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.fb_blue)));
        //actionBar.setTitle(mTitle);
    }


    @Override
    public void onBackPressed () {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);
        if (frag instanceof  WebAppsFragment) {
            WebView wv = (WebView)frag.getView().findViewById(R.id.section_label);
            if(wv != null &&  wv.canGoBack()) {
                wv.goBack();
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200){
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            if (frag instanceof  InstalledAppsFragment) {
                ((InstalledAppsFragment) frag).onAppDeleted();
            }
        }

    }


    @Override
    public void onResume () {
        super.onResume();
        if (!getSupportActionBar().isShowing()) {
            getSupportActionBar().show();
            //mNavigationDrawerFragment.setMenuVisibility(true);
        }
//        else
//            super.onResume();


    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.

        // May return null if EasyTracker has not yet been initialized with a property ID.
        Tracker easyTracker = EasyTracker.getInstance(this);
        if (easyTracker != null) {
            // This screen name value will remain set on the tracker and sent with
            // hits until it is set to a new value or to null.
            easyTracker.set(Fields.SCREEN_NAME, "MainActivity");
            easyTracker.send(MapBuilder
                            .createAppView()
                            .build()
            );

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_full_screen) {
            /*FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SettingsFragment.newInstance())
                    .commit();*/
            //startActivity(new Intent(this, InstalledAppsActivity.class));
            if(getSupportActionBar() != null) {
                getSupportActionBar().hide();
            return true;
            } else
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

/*    @Override
    public void onFinishEditDialog(String name, String url) {
        mNavigationDrawerFragment.onFinishEditDialog(name, url);

    }*/


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_ITEM_NAME = "DRAWER_ITEM_NAME";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String name) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_ITEM_NAME, name);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
/*
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            WebView webView = (WebView) rootView.findViewById(R.id.section_label);
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
            webView.loadUrl(getArguments().getString(ARG_URL));
*/
            View rootView = inflater.inflate(R.layout.fragment_installed_apps, container, false);


            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ///TODO
            /*((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));*/
        }

    }

}
