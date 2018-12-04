package lu.binarylogic.notificationforwarder;

import android.app.Notification;
import android.app.NotificationManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MissedCalledNotificationListenerService extends NotificationListenerService {
    private static final String TAG = "MissedCalledNotificationListenerService";

    /**
     * {@inheritDoc}
     * @param sbn
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();

        Log.v(TAG, sbn.toString());
        Log.v(TAG, notification.toString());

        final String notificationKey = sbn.getKey();
        if ("msg".equals(notification.category)
                || notificationKey.contains("MissedCall")) {
            cancelNotification(notificationKey);

            Log.v(TAG, String.format("cancel notification with key %s", notificationKey));
        }
    }
}
