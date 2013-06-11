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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.database.UserTable;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public class ScreenSlidePageFragment extends Fragment {

    /**
     * The image file path where the user's picture is located.
     */
    private String mImgPath;

    /**
     * The user's email.
     */
    private String mEmail;

    /**
     * The user's name.
     */
    private String mName;

    /**
     * The user's ID.
     */
    private long mId;

    private View mPhoto;
    private View mVideo;
    private View mAudio;
    private View mWave;
    private View mCancel;
    private int mShortAnimationDuration;
    private boolean optionsOpen;

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        mId = Long.valueOf(getArguments().getString(UserTable.COL_ID));
        mImgPath = getArguments().getString(UserTable.COL_IMG);
        mEmail = getArguments().getString(UserTable.COL_EMAIL);
        mName = getArguments().getString(UserTable.COL_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);

        Bitmap picture = BitmapFactory.decodeFile(mImgPath);
        rootView.setBackground(new BitmapDrawable(getResources(), picture));

        ((TextView) rootView.findViewById(R.id.name)).setText(mName);
        rootView.findViewById(R.id.control).getBackground().setAlpha(200);

        optionsOpen = false;

        mPhoto = rootView.findViewById(R.id.photo);
        mVideo = rootView.findViewById(R.id.video);
        mAudio = rootView.findViewById(R.id.audio);
        mWave = rootView.findViewById(R.id.wave);
        mCancel = rootView.findViewById(R.id.close);

        mPhoto.getBackground().setAlpha(200);
        mVideo.getBackground().setAlpha(200);
        mAudio.getBackground().setAlpha(200);
        mWave.getBackground().setAlpha(200);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!optionsOpen) showOptions();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionsOpen) hideOptions();
            }
        });

        return rootView;
    }


    private void hideOptions() {
        mPhoto.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mPhoto.setVisibility(View.GONE);
                    }
                });

        mVideo.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mVideo.setVisibility(View.GONE);
                    }
                });

        mAudio.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mAudio.setVisibility(View.GONE);
                    }
                });

        mWave.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mWave.setVisibility(View.GONE);
                    }
                });

        mCancel.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCancel.setVisibility(View.INVISIBLE);
                    }
                });

        optionsOpen = false;
    }


    private void showOptions() {
        mPhoto.setAlpha(0f);
        mPhoto.setVisibility(View.VISIBLE);

        mVideo.setAlpha(0f);
        mVideo.setVisibility(View.VISIBLE);

        mAudio.setAlpha(0f);
        mAudio.setVisibility(View.VISIBLE);

        mWave.setAlpha(0f);
        mWave.setVisibility(View.VISIBLE);

        mCancel.setAlpha(0f);
        mCancel.setVisibility(View.VISIBLE);

        mPhoto.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mVideo.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mAudio.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mWave.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mCancel.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        optionsOpen = true;
    }
}
