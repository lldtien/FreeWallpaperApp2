package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.ResAppConst;
import model.ResCategory;

public class PrefManager {
	private static final String TAG = PrefManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "AwesomeWallpapers";

	// Google's username
	private static final String KEY_GOOGLE_USERNAME = "google_username";

	// No of grid columns
	private static final String KEY_NO_OF_COLUMNS = "no_of_columns";

	// Gallery directory name
	private static final String KEY_GALLERY_NAME = "gallery_name";

	// gallery albums key
	private static final String KEY_ALBUMS = "albums";

	public PrefManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

	}

	/**
	 * Storing google username
	 * */
	public void setGoogleUsername(String googleUsername) {
		editor = pref.edit();

		editor.putString(KEY_GOOGLE_USERNAME, googleUsername);

		// commit changes
		editor.commit();
	}

	public String getGoogleUserName() {
		return pref.getString(KEY_GOOGLE_USERNAME, ResAppConst.PICASA_USER);
	}

	/**
	 * store number of grid columns
	 * */
	public void setNoOfGridColumns(int columns) {
		editor = pref.edit();

		editor.putInt(KEY_NO_OF_COLUMNS, columns);

		// commit changes
		editor.commit();
	}

	public int getNoOfGridColumns() {
		return pref.getInt(KEY_NO_OF_COLUMNS, ResAppConst.NUM_OF_COLUMNS);
	}

	/**
	 * storing gallery name
	 * */
	public void setGalleryName(String galleryName) {
		editor = pref.edit();

		editor.putString(KEY_GALLERY_NAME, galleryName);

		// commit changes
		editor.commit();
	}

	public String getGalleryName() {
		return pref.getString(KEY_GALLERY_NAME, ResAppConst.SDCARD_DIR_NAME);
	}

	/**
	 * Storing albums in shared preferences
	 * */
	public void storeCategories(List<ResCategory> albums) {
		editor = pref.edit();
		Gson gson = new Gson();

		Log.d(TAG, "Albums: " + gson.toJson(albums));

		editor.putString(KEY_ALBUMS, gson.toJson(albums));

		// save changes
		editor.commit();
	}

	/**
	 * Fetching albums from shared preferences. Albums will be sorted before
	 * returning in alphabetical order
	 * */
	public List<ResCategory> getCategories() {
		List<ResCategory> albums = new ArrayList<ResCategory>();

		if (pref.contains(KEY_ALBUMS)) {
			String json = pref.getString(KEY_ALBUMS, null);
			Gson gson = new Gson();
			ResCategory[] albumArry = gson.fromJson(json, ResCategory[].class);

			for(ResCategory es : albumArry)
				Log.d("-------------",es.toString());
			/*Doc lai cho ni*/
			albums = Arrays.asList(albumArry);
			albums = new ArrayList<ResCategory>(albums);
		} else {
			return null;
		}

		List<ResCategory> allAlbums = albums;

		// Sort the albums in alphabetical order
		Collections.sort(allAlbums, new Comparator<ResCategory>() {
			public int compare(ResCategory a1, ResCategory a2) {
				return a1.getTitle().compareToIgnoreCase(a2.getTitle());
			}
		});

		return allAlbums;

	}

	/**
	 * Comparing albums titles for sorting
	 * */
	public class CustomComparator implements Comparator<ResCategory> {
		@Override
		public int compare(ResCategory c1, ResCategory c2) {
			return c1.getTitle().compareTo(c2.getTitle());
		}
	}
}