package com.kelleyscanlon;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kelleyscanlon.R;

public class ContactAdapter extends SimpleCursorAdapter {

    private Context context;
    private int layout;
    private String name;
    private String text;
    private String call;
    private String email;
    private SmsManager smsManager;


    public ContactAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to);
        this.context = context;
        this.layout = layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =  super.getView(position, convertView, parent);
        return v;
    }

    /**
     * @param   v
     *          The view in which the elements we set up here will be displayed.
     *
     * @param   context
     *          The running context where this ListView adapter will be active.
     *
     * @param   c
     *          The Cursor containing the query results we will display.
     */

    @Override
    public void bindView(View v, Context context, Cursor c) {

        name =  c.getString(c.getColumnIndexOrThrow("name"));
        text = c.getString(c.getColumnIndexOrThrow("text"));
        call = c.getString(c.getColumnIndexOrThrow("call"));
        email = c.getString(c.getColumnIndexOrThrow("email"));

        TextView nameTextView = (TextView) v.findViewById(R.id.contactTextView);
        ImageView textImageView = (ImageView) v.findViewById(R.id.textImageView);
        ImageView callImageView = (ImageView) v.findViewById(R.id.callImageView);
        ImageView emailImageView = (ImageView) v.findViewById(R.id.emailImageView);

        // create SmsManger
        smsManager = SmsManager.getDefault();

        // set name
        nameTextView.setText(name);

        /**
         * Decide if we should display the text icon denoting contact has text message service
         */

        if (text != null && text.length() != 0 && textImageView != null) {
            textImageView.setVisibility(ImageView.VISIBLE);
            textImageView.setOnClickListener(new TextClickListener(name, text));
        }

        /**
         * Decide if we should display the call icon denoting contact has phone service
         */

        if (call != null && call.length() != 0 && callImageView != null) {
            callImageView.setVisibility(ImageView.VISIBLE);
            callImageView.setOnClickListener(new CallClickListener(name, call));
        }

        /**
         * Decide if we should display the email icon denoting contact has email service
         */

        if (email != null && email.length() != 0 && emailImageView != null) {
            emailImageView.setVisibility(ImageView.VISIBLE);
            emailImageView.setOnClickListener(new EmailClickListener(name, email));
        }

    } // end bindView

    private class TextClickListener implements OnClickListener {

        private String text;
        private String name;

        public TextClickListener(String name, String text) {
            this.name = name;
            this.text = text;
        }

        public void onClick(View v) {
            try {
                smsManager.sendTextMessage(text, null, "TESTING 1 2 3", null, null);
                Toast.makeText(v.getContext(), "SMS sent.", Toast.LENGTH_SHORT).show();
            } catch(Exception e) {
                Toast.makeText(v.getContext(), "ERROR: SMS not sent.", Toast.LENGTH_SHORT).show();
            }

        }

        public String getName() {
            return name;
        }

        public String getText() {
            return text;
        }
    }

    private class CallClickListener implements OnClickListener {

        private String call;
        private String name;

        public CallClickListener(String name, String call) {
            this.name = name;
            this.call = call;
        }

        public void onClick(View v) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL); callIntent.setData(Uri.parse("tel:" + getCall()));
                v.getContext().startActivity(callIntent);
            } catch(Exception e) {
                Toast.makeText(v.getContext(), "ERROR: Phone call not sent.", Toast.LENGTH_SHORT).show();
            }
        }

        public String getName() {
            return name;
        }

        public String getCall() {
            return call;
        }
    }

    private class EmailClickListener implements OnClickListener {

        private String email;
        private String name;

        public EmailClickListener(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public void onClick(View v) {
            Log.i("CONTACT NAME: " + getName() + "   FUCK EMAIL: ", getEmail());

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getEmail()});
            i.putExtra(Intent.EXTRA_SUBJECT, "LifeLine Emergency");
            i.putExtra(Intent.EXTRA_TEXT   , "");
            try {
                v.getContext().startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(v.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

} // ContactAdapter




