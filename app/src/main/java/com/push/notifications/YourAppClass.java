package com.push.notifications;

/**
 * Created by abhijitk on 9/7/2016.
 */
import android.app.Application;
import android.provider.Settings;
import android.util.Log;

//import com.onesignal.OneSignal;

public class YourAppClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        OneSignal.startInit(this).init();

        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.i("DeviceToken ",""+android_id);
    }
}