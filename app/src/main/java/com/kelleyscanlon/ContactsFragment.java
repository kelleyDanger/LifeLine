package com.kelleyscanlon;


import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    View view;

    // key-value pair passed values between activities
    public static final String ROW_ID = "row_id";
    private ListView contactsListView;
    private Button addNewContactButton;
    // adapter for populating the ListView
    private CursorAdapter ContactAdapter;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // listener for Add New Contact Button
        addNewContactButton = (Button) view.findViewById(R.id.addNewContactButton);
        addNewContactButton.setOnClickListener(AddContactListener);

        contactsListView = (ListView) view.findViewById(R.id.contactsListView);
        //contactsListView.setOnItemClickListener(viewContactListener);
        contactsListView.setBackgroundColor(Color.BLACK);


        // map each contact name to a TextView in the ContactListView layout
        String[] from = new String[]{"name"};
        int[] to = new int[]{R.id.contactTextView};
        ContactAdapter = new ContactAdapter(view.getContext(), R.layout.contacts_list_item, null, from, to, 0); // 0 = start at first row

        contactsListView.setAdapter(ContactAdapter);


        //create a new GetAllUserContactsTask and execute
        new GetAllUserContactsTask().execute((Object[])null);

        return view;
    }

    //perform the database query outside the GUI Thread
    private class GetAllUserContactsTask extends AsyncTask<Object, Object, Cursor> {

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        //perform the database access
        @Override // from AsyncTask Class
        protected Cursor doInBackground(Object... params){
            databaseConnector.open();

            Log.d("ALL CONTACTS: ", DatabaseUtils.dumpCursorToString(databaseConnector.getAllUserContacts()));

            //get cursor containing all user contacts
            return databaseConnector.getAllUserContacts();
        }

        // use the cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result){
            ContactAdapter.changeCursor(result); //set the adapters cursor
            databaseConnector.close(); // close connection
        }

    }

    // responds to event generated when Add New Contact button clicked
    View.OnClickListener AddContactListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AsyncTask<Object, Object, Object> saveContactTask =
                    new AsyncTask<Object, Object, Object>()
                    {
                        @Override
                        protected Object doInBackground(Object... params)
                        {
                            //saveContact(); // get all contacts from the database
                            return null;
                        } // end method doInBackground

                        @Override
                        protected void onPostExecute(Object result)
                        {
                            //finish(); // return to the previous Activity
                        } // end method onPostExecute
                    }; // end AsyncTask

            // save the contact to the database using a separate thread
            saveContactTask.execute((Object[]) null);
        } // end method onClick
    }; // end OnClickListener AddContactListener



//    AdapterView.OnItemClickListener viewContactListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Do What: ")
//                    .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // The 'which' argument contains the index position
//                            // of the selected item
//                        }
//                    });
//            return builder.create();
//        }
//    }; //end viewFriendListener



}
