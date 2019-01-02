package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFModule;
import dk.jonaslindstrom.mosef.modules.arpeggio.Arpeggio;

/**
 * This application tests pulse width modulation synthesis where the widt of a
 * pulse wave is modulated by an LFO.
 * 
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class PulseWidthModulation {

	public static void main(String[] arguments) {
		
		MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
		MOSEF m = new MOSEF(settings);

		MOSEFModule arpeggio = new Arpeggio(settings, m.constant(50.0f), m.constants(440.0f, 440.0f * 6.0f / 5.0f, 660.0f));
		MOSEFModule modulator = m.sine(m.constant(15.0f));
		MOSEFModule oscillator = m.pulse(arpeggio, m.center(modulator, m.constant(0.3f), m.constant(0.1f)));
		MOSEFModule out = m.amplifier(oscillator, 0.2f);
		
		m.audioOut(out);
		m.start();
		
		long time = 5000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		m.stop();
	}
	
}
