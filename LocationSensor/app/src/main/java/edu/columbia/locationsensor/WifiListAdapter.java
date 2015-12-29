package edu.columbia.locationsensor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pooja on 4/9/15.
 */
public class WifiListAdapter extends ArrayAdapter<WifiNetwork> {
    Context context;

    public WifiListAdapter(Context context, int resourceId, //resourceId=your layout
                                 List<WifiNetwork> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        WifiNetwork rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_layout, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.textView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(rowItem.getSSID());

        switch(rowItem.getLevel()) {

            case 0:
                holder.imageView.setImageResource(R.drawable.ic_wifi_levels_0);
                break;

            case 1:
                holder.imageView.setImageResource(R.drawable.ic_wifi_levels_1);
                break;

            case 2:
                holder.imageView.setImageResource(R.drawable.ic_wifi_levels_2);
                break;

            case 3:
                holder.imageView.setImageResource(R.drawable.ic_wifi_levels_3);
                break;

            case 4:
                holder.imageView.setImageResource(R.drawable.ic_wifi_levels_4);
                break;
        }
        return convertView;
    }
}
