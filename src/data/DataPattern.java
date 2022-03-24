package data;

import math.Vektor;


/**
 * B�ndelt ein Trainingsmuster, bestehend aus einem Eingabevektor und einem
 * (Soll-)Ausgabevektor.
 * 
 * @author Roland V�lker
 */
public class DataPattern {

  /**
   * Der Muster-Eingabevektor.
   */
  private final Vektor input;

  /**
   * Der Muster-/Soll-Ausgabevektor.
   */
  private final Vektor output;

  /**
   * Gibt an, wie oft das Training f�r dieses Pattern wiederholt werden soll
   * (Standardwert: 1).
   */
  private final int priority;


  /**
   * Erstellt ein Pattern f�r das Training. Dazu wird die Eingabeliste in ein
   * Vektor-Objekt �bertragen und aus der Outputliste wird der Soll-Sch�tzwert
   * errechnet. Au�erdem wird angegeben, wie oft das Pattern wiederholt werden
   * soll.
   * 
   * @param input    Die Eingabedaten.
   * @param output   Die Soll-Ausgabedaten.
   * @param priority Die Anzahl der Wiederholungen f�r dieses Pattern im
   *                 Training.
   */
  public DataPattern(Vektor input, Vektor output, int priority) {
    this.input = input;
    this.output = output;

    this.priority = priority;
  }


  /**
   * Erstellt ein Pattern f�r das Training. Dazu wird die Eingabeliste in ein
   * Vektor-Objekt �bertragen und aus der Outputliste wird der Soll-Sch�tzwert
   * errechnet.
   * 
   * @param input  Die Eingabedaten.
   * @param output Die Soll-Ausgabedaten.
   */
  public DataPattern(Vektor input, Vektor output) {
    this(input, output, 1);
  }


  /**
   * Gibt den Muster-Eingabevektor zur�ck.
   * 
   * @return Den Eingabevektor des Patterns.
   */
  public Vektor getInput() {
    return input;
  }


  /**
   * Gibt den Muster-/Soll-Ausgabevektor zur�ck.
   * 
   * @return Den Ausgabevektor des Patterns.
   */
  public Vektor getOutput() {
    return output;
  }


  /**
   * Gibt an, wie oft das Training f�r dieses Pattern wiederholt werden soll.
   * 
   * @return Anzahl, wie oft das Training f�r dieses Pattern wiederholt werden
   *         soll.
   */
  public int getPriority() {
    return priority;
  }
}
