package dk.jonaslindstrom.mosef.demo;

import java.io.File;

import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFFactory;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.filter.LowPassFilterFixed;
import dk.jonaslindstrom.mosef.modules.limiter.Distortion;
import dk.jonaslindstrom.mosef.modules.output.Output;

/**
 * This application tests applying the low pass filter to a rich input signal
 * consisting of square waves.
 * 
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class Chorus {

	public static void main(String[] args) {

		MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
		MOSEFFactory m = new MOSEFFactory(settings);
				
		Module input = m.sample(new File("samples/guitar.wav"));

		// Overdrive
		Module drive = new Distortion(settings, input, m.constant(0.2f));
		
		// Chorus
		Module[] splits = m.split(drive, 2);
		Module dry = splits[0];
		
		Module lfo = m.center(m.sine(m.constant(5.0f)), m.constant(0.0035f), m.constant(0.0005f));
		Module wet = m.delay(splits[1], lfo, 0.01f);
		
		Module mix = m.mixer(dry, wet);
				
		Module out = m.amplifier(mix, 0.2f);
		
		Module filter = new LowPassFilterFixed(settings, out, 10000.0f);
		
		Output output = new Output(settings, filter);
		output.start();
		
		long time = 5000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		output.stop();
		
	}

}
