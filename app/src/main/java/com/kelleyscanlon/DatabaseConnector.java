package com.kelleyscanlon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector
{
    // database name
    private static final String DATABASE_NAME = "LifeLine";
    private SQLiteDatabase database; // database object
    private DatabaseOpenHelper databaseOpenHelper; // database helper

    // public constructor for DatabaseConnector
    public DatabaseConnector(Context context)
    {
        // create a new DatabaseOpenHelper
        databaseOpenHelper =
                new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    } // end DatabaseConnector constructor

    // open the database connection
    public void open() throws SQLException
    {
        // create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    } // end method open

    // close the database connection
    public void close()
    {
        if (database != null)
            database.close(); // close the database connection
    } // end method close

    // inserts a new friend in the database
    public void insertFriend(String nickname, String phone, String defaultEmergencyText)
    {
        ContentValues newFriend = new ContentValues();
        newFriend.put("nickname", nickname);
        newFriend.put("phone", phone);
        newFriend.put("defaultEmergencyText", defaultEmergencyText);

        open(); // open the database
        database.insert("friends", null, newFriend);
        close(); // close the database
    } // end method insertFriend

    // updates a friend in the database
    public void updateFriend(long id, String nickname, String phone, String defaultEmergencyText)
    {
        ContentValues editFriend = new ContentValues();
        editFriend.put("nickname", nickname);
        editFriend.put("phone", phone);
        editFriend.put("defaultEmergencyText", defaultEmergencyText);

        open(); // open the database
        database.update("friends", editFriend, "_id=" + id, null);
        close(); // close the database
    } // end method updateFriend

    // return a Cursor with all friend information in the database
    public Cursor getAllFriends()
    {
        // tableName, returnColumns, selection, selectionArgs, groupBy, having, orderBy
        return database.query("friends", new String[] {"_id", "nickname"},
                null, null, null, null, "nickname");
    } // end method getAllFriends

    // return a Cursor with all user contact information in the database
    public Cursor getAllUserContacts()
    {
        // tableName, returnColumns, selection, selectionArgs, groupBy, having, orderBy
        return database.query("userContacts", null,
                null, null, null, null, "name");
    } // end method getAllUserContacts

    // return a Cursor with user contact information in the database
    public Cursor getUserContact(long id)
    {
        // tableName, returnColumns, selection, selectionArgs, groupBy, having, orderBy
        return database.query(
                "userContacts", null, "_id=" + id, null, null, null, null);
    } // end method getUserContact

    // get a Cursor containing all information about the friend specified
    // by the given id
    public Cursor getFriend(long id)
    {
        return database.query(
                "friends", null, "_id=" + id, null, null, null, null);
    } // end method getFriend

    // delete the friend specified by the given String name
    public void deleteFriend(long id)
    {
        open(); // open the database
        // table, whereClause, whereArgs
        database.delete("friends", "_id=" + id, null);
        close(); // close the database
    } // end method deleteFriend

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        // public constructor
        public DatabaseOpenHelper(Context context, String name,
                                  CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        } // end DatabaseOpenHelper constructor

        // creates the contacts table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // query to create a new table named friends
            String createFriendsQuery = "CREATE TABLE friends" +
                    "(_id integer primary key autoincrement," +
                    "nickname TEXT, phone TEXT, defaultEmergencyText TEXT);";

            db.execSQL(createFriendsQuery); // execute the query

            // insert 3 default friend records
            String insertFriendsQuery;
            for (int i = 0; i<3; i++) {
                insertFriendsQuery = "INSERT INTO friends" + " VALUES(null, 'Friend " + i + "', ' ' , 'Emergency: I am in trouble and need help. Please call the police with my following coordinates:')";
                db.execSQL(insertFriendsQuery); // execute the query
            }

            // query to create a new table named userContacts
            String createUserContactsQuery = "CREATE TABLE userContacts" +
                    "(_id integer primary key autoincrement," +
                    "name TEXT, " +
                    "call TEXT, " +
                    "text TEXT," +
                    "email TEXT," +
                    "address TEXT," +
                    "hours TEXT);";

            db.execSQL(createUserContactsQuery); // execute the query

            // insert 3 default user contacts records
            String insertUserContactsQuery;
            insertUserContactsQuery = "INSERT INTO userContacts" + " VALUES" +
                    "(null, 'Public Safety', '5854346263' , '5854346263', 'kas3101@rit.edu', '19 Loden Lane', 'Monday-Friday : 9AM - 5PM'), " +
                    "(null, 'Center for Women and Gender', '5854346263' , '', 'kas3101@rit.edu', 'SAU Rit Campus', 'Monday-Friday : 9AM - 4PM')," +
                    "(null, 'Student Health Center', '' , '', 'kas3101@rit.edu', 'Student Healthy center RIT Campus', 'Monday-Friday : 10AM - 4PM');";
            db.execSQL(insertUserContactsQuery); // execute the query

        } // end method onCreate

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS userContacts");
        } // end method onUpgrade
    } // end class DatabaseOpenHelper
} // end class DatabaseConnector