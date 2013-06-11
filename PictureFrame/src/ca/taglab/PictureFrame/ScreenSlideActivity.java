/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.taglab.PictureFrame;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;
import ca.taglab.PictureFrame.adapter.CursorPagerAdapter;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.provider.UserContentProvider;

/**
 * Demonstrates a "screen-slide" animation using a {@link ViewPager}. Because {@link ViewPager}
 * automatically plays such an animation when calling {@link ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see ScreenSlidePageFragment
 */
public class ScreenSlideActivity extends FragmentActivity {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private CursorPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        getActionBar().hide();

        Cursor cursor;
        cursor = getContentResolver().query(UserContentProvider.CONTENT_URI, UserTable.PROJECTION, null, null, null);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new CursorPagerAdapter<ScreenSlidePageFragment>(
                getSupportFragmentManager(),
                ScreenSlidePageFragment.class,
                UserTable.PROJECTION,
                cursor
        );
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
    }

    public void videoClicked(View v) {
        Toast.makeText(
                getApplicationContext(),
                "Video clicked",
                Toast.LENGTH_SHORT
        ).show();
    }

    public void audioClicked(View v) {
        Toast.makeText(
                getApplicationContext(),
                "Audio clicked",
                Toast.LENGTH_SHORT
        ).show();
    }

    public void photoClicked(View v) {
        Toast.makeText(
                getApplicationContext(),
                "Photo clicked",
                Toast.LENGTH_SHORT
        ).show();
    }

    public void waveClicked(View v) {
        Toast.makeText(
                getApplicationContext(),
                "Wave clicked",
                Toast.LENGTH_SHORT
        ).show();
    }

}
