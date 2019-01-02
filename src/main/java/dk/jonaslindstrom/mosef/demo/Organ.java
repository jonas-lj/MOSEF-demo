package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFModule;
import dk.jonaslindstrom.mosef.modules.oscillator.waves.TriangleWave;
import dk.jonaslindstrom.mosef.modules.oscillator.waves.Wave;

/**
 * This class emulates an electric drawbar organ.
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class Organ {

	public static void main(String[] args) {
		
		MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
		MOSEF m = new MOSEF(settings);

		float f = 261.626f;
		
		MOSEFModule frequency = m.constant(f);
		MOSEFModule[] frequencySplits = m.split(frequency, 4);
		
		Wave wave = new TriangleWave(settings);

		float[] drawbars = new float[] { 1.0f, 1.0f, 1.0f };
		
		MOSEFModule o1 = m.amplifier(m.constant(0.1f), m.organ(frequencySplits[0], wave,
				m.constants(drawbars)));
		
		// Third
		MOSEFModule o2 = m.amplifier(m.constant(0.1f), m.organ(m.amplifier(frequencySplits[1], 5.0f / 4.0f), wave,
				m.constants(drawbars)));
		
		// Fifth
		MOSEFModule o3 = m.amplifier(m.constant(0.1f), m.organ(m.amplifier(frequencySplits[2], 3.0f / 2.0f), wave,
				m.constants(drawbars)));
		
		// Lower octave
		MOSEFModule o4 = m.amplifier(m.constant(0.1f), m.organ(m.amplifier(frequencySplits[3], 1.0f / 2.0f), wave,
				m.constants(drawbars)));
		MOSEFModule chord = m.mixer(o1, o2, o3, o4);
		MOSEFModule organ = m.amplifier(m.ensemble(chord), m.constant(0.2f));
		
		// Chorus
		MOSEFModule[] chorusSplits = m.split(organ, 2);
		MOSEFModule dry = chorusSplits[0];
		MOSEFModule lfo = m.center(m.sine(m.constant(3.0f)), m.constant(0.0035f), m.constant(0.0002f));
		MOSEFModule wet = m.delay(chorusSplits[1], lfo, 0.01f);
		
		MOSEFModule mix = m.mixer(dry, wet);
		MOSEFModule out = m.amplifier(mix, 0.3f);
		
		m.audioOut(out);
		m.start();
		
		long time = 10000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		
		m.stop();
		
	}

}
