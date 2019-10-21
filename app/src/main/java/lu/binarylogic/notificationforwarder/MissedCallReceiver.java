package lu.binarylogic.notificationforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MissedCallReceiver extends BroadcastReceiver {
    private static final String TAG = "MissedCallReceiver";

    /**
     * {@inheritDoc}
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())
                && TelephonyManager.EXTRA_STATE_RINGING.equals(intent.getStringExtra(TelephonyManager.EXTRA_STATE))) {


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);

            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String contactName = "Unknown";
            String formattedNumber = "Private phone number";

            if (phoneNumber == null) {
                Log.v(TAG, "Calling phone number is unknown");

            } else {
                Log.v(TAG, phoneNumber);

                String number = phoneNumber;
                contactName = ContactLookup.lookupContactNameFromNumber(context.getContentResolver(), number);
                formattedNumber = ContactLookup.formattedPhoneNumber(number, context);
            }


            Log.v(TAG, contactName);
            Log.v(TAG, formattedNumber);

            String subject = String.format("Missed call from %s %s", contactName, formattedNumber);

            SendEmail.send("", subject, context);
        }
    }
}
