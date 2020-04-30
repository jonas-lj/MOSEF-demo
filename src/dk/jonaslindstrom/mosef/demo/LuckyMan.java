package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.midi.MIDIParser;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.envelope.ADSREnvelope;
import dk.jonaslindstrom.mosef.modules.glide.Glide;
import dk.jonaslindstrom.mosef.modules.melody.SimpleMelody;
import dk.jonaslindstrom.mosef.modules.tuning.tuningfunction.WellTemperedTuningFunction;
import dk.jonaslindstrom.mosef.util.Pair;
import dk.jonaslindstrom.mosef.util.Utils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.sound.midi.InvalidMidiDataException;

/**
 * This demo plays the solo from "Lucky Man" by Emerson, Lake & Palmer.
 *
 * @author Jonas Lindstrøm (jonas.lindstrom@alexandra.dk)
 */
public class LuckyMan {

  public static void main(String[] arguments)
      throws InvalidMidiDataException, IOException, InterruptedException {

    MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
    MOSEF m = new MOSEF(settings);

    SimpleMelody melody = MIDIParser.parse("elp.mid");
    Pair<Module, Module> melodyModule = melody
        .getMonophonicVoice(settings, new WellTemperedTuningFunction());

    Module v1 = buildVoice(settings, melodyModule.first, melodyModule.second);

    Module filtered = m.filter(v1, 500);

    Module out = m.amplifier(filtered, 0.2);

    Module[] delaySplit = m.split(out, 2);

    m.audioOut(delaySplit[0], m.delay(delaySplit[1], 0.01));
    m.start();

    TimeUnit.MINUTES.sleep(2);

    m.stop();
  }

  private static Module buildVoice(MOSEFSettings settings, Module in, Module gate) {
    MOSEF m = new MOSEF(settings);

    Module f = new Glide(settings, in, 1800);

    Module[] gateSplit = m.split(gate, 2);
    Module[] fSplit = m.split(f, 3);

    Module square1 = m.square(m.multiplier(fSplit[0], Utils.ratioFromCents(3)));
    Module square2 = m.square(m.multiplier(fSplit[1], Utils.ratioFromCents(-4)));
    Module square3 = m.square(m.multiplier(fSplit[2], Utils.ratioFromCents(11)));

    Module mix = m.mixer(square1, square2, m.amplifier(square3, 0.5));

    Module envelope = new ADSREnvelope(settings, gateSplit[0], 0.01, 1.0, 0.8, 0.8);

    return m.amplifier(mix, envelope);
  }

}
