package dk.jonaslindstrom.mosef.demo;

import dk.jonaslindstrom.mosef.MOSEF;
import dk.jonaslindstrom.mosef.MOSEFSettings;
import dk.jonaslindstrom.mosef.modules.MOSEFModule;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class emulates an electric drawbar organ.
 * 
 * @author Jonas Lindstrøm (mail@jonaslindstrom.dk)
 *
 */
public class Harmonies {

  private static class Fraction {
    private int numerator;
    private int denominator;

    public Fraction(int numerator, int denominator) {
      this.setNumerator(numerator);
      this.setDenominator(denominator);
    }

    public int getNumerator() {
      return numerator;
    }

    private void setNumerator(int numerator) {
      this.numerator = numerator;
    }

    public int getDenominator() {
      return denominator;
    }

    private void setDenominator(int denominator) {
      this.denominator = denominator;
    }

    @Override
    public String toString() {
      return numerator + "/" + denominator;
    }

    public float asFloat() {
      return (float) numerator / denominator;
    }
  }

  private static int BASE = 16;

  private static List<Fraction> getFarey(Fraction left, Fraction right) {
    Fraction mediant = new Fraction(left.getNumerator() + right.getNumerator(),
        left.getDenominator() + right.getDenominator());
    if (mediant.getDenominator() > BASE) {
      return Collections.emptyList();
    }
    List<Fraction> result = new LinkedList<>();
    result.addAll(getFarey(left, mediant));
    result.add(mediant);
    result.addAll(getFarey(mediant, right));
    return result;
  }

  private static List<Fraction> getFarey() {
    List<Fraction> result = new LinkedList<>();

    Fraction left = new Fraction(0, 1);
    Fraction right = new Fraction(1, 1);

    result.add(left);
    result.addAll(getFarey(left, right));
    result.add(right);

    return result;
  }

  public static void main(String[] args) {

    final MOSEFSettings settings = new MOSEFSettings(44100, 512, 16);
    MOSEF m = new MOSEF(settings);

    float f = 300.0f;

    List<Fraction> seq = getFarey();
    float[] farey = new float[seq.size()];
    IntStream.range(0, seq.size()).forEach(i -> {
      if (i % 2 ==0) {
        farey[i] = seq.get(i).asFloat();
      } else {
        farey[i] = -seq.get(i).asFloat() / 2.0f;
      }
    });

    MOSEFModule o1 = m.triangle(f);
    MOSEFModule o2 = m.triangle(f * 5/4);
    MOSEFModule o3 = m.triangle(f * 7/4);
    MOSEFModule o4 = m.triangle(f * 3/2);
//    Module o2 = m.triangle(f * a/(a-1));
//    Module o3 = m.triangle(f * (a+1) / (a-1));
    
//    Module p = new Periodic(settings, new ClockFixed(settings, 200), farey);
//    Module o2 = m.sine(new SSampler(settings, new SAmplifier((m.mixer(new SSampler(settings, new SConstant(1.0f)), p), f));

    m.audioOut(m.amplifier(m.mixer(o1, o2, o3, o4), 0.02f));
    m.start();

    long time = 5000;
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < time) {
      /* wait for it... */
    }
    m.stop();
  }

}
