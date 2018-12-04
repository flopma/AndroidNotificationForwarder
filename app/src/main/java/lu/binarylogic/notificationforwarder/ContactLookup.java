package lu.binarylogic.notificationforwarder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ContactLookup {
    private static final String TAG = "ContactLookup";

    public static String lookupContactNameFromNumber(ContentResolver resolver, String number) {
        if (number == null) {
            return "";
        }

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor query = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        try {
            if (query.moveToFirst()) {
                String s = query.getString(0);
                if (s != null) {
                    return s;
                }
            }
        } finally {
            if (query != null) {
                query.close();
            }
        }

        return "";
    }

    public static String formattedPhoneNumber(String number, Context ctx) {
        Log.v(TAG, "formatting number: " + String.valueOf(number));
        TelephonyManager telephonyManager = ctx.getSystemService(TelephonyManager.class);
        String phoneCountry = telephonyManager.getNetworkCountryIso();

        String formattedNumber = PhoneNumberUtils.formatNumber(number, phoneCountry);
        Log.v(TAG, "formatted number: " + String.valueOf(formattedNumber));

        return formattedNumber != null ? formattedNumber : number;
    }
}
