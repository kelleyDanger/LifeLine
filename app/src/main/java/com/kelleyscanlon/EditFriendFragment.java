package com.kelleyscanlon;


import android.app.FragmentManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class EditFriendFragment extends Fragment {

    private long rowID; // selected friend's nickname

    private Fragment currentFragment;

    // EditTexts for friend information
    private EditText nicknameEditText; // displays friend's nickname
    private EditText phoneEditText; // displays friend's phone
    private EditText defaultEmergencyTextEditText; // displays friend's default emergency text


    public EditFriendFragment() {
        // Required empty public constructor
        currentFragment = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_friend, container, false);

        // get the EditTexts
        nicknameEditText = (EditText) view.findViewById(R.id.nicknameEditText);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        defaultEmergencyTextEditText = (EditText) view.findViewById(R.id.defaultEmergencyTextEditText);

        // get the selected friends's row ID
        Bundle bundle = this.getArguments();
        rowID = bundle.getLong("ROW_ID", 0);

        //create a new LoadFriendTask and execute
        new LoadFriendTask().execute(rowID);

        // set event listener for the Save Friend Button
        Button saveFriendButton = (Button) view.findViewById(R.id.saveFriendButton);
        saveFriendButton.setOnClickListener(saveFriendListener);

        return view;
    } // end onCreate

    // performs database query outside GUI thread
    private class LoadFriendTask extends AsyncTask<Long, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getActivity());

        // perform the database access
        @Override
        protected Cursor doInBackground(Long... params)
        {
            databaseConnector.open();

            // get a cursor containing all data on given entry
            return databaseConnector.getFriend(params[0]);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            int nicknameIndex = result.getColumnIndex("nickname");
            int phoneIndex = result.getColumnIndex("phone");
            int defaultEmergencyTextIndex = result.getColumnIndex("defaultEmergencyText");

            // fill TextViews with the retrieved data
            nicknameEditText.setText(result.getString(nicknameIndex));
            phoneEditText.setText(result.getString(phoneIndex));
            defaultEmergencyTextEditText.setText(result.getString(defaultEmergencyTextIndex));

            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    } // end class LoadFriendTask

    View.OnClickListener saveFriendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AsyncTask<Object, Object, Object> saveFriendTask =
                    new AsyncTask<Object, Object, Object>()
                    {
                        @Override
                        protected Object doInBackground(Object... params)
                        {
                            saveFriend(); // save friend to the database

                            Log.i("SAVE FRIEND", "rowID: " + rowID);
                            return null;
                        } // end method doInBackground

                        @Override
                        protected void onPostExecute(Object result)
                        {
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.popBackStack(); // return to the previous fragment
                        } // end method onPostExecute
                    }; // end AsyncTask

            // save the friend to the database using a separate thread
            saveFriendTask.execute((Object[]) null);
        }
    }; //end saveFriendListener

    // saves friend information to the database
    private void saveFriend() {

        // get DatabaseConnector to interact with the SQLite database
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        // update the friend information in the database
        databaseConnector.updateFriend(rowID,
                nicknameEditText.getText().toString(),
                phoneEditText.getText().toString(),
                defaultEmergencyTextEditText.getText().toString());

    } // end saveFriend


}
