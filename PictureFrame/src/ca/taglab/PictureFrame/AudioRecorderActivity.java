package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.Intent;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.content.Context;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;


public class AudioRecorderActivity extends Activity {
    private static final String TAG = "AudioRecorderActivity";
    private static String mFileName = null;

    private Button mRecord;
    private Button mPlay;
    private Button mCancel;
    private Button mSend;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
            mPlay.setVisibility(View.VISIBLE);
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                    mStartPlaying = true;
                    mPlay.setBackgroundResource(R.drawable.play);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
        mSend.setVisibility(View.VISIBLE);
    }

    public AudioRecorderActivity() {
        // Download/save the audio file in external storage
        File folder = new File(Environment.getExternalStorageDirectory() + "/PictureFrame/AudioRecorder");
        boolean isFolderCreated = true;
        if (!folder.exists()) {
            Log.d(TAG, "Creating folder...");
            isFolderCreated = folder.mkdirs();
        }

        if (isFolderCreated) {
            this.mFileName = folder.getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis()) + ".3gp";
            Log.d(TAG, "Filepath of audio recording: " + this.mFileName);
        } else {
            this.mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecording.3gp";
            Log.d(TAG, "Error: Folder was not found/created!");
        }
    }

    boolean mStartRecording;
    boolean mStartPlaying;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.audio_rec);

        getActionBar().hide();

        mStartRecording = true;
        mRecord = (Button) findViewById(R.id.record);
        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    mRecord.setBackgroundResource(R.drawable.stop);
                } else {
                    mRecord.setBackgroundResource(R.drawable.record);
                }
                mStartRecording = !mStartRecording;
            }
        });

        mStartPlaying = true;
        mPlay = (Button) findViewById(R.id.play);
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    mPlay.setBackgroundResource(R.drawable.stop);
                } else {
                    mPlay.setBackgroundResource(R.drawable.play);
                }
                mStartPlaying = !mStartPlaying;
            }
        });
        mPlay.setVisibility(View.INVISIBLE);

        mSend = (Button) findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("audio_location", mFileName);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        mSend.setVisibility(View.INVISIBLE);

        mCancel = (Button) findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
