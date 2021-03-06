package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.Module;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Here, we apply ring modulation to a speech sample, emulating something
 * similar to that of a Dalek of Dr. Who.
 * 
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class Dalek {

	public static void main(String[] args)
			throws UnsupportedAudioFileException, IOException, InterruptedException {

		MOSEF m = new MOSEF(new MOSEFSettings(44100, 512, 16));
		Module input = m.sample(new File("samples/exterminate.wav"));
		
		Module lfo = m.amplifier(m.sine(m.constant(30)), 2);
		Module ringModulator = m.multiplier(input, lfo);
		
		Module clip = m.limiter(ringModulator, m.constant(0.6));
				
		Module filter = m.filter(clip, 7000);
		
		m.audioOut(filter);
		m.start();
		TimeUnit.SECONDS.sleep(5);
		m.stop();
	}

}
