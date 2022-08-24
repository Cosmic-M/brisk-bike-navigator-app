package brisk.bike.navigator;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class SharedPreferences {
    private static final String SWITCHER_SINGLE_EXECUTION = "com.development.cosmic_m.navigator";

    public static boolean getSharedPreferenceFlag(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SWITCHER_SINGLE_EXECUTION, false);
    }

    public static void setSharedPreferenceFlag(Context context, boolean flag){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SWITCHER_SINGLE_EXECUTION, flag)
                .apply();
    }
}
