package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFFactory;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.filter.LowPassFilter;
import dk.jonaslindstrom.mosef.modules.filter.filters.windows.HannPoissonWindow;
import dk.jonaslindstrom.mosef.modules.output.Output;

/**
 * This demo application tests subtractive synthesis. A square wave signal is
 * modulated with other square waves with nearby frequencies and then sent
 * through a low pass filter.
 * 
 * @author Jonas Lindstrøm (jonas.lindstrom@alexandra.dk)
 *
 */
public class SubtractiveSynthesis {

	public static void main(String[] arguments) {
		
		MOSEFSettings settings = new MOSEFSettings(44100, 1024, 16);
		MOSEFFactory m = new MOSEFFactory(settings);
		
		Module lfo1 = m.sine(m.constant(0.2f));
		Module base = m.center(lfo1, m.constant(128.0f), m.constant(100.0f));
		
		Module[] splits = m.split(base, 3);
		Module off1 = m.amplifier(splits[1], m.constant(1.003f));
		Module off2 = m.amplifier(splits[2], m.constant(0.994f));
		Module[] osc = new Module[] {m.square(splits[0]), m.square(off1), m.square(off2)};
		Module mix = m.amplifier(m.mixer(osc), 0.2f);
				
		Module lfo2 = m.center(m.sine(m.constant(0.15f)), m.constant(2048.0f), m.constant(1024.0f));
		Module filter = new LowPassFilter(settings, mix, lfo2, 512, 101,
				new HannPoissonWindow(101, 0.5));
		
		Output output = new Output(settings, filter);
		output.start();
		
		long time = 10000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < time) {
			/* wait for it... */
		}
		output.stop();
	}
	
}
