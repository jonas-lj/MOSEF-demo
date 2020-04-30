package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.feedback.Feedback;
import dk.jonaslindstrom.mosef.modules.filter.filters.VCF;
import dk.jonaslindstrom.mosef.modules.oscillator.Oscillator;
import dk.jonaslindstrom.mosef.modules.oscillator.waves.MoogSquareWave;
import dk.jonaslindstrom.mosef.modules.sequencers.ClockFixed;
import dk.jonaslindstrom.mosef.modules.sequencers.Periodic;
import dk.jonaslindstrom.mosef.modules.tuning.tuningfunction.TuningFunction;
import dk.jonaslindstrom.mosef.modules.tuning.tuningfunction.WellTemperedTuningFunction;
import java.util.concurrent.TimeUnit;

/**
 * This demo application tests subtractive synthesis. A square wave signal is modulated with other
 * square waves with nearby frequencies and then sent through a low pass filter.
 * 
 * @author Jonas Lindstrøm (jonas.lindstrom@alexandra.dk)
 *
 */
public class SubtractiveSynthesis {

  public static void main(String[] arguments) throws InterruptedException {

    MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
    MOSEF m = new MOSEF(settings);

    Module clock = new ClockFixed(settings, 600);
    TuningFunction tuner = new WellTemperedTuningFunction();
    Module sequencer = new Periodic(settings, clock, new double[] {
            tuner.getFrequency(40),
            tuner.getFrequency(43),
            tuner.getFrequency(47),
            tuner.getFrequency(52),
            tuner.getFrequency(47),
            tuner.getFrequency(43)
    });

    int n = 5;
    Module[] splits = m.split(sequencer, n);
    Module[] offsets = new Module[n];

    offsets[0] = m.square(splits[0]);
    for (int i = 1; i < n; i++) {
      Module off = m.multiplier(splits[i], 1.0 + 2.0 * (Math.random() - 0.5) * 0.005);
      offsets[i] = m.square(off);
    }

    Module mix =
        m.amplifier(m.mixer(offsets), 0.1);

    // Add resonance
    Feedback feedback = m.feedback(mix, 0.1);

    Module lfo = m.offset(m.sine(0.05), 2000, 1800);
    Module filtered = m.vcf(mix, lfo);

    Module mod = feedback.attachFeedback(filtered);

    m.audioOut(mod);
    m.start();
    TimeUnit.SECONDS.sleep(60);
    m.stop();

  }

}
