package com.son.hinhnendep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.son.hinhnendep.BaseActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import app.ResAppController;
import model.ResWallpaper;
import util.PrefManager;
import util.ResUtils;

public class ResFullScreenViewActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = ResFullScreenViewActivity.class
			.getSimpleName();
	public static final String TAG_SEL_IMAGE = "selectedImage";
	private ResWallpaper selectedPhoto;
	private ImageView fullImageView;
	private LinearLayout llSetWallpaper, llDownloadWallpaper, llShare;
	private ResUtils utils;
	private ProgressBar pbLoader;
	private AdView adView;
	private PrefManager pref;
	// Picasa JSON response node keys
	private static final String TAG_ENTRY = "entry",
			TAG_MEDIA_GROUP = "media$group",
			TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",
			TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreen_sondeptrai);
		adView = (AdView)findViewById(R.id.adver);
		adView.loadAd(new AdRequest.Builder()
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)   
		.addTestDevice("85AFA7708F0526A57D3599F39F71656D").build());
		fullImageView = (ImageView) findViewById(R.id.imgFullscreen);
		llSetWallpaper = (LinearLayout) findViewById(R.id.llSetWallpaper);
		llDownloadWallpaper = (LinearLayout) findViewById(R.id.download);
		llShare =(LinearLayout) findViewById(R.id.share);
		pbLoader = (ProgressBar) findViewById(R.id.pbLoader);

		// hide the action bar in fullscreen mode
//		getActionBar().hide();

		utils = new ResUtils(getApplicationContext());

		// layout click listeners
		llSetWallpaper.setOnClickListener(this);
		llShare.setOnClickListener(this);
		llDownloadWallpaper.setOnClickListener(this);

		// setting layout buttons alpha/opacity
		llSetWallpaper.getBackground().setAlpha(80);
//		llShare.getBackground().setAlpha(80);
		llDownloadWallpaper.getBackground().setAlpha(80);
		Intent i = getIntent();
		selectedPhoto = (ResWallpaper) i.getSerializableExtra(TAG_SEL_IMAGE);

		// check for selected photo null
		if (selectedPhoto != null) {

			// fetch photo full resolution image by making another json request
			fetchFullResolutionImage();

		} else {
			Toast.makeText(getApplicationContext(),
					getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * Fetching image fullresolution json
	 * */
	private void fetchFullResolutionImage() {
		String url = selectedPhoto.getPhotoJson();

		// show loader before making request
		pbLoader.setVisibility(View.VISIBLE);
		llSetWallpaper.setVisibility(View.GONE);
		llDownloadWallpaper.setVisibility(View.GONE);

		// volley's json obj request
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, url,
				null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Log.d(TAG,
						"Image full resolution json: "
								+ response.toString());
				try {
					// Parsing the json response
					JSONObject entry = response
							.getJSONObject(TAG_ENTRY);

					JSONArray mediacontentArry = entry.getJSONObject(
							TAG_MEDIA_GROUP).getJSONArray(
									TAG_MEDIA_CONTENT);

					JSONObject mediaObj = (JSONObject) mediacontentArry
							.get(0);

					String fullResolutionUrl = mediaObj
							.getString(TAG_IMG_URL);

					// image full resolution widht and height
					final int width = mediaObj.getInt(TAG_IMG_WIDTH);
					final int height = mediaObj.getInt(TAG_IMG_HEIGHT);

					Log.d(TAG, "Full resolution image. url: "
							+ fullResolutionUrl + ", w: " + width
							+ ", h: " + height);

					ImageLoader imageLoader = ResAppController
							.getInstance().getImageLoader();

					// We download image into ImageView instead of
					// NetworkImageView to have callback methods
					// Currently NetworkImageView doesn't have callback
					// methods

					imageLoader.get(fullResolutionUrl,
							new ImageListener() {

						@Override
						public void onErrorResponse(
								VolleyError arg0) {
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.msg_wall_fetch_error),
									Toast.LENGTH_LONG).show();
						}

						@Override
						public void onResponse(
								ImageContainer response,
								boolean arg1) {
							if (response.getBitmap() != null) {
								// load bitmap into imageview
								fullImageView
								.setImageBitmap(response
										.getBitmap());
								adjustImageAspect(width, height);

								// hide loader and show set &
								// download buttons
								pbLoader.setVisibility(View.GONE);
								llSetWallpaper
								.setVisibility(View.VISIBLE);
								llDownloadWallpaper
								.setVisibility(View.VISIBLE);
							}
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							getString(R.string.msg_unknown_error),
							Toast.LENGTH_LONG).show();
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Error: " + error.getMessage());
				// unable to fetch wallpapers
				// either google username is wrong or
				// devices doesn't have internet connection
				Toast.makeText(getApplicationContext(),
						getString(R.string.msg_wall_fetch_error),
						Toast.LENGTH_LONG).show();

			}
		});

		// Remove the url from cache
		ResAppController.getInstance().getRequestQueue().getCache().remove(url);

		// Disable the cache for this url, so that it always fetches updated
		// json
		jsonObjReq.setShouldCache(false);

		// Adding request to request queue
		ResAppController.getInstance().addToRequestQueue(jsonObjReq);
	}

	/**
	 * Adjusting the image aspect ration to scroll horizontally, Image height
	 * will be screen height, width will be calculated respected to height
	 * */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void adjustImageAspect(int bWidth, int bHeight) {
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		if (bWidth == 0 || bHeight == 0)
			return;

		int sHeight = 0;

		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			sHeight = size.y;
		} else {
			Display display = getWindowManager().getDefaultDisplay();
			sHeight = display.getHeight();
		}

		int new_width = (int) Math.floor((double) bWidth * (double) sHeight
				/ (double) bHeight);
		params.width = new_width;
		params.height = sHeight;

		Log.d(TAG, "Fullscreen image new dimensions: w = " + new_width
				+ ", h = " + sHeight);

		fullImageView.setLayoutParams(params);
	}

	/**
	 * View click listener
	 * */
	@Override
	public void onClick(View v) {
		Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
				.getBitmap();
		switch (v.getId()) {
		// button Download Wallpaper tapped
		case R.id.download:
			utils.saveImageToSDCard(bitmap);
			break;
			// button Set As Wallpaper tapped
		case R.id.llSetWallpaper:
			utils.setAsWallpaper(bitmap);
			break;
		case R.id.share:
			Toast.makeText(ResFullScreenViewActivity.this, "Share hey", Toast.LENGTH_SHORT).show();
			shareImage();
			break;
		default:
			break;
		}

	}
	private void shareImage() { Intent share = new Intent(Intent.ACTION_SEND); 
	// If you want to share a png image only, you can do: 
	// setType("image/png"); OR for jpeg: setType("image/jpeg"); 
	Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
			.getBitmap();
	utils.saveImageToSDCard(bitmap, "walpapers.jpg");
	share.setType("image/*"); 
	String imagePath = Environment.getExternalStorageDirectory()+"/Pictures/Awesome Wallpapers/walpapers.jpg";
	File imageFileToShare = new File(imagePath);
	Uri uri = Uri.fromFile(imageFileToShare); 
	share.putExtra(Intent.EXTRA_STREAM, uri);
	startActivity(Intent.createChooser(share, "Share image to..."));
}
@Override
public void onPause() {
	adView.pause();
	super.onPause();
}

@Override
public void onResume() {
	super.onResume();
	adView.resume();
}

@Override
public void onDestroy() {
	adView.destroy();
	super.onDestroy();
}
}