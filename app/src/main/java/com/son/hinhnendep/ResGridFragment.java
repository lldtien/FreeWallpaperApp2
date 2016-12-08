package com.son.hinhnendep;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.ResAppConst;
import app.ResAppController;
import helper.ResGridViewAdapter;
import model.ResWallpaper;
import util.PrefManager;
import util.ResUtils;

public class ResGridFragment extends Fragment {
    private static final String TAG = ResGridFragment.class.getSimpleName();
    private ResUtils utils;
    private ResGridViewAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private static final String bundleAlbumId = "albumId";
    private String selectedAlbumId;
    private List<ResWallpaper> photosList;
    private ProgressBar pbLoader;
    private PrefManager pref;
    private AdView adView;
    // Picasa JSON response node keys
    private static final String TAG_FEED = "feed", TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "media$group",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",
            TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height", TAG_ID = "id",
            TAG_T = "$t";

    public ResGridFragment() {
    }

    public static ResGridFragment newInstance(String albumId) {
        ResGridFragment f = new ResGridFragment();
        Bundle args = new Bundle();
        args.putString(bundleAlbumId, albumId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        photosList = new ArrayList<ResWallpaper>();
        pref = new PrefManager(getActivity());

        // Getting Album Id of the item selected in navigation drawer
        // if Album Id is null, user is selected recently added option
        if (getArguments().getString(bundleAlbumId) != null) {
            selectedAlbumId = getArguments().getString(bundleAlbumId);
            Log.d(TAG,
                    "Selected album id: "
                            + getArguments().getString(bundleAlbumId));
        } else {
            Log.d(TAG, "Selected recentlyed added album");
            selectedAlbumId = null;
        }

        // Preparing the request url
        String url = null;
        if (selectedAlbumId == null) {
            // Recently added album url

            // edited
            url = ResAppConst.URL_ALBUM_PHOTOS.replace("_PICASA_USER_",
                    pref.getGoogleUserName()).replace("_ALBUM_ID_",
                    "6263287504960320385");

            /*url = ResAppConst.URL_RECENTLY_ADDED.replace("_PICASA_USER_",
					pref.getGoogleUserName());
*/
        } else {
            // Selected an album, replace the Album Id in the url
            url = ResAppConst.URL_ALBUM_PHOTOS.replace("_PICASA_USER_",
                    pref.getGoogleUserName()).replace("_ALBUM_ID_",
                    selectedAlbumId);
        }

        Log.d(TAG, "Final request url: " + url);

        View rootView = inflater.inflate(R.layout.fragment_grid, container,
                false);

        // Hiding the gridview and showing loader image before making the http
        // request
        gridView = (GridView) rootView.findViewById(R.id.grid_view);
        adView = (AdView) rootView.findViewById(R.id.adver);
        adView.loadAd(new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("85AFA7708F0526A57D3599F39F71656D").build());
        gridView.setVisibility(View.GONE);
        pbLoader = (ProgressBar) rootView.findViewById(R.id.pbLoader);
        pbLoader.setVisibility(View.VISIBLE);

        utils = new ResUtils(getActivity());

        /**
         * Making volley's json object request to fetch list of photos of an
         * album
         * */
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG,
                        "List of photos json reponse: "
                                + response.toString());
                try {
                    // Parsing the json response
                    JSONArray entry = response.getJSONObject(TAG_FEED)
                            .getJSONArray(TAG_ENTRY);

                    // looping through each photo and adding it to list
                    // data set
                    for (int i = 0; i < entry.length(); i++) {
                        JSONObject photoObj = (JSONObject) entry.get(i);
                        JSONArray mediacontentArry = photoObj
                                .getJSONObject(TAG_MEDIA_GROUP)
                                .getJSONArray(TAG_MEDIA_CONTENT);

                        if (mediacontentArry.length() > 0) {
                            JSONObject mediaObj = (JSONObject) mediacontentArry
                                    .get(0);

                            String url = mediaObj
                                    .getString(TAG_IMG_URL);

                            String photoJson = photoObj.getJSONObject(
                                    TAG_ID).getString(TAG_T)
                                    + "&imgmax=d";

                            int width = mediaObj.getInt(TAG_IMG_WIDTH);
                            int height = mediaObj
                                    .getInt(TAG_IMG_HEIGHT);

                            ResWallpaper p = new ResWallpaper(photoJson, url, width,
                                    height);

                            // Adding the photo to list data set
                            photosList.add(p);

                            Log.d(TAG, "Photo: " + url + ", w: "
                                    + width + ", h: " + height);
                        }
                    }

                    // Notify list adapter about dataset changes. So
                    // that it renders grid again
                    adapter.notifyDataSetChanged();

                    // Hide the loader, make grid visible
                    pbLoader.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
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
                Toast.makeText(getActivity(),
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

        // Initilizing Grid View
        InitilizeGridLayout();

        // Gridview adapter
        adapter = new ResGridViewAdapter(getActivity(), photosList, columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapter);

        // Grid item select listener
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // On selecting the grid image, we launch fullscreen activity
                Intent i = new Intent(getActivity(),
                        ResFullScreenViewActivity.class);

                // Passing selected image to fullscreen activity
                ResWallpaper photo = photosList.get(position);

                i.putExtra(ResFullScreenViewActivity.TAG_SEL_IMAGE, photo);
                startActivity(i);
            }
        });

        return rootView;
    }

    /**
     * Method to calculate the grid dimensions Calculates number columns and
     * columns width in grid
     */
    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                ResAppConst.GRID_PADDING, r.getDisplayMetrics());

        // Column width
        columnWidth = (int) ((utils.getScreenWidth() - ((pref
                .getNoOfGridColumns() + 1) * padding)) / pref
                .getNoOfGridColumns());

        // Setting number of grid columns
        gridView.setNumColumns(pref.getNoOfGridColumns());
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);

        // Setting horizontal and vertical padding
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
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
