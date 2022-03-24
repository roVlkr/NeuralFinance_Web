package data;

import math.Vektor;
import util.BinaryConverter;


/**
 * Konvertiert die Daten so, dass die logarithmisierten multiplikativen Zuwächse
 * betrachtet werden können.
 * 
 * @author Roland Völker
 */
public class Logalizer implements BinaryConverter {

  /**
   * Der originale Anfangswert der Liste. Dies ist zum Wiederherstellen der
   * Daten wichtig.
   */
  private ChartPoint startValue;


  public Logalizer(Chart original) {
    this.startValue = original.get(0);
  }


  /**
   * Implementiert die Funktion (a, b) -> Math.log(b / a).
   */
  @Override
  public double convert(double first, double second) {
    return Math.log(second / first);
  }


  /**
   * Implementiert die Funktion (a, b) -> a * exp(b).
   */
  @Override
  public double reconvert(double original, double converted) {
    return original * Math.exp(converted);
  }


  @Override
  public Chart convertChart(Chart original) {
    var convertedList = new Chart();
    ChartPoint previousValue = null;

    for (var point : original) {
      if (previousValue != null)
        convertedList
            .add(ChartPoint.apply(this::convert, previousValue, point));

      previousValue = point;
    }

    return convertedList;
  }


  @Override
  public Chart reconvertChart(Chart converted) {
    var reconvertedList = new Chart();
    ChartPoint lastValue = null;

    for (var point : converted) {
      if (lastValue != null)
        lastValue = ChartPoint.apply(this::reconvert, lastValue, point);
      else
        lastValue = ChartPoint.apply(this::reconvert, startValue, point);

      reconvertedList.add(lastValue);
    }

    return reconvertedList;
  }


  @Override
  public Vektor convertVektor(Vektor vektor) {
    var convertedVektor = new Vektor(vektor);
    double value = vektor.get(0);

    for (int i = 1; i < vektor.getLength(); i++)
      vektor.set(i, convert(value, vektor.get(i)));

    return convertedVektor;
  }


  @Override
  public Vektor reconvertVektor(Vektor vektor, String key) {
    var reconvertedVektor = new Vektor(vektor);
    double value = startValue.get(key);

    for (int i = 0; i < vektor.getLength(); i++) {
      value = reconvert(value, vektor.get(i));
      reconvertedVektor.set(i, value);
    }

    return reconvertedVektor;
  }
}
