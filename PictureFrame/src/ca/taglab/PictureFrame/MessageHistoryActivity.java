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

    static final String[] MESSAGES = new String[]
            { "This is message 1. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing",
              "This is message 2. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing",
              "This is message 3. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing",
              "This is message 4. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing",
              "This is message 5. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing",
              "This is message 6. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing",
              "This is message 7. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing",
              "This is message 8. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing",
              "This is message 9. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing",
              "This is message 10. Testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing" };


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
                hideOptions();
            }
        });

        Context context = getApplicationContext();

        setListAdapter(new MobileArrayAdapter(context, R.layout.list_message_history, MESSAGES));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // get selected items
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
    }

    private void hideOptions() {
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
