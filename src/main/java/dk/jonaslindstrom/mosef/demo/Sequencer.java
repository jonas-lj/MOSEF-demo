package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.envelope.Envelope;
import dk.jonaslindstrom.mosef.modules.filter.LowPassFilterFixed;
import dk.jonaslindstrom.mosef.modules.filter.filters.windows.HannPoissonWindow;
import dk.jonaslindstrom.mosef.modules.output.Output;
import dk.jonaslindstrom.mosef.modules.sequencers.ClockDivider;
import dk.jonaslindstrom.mosef.modules.sequencers.ClockFixed;
import dk.jonaslindstrom.mosef.modules.splitter.Splitter;

/**
 * This application tests a simple sequencer patch.
 * 
 * @author Jonas Lindstrøm (jonas.lindstrom@alexandra.dk)
 *
 */
public class Sequencer {

	public static void main(String[] arguments) {

		MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
		MOSEF m = new MOSEF(settings);

		float[] notes = new float[] { 120.0f, 150.0f, 90.0f, 60.0f };
		int[] periods = new int[] { 2, 3, 5, 7 };
		int n = notes.length;

		Module[] clocks = Splitter.split(new ClockFixed(settings, 300), n);

		Module[] outs = new Module[n];
		for (int i = 0; i < n; i++) {
			Module input = m.square(m.constant(notes[i]));
			Module envelope = new Envelope(settings, m.constant(0.01f), m.constant(0.01f),
					m.constant(1.0f), m.constant(0.3f),
					new ClockDivider(settings, clocks[i], periods[i]));
			outs[i] = m.amplifier(input, envelope);
		}
		Module mix = m.amplifier(m.mixer(outs), 0.3f);
		
		Module filter = new LowPassFilterFixed(settings, mix, 4096.0f, 101,
				new HannPoissonWindow(101, 0.5));

		Output output = new Output(settings, filter);
		output.start();

		long time = 10000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		output.stop();
		start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < 500) {
			/* wait for it... */
		}
	}

}
