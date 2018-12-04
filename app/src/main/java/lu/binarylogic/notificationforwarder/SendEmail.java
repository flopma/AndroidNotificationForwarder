package lu.binarylogic.notificationforwarder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static javax.mail.internet.InternetAddress.parse;

public class SendEmail {
    private static final String TAG = "SendEmail";

    static void send(String message, String subject, Context ctx) {
        Log.d(TAG, message);
        Log.d(TAG, subject);

        Properties mailProps = new Properties();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mailProps.setProperty("mail.smtp.host", prefs.getString("smtp-server", ""));
        mailProps.setProperty("mail.smtp.port", prefs.getString("smtp-port", "0"));
        mailProps.setProperty("mail.smtp.ssl.enable", String.valueOf(prefs.getBoolean("tls-enabled", true)));

        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");

        TelephonyManager telephonyManager = ctx.getSystemService(TelephonyManager.class);
        String phoneCountry = telephonyManager.getNetworkCountryIso();

        Session session = Session.getInstance(mailProps);

        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress("android@phones.com"));
        } catch (MessagingException me) {
            Log.e(TAG, "Error while setting From address to email message", me);
            return;
        }

        String recipient = prefs.getString("recipient", "");
        if (recipient.isEmpty()) {
            return;
        }
        try {
            msg.setRecipients(Message.RecipientType.TO, parse(recipient, true));
        } catch (MessagingException me) {
            Log.e(TAG, "Error while setting To address to email message", me);
            return;
        }

        try {
            msg.setSubject(String.format("From %s : %s", phoneCountry.toUpperCase(), subject));
        } catch (MessagingException me) {
            Log.e(TAG, "Unable to set the subject of the email message", me);
            return;
        }

        try {
            msg.setText(message);
        } catch (MessagingException me) {
            Log.e(TAG, "Unable to set the body of the email message", me);
            return;
        }

        try {
            Transport.send(msg, username, password);
        } catch (MessagingException me) {
            Log.e(TAG, "Error occured while sending the email message", me);
            return;
        }

        Log.v(TAG, "message sent");
    }

}
