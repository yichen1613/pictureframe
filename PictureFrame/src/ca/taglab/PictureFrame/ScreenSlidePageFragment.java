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
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.email.SendEmailAsyncTask;

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
    
    private static final String TAG = "ScreenSlidePageFragment";

    private View mPhoto;
    private View mVideo;
    private View mAudio;
    private View mWave;
    private View mMsgHistory;
    private View mCancel;
    private int mShortAnimationDuration;
    private int mLongAnimationDuration;
    private boolean optionsOpen;

    private ImageView mConfirmation;

    private static final int CAPTURE_PICTURE = 100;
    private static final int CAPTURE_VIDEO = 110;
    private static final int CAPTURE_AUDIO = 120;
    Uri mCapturedImageURI;
    Uri mCapturedVideoURI;

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
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

        mConfirmation = (ImageView) rootView.findViewById(R.id.confirm);

        ((TextView) rootView.findViewById(R.id.name)).setText(mName);
        rootView.findViewById(R.id.control).getBackground().setAlpha(200);

        optionsOpen = false;

        mMsgHistory = rootView.findViewById(R.id.msg);
        mMsgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessageHistoryActivity.class);
                intent.putExtra("user_name", mName);
                startActivity(intent);
                hideOptions();
            }
        });

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
                    startActivityForResult(intent, CAPTURE_PICTURE);
                    hideOptions();
                } catch (Exception e) {
                    Log.e(TAG, "Camera intent failed");
                }
            }
        });

        mVideo = rootView.findViewById(R.id.video);
        mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String filename = String.valueOf(System.currentTimeMillis()) + ".3gp";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.TITLE, filename);
                    mCapturedVideoURI = getActivity().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedVideoURI);
                    startActivityForResult(intent, CAPTURE_VIDEO);
                    hideOptions();
                } catch (Exception e) {
                    Log.e(TAG, "Video intent failed");
                }
            }
        });

        mAudio = rootView.findViewById(R.id.audio);
        mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getActivity(), AudioRecorderActivity.class);
                    startActivityForResult(intent, CAPTURE_AUDIO);
                    hideOptions();
                } catch (Exception e) {
                    Log.e(TAG, "Audio intent failed");
                }
            }
        });

        mWave = rootView.findViewById(R.id.wave);
        mWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SendEmailAsyncTask(mEmail, "PictureFrame: I'm thinking of you", "Wave sent via PictureFrame", "").execute();
                    //Toast.makeText(getActivity(), "Wave sent to: " + mEmail, Toast.LENGTH_SHORT).show();
                    hideOptions();
                    messageSent(v);
                } catch (Exception e) {
                    Log.e("SendEmailAsyncTask", e.getMessage(), e);
                    //Toast.makeText(getActivity(), "Wave to " + mEmail + " failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancel = rootView.findViewById(R.id.close);

        mPhoto.getBackground().setAlpha(200);
        mVideo.getBackground().setAlpha(200);
        mAudio.getBackground().setAlpha(200);
        mWave.getBackground().setAlpha(200);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLongAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

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
        switch (requestCode) {
            case CAPTURE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    // Image captured and saved
                    try {
                        String photo_location = getLastImageId();
                        new SendEmailAsyncTask(mEmail, "PictureFrame: I have a photo for you", "", photo_location).execute();
                        messageSent(mPhoto);
                        //Toast.makeText(getActivity(), "Photo stored at " + photo_location + " sent to: " + mEmail, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("SendEmailAsyncTask", e.getMessage(), e);
                        //Toast.makeText(getActivity(), "Photo to " + mEmail + " failed", Toast.LENGTH_SHORT).show();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled photo capture
                    //Toast.makeText(getActivity(), "Photo capture was cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getActivity(), "Photo capture failed", Toast.LENGTH_SHORT).show();
                }
                break;

            case CAPTURE_VIDEO:
                if (resultCode == Activity.RESULT_OK) {
                    // Video captured and saved
                    try {
                        String video_location = getLastVideoId();
                        new SendEmailAsyncTask(mEmail, "PictureFrame: I have a video message for you", "", video_location).execute();
                        messageSent(mVideo);
                        //Toast.makeText(getActivity(), "Video sent to: " + mEmail, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("SendEmailAsyncTask", e.getMessage(), e);
                        //Toast.makeText(getActivity(), "Video to " + mEmail + " failed", Toast.LENGTH_SHORT).show();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled video capture
                   // Toast.makeText(getActivity(), "Video capture was cancelled", Toast.LENGTH_SHORT).show();
                } else {
                   // Toast.makeText(getActivity(), "Video capture failed", Toast.LENGTH_SHORT).show();
                }
                break;

            case CAPTURE_AUDIO:
                if (resultCode == Activity.RESULT_OK) {
                    // Audio captured and saved
                    try {
                        String audio_location = data.getStringExtra("audio_location");
                        new SendEmailAsyncTask(mEmail, "PictureFrame: I have an audio message for you", "", audio_location).execute();
                        messageSent(mAudio);
                        // Toast.makeText(getActivity(), "Audio sent to: " + mEmail, Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Log.e("SendEmailAsyncTask", e.getMessage(), e);
                        // Toast.makeText(getActivity(), "Audio to " + mEmail + " failed", Toast.LENGTH_SHORT).show();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled audio capture
                    // Toast.makeText(getActivity(), "Audio capture was cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    // Toast.makeText(getActivity(), "Audio capture failed", Toast.LENGTH_SHORT).show();
                }
                break;
            
            default:
                Log.e(TAG, "Intent to start an activity failed");
                break;
        }
    }


    /**
     * Get the last image file path from the media store
     * @return  Image ID
     */
    private String getLastImageId() {
        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
        Cursor imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        if (imageCursor.moveToFirst()) {
            int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d(TAG, "getLastImageId::id " + id);
            Log.d(TAG, "getLastImageId::path " + fullPath);
            imageCursor.close();
            return fullPath;
        } else {
            return "";
        }
    }


    /**
     * Get the last video file path from the media store
     * @return  Video ID
     */
    private String getLastVideoId() {
        final String[] videoColumns = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA };
        final String videoOrderBy = MediaStore.Video.Media._ID+" DESC";
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, null, null, videoOrderBy);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            String fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            Log.d(TAG, "getLastVideoId::id " + id);
            Log.d(TAG, "getLastVideoId::path " + fullPath);
            cursor.close();
            return fullPath;
        } else {
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


    private void messageSent(View v) {
        MediaPlayer mPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.success_low);
        switch(v.getId()) {
            case R.id.video:
                mConfirmation.setImageResource(R.drawable.confirm_red);
                mPlayer.start();
                break;

            case R.id.photo:
                mConfirmation.setImageResource(R.drawable.confirm_blue);
                mPlayer.start();
                break;
            
            case R.id.audio:
                mConfirmation.setImageResource(R.drawable.confirm_green);
                mPlayer.start();
                break;

            case R.id.wave:
                mConfirmation.setImageResource(R.drawable.confirm_orange);
                mPlayer.start();
                break;
        }

        mConfirmation.setAlpha(0f);
        mConfirmation.setVisibility(View.VISIBLE);
        mConfirmation.animate()
                .alpha(1f)
                .setDuration(mLongAnimationDuration * 2)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mConfirmation.animate()
                                .alpha(0f)
                                .setDuration(mLongAnimationDuration * 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mConfirmation.setVisibility(View.GONE);
                                    }
                                });
                    }
                });

    }
}
