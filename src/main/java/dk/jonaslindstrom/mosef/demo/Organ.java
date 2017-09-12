package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFFactory;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.misc.Constant;
import dk.jonaslindstrom.mosef.modules.oscillator.waves.TriangleWave;
import dk.jonaslindstrom.mosef.modules.oscillator.waves.Wave;
import dk.jonaslindstrom.mosef.modules.output.Output;

/**
 * This class emulates an electric drawbar organ.
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class Organ {

	public static void main(String[] args) {
		
		MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
		MOSEFFactory m = new MOSEFFactory(settings);

		float f = 261.626f;
		
		Constant frequency = new Constant(settings, f);
		Module[] frequencySplits = m.split(frequency, 4);
		
		Wave wave = new TriangleWave();

		float[] drawbars = new float[] { 1.0f, 1.0f, 1.0f };
		
		Module o1 = m.amplifier(m.constant(0.1f), m.organ(frequencySplits[0], wave,
				m.constants(drawbars)));
		
		// Third
		Module o2 = m.amplifier(m.constant(0.1f), m.organ(m.amplifier(frequencySplits[1], 5.0f / 4.0f), wave,
				m.constants(drawbars)));
		
		// Fifth
		Module o3 = m.amplifier(m.constant(0.1f), m.organ(m.amplifier(frequencySplits[2], 3.0f / 2.0f), wave,
				m.constants(drawbars)));
		
		// Lower octave
		Module o4 = m.amplifier(m.constant(0.1f), m.organ(m.amplifier(frequencySplits[3], 1.0f / 2.0f), wave,
				m.constants(drawbars)));
		Module chord = m.mixer(o1, o2, o3, o4);
		Module organ = m.amplifier(m.ensemble(chord), m.constant(0.2f));
		
		// Chorus
		Module[] chorusSplits = m.split(organ, 2);
		Module dry = chorusSplits[0];
		Module lfo = m.center(m.sine(m.constant(3.0f)), m.constant(0.0035f), m.constant(0.0002f));
		Module wet = m.delay(chorusSplits[1], lfo, 0.01f);
		
		Module mix = m.mixer(dry, wet);
		Module out = m.amplifier(mix, 0.3f);
		
		Output output = new Output(settings, out);
		output.start();
		
		long time = 1000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		
		frequency.setValue(f * 4.0f / 3.0f);
		
		start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}

		frequency.setValue(f * 3.0f / 2.0f);
		
		start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		
		output.stop();
		
	}

}
