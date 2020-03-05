package comdrinik.github.musicplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.media.MediaPlayer;
import android.widget.TextView;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, MediaPlayer.OnCompletionListener {

    Spinner spinner;
    ImageButton previous;
    ImageButton play;
    ImageButton next;
    TextView timePlayed;
    TextView timeLeft;

    Handler handler = new Handler();

    int sTime;
    int eTime;
    int oTime;

    MediaPlayer song;
    Integer songs[] = new Integer[7];
    int position = 0;

    private String[] listOfSongs;
    private TypedArray images;
    private ImageView itemImage;
    private ArrayAdapter<String> spinnerAdapter;
    private SeekBar songProgress;
    private SeekBar sound;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner)findViewById(R.id.spinner);
        previous = (ImageButton) findViewById(R.id.buttonPrevious);
        play = (ImageButton) findViewById(R.id.buttonPlay);
        next = (ImageButton) findViewById(R.id.buttonNext);
        timePlayed = (TextView) findViewById(R.id.SongPlayed);
        timeLeft = (TextView) findViewById(R.id.SongTimeLeft);

        songProgress = (SeekBar) findViewById(R.id.seekBar);
        listOfSongs = getResources().getStringArray(R.array.songs_array);
        images = getResources().obtainTypedArray(R.array.images_array);
        itemImage = (ImageView)findViewById(R.id.imageView);
        sound = (SeekBar) findViewById(R.id.seekBar2);


        spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.layout_spinner_item, listOfSongs);
        spinnerAdapter.setDropDownViewResource(R.layout.layout_spinner_item);
        spinner.setAdapter(spinnerAdapter);

        songProgress.setOnSeekBarChangeListener(this);
        spinner.setOnItemSelectedListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        sound.setOnSeekBarChangeListener(this);


        load_song();
        song = MediaPlayer.create(getApplicationContext(), songs[position]);
        song.setOnCompletionListener(this);
        songProgress.setProgress(0);
        songProgress.setMax(song.getDuration());
        timePlayed.setText("0:00");
        long timeLeftSec = TimeUnit.MILLISECONDS.toSeconds(oTime) % 60;
        String timeLeftSecStr = timeLeftSec < 10 ? ("0" + String.valueOf(timeLeftSec)) : String.valueOf(timeLeftSec);
        timeLeft.setText(String.format(Locale.getDefault(), "-%d:%s", TimeUnit.MILLISECONDS.toMinutes(oTime), timeLeftSecStr));
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alright);
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.crazy);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.hell);
        Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.life);
        Bitmap bitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.simple);
        Bitmap bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.up);
        itemImage.setImageBitmap(bitmap);
        itemImage.setImageBitmap(bitmap1);
        itemImage.setImageBitmap(bitmap2);
        itemImage.setImageBitmap(bitmap3);
        itemImage.setImageBitmap(bitmap4);
        itemImage.setImageBitmap(bitmap5);
        itemImage.setImageBitmap(bitmap6);

        initControls();

        handler.postDelayed(UpdateSongTime,1000);
        handler.post(UpdateSongProgress);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (song.isPlaying()) {
            song.stop();
            itemImage.setImageResource(images.getResourceId(spinner.getSelectedItemPosition(), -1));
            song = MediaPlayer.create(getApplicationContext(), songs[position]);
            song.setOnCompletionListener(this);
            song.start();
            handler.post(UpdateSongTime);
        } else {
            song.stop();
            itemImage.setImageResource(images.getResourceId(spinner.getSelectedItemPosition(), -1));
            song = MediaPlayer.create(getApplicationContext(), songs[position]);
            song.setOnCompletionListener(this);
            handler.post(UpdateSongTime);
        }

        this.position = position;
        songProgress.setProgress(0);
        songProgress.setMax(song.getDuration());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (Math.abs(song.getCurrentPosition() - progress) >= 1500) {
            song.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == play.getId()) {
            play();
        } else if (v.getId() == next.getId()) {
            next();
        } else if (v.getId() == previous.getId()) {
            previous();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(position == spinnerAdapter.getCount() - 1) {
            position = 0;
        } else {
            position++;
        }
        spinner.setSelection(position);
        song = MediaPlayer.create(getApplicationContext(), songs[position]);
        song.setOnCompletionListener(this);
        songProgress.setProgress(0);
        songProgress.setMax(song.getDuration());
        song.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            int index = sound.getProgress();
            sound.setProgress(index + 1);
            return  true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            int index = sound.getProgress();
            sound.setProgress(index - 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void play() {
        if(song.isPlaying()) {
            song.pause();
            play.setImageResource(R.drawable.ic_play);
            song.setOnCompletionListener(this);
        } else {
            song.start();
            play.setImageResource(R.drawable.ic_pause);
            song.setOnCompletionListener(this);
        }
    }

    private void next() {
        boolean shouldPlay = false;
        if(song.isPlaying()) {
            song.stop();
            shouldPlay = true;
        }
        if(position == spinnerAdapter.getCount() - 1) {
            position = 0;
        } else {
            position++;
        }
        spinner.setSelection(position);
        song = MediaPlayer.create(getApplicationContext(), songs[position]);
        song.setOnCompletionListener(this);
        songProgress.setProgress(0);
        songProgress.setMax(song.getDuration());
        if (shouldPlay) {
            song.start();
        }
    }

    private void previous() {
        boolean shouldPlay = false;
        if(song.isPlaying()) {
            song.stop();
            shouldPlay = true;
        }
        if(position == 0) {
            position = spinnerAdapter.getCount() - 1;
        } else {
            position--;
        }
        spinner.setSelection(position);
        song = MediaPlayer.create(getApplicationContext(), songs[position]);
        song.setOnCompletionListener(this);
        songProgress.setProgress(0);
        songProgress.setMax(song.getDuration());
        if (shouldPlay) {
            song.start();
        }
    }

    private void load_song() {
        songs[0] = R.raw.simple;
        songs[1] = R.raw.crazy;
        songs[2] = R.raw.hell;
        songs[3] = R.raw.girl;
        songs[4] = R.raw.alright;
        songs[5] = R.raw.up;
        songs[6] = R.raw.life;
    }

    private  void  initControls() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            sound.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

            sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sound.getProgress(), 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } catch (Exception e) {

        }
    }

    private Runnable UpdateSongProgress = new Runnable() {
        @Override
        public void run() {
            songProgress.setProgress(song.getCurrentPosition());
            sound.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            handler.post(this);
        }
    };

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            eTime = song.getDuration();
            sTime = song.getCurrentPosition();
            oTime = eTime - sTime;

            long timeLeftSec = TimeUnit.MILLISECONDS.toSeconds(oTime) % 60;
            String timeLeftSecStr = timeLeftSec < 10 ? ("0" + String.valueOf(timeLeftSec)) : String.valueOf(timeLeftSec);
            timeLeft.setText(String.format(Locale.getDefault(), "-%d:%s", TimeUnit.MILLISECONDS.toMinutes(oTime), timeLeftSecStr));

            long timePlayedSec = TimeUnit.MILLISECONDS.toSeconds(sTime) % 60;
            String timePlayedSecStr = timePlayedSec < 10 ? ("0" + String.valueOf(timePlayedSec)) : String.valueOf(timePlayedSec);
            timePlayed.setText(String.format(Locale.getDefault(), "%d:%s", TimeUnit.MILLISECONDS.toMinutes(sTime), timePlayedSecStr));

            handler.postDelayed(this,1000);
        }
    };
}