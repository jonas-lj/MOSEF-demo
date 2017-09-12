package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFFactory;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.filter.LowPassFilter;
import dk.jonaslindstrom.mosef.modules.filter.filters.windows.BartlettHannWindow;
import dk.jonaslindstrom.mosef.modules.output.Output;

/**
 * This application tests applying the low pass filter to a rich input signal
 * consisting of square waves.
 * 
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class Filter {

	public static void main(String[] args) {

		MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
		MOSEFFactory m = new MOSEFFactory(settings);
		
		Module osc = m.amplifier(m.mixer(m.square(m.constant(440.0f)), m.square(m.constant(660.0f))), 0.3f);
		
		Module lfo = m.center(m.sine(m.constant(0.2f)), m.constant(3000.0f), m.constant(2950.0f));
		Module filter = new LowPassFilter(settings, osc, lfo, 512, 101,
				new BartlettHannWindow(101));

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
