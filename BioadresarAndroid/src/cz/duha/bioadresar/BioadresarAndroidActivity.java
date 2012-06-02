package cz.duha.bioadresar;

import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

public class BioadresarAndroidActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		DatabaseHelper dbHelper = new DatabaseHelper(this);

		try {
			dbHelper.createDb();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		try {
			dbHelper.openDb();
			Log.d("ha", "db opened");
			Cursor c = dbHelper.getCategoryCursor();

			c.moveToNext();
			while (!c.isAfterLast()) {
				String name = c.getString(0);

				Log.d("category", name);
				c.moveToNext();
			}
			c.close();
			
			c = dbHelper.getFarmCursor();
			c.moveToNext();
			while (!c.isAfterLast()) {
				String name = c.getString(0);

				Log.d("farm", name);
				c.moveToNext();
			}
			c.close();
			
			c = dbHelper.getFarmCursorInArea(49.57124, 16.49922, 50, 17);
			c.moveToNext();
			while (!c.isAfterLast()) {
				String name = c.getString(0);
				Double gpsLat = c.getDouble(1);
				Double gpsLong = c.getDouble(2);

				Log.d("in area", name + ": " + gpsLat + "; " + gpsLong);
				c.moveToNext();
			}
			c.close();
		} catch (SQLException sqle) {
			throw sqle;
		} finally {
			dbHelper.close();
		}

	}
}
