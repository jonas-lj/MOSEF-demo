package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFModule;
import dk.jonaslindstrom.mosef.modules.filter.LowPassFilter;
import dk.jonaslindstrom.mosef.modules.filter.filters.windows.HannPoissonWindow;

/**
 * This demo application tests subtractive synthesis. A square wave signal is modulated with other
 * square waves with nearby frequencies and then sent through a low pass filter.
 * 
 * @author Jonas Lindstrøm (jonas.lindstrom@alexandra.dk)
 *
 */
public class SubtractiveSynthesis {

  public static void main(String[] arguments) {

    MOSEFSettings settings = new MOSEFSettings(44100, 1024, 16);
    MOSEF m = new MOSEF(settings);

    MOSEFModule lfo1 = m.sine(0.2f);
    MOSEFModule base = m.center(lfo1, 200.0f, 180.0f);

    MOSEFModule[] splits = m.split(base, 3);
    MOSEFModule off1 = m.amplifier(splits[1], 1.003f);
    MOSEFModule off2 = m.amplifier(splits[2], 0.993f);
    MOSEFModule mix =
        m.amplifier(m.mixer(m.square(splits[0]), m.square(off1), m.square(off2)), 0.2f);

    MOSEFModule lfo2 = m.center(m.sine(0.35f), 800.0f, 400.0f);
    MOSEFModule filtered =
        new LowPassFilter(settings, mix, lfo2, 512, 101, new HannPoissonWindow(101, 0.5));
    
    m.audioOut(filtered);    
    m.start();

    long time = 10000;
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < time) {
      /* wait for it... */
    }
    m.stop();
  }

}
