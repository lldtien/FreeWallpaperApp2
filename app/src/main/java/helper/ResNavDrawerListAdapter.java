package helper;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.son.hinhnendep.R;
import com.son.hinhnendep.ResNavDrawerItem;

import java.util.ArrayList;

public class ResNavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<ResNavDrawerItem> navDrawerItems;
	int image[] =  {
			R.drawable.newpic,
			R.drawable.animee,
			R.drawable.girll,
			R.drawable.animall,
			R.drawable.cutee,
			R.drawable.flowerr,
			R.drawable.foodd,
			R.drawable.gamee,
			R.drawable.lovee,
			R.drawable.naturee,
			R.drawable.travell,
			R.drawable.banh, 
			R.drawable.chim,
			R.drawable.dongho, 
			R.drawable.chimngu,
			R.drawable.nato, 
			R.drawable.hoadep, 
			R.drawable.chim
			};
	private int save;

	public ResNavDrawerListAdapter(Context context,
			ArrayList<ResNavDrawerItem> navDrawerItems, int save) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		this.save = save;
	}

	public void setSave(int save) {
		this.save = save;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.res_list_item, null);
		}

		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		ImageView img =  (ImageView) convertView.findViewById(R.id.imageView1);
		img.setImageResource(image[position]);
		txtTitle.setText(navDrawerItems.get(position).getTitle());

		if(save == position){
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

				float finalRadius = (int)Math.hypot(convertView.getWidth()/2,convertView.getHeight()/2);
				Animator anim = ViewAnimationUtils.createCircularReveal(convertView,convertView.getWidth()/2,convertView.getHeight()/2,0,finalRadius);
				convertView.setBackgroundColor(Color.parseColor("#2196F3"));
				anim.start();
			}else{
				convertView.setBackgroundColor(Color.parseColor("#2196F3"));
			}
		}else{
			convertView.setBackgroundColor(Color.WHITE);
		}

		return convertView;
	}

}
