package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.CompositeModule;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.amplifier.Amplifier;
import dk.jonaslindstrom.mosef.modules.envelope.ADSREnvelope;
import dk.jonaslindstrom.mosef.modules.misc.Constant;
import dk.jonaslindstrom.mosef.modules.oscillator.Oscillator;
import dk.jonaslindstrom.mosef.modules.oscillator.waves.SquareWave;
import dk.jonaslindstrom.mosef.modules.sequencers.ClockFixed;
import dk.jonaslindstrom.mosef.modules.sequencers.EuclideanRhythm;
import dk.jonaslindstrom.mosef.modules.sequencers.Expander;
import dk.jonaslindstrom.mosef.modules.splitter.Splitter;

import java.util.Map;

public class EuclideanRhythmTest {

    private static class EuclideanDrum extends CompositeModule {

        public EuclideanDrum(MOSEFSettings settings, Module clock, Module frequency, int beats, int length) {
            super(settings, Map.of("Clock", clock, "Frequency", frequency), Map.of("Beats", Double.valueOf(beats), "Length", Double.valueOf(length)));
        }

        @Override
        public Module buildModule(MOSEFSettings settings, Map<String, Module> inputs, Map<String, Double> parameters) {
            Module euclidean = new EuclideanRhythm(settings, inputs.get("Clock"), parameters.get("Beats").intValue(), parameters.get("Length").intValue());
            Module expander = new Expander(settings, euclidean, new Constant(settings, 0.05));
            Module envelope = new ADSREnvelope(settings, expander,0.0, 0.0, 1.0, 0.1);
            Module osc = new Oscillator(settings, inputs.get("Frequency"), new SquareWave());
            Module vca = new Amplifier(settings, osc, envelope);
            return vca;
        }
    }

    public static void main(String[] arguments) {
        MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
        MOSEF m = new MOSEF(settings);

        Module clock = new ClockFixed(settings, 400);

        Module[] splitter  = Splitter.split(clock, 3);
        Module lfo = m.offset(m.sine(0.1), 220.0, 5.0);
        Module[] lfoSplit = Splitter.split(lfo, 3);

        Module v1 = new EuclideanDrum(settings, splitter[0], m.multiplier(lfoSplit[0], 5/4), 11, 31);
        Module v2 = new EuclideanDrum(settings, splitter[1], m.multiplier(lfoSplit[1], 1), 13, 37);
        Module v3 = new EuclideanDrum(settings, splitter[2], m.multiplier(lfoSplit[2], 3/2), 17, 23);

        Module mixer = m.mixer(v1, v2, v3);
        Module amplifier = m.amplifier(mixer,0.1);
        Module filter = m.filter(amplifier, 100.0);

        m.audioOut(filter);

        m.start();
        long time = 10000;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < time) {
            /* wait for it... */
        }
        m.stop();
    }

}
