package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.envelope.Envelope;
import dk.jonaslindstrom.mosef.modules.sequencers.ClockFixed;
import dk.jonaslindstrom.mosef.modules.sequencers.EuclideanRhythm;
import dk.jonaslindstrom.mosef.modules.sequencers.Rhythm;

/**
 * This application tests pulse width modulation synthesis where the widt of a
 * pulse wave is modulated by an LFO.
 * 
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class EuclideanRhythmBox {

	public static void main(String[] arguments) {
		
		int n = 8;
		int k = 3;
		
		MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
		MOSEF m = new MOSEF(settings);

		int[] r = new EuclideanRhythm(k, n).getRhythm();
		Module clock = new Rhythm(settings, new ClockFixed(settings, 240), r);

		Module input = m.noise();
		Module envelope = new Envelope(settings, m.constant(0.01f), m.constant(0.01f),
					m.constant(1.0f), m.constant(0.1f), clock);

		m.audioOut(m.amplifier(input, envelope));
		m.start();
		
		long time = 10000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		m.stop();
		
	}
	
}
