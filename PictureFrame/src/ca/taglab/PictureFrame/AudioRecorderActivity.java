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

    //private RecordButton mRecordButton = null;
    //private MediaRecorder mRecorder = null;

    //private PlayButton mPlayButton = null;
    //private MediaPlayer mPlayer = null;
    
    //private SendButton mSendButton = null;
    //private CancelButton mCancelButton = null;

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

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
    
    class SendButton extends Button {
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                // set data (audio file location) to pass back
                Intent data = new Intent();
                data.putExtra("audio_location", mFileName);
                setResult(RESULT_OK, data);
                finish();
            }
        };
        
        public SendButton(Context context) {
            super(context);
            setText("Send");
            setOnClickListener(clicker);
        }
    }
    
    class CancelButton extends Button {
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        };
        
        public CancelButton(Context context) {
            super(context);
            setText("Cancel");
            setOnClickListener(clicker);
        }
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


        /*
        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        mCancelButton = new CancelButton(this);
        ll.addView(mCancelButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        mSendButton = new SendButton(this);
        ll.addView(mSendButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        mSendButton.setVisibility(View.GONE);
        
        setContentView(ll);  */
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
