package com.sawtoothdev.mgoa;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.sawtoothdev.audioanalysis.Beat;
import com.sawtoothdev.audioanalysis.BeatsProcessor;
import com.sawtoothdev.audioanalysis.FastBeatDetector;

/**
 * Responsible for loading all resources before gameplay begins.
 * This includes audio analysis, map generation, and graphics.
 * 
 * I put all the loading code its own thread so we can display
 * an interactive load screen at some point.
 * 
 * @author albatross
 *
 */

public class LoadScreen implements Screen {
	
	public class LoadingThread extends Thread{
		
		public FileHandle audioFile;
		public BeatMap map;
		
		public LoadingThread(FileHandle audioFile){
			this.audioFile = audioFile;
		}
		
		@Override
		public void run() {
			
			float sensitivity = FastBeatDetector.SENSITIVITY_STANDARD;
			
			ArrayList<Beat> beats = null;
			
			try {
				beats = FastBeatDetector.detectBeats(sensitivity, audioFile);
			} catch (IOException e) { Gdx.app.log("Load Screen", e.getMessage()); }
			
			
			ArrayList<Beat> easy, medium, hard, original;
			
			easy = BeatsProcessor.removeCloseBeats(beats, 250);
			medium = BeatsProcessor.removeCloseBeats(beats, 150);
			hard = BeatsProcessor.removeCloseBeats(beats, 100);
			original = beats;
			
			map = new BeatMap(easy, medium, hard, original);
		}
		
	}
	
	private LoadingThread loadThread;
	private BitmapFont font = new BitmapFont();
	
	
	public LoadScreen(){
		font.setScale(.1f);
	}
	
	@Override
	public void render(float delta) {
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		Resources.spriteBatch.begin();
		font.draw(Resources.spriteBatch, "Loading...", -4.8f, 2.8f);
		Resources.spriteBatch.end();
		
		//if done loading, move on
		if (!loadThread.isAlive()) {
			Resources.menuMusic.stop();
			PlayScreen playScreen = new PlayScreen(loadThread.map, loadThread.audioFile);
			Resources.game.setScreen(playScreen);
		}
	}

	@Override
	public void show() {
		loadThread = new LoadingThread(Resources.currentSong);
		loadThread.start();
	}
	
	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

}
