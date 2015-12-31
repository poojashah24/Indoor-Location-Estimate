package edu.columbia.locationsensor;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.List;

/**
 * Defines the button for each sensor widget, i.e., barometer, wifi, location and magnetometer sensor.
 */
public class ButtonAdapter extends BaseAdapter {

    private Context context;

    private Integer[] mThumbIds = {
            R.drawable.ic_barometer_128, R.drawable.ic_wifi,
            R.drawable.ic_location, R.drawable.ic_magnetometer
    };

    private List<Intent> intents;

    public ButtonAdapter(Context c, List<Intent> i) {

        context = c;
        intents = i;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {
            button = new Button(context);
            button.setLayoutParams(new GridView.LayoutParams(500, 500));
            button.setMinimumHeight(500);
            button.setMinimumWidth(500);
            button.setBackgroundColor(context.getResources().getColor(R.color.button_material_dark));
            button.setPadding(8, 8, 8, 8);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(intents.get(position));
                }
            });
        } else {
            button = (Button) convertView;
        }

        button.setBackgroundResource(mThumbIds[position]);
        return button;
    }


}
