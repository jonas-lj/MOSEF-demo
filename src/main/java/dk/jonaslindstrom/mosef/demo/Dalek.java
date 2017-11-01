package dk.jonaslindstrom.mosef.demo;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.output.Output;

/**
 * Here, we apply ring modulation to a speech sample, emulating something
 * similar to that of a Dalek of Dr. Who.
 * 
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class Dalek {

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {

		MOSEF m = new MOSEF(new MOSEFSettings(44100, 512, 16));
		Module input = m.sample(new File("samples/exterminate.wav"));
		
		Module lfo = m.amplifier(m.sine(m.constant(30.0f)), 2.0f);
		Module ringModulator = m.multiplier(input, lfo);
		
		Module clip = m.limiter(ringModulator, m.constant(0.6f));
				
		Module filter = m.lowPassFilter(clip, 7000.0f);
		
		Output output = m.output(filter);
		output.start();
		
		long time = 6000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		output.stop();		
	}

}
