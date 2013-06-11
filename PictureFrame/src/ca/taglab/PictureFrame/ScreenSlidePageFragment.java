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
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.email.SendEmailAsyncTask;

import java.io.File;

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

    private static final int TAKE_PICTURE = 100;
    Uri mCapturedImageURI;

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

        ((TextView) rootView.findViewById(R.id.name)).setText(mName);
        rootView.findViewById(R.id.control).getBackground().setAlpha(200);

        optionsOpen = false;

        mPhoto = rootView.findViewById(R.id.photo);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String filename = String.valueOf(System.currentTimeMillis()) + ".jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, filename);
                    mCapturedImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    startActivityForResult(intent, TAKE_PICTURE);
                } catch (Exception e) {
                    Log.e("ScreenSlidePageFragment", "Camera intent failed");
                }
            }
        });

        mVideo = rootView.findViewById(R.id.video);
        mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SendEmailAsyncTask(mEmail, "PictureFrame: I have a video message for you", "", "").execute();
                    Toast.makeText(getActivity(), "Video sent to: " + mEmail, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("SendEmailAsyncTask", e.getMessage(), e);
                    Toast.makeText(getActivity(), "Video to " + mEmail + " failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAudio = rootView.findViewById(R.id.audio);
        mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SendEmailAsyncTask(mEmail, "PictureFrame: I have an audio message for you", "", "").execute();
                    Toast.makeText(getActivity(), "Audio sent to: " + mEmail, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("SendEmailAsyncTask", e.getMessage(), e);
                    Toast.makeText(getActivity(), "Audio to " + mEmail + " failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mWave = rootView.findViewById(R.id.wave);
        mWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SendEmailAsyncTask(mEmail, "PictureFrame: I'm thinking of you", "Wave sent via PictureFrame", "").execute();
                    Toast.makeText(getActivity(), "Wave sent to: " + mEmail, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("SendEmailAsyncTask", e.getMessage(), e);
                    Toast.makeText(getActivity(), "Wave to " + mEmail + " failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE) {
            try {
                String photo_location = getLastImageId();
                new SendEmailAsyncTask(mEmail, "PictureFrame: I have a photo for you", "", photo_location).execute();
                Toast.makeText(getActivity(), "Photo stored at " + photo_location + " sent to: " + mEmail, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("SendEmailAsyncTask", e.getMessage(), e);
                Toast.makeText(getActivity(), "Photo to " + mEmail + " failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Failed
        }
    }

    /**
     * Get the last image filepath from the media store
     * @return
     */
    private String getLastImageId(){
        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
        Cursor imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        if(imageCursor.moveToFirst()){
            int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d("ScreenSlidePageFragment", "getLastImageId::id " + id);
            Log.d("ScreenSlidePageFragment", "getLastImageId::path " + fullPath);
            imageCursor.close();
            return fullPath;
        }else{
            return "";
        }
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
