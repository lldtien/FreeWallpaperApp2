package helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.son.hinhnendep.R;

import java.util.ArrayList;
import java.util.List;

import app.ResAppController;
import model.ResWallpaper;

public class ResGridViewAdapter extends BaseAdapter {

	private Activity _activity;
	private LayoutInflater inflater;
	private List<ResWallpaper> wallpapersList = new ArrayList<ResWallpaper>();
	private int imageWidth;
	ImageLoader imageLoader = ResAppController.getInstance().getImageLoader();

	public ResGridViewAdapter(Activity activity, List<ResWallpaper> wallpapersList,
			int imageWidth) {
		this._activity = activity;
		this.wallpapersList = wallpapersList;
		this.imageWidth = imageWidth;
	}

	@Override
	public int getCount() {
		return this.wallpapersList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.wallpapersList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater == null)
			inflater = (LayoutInflater) _activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.item_photo, null);

		if (imageLoader == null)
			imageLoader = ResAppController.getInstance().getImageLoader();

		// Grid thumbnail image view
		NetworkImageView thumbNail = (NetworkImageView) convertView
				.findViewById(R.id.thumbnail);

		ResWallpaper p = wallpapersList.get(position);

		thumbNail.setScaleType(ImageView.ScaleType.CENTER_CROP);
		thumbNail.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth,
				imageWidth));
		thumbNail.setImageUrl(p.getUrl(), imageLoader);

		return convertView;
	}

}