package dk.jonaslindstrom.mosef.demo;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFFactory;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.filter.LowPassFilterFixed;
import dk.jonaslindstrom.mosef.modules.limiter.Limiter;
import dk.jonaslindstrom.mosef.modules.output.Output;
import dk.jonaslindstrom.mosef.modules.sample.SampleFactory;

/**
 * Here, we apply ring modulation to a speech sample, emulating something
 * similar to that of a Dalek of Dr. Who.
 * 
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class Dalek {

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {

		MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
		MOSEFFactory m = new MOSEFFactory(settings);
				
		Module input = SampleFactory.fromFile(settings, new File("samples/exterminate.wav"), true);
		
		Module lfo = m.amplifier(m.sine(m.constant(30.0f)), 2.0f);
		Module ringModulator = m.amplifier(input, lfo);
		
		Module clip = new Limiter(settings, ringModulator, m.constant(0.6f));
				
		Module filter = new LowPassFilterFixed(settings, clip, 7000.0f);
		
		Output output = new Output(settings, filter);
		output.start();
		
		long time = 6000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		output.stop();		
	}

}