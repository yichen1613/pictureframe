package ca.taglab.PictureFrame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.taglab.PictureFrame.R;

public class MobileArrayAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    private String[] values;

    private int mViewResourceId;

    public MobileArrayAdapter(Context context, int viewResourceId, String[] values) {
        super(context, viewResourceId, values);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.values = values;
        this.mViewResourceId = viewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(mViewResourceId, null);

        //ImageView iv = (ImageView)convertView.findViewById(R.id.option_icon);
        //iv.setImageDrawable(mIcons.getDrawable(position));

        TextView textView = (TextView) convertView.findViewById(R.id.message);
        textView.setText(values[position]);

        return convertView;
    }
}
