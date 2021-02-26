package net.darktree.virus.ui.sound;

import net.darktree.virus.logger.Logger;

import javax.sound.sampled.*;
import java.io.File;

public class Sounds {

    private static boolean play;
    public static final Sound CLICK = load("./data/sounds/click.wav", 0.5f);

    private static Sound load( String path, float volume ) {
        Clip clip = null;

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File( path ));
            DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
        } catch (Exception e) {
            Logger.warn( "Failed to load sound: '" + path + "'" );
            e.printStackTrace();
        }

        return new Sound( clip, volume );
    }

    public static void setEnabled( boolean flag ) {
        play = flag;
    }

    public static void init() {
        setEnabled(true);
    }

}
