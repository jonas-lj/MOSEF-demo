package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFModule;
import dk.jonaslindstrom.mosef.modules.oscillator.ExactOscillator;
import java.util.ArrayList;
import java.util.List;

/**
 * This demo application tests subtractive synthesis. A square wave signal is modulated with other
 * square waves with nearby frequencies and then sent through a low pass filter.
 * 
 * @author Jonas Lindstrøm (jonas.lindstrom@alexandra.dk)
 *
 */
public class ExactOscillators {

  public static void main(String[] arguments) {

    MOSEFSettings settings = new MOSEFSettings(44100, 1024, 16);
    MOSEF m = new MOSEF(settings);

    List<MOSEFModule> o = new ArrayList<>();    
    int[] l = {150, 120, 100};
    for (int k : l) {
      o.add(new ExactOscillator(settings, k));
    }
    MOSEFModule[] oscillators = new MOSEFModule[l.length];
    o.toArray(oscillators);
    MOSEFModule mix = m.amplifier(m.mixer(o.toArray(oscillators)), 0.5f);

    m.audioOut(m.lowPassFilter(mix, 128.0f));
    
    m.start();

    long time = 30000;
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < time) {
      /* wait for it... */
    }
    m.stop();
  }

}
