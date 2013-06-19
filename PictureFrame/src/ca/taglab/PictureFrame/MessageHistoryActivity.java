package ca.taglab.PictureFrame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ca.taglab.PictureFrame.adapter.MobileArrayAdapter;
import ca.taglab.PictureFrame.database.UserTable;

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
            new MessageItem("text", R.string.message_history_1),
            new MessageItem("text", R.string.message_history_2),
            new MessageItem("text", R.string.message_history_3),
            new MessageItem("picture", R.drawable.person1),
            new MessageItem("text", R.string.message_history_4),
            new MessageItem("picture", R.drawable.person2),
            new MessageItem("text", R.string.message_history_5)
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
            Toast.makeText(this, "Text: " + getApplicationContext().getText(selectedValue.resId), Toast.LENGTH_SHORT).show();
        } else if (selectedValue.msgType.equals("picture")) {
            Toast.makeText(this, "Picture: " + getApplicationContext().getText(selectedValue.resId), Toast.LENGTH_SHORT).show();
        } else {
            // something else
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
