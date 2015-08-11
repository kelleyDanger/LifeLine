package com.kelleyscanlon;


import android.app.FragmentManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    View view;

    // key-value pair passed values between activities
    public static final String ROW_ID = "row_id";
    private ListView friendListView;
    // adapter for populating the ListView
    private CursorAdapter friendAdapter;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        friendListView = (ListView) view.findViewById(R.id.listView);
        friendListView.setOnItemClickListener(viewFriendListener);
        friendListView.setBackgroundColor(Color.BLACK);

        // display message on an empty list
        TextView emptyText = (TextView)View.inflate(view.getContext(), R.layout.friends_list_empty, null);
        emptyText.setVisibility(View.GONE); // will be automatically toggled by listview
        ((ViewGroup)friendListView.getParent()).addView(emptyText);
        friendListView.setEmptyView(emptyText);
        // end of display empty list code


        // map each friend name to a TextView in the ListView layout
        String[] from = new String[]{"nickname"};
        int[] to = new int[]{R.id.friendTextView};
        friendAdapter = new SimpleCursorAdapter(view.getContext(), R.layout.friend_list_item, null, from, to, 0); // 0 = start at first row

        friendListView.setAdapter(friendAdapter);

        //create a new GetContactTask and execute
        new GetFriendsTask().execute((Object[])null);

        return view;
    }

    //perform the database query outside the GUI Thread
    private class GetFriendsTask extends AsyncTask<Object, Object, Cursor> {

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        //perform the database access
        @Override // from AsyncTask Class
        protected Cursor doInBackground(Object... params){
            databaseConnector.open();

            //get cursor containing all friends
            return databaseConnector.getAllFriends();
        }

        // use the cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result){
            friendAdapter.changeCursor(result); //set the adapters cursor
            databaseConnector.close(); // close connection
        }

    }

    AdapterView.OnItemClickListener viewFriendListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Fragment fragment = new EditFriendFragment();

            Bundle args = new Bundle();
            args.putLong("ROW_ID", id);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

//            // create and Intent to launch the EditFriendFragment
//            Intent editFriend = new Intent(view.getContext(), EditFriendFragment.class);
//
//            //pass the selected friend row ID as extra with the Intent
//            editFriend.putExtra(ROW_ID, id);
//            startActivity(editFriend);
        }
    }; //end viewFriendListener

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // launch EditFriend fragment

        Fragment fragment = new EditFriendFragment();

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

//            mDrawerList.setItemChecked(position, true);
//            mDrawerList.setSelection(position);
//            setTitle(mNavigationDrawerItemTitles[position]);
//            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }

//        Intent addNewContact = new Intent(view.getContext(), EditFriend.class); // context to current activity, new activity class
//        startActivity(addNewContact); // this starts the activity

        return super.onOptionsItemSelected(item);
    }


}
