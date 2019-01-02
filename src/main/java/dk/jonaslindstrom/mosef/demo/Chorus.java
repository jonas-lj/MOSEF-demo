package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFModule;
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
				
		MOSEFModule input = m.sample(new File("samples/guitar.wav"));

		// Overdrive
		MOSEFModule drive = m.distortion(input, m.constant(0.2f));
		
		// Chorus
		MOSEFModule[] splits = m.split(drive, 2);
		MOSEFModule dry = splits[0];
		
		MOSEFModule lfo = m.center(m.sine(m.constant(5.0f)), m.constant(0.0035f), m.constant(0.0005f));
		MOSEFModule wet = m.delay(splits[1], lfo, 0.01f);
		
		MOSEFModule mix = m.mixer(dry, wet);
				
		MOSEFModule out = m.amplifier(mix, 0.2f);
		
		MOSEFModule filter = m.lowPassFilter(out, 5000.0f);
		
		m.audioOut(filter);
		m.start();
		
		long time = 50000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		m.stop();
		
	}

}
