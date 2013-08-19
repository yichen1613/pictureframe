package ca.taglab.PictureFrame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import ca.taglab.PictureFrame.adapter.MobileArrayAdapter;

public class MessageHistoryActivity extends ListActivity {

    private View mCancel;
    private int mShortAnimationDuration;

    private String mName;

    public class MessageItem {
        public String msgType;
        public int resId;

        public MessageItem(String msgType, int resId) {
            this.msgType = msgType;
            this.resId = resId;
        }
    }

    private final MessageItem[] MESSAGES = new MessageItem[] {
            new MessageItem("text", R.string.message_history_6),
            new MessageItem("picture", R.drawable.message_history_6),
            new MessageItem("text", R.string.message_history_5),
            new MessageItem("video_temp", R.raw.message_history_8),

            new MessageItem("text", R.string.message_history_1),
            new MessageItem("picture", R.drawable.message_history_1),
            new MessageItem("text", R.string.message_history_2),
            new MessageItem("picture", R.drawable.message_history_2),
            new MessageItem("text", R.string.message_history_3),
            new MessageItem("video", R.raw.message_history_3),
            new MessageItem("text", R.string.message_history_4),
            new MessageItem("picture", R.drawable.message_history_4)
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_history);

        Intent intent = getIntent();
        mName = intent.getExtras().getString("user_name");
        ((TextView) findViewById(R.id.name)).setText(mName);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mCancel = findViewById(R.id.close);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        Context context = getApplicationContext();

        setListAdapter(new MobileArrayAdapter(context, R.layout.list_message_history, MESSAGES));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MessageItem selectedValue = (MessageItem) getListAdapter().getItem(position);

        if (selectedValue.msgType.equals("text")) {
            Log.v("MessageHistoryActivity", "Text selected: " + getApplicationContext().getText(selectedValue.resId));

        } else if (selectedValue.msgType.equals("picture")) {
            Log.v("MessageHistoryActivity", "Picture selected: " + getApplicationContext().getText(selectedValue.resId));

        } else if (selectedValue.msgType.equals("video") || selectedValue.msgType.equals("video_temp")) {
            Log.v("MessageHistoryActivity", "Video selected: " + getApplicationContext().getText(selectedValue.resId));

            final VideoView vd = (VideoView) findViewById(R.id.VideoView);
            vd.setVisibility(VideoView.VISIBLE);

            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + selectedValue.resId);
            MediaController mediaController = new MediaController(this);
            vd.setMediaController(mediaController);
            vd.setVideoURI(uri);
            vd.setZOrderOnTop(true);

            vd.start();

            // After video playback is done, hide the video player
            vd.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    vd.setVisibility(VideoView.INVISIBLE);
                }
            });

        } else {
            Log.e("MessageHistoryActivity", "onListItemClick() - unsupported message type");
        }

    }

    private void closeActivity() {
        mCancel.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finish();
                    }
                });
    }
}
