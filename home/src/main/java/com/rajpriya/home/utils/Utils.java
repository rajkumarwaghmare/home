package com.rajpriya.home.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.rajpriya.home.InstalledAppsFragment;
import com.rajpriya.home.R;

import java.io.File;
import java.util.List;

/**
 * Created by rajkumar on 3/8/14.
 */
public class Utils {

    public interface AppUninstall {
        public void onAppDeleted();
    }

    public static void launchAppDetails(final Context context, final String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        context.startActivity(intent);

    }


    public static void launchApp(final Context context, final String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
        }

    }


    public static void deleteApp(final Context context, final String packageName, InstalledAppsFragment caller) {
        Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:"+packageName));
        ((Activity)context).startActivityForResult(intent, 200);
    }


    public static void showActionDialog(final Context context, final String packageName, final InstalledAppsFragment caller) {
        final AlertDialog levelDialog;
        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] items = { " Share this App ", " Launch  ", " View in Google Play ", " View Details "," Uninstall from Device ",};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Action");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 1:
                        // Your code when first option seletced
                        Utils.launchApp(context, packageName);
                        break;
                    case 3:
                        // Your code when 2nd  option seletced
                        Utils.launchAppDetails(context, packageName);
                        break;
                    case 4:
                        // Your code when 3rd option seletced
                        Utils.deleteApp(context, packageName, caller);
                        break;
                    case 0:
                        // Your code when 4th  option seletced
                        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (btAdapter == null) {
                            // Device does not support Bluetooth
                            // Inform user that we're done.
                            Toast.makeText(context, "Your device does not support Bluetooth!", Toast.LENGTH_LONG).show();
                            break;
                        }
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("application/zip");

                       //list of apps that can handle our intent
                      PackageManager pm = context.getPackageManager();
                      List<ResolveInfo> appsList = pm.queryIntentActivities( intent, 0);

                     /* if(appsList.size() > 0) {
                        // proceed
                        //select bluetooth
                        String packageName = null;
                        String className = null;
                        boolean found = false;

                        for(ResolveInfo info: appsList){
                            packageName = info.activityInfo.packageName;
                            if( packageName.equals("com.android.bluetooth")){
                                className = info.activityInfo.name;
                                found = true;
                                break;// found
                            }
                        }
                        if(! found){
                            Toast.makeText(context, "Bluetooth package not found", Toast.LENGTH_SHORT).show();
                            //break;
                        }
                          intent.setClassName(packageName, className);
                       }*/

                    ApplicationInfo app = null;
                        try {
                            app = (context.getPackageManager()).getApplicationInfo(packageName, 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (app != null ) {
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(app.publicSourceDir)) );
                            context.startActivity(intent);
                        }
                        break;
                    case 2:
                        final String appPackageName = packageName; // getPackageName() from Context or Activity object
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        break;

                }
                dialog.dismiss();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();
    }


    /** Check if the application is uninstalled from System */
    public static boolean  isAppPresent(String packageName,Context context) {
        try{
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0 );
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
    }
}
