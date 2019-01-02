package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFModule;
import dk.jonaslindstrom.mosef.modules.filter.LowPassFilter;
import dk.jonaslindstrom.mosef.modules.filter.filters.windows.HannPoissonWindow;
import dk.jonaslindstrom.mosef.printer.Printer;

/**
 * This demo application tests subtractive synthesis. A square wave signal is modulated with other
 * square waves with nearby frequencies and then sent through a low pass filter.
 * 
 * @author Jonas Lindstrøm (jonas.lindstrom@alexandra.dk)
 *
 */
public class Pad {

  public static void main(String[] arguments) {

    MOSEFSettings settings = new MOSEFSettings(44100, 1024, 16);
    MOSEF m = new MOSEF(settings);

    int n = 5;
    
    MOSEFModule base = m.center(m.sine(0.1f), 55.0f, 5.0f);
    MOSEFModule[] frequencies = m.split(base, n);
    MOSEFModule[] oscillators = new MOSEFModule[n];
    float scale = 1.0f;
    for (int i = 0; i < n; i++) {
      oscillators[i] = m.triangle(m.amplifier(frequencies[i], scale));
      scale *= 4.0f / 3.0f;
    }
    MOSEFModule mix = m.amplifier(m.mixer(oscillators), 0.5f / n);

    MOSEFModule lfo2 = m.constant(200.0f); //m.center(m.sine(0.1f), 256.0f, 200.0f);
    MOSEFModule filtered =
        new LowPassFilter(settings, mix, lfo2, 512, 101, new HannPoissonWindow(101, 0.5));
    
    m.audioOut(filtered);
    
    Printer printer = new Printer(filtered);
    System.out.println(printer.print());
    m.start();

    long time = 30000;
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < time) {
      /* wait for it... */
    }
    m.stop();
  }

}
