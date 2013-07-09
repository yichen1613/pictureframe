package ca.taglab.PictureFrame;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ca.taglab.PictureFrame.database.AddExistingPicture;
import ca.taglab.PictureFrame.database.AddPicture;
import ca.taglab.PictureFrame.database.DatabaseHelper;

public class MyActivity extends ListActivity {
    private static Sample[] mSamples;
    DatabaseHelper db;


    private class Sample {
        private CharSequence title;
        private Class<? extends Activity> activityClass;

        public Sample(int titleResId, Class<? extends Activity> activityClass) {
            this.activityClass = activityClass;
            this.title = getResources().getString(titleResId);
        }

        @Override
        public String toString() {
            return title.toString();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mSamples = new Sample[] {
                new Sample(R.string.title_log_in, LoginActivity.class),
                new Sample(R.string.title_add_picture, AddPicture.class),
                new Sample(R.string.title_add_existing_picture, AddExistingPicture.class),
                new Sample(R.string.title_view_gallery, ScreenSlideActivity.class)
        };

        setListAdapter(new ArrayAdapter<Sample>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                mSamples));

    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        // Launch the activity associated with this list position
        startActivity(new Intent(MyActivity.this, mSamples[position].activityClass));
    }
}