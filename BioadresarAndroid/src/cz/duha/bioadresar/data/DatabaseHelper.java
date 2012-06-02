package cz.duha.bioadresar.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/cz.duha.bioadresar/databases/";

	private static String DB_NAME = "bioadr";
	
	private static int DB_VERSION = 1;

	private SQLiteDatabase db;

	private final Context context;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDb() throws IOException {
		boolean dbExist = checkDb();

		if (dbExist) {
			// do nothing - database already exist
		} else {
			this.getReadableDatabase();

			try {
				copyDb();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time application is opened.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDb() {
		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies database from local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDb() throws IOException {
		InputStream myInput = context.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDb() throws SQLException {
		String path = DB_PATH + DB_NAME;
		db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {
		if (db != null)
			db.close();

		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public Cursor getCategoryCursor() {
		return db.rawQuery("SELECT name FROM category", null);
	}
	
	public Cursor getFarmCursor() {
		return db.rawQuery("SELECT name FROM farm", null);
	}
	
	public void setFilter(DataFilter filter)
	{
		//TODO: implement this
	}
	
	public void clearFilter()
	{
		//TODO: implement this
	}
	
	public Hashtable<Long, FarmInfo> getFarmCursorInRectangle(double lat1, double long1, double lat2, double long2) {
		//TODO: implement this
		return null;
		/* return db.rawQuery("SELECT name, gps_lat, gps_long FROM farm WHERE gps_lat >= ? AND gps_long >= ? AND gps_lat <= ? AND gps_long <= ?",
				new String[] { Double.toString(lat1), Double.toString(long1), Double.toString(lat2), Double.toString(long2) });
				*/
	}
	
	public Hashtable<Long, FarmInfo> getFarmInDistance(double lat, double lon, int distanceInKm)
	{
		//TODO: implement this
		return null;
	}
	
	public void fillDetails(FarmInfo info)
	{
		// TODO: implement this
	}

}
