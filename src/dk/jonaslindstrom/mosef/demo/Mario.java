package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.midi.MIDIParser;
import dk.jonaslindstrom.mosef.modules.Module;
import dk.jonaslindstrom.mosef.modules.tuning.tuningfunction.WellTemperedTuningFunction;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.sound.midi.InvalidMidiDataException;

/**
 *
 * Super Mario Bros theme.
 *
 * @author Jonas Lindstrøm (jonas.lindstrom@alexandra.dk)
 */
public class Mario {

  public static void main(String[] arguments)
      throws InvalidMidiDataException, IOException, InterruptedException {

    MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
    MOSEF m = new MOSEF(settings);

    List<Module> voices = MIDIParser.parse("mario.mid")
        .getPolyphonicVoices(settings, new WellTemperedTuningFunction(), 8).stream()
        .map(mel -> buildVoicePwm(m, mel.first, mel.second)).collect(Collectors.toList());

    Module mix = m.mixer(voices);
    Module out = m.amplifier(mix, 0.2);

    m.audioOut(out);

    m.start();
    TimeUnit.MINUTES.sleep(2);
    m.stop();
  }

  private static Module buildVoice(MOSEF m, Module in, Module gate) {

    Module[] inSplit = m.split(in, 3);



    Module osc1 = m.square(inSplit[0]);
    Module osc2 = m.saw(inSplit[1]);
    //Module osc3 = m.amplifier(m.square(m.multiplier(inSplit[2], 0.5)), 0.2);
    //Module noise = m.amplifier(m.noise(), 0.01);

    Module envelope = m.envelope(gate, 0.01, 0.2, 0.0, 0.2);

    return m.amplifier(m.mixer(osc1, osc2), envelope);
  }

  private static Module buildVoicePwm(MOSEF m, Module in, Module gate) {
    Module osc =
        m.pulse(in, m.constant(0.4));
    Module envelope = m.envelope(gate, 0.01, 0.2, 0.0, 0.2);
    return m.amplifier(osc, envelope);

  }

}
