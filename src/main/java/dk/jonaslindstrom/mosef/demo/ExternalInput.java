package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.Module;

public class ExternalInput {

  public static void main(String[] args) {

    MOSEFSettings settings = new MOSEFSettings(44100, 1024, 16);
    MOSEF m = new MOSEF(settings);

    Module external = m.audioIn();
    m.audioOut(m.echo(external, 3, m.constant(0.2f), 2.0f));
    
    m.start();
    long time = 20000;
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < time) {
      /* wait for it... */
    }    
    m.stop();

  }

}
