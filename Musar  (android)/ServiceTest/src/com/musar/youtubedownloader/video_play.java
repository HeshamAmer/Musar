package com.musar.youtubedownloader;

import java.io.File;

import com.musar.gui.R;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class video_play extends Activity{
	 private VideoView videoView ;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_video);
		Bundle b = getIntent().getExtras();
		String link = b.getString("key");
		System.out.println("activity:"+link);
		//get the file name and then set readable the file and play the video
		File file=new File(link);
		file.setReadable(true, false);
		videoView=(VideoView) findViewById(R.id.videoview);
		//play the video
		MediaController mc = new MediaController(this);
	    mc.setAnchorView(videoView);
	    mc.setMediaPlayer(videoView);
	    videoView.setMediaController(mc);
	    videoView.setVideoPath(link);
	    videoView.start();
	}
}
