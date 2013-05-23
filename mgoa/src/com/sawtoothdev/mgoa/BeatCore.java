package com.sawtoothdev.mgoa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.sawtoothdev.audioanalysis.Beat;

public class BeatCore implements IGameObject, Poolable {

	// accuracy feedback
	public static enum Accuracy {
		STELLAR, PERFECT, EXCELLENT, GOOD, ALMOST, INACTIVE
	};

	// animation
	private final float shrinkRate;
	private static final float SYNCH_SIZE = .45f;

	// gfx
	private final Sprite ring, core;
	private Color c;
	private float alpha = 1;

	// mechanical
	private Vector2 position;
	private Beat beat;

	// state
	private boolean complete = false;
	private boolean beenHit = false;
	private boolean fading = false;

	public BeatCore() {
		// delta size / delta time
		shrinkRate = (1 - SYNCH_SIZE) / (0 - (Resources.difficulty.ringTimeMs / 1000f));

		Texture t = new Texture("data/textures/ring.png");
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		ring = new Sprite(t, 174, 179);
		ring.setSize(1.6f, 1.6f * ring.getHeight() / ring.getWidth());
		
		Texture y = new Texture("data/textures/core.png");
		y.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		core = new Sprite(y);

		reset();
	}

	// lifecycle
	public void setBeat(Beat beat) {
		this.beat = beat;

		Color coreColor;

		if (beat.energy > .75)
			coreColor = Color.RED;
		else if (beat.energy > .6)
			coreColor = Color.ORANGE;
		else if (beat.energy > .45)
			coreColor = Color.YELLOW;
		else if (beat.energy > .3)
			coreColor = Color.GREEN;
		else if (beat.energy > .15)
			coreColor = Color.BLUE;
		else
			coreColor = Color.MAGENTA;

		this.core.setColor(coreColor);
	}

	@Override
	public void render(float delta) {

		{// update

			// approach circle
			if (ring.getScaleX() > SYNCH_SIZE && !beenHit && !fading)
				ring.scale(delta * shrinkRate);
			else if (!fading)
				fading = true;

			
			
			{// colors and fading

				c = ring.getColor();
				
				// approach circle fade-in
				if (c.a < .95f && !fading) {
					float alpha = c.a + (delta * 3) > 1 ? 1 : c.a + (delta * 3);
					ring.setColor(c.r, c.g, c.b, alpha);
				}

				// fade-out
				if (fading) {
					if (alpha > .05) {
						alpha -= (delta * 2);

						// make sure the alpha isn't set below 0
						alpha = alpha < 0 ? 0 : alpha;

						c = core.getColor();
						core.setColor(c.r, c.g, c.b, alpha);
						c = ring.getColor();
						ring.setColor(c.r, c.g, c.b, alpha);
					} else if (c.a <= .05f)
						complete = true;
				}
			}

		}

		{// draw
			
			core.draw(Resources.worldBatch);
			ring.draw(Resources.worldBatch);
		}
	}

	@Override
	public void reset() {

		ring.setOrigin(ring.getWidth() / 2f, ring.getHeight() / 2f);
		ring.setScale(1);
		
		core.setSize(.8f, .8f * core.getHeight() / core.getWidth());

		ring.setColor(1, 1, 1, 0f);
		beat = null;
		complete = false;
		beenHit = false;
		fading = false;
		alpha = 1;

	}

	// modifiers
	public void setPosition(Vector2 worldPos) {

		this.position = worldPos;

		ring.setPosition(worldPos.x - ring.getWidth() / 2f,
				worldPos.y - ring.getHeight() / 2f);
		core.setPosition(worldPos.x - core.getWidth() / 2f,
				worldPos.y - core.getHeight() / 2f);

	}

	public Accuracy onHit(long songTimeMs) {
		long diff = songTimeMs - beat.timeMs;

		Gdx.app.log("diff", String.valueOf(diff));

		if (beenHit || diff < -300)
			return Accuracy.INACTIVE;
		else {
			beenHit = true;
			fading = true;

			if (diff < -210)
				return Accuracy.ALMOST;
			else if (diff < -150)
				return Accuracy.GOOD;
			else if (diff < -90)
				return Accuracy.EXCELLENT;
			else if (diff < -30)
				return Accuracy.PERFECT;
			else if (diff < 40)
				return Accuracy.STELLAR;
			else if (diff < 120)
				return Accuracy.PERFECT;
			else if (diff < 200)
				return Accuracy.EXCELLENT;
			else if (diff < 280)
				return Accuracy.GOOD;
			else if (diff < 400)
				return Accuracy.ALMOST;
			else
				return Accuracy.INACTIVE;
		}
		
	}

	// readers
	public boolean isComplete() {
		return complete;
	}

	public Rectangle getHitbox() {
		return new Rectangle(position.x - .5f, position.y - .5f, 1f, 1f);
	}

	public int getScoreValue() {
		return (int) (beat.energy * 1000);
	}

	public Vector2 getPosition() {
		return position;
	}

	public boolean beenHit() {
		return beenHit;
	}
}
