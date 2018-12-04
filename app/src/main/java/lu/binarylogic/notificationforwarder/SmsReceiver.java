package lu.binarylogic.notificationforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smses = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (smses == null || smses.length == 0) {
            return;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        // Sometimes, SmsMessage.mWrappedSmsMessage is null causing NPE when we access
        // the methods on it although the SmsMessage itself is not null. So do this check
        // before we do anything on the parsed SmsMessages.
        for (final SmsMessage sms : smses) {
            try {
                sms.getDisplayMessageBody();
            } catch (NullPointerException e) {
                return;
            }
        }

        String message = buildMessageBodyFromPdus(smses);
        String number = smses[0].getOriginatingAddress();
        String contactName = ContactLookup.lookupContactNameFromNumber(context.getContentResolver(), number);
        String subject = String.format("SMS from %s %s", contactName, ContactLookup.formattedPhoneNumber(number, context));

        SendEmail.send(message, subject, context);
    }

    /**
     * Build up the SMS message body from the SmsMessage array of received SMS
     *
     * @param msgs The SmsMessage array of the received SMS
     * @return The text message body
     */
    private static String buildMessageBodyFromPdus(SmsMessage[] msgs) {
        if (msgs.length == 1) {
            // There is only one part, so grab the body directly.
            return replaceFormFeeds(msgs[0].getDisplayMessageBody());
        } else {
            // Build up the body from the parts.
            StringBuilder body = new StringBuilder();
            for (SmsMessage msg: msgs) {
                // getDisplayMessageBody() can NPE if mWrappedMessage inside is null.
                body.append(msg.getDisplayMessageBody());
            }
            return replaceFormFeeds(body.toString());
        }
    }

    // Some providers send formfeeds in their messages. Convert those formfeeds to newlines.
    private static String replaceFormFeeds(String s) {
        return s == null ? "" : s.replace('\f', '\n');
    }


}
