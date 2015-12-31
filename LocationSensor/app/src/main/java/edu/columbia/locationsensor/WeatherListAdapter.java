package edu.columbia.locationsensor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pooja on 4/9/15.
 */
public class WeatherListAdapter extends BaseAdapter {
    private Context context;
    private static final Integer[] weather_icons = {R.drawable.ic_brightness_5_black_48dp, R.drawable.ic_flash_on_black_48dp,
            R.drawable.ic_wb_cloudy_black_48dp};


    public WeatherListAdapter(Context context, int resourceId) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return weather_icons.length;
    }

    private class ViewHolder {
        ImageView imageView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.weather_list_layout, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.weatherIconView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.imageView.setImageDrawable(context.getResources().getDrawable(weather_icons[position]));
        return convertView;
    }
}
