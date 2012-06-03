package cz.duha.bioadresar.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/cz.duha.bioadresar/databases/";

	private static String DB_NAME = "bioadr";
	
	private static int DB_VERSION = 1;
	
	private static DatabaseHelper defaultDb = null;

	private SQLiteDatabase db;

	private static Context appContext = null;

	public DatabaseHelper() {
		super(appContext, DB_NAME, null, DB_VERSION);
	}
	
	public static void setContext(Context context)
	{
		appContext = context;
	}
	
	public static DatabaseHelper getDefaultDb()
	{
		if (defaultDb == null && appContext != null)
		{
			try {
				defaultDb = new DatabaseHelper();
				defaultDb.createDb();
				defaultDb.openDb();
			} catch (IOException e) {
				Log.e("db", "error opening db " + e.toString());
			}
		}
		return defaultDb; 
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
		InputStream myInput = appContext.getAssets().open(DB_NAME);
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
		// nothing to do
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// nothing to do
	}

	public void setFilter(DataFilter filter) {
		//TODO: implement this
	}
	
	public void clearFilter() {
		//TODO: implement this
	}
	
	public Hashtable<Long, FarmInfo> getFarmsInRectangle(double lat1, double lon1, double lat2, double lon2) {
		String[] columns = new String[] { "_id", "name", "gps_lat", "gps_long" };
		String selection = "gps_lat >= ? AND gps_long >= ? AND gps_lat <= ? AND gps_long <= ?";
		String[] args = new String[] {
				Double.toString(Math.min(lat1, lat2)), Double.toString(Math.min(lon1, lon2)),
				Double.toString(Math.max(lat1, lat2)), Double.toString(Math.max(lon1, lon2))
		};
		Cursor c = db.query("farm", columns, selection, args, null, null, "gps_lat, gps_long");
		Hashtable<Long, FarmInfo> result = new Hashtable<Long, FarmInfo>();
		
		c.moveToNext();
		while (!c.isAfterLast()) {
			FarmInfo farmInfo = new FarmInfo();

			farmInfo.id = c.getLong(0);
			farmInfo.name = c.getString(1);
			farmInfo.lat = c.getDouble(2);
			farmInfo.lon = c.getDouble(3);
			
			//Log.d("farm info", farmInfo.id + "; " + farmInfo.name + "; " + farmInfo.lat + "; " + farmInfo.lon);
			result.put(farmInfo.id, farmInfo);
			c.moveToNext();
		}
		c.close();
		
		return result;
	}
	
	public TreeSet<FarmInfo> getFarmInfoInDistance(double lat, double lon, int distanceInKm) {
		FarmInfoComparator comparator = new FarmInfoComparator();
		double[] point1 = new double[2];
		double[] point2 = new double[2];
		Hashtable<Long, FarmInfo> farms;
		TreeSet<FarmInfo> result = new TreeSet<FarmInfo>(comparator);
		
		// TODO implement comparator and isInDistance methods
		farms = getFarmsInRectangle(point1[0], point1[1], point2[0], point2[1]);
		for (FarmInfo farm : farms.values()) {
			if (farm.isInDistance(lat, lon, distanceInKm)) {
				result.add(farm);
			}
		}
		
		return result;
	}
	
	public void fillDetails(FarmInfo info) {
		String[] columns = new String[] { "type", "desc" };
		String selection = "_id = ?";
		String[] args = new String[] { Long.toString(info.id) };
		Cursor c = db.query("farm", columns, selection, args, null, null, null);
		FarmContact farmContact = new FarmContact();
		List<String> categories = new ArrayList<String>();
		List<String> products = new ArrayList<String>();
		
		c.moveToNext();
		if (!c.isAfterLast()) {
			info.type = c.getString(0);
			info.description = c.getString(1);
		}
		c.close();
		
		farmContact.phoneNumbers = new ArrayList<String>();
		columns = new String[] { "type", "contact" };
		selection = "farm_id = ?";
		c = db.query("contact", columns, selection, args, null, null, null);
		c.moveToNext();
		while (!c.isAfterLast()) {
			String type = c.getString(0);
			String contact = c.getString(1);
			
			if (type.equals("city")) {
				farmContact.city = contact;
			}
			else if (type.equals("email")) {
				farmContact.email = contact;
			}
			else if (type.equals("eshop")) {
				farmContact.eshop = contact;
			}
			else if (type.equals("phone")) {
				farmContact.phoneNumbers.add(contact);
			}
			else if (type.equals("street")) {
				farmContact.street = contact;
			}
			else if (type.equals("web")) {
				farmContact.web = contact;
			}
			c.moveToNext();
		}
		c.close();
		info.contact = farmContact;
		
		c = db.rawQuery("SELECT category.name FROM category, farm_category WHERE category._id = farm_category.category_id AND farm_category.farm_id = ? ORDER BY category.name", args);
		c.moveToNext();
		while (!c.isAfterLast()) {
			categories.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
		info.categories = categories;
		
		c = db.rawQuery("SELECT product.name FROM product, farm_product WHERE product._id = farm_product.product_id AND farm_product.farm_id = ? ORDER BY product.name", args);
		c.moveToNext();
		while (!c.isAfterLast()) {
			products.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
		info.products = products;
	}

}
