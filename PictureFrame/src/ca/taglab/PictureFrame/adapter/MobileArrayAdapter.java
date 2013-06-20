package ca.taglab.PictureFrame.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.taglab.PictureFrame.MessageHistoryActivity;
import ca.taglab.PictureFrame.R;

public class MobileArrayAdapter extends ArrayAdapter<MessageHistoryActivity.MessageItem> {

    private LayoutInflater mInflater;
    private Context mContext;
    private int mViewResourceId;

    private TextView textView;
    private ImageView imageView;
    private MessageHistoryActivity.MessageItem[] messages;

    public MobileArrayAdapter(Context context, int viewResourceId, MessageHistoryActivity.MessageItem[] messages) {
        super(context, viewResourceId, messages);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mContext = context;
        this.messages = messages;
        this.mViewResourceId = viewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(mViewResourceId, null);

        MessageHistoryActivity.MessageItem msg = messages[position];

        if ((msg.msgType).equals("text")) {
            Log.v("MobileArrayAdapter", "TextView contents: " + mContext.getText(msg.resId));
            textView = (TextView) convertView.findViewById(R.id.message);
            textView.setText(msg.resId);

        } else if ((msg.msgType).equals("picture")) {
            Log.v("MobileArrayAdapter", "ImageView contents: " + mContext.getText(msg.resId));
            imageView = (ImageView) convertView.findViewById(R.id.picture);
            imageView.setImageDrawable(mContext.getResources().getDrawable(msg.resId));

        } else if ((msg.msgType).equals("video")) {
            Log.v("MobileArrayAdapter", "Video contents: " + mContext.getText(msg.resId));
            imageView = (ImageView) convertView.findViewById(R.id.picture);
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.message_history_3));

        } else {
            Log.e("MobileArrayAdapter", "Unsupported message type");
        }

        return convertView;
    }
}
