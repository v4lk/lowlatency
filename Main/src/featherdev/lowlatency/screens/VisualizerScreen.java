package featherdev.lowlatency.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import featherdev.lowlatency.LowLatency;
import featherdev.lowlatency.game.LightShow;
import featherdev.lowlatency.subsystems.Holder;
import featherdev.lowlatency.subsystems.MusicPlayer;

public class VisualizerScreen implements Screen {
    enum State {RUNNING, PAUSED}

    LightShow lightshow;
    State state;
    Stage pausedMenu;


    public VisualizerScreen() {
        MusicPlayer.instance().load(Holder.song.getHandle());
        lightshow = new LightShow(Holder.rawmap);

        pausedMenu = new Stage();
        TextButton unpause = new TextButton("Resume", LowLatency.instance().skin);
        unpause.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                state = State.RUNNING;
                Gdx.input.setInputProcessor(null);
                MusicPlayer.instance().play();
                super.clicked(event, x, y);
            }
        });
        TextButton quit = new TextButton("Quit to Main Menu", LowLatency.instance().skin);
        quit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(null);
                LowLatency.instance().setScreen(new MenuScreen());
                super.clicked(event, x, y);
            }
        });
        Table root = new Table();
        root.defaults().pad(10);
        root.defaults().fill();
        root.setSkin(LowLatency.instance().skin);
        root.add("Paused");
        root.row();
        root.add(unpause);
        root.row();
        root.add(quit);
        root.setFillParent(true);
        pausedMenu.addActor(root);
    }

    public void render(float delta) {

        switch (state) {
            case RUNNING:
                if (!MusicPlayer.instance().isPlaying())
                    LowLatency.instance().setScreen(new MenuScreen());

                lightshow.update(delta);
                lightshow.draw(null);

                if (Gdx.input.justTouched()) {
                    state = State.PAUSED;
                    MusicPlayer.instance().pause();
                    Gdx.input.setInputProcessor(pausedMenu);
                }
                break;
            case PAUSED:
                lightshow.draw(null);
                pausedMenu.act();
                pausedMenu.draw();
                break;
        }
    }

    public void resize(int width, int height) {

    }

    public void show() {
        MusicPlayer.instance().play();
        state = State.RUNNING;
    }

    public void hide() {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void dispose() {

    }

}
