package com.kelleyscanlon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;

import java.util.Arrays;
import java.util.List;


public class HomeFragment extends Fragment {

    private ImageView imageViewEmergency;
    private ImageView friend1;
    private ImageView friend2;
    private ImageView friend3;
    private ImageView imageViewBlueCircle;
    private Animation animationScale;
    private SmsManager smsManager;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        // load animation
        animationScale = AnimationUtils.loadAnimation(this.getActivity(), R.anim.scale);

        // create SmsManger
        smsManager = SmsManager.getDefault();

        // get references to friend ImageViews and to blue circle
        friend1 = (ImageView) view.findViewById(R.id.circleFriend1);
        friend2 = (ImageView) view.findViewById(R.id.circleFriend2);
        friend3 = (ImageView) view.findViewById(R.id.circleFriend3);
        imageViewBlueCircle = (ImageView) view.findViewById(R.id.blueCircle);

        // set listener on Emergency Button
        imageViewEmergency = (ImageView) view.findViewById(R.id.imageViewHomeEmergency);
        imageViewEmergency.setOnClickListener(emergencyListener);

        // set listener on friend ImageViews
        friend1.setOnClickListener(friendListener);
        friend2.setOnClickListener(friendListener);
        friend3.setOnClickListener(friendListener);

        return view;
    }

    OnClickListener emergencyListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("OnClick: ", "clicked!");
            Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show();

            imageViewBlueCircle.setAnimation(animationScale);
            imageViewEmergency.startAnimation(animationScale);
        }
    };

    OnClickListener friendListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // variables
            String phone = "";
            String defaultEmergencyText = "";

            // create database connector
            DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
            databaseConnector.open();

            switch(v.getId()) {
                case R.id.circleFriend1:

                    Log.d("FRIEND 1: ", DatabaseUtils.dumpCursorToString(databaseConnector.getFriend(1)));

                    // get data on Friend 0
                    Cursor result = databaseConnector.getFriend(1);

                    //move to first item
                    result.moveToFirst();
                    // set each data item
                     phone = result.getString(result.getColumnIndex("phone"));
                     defaultEmergencyText = result.getString(result.getColumnIndex("defaultEmergencyText"));

                    Log.i("FRIEND DATA: ", "PHONE: " + phone + "   DEF TEXT: " + defaultEmergencyText);

                    result.close(); // close the result cursor
                    databaseConnector.close(); // close database connection

                    break;
                default:
                    // nothing...
                    break;
            }

            // create custom list view dialog
            createCustomListViewDialog(v, phone, defaultEmergencyText);
        }// end OnClick
    }; // end friendListener

    public void createCustomListViewDialog(View v, final String phone, final String defaultEmergencyText) {

        final String [] items = new String[] {"Text", "Call", "Fake Call"};
        final Integer[] icons = new Integer[] {
                R.drawable.ic_communication_message_black,
                R.drawable.ic_communication_call_black,
                R.drawable.ic_communication_fake_call};
        ListAdapter adapter = new ArrayAdapterWithIcon(getActivity(), items, icons);

        new AlertDialog.Builder(getActivity()).setTitle("Select Action").setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item ) {
                        Log.i("ITEM: ", Integer.toString(item));
                        switch(item) {
                            case 0:
                                // send text message
                                try {
                                    smsManager.sendTextMessage(phone, null, defaultEmergencyText, null, null);
                                    Toast.makeText(getActivity(), "SMS sent.", Toast.LENGTH_SHORT).show();
                                } catch(Exception e) {
                                    Toast.makeText(getActivity(), "ERROR: SMS not sent.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1:
                                // make phone call
                                try {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL); callIntent.setData(Uri.parse("tel:" + phone));
                                    getActivity().startActivity(callIntent);
                                } catch(Exception e) {
                                    Toast.makeText(getActivity(), "ERROR: Phone call not sent.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2:
                                // make fake call
                                //create a list of the possible alarm time choices
                                final String[] alarmChoices =
                                        getResources().getStringArray(R.array.fake_call_alarm_items_array);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Set Fake Call Time: ")
                                        .setItems(R.array.fake_call_alarm_items_array, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                String alarm = alarmChoices[which];
                                                Log.i("WHICH ALARM: ", alarm);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                                break;
                            default:
                                // nothing...
                        }
                    }
                }).show();
    } // end createCustomListViewDialog

    private class ArrayAdapterWithIcon extends ArrayAdapter<String> {

        private List<Integer> images;

        public ArrayAdapterWithIcon(Context context, String[] items, Integer[] images) {
            super(context, android.R.layout.select_dialog_item, items);
            this.images = Arrays.asList(images);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setCompoundDrawablesWithIntrinsicBounds(images.get(position), 0, 0, 0);
            textView.setCompoundDrawablePadding(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
            return view;
        }

    } // end ArrayAdapterWithIcon

}
