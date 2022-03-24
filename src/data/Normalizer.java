package data;

import math.Vektor;
import util.UnaryConverter;


/**
 * Bringt eine normalverteilte Datenmenge auf die Standardabweichung 2 und den
 * Erwartungswert 0 (sodass die sigmoid- Funktion möglichst viele verschiedene
 * Werte annimmt).
 * 
 * 
 * @author Roland Völker
 */
public class Normalizer implements UnaryConverter {

  /**
   * Der Erwartungswert der Daten.
   */
  private double expectedValue;

  /**
   * Die Standardabweichung der Daten vom Erwartungswert.
   */
  private double standardDeviation;


  /**
   * Erstellt einen Normalizer. Dabei können die Parameter offset und
   * referenceValue noch beim ersten Lesen der Daten ermittelt werden.
   */
  public Normalizer(Chart original) {
    var firstList = original.get(original.keySet().iterator().next());
    expectedValue = firstList.stream().reduce(0.,
        (a, b) -> a + b / firstList.size());

    standardDeviation = firstList.stream().reduce(0., (a, b) -> a
        + Math.pow((b - expectedValue), 2) / (firstList.size() - 1));
    standardDeviation = Math.sqrt(standardDeviation); // Sonst Varianz
  }


  @Override
  public Chart convertChart(Chart original) {
    return original.apply(this::convert);
  }


  @Override
  public Chart reconvertChart(Chart converted) {
    return converted.apply(this::reconvert);
  }


  /**
   * Implementiert die Funktion d -> (d - expectedValue) / standardDeviation;
   */
  @Override
  public double convert(double d) {
    return (d - expectedValue) / standardDeviation * 2;
  }


  /**
   * Implementiert die Funktion d -> d * standardDeviation + expectedValue;
   */
  @Override
  public double reconvert(double d) {
    return d * standardDeviation / 2 + expectedValue;
  }


  @Override
  public Vektor convertVektor(Vektor vektor) {
    return vektor.apply(this::convert);
  }


  @Override
  public Vektor reconvertVektor(Vektor vektor, String key) {
    return vektor.apply(this::reconvert);
  }
}
