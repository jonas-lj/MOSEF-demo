package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.Module;
import java.io.File;

/**
 * This application tests applying the low pass filter to a rich input signal
 * consisting of square waves.
 * 
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class Chorus {

	public static void main(String[] args) {

		MOSEF m = new MOSEF(new MOSEFSettings(44100, 512, 16));
				
		Module input = m.sample(new File("samples/guitar.wav"));

		// Overdrive
		Module drive = m.distortion(input, m.constant(0.2));
		
		// Chorus
		Module[] splits = m.split(drive, 2);
		Module dry = splits[0];
		
		Module lfo = m.center(m.sine(5.0f), m.constant(0.0035), m.constant(0.0005));
		Module wet = m.delay(splits[1], lfo, 0.01);
		
		Module mix = m.mixer(dry, wet);
				
		Module out = m.amplifier(mix, 0.2);
		
		Module filter = m.lowPassFilter(out, 4000.0);
		
		m.audioOut(filter);
		m.start();
		
		long time = 5000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		m.stop();
		
	}

}
