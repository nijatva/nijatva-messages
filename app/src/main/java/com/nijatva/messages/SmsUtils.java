package com.nijatva.messages;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.util.ArrayList;
import java.util.List;

/** SMS helpers: contact names, OTP detection, sending and thread loading. */
public class SmsUtils {

    public static class Thread {
        public long threadId;
        public String address = "";
        public String name = "";
        public String snippet = "";
        public long date;
        public int count;
        public boolean otp;
    }

    public static class Msg {
        public long id;
        public String body = "";
        public long date;
        public int type;
    }

    /** True when the app currently holds the default SMS role. */
    public static boolean isDefaultSms(Context ctx) {
        try {
            String pkg = Telephony.Sms.getDefaultSmsPackage(ctx);
            return pkg != null && pkg.equals(ctx.getPackageName());
        } catch (Exception e) {
            return false;
        }
    }

    /** Looks up a display name for a phone number. */
    public static String contactName(Context ctx, String number) {
        if (number == null || number.trim().length() == 0) return "";
        Cursor c = null;
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            c = ctx.getContentResolver().query(uri,
                    new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (c != null && c.moveToFirst()) {
                String n = c.getString(0);
                return n == null ? "" : n;
            }
        } catch (Exception ignore) {
        } finally {
            if (c != null) c.close();
        }
        return "";
    }

    /** Heuristic: does this message look like a one-time password? */
    public static boolean looksLikeOtp(String body) {
        if (body == null) return false;
        String b = body.toLowerCase();
        boolean keyword = b.contains("otp") || b.contains("one time") || b.contains("verification")
                || b.contains("verify") || b.contains("code") || b.contains("password");
        boolean digits = body.matches(".*\\b\\d{4,8}\\b.*");
        return keyword && digits || (digits && b.length() < 120 && keyword);
    }

    /** Sends a (possibly multipart) SMS. Returns true when the send was accepted. */
    public static boolean send(Context ctx, String address, String body) {
        try {
            SmsManager sm = SmsManager.getDefault();
            ArrayList<String> parts = sm.divideMessage(body);
            if (parts.size() > 1) {
                sm.sendMultipartTextMessage(address, null, parts, null, null);
            } else {
                sm.sendTextMessage(address, null, body, null, null);
            }
            // when we are the default SMS app, record the message in the provider
            if (isDefaultSms(ctx)) {
                ContentValues v = new ContentValues();
                v.put("address", address);
                v.put("body", body);
                v.put("date", System.currentTimeMillis());
                v.put("type", Telephony.Sms.MESSAGE_TYPE_SENT);
                v.put("read", 1);
                v.put("seen", 1);
                try { ctx.getContentResolver().insert(Telephony.Sms.CONTENT_URI, v); } catch (Exception ignore) {}
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Stores an incoming SMS when the app is the default SMS app. */
    public static void storeIncoming(Context ctx, SmsMessage[] messages) {
        if (messages == null) return;
        for (SmsMessage m : messages) {
            if (m == null) continue;
            try {
                ContentValues v = new ContentValues();
                v.put("address", m.getDisplayOriginatingAddress());
                v.put("body", m.getDisplayMessageBody());
                v.put("date", m.getTimestampMillis());
                v.put("type", Telephony.Sms.MESSAGE_TYPE_INBOX);
                v.put("read", 0);
                v.put("seen", 0);
                ctx.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, v);
            } catch (Exception ignore) {}
        }
    }

    /** Loads the most recent message per thread. */
    public static List<Thread> loadThreads(Context ctx) {
        List<Thread> out = new ArrayList<Thread>();
        Cursor c = null;
        try {
            c = ctx.getContentResolver().query(Telephony.Sms.CONTENT_URI,
                    new String[]{"_id", "thread_id", "address", "body", "date", "type"},
                    null, null, "date DESC");
            if (c == null) return out;
            java.util.HashSet<Long> seen = new java.util.HashSet<Long>();
            while (c.moveToNext()) {
                long tid = c.getLong(1);
                if (seen.contains(tid)) continue;
                seen.add(tid);
                Thread t = new Thread();
                t.threadId = tid;
                t.address = c.getString(2);
                t.snippet = c.getString(3);
                t.date = c.getLong(4);
                t.otp = looksLikeOtp(t.snippet);
                t.name = contactName(ctx, t.address);
                out.add(t);
            }
        } catch (Exception ignore) {
        } finally {
            if (c != null) c.close();
        }
        return out;
    }

    /** Loads all messages in one thread, oldest first. */
    public static List<Msg> loadMessages(Context ctx, long threadId) {
        List<Msg> out = new ArrayList<Msg>();
        Cursor c = null;
        try {
            c = ctx.getContentResolver().query(Telephony.Sms.CONTENT_URI,
                    new String[]{"_id", "body", "date", "type"},
                    "thread_id=?", new String[]{String.valueOf(threadId)}, "date ASC");
            if (c == null) return out;
            while (c.moveToNext()) {
                Msg m = new Msg();
                m.id = c.getLong(0);
                m.body = c.getString(1);
                m.date = c.getLong(2);
                m.type = c.getInt(3);
                out.add(m);
            }
        } catch (Exception ignore) {
        } finally {
            if (c != null) c.close();
        }
        return out;
    }
}
