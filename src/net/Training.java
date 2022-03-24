package net;

import data.DataPattern;
import math.Matrix;
import math.Vektor;


/**
 * Trainiert ein neuronales Netz. Dabei kommt als Trainingsalgorithmus die
 * Variante RPROP (Resilient propagation) zum Einsatz.
 * 
 * @author Roland Völker
 */
public class Training {

  /**
   * Das zu trainierende Netz.
   */
  private Net net;

  /**
   * Die zu trainierenden Patterns. Ein Pattern besteht aus einem Eingabevektor
   * und einem Soll-Ausgabevektor.
   */
  private DataPattern patterns[];

  /**
   * Der Faktor zum Verringern der Gewichtsveränderungen.
   */
  private double c_p;

  /**
   * Der Faktor zum Erhöhen der Gewichtsveränderungen.
   */
  private double c_m;

  /**
   * Die aktuelle Epoche, also die bisherige Anzahl der Wiederholungen des
   * Trainings
   */
  private int epoch;

  /**
   * Die maximale Anzahl Epochen, bis zu der trainiert werden soll.
   */
  private int maxEpochs;


  /**
   * Erzeugt ein Objekt, das das angegebene Netz trainieren soll. Hierbei werden
   * die Grundparameter des RPROP-Algorithmus mitgegeben.
   * 
   * @param net            Das zu trainierende Netz.
   * @param increaseFactor Der Faktor zum Erhöhen der Gewichtsveränderungen.
   * @param decreaseFactor Der Faktor zum Verringern der Gewichtsveränderungen.
   */
  public Training(Net net, double increaseFactor, double decreaseFactor) {
    this.net = net;

    c_p = increaseFactor;
    c_m = decreaseFactor;

    epoch = 0;
  }


  /*
   * //////////////////////////////////////////////// Methoden zum Trainieren
   * des neuronalen Netzes ////////////////////////////////////////////////
   */

  /**
   * Implementiert die gewöhnliche Backpropagation. Hierbei handelt es sich um
   * die Fehlerrückübertragung vom letzten (Ausgabe-)Layer bis hin zum ersten
   * Hidden-Layer.
   * 
   * @param netOutput Die aktuelle Netzausgabe.
   * @param pattern   Das aktuelle Trainingspattern.
   * @throws Exception Möglicher Fehler bei der Rechnung mit Vektoren und
   *                   Matrizen.
   */
  private void backpropagation(Vektor netOutput, DataPattern pattern)
      throws Exception {
    var layerIter = net.end();
    Layer layer = layerIter.previous();

    var delta = Vektor.subtract(pattern.getOutput(), netOutput)
        .mult(layer.getLambda());
    layer.addGradient(Vektor.dyadicProduct(delta, layer.getInput()));

    while (layerIter.hasPrevious()) {
      delta = Matrix.mult(delta, layer.getWeights()).dropLast();
      layer = layerIter.previous();
      delta.mult(layer.getLambda());

      layer.addGradient(Vektor.dyadicProduct(delta, layer.getInput()));
    }
  }


  /**
   * Ein Trainingsdurchlauf. Hier werden alle Patterns einmal (bzw. so oft wie
   * Pattern.getPriority() es angibt) eintrainiert. Das bedeutet, pro Pattern
   * wird die Eingabe durch das Netz geschickt und die Ausgabe wird über eine
   * Backpropagation korrigiert.
   * 
   * Nachdem die Patterns alle eintrainiert wurden, werden die gespeicherten
   * Änderungen übertragen (Batch-Training).
   */
  public void train() {
    try {
      for (DataPattern p : patterns) {
        // Wird "priority"-mal wiederholt
        for (int i = 0; i < p.getPriority(); i++) {
          Vektor netOutput = net.feed(p.getInput());
          backpropagation(netOutput, p);
        }
      }

      // Batch Training
      for (Layer layer : net) {
        calcWeightChanges(layer);
        layer.applyWeightChanges();
      }
    } catch (Exception e) {
      System.err
          .println("Training hat nicht funktioniert: Falsche Dimensionen!");
      e.printStackTrace();
    }
  }


  /**
   * Trainiert das Netz mit der Methode Net.train() epochs-mal.
   * 
   * @param epochs Maximale Anzahl an Epochen (also Wiederholungen des
   *               Trainings).
   */
  public void train(int epochs) {
    this.maxEpochs = epochs;

    for (epoch = 0; epoch < epochs && !Thread.interrupted(); epoch++) {
      train();
    }
  }


  /**
   * Implementiert den RPROP-Algorithmus. Dies ist mitunter der in der am
   * schnellsten gegen das richtige Trainingsergebnis konvergierende
   * Algorithmus, den es gibt. Dieser Algorithmus berechnet die Änderungen, die
   * an den Gewichten eines Layers nach (beim Batch-Training) der
   * Backpropagation vorgenommen werden müssen.
   *
   * Der RPROP-Algorithmus ist aus mehreren Quellen zusammengetragen worden
   * (https://de.wikipedia.org/wiki/Resilient_Propagation, Lehrbuch
   * "Computational Intelligence", Artikel "A direct adaptive method for faster
   * backpropagation learning: The Rprop algorithm").
   *
   * @param layer Der Layer, dessen Gewichte nach dem Training angepasst werden
   *              sollen.
   * @throws Exception Möglicher Fehler bei der Rechnung mit Vektoren und
   *                   Matrizen.
   */
  private void calcWeightChanges(Layer layer) throws Exception {
    if (layer.getGradient(2) == null) {
      layer.getWeightsDiff().fill(0.1);
      return;
    }

    for (int i = 0; i < layer.getWeights().getRows(); i++) {
      for (int j = 0; j < layer.getWeights().getCols(); j++) {
        double cond1 = layer.getGradient(0).get(i, j)
            * layer.getGradient(1).get(i, j);
        double cond2 = layer.getGradient(1).get(i, j)
            * layer.getGradient(2).get(i, j);

        if (cond1 > 0 && cond2 >= 0)
          increaseWeightsDiff(layer, i, j);
        else if (cond1 < 0)
          decreaseWeightsDiff(layer, i, j);
      }
    }
  }


  /**
   * Erhöht die Gewichtsänderungen entsprechend des angegebenen Faktors c_p.
   * 
   * @param layer Der Layer, dessen Eintrag in der Gewichtsveränderungsmatrix
   *              erhöht werden soll.
   * @param i     Die Reihe des Eintrags in der Matrix.
   * @param j     Die Spalte des Eintrags in der Matrix.
   */
  private void increaseWeightsDiff(Layer layer, int i, int j) {
    layer.getWeightsDiff().set(i, j,
        Math.min(layer.getWeightsDiff().get(i, j) * c_p, 1));
  }


  /**
   * Verringert die Gewichtsänderungen entsprechend des angegebenen Faktors c_p.
   * 
   * @param layer Der Layer, dessen Eintrag in der Gewichtsveränderungsmatrix
   *              verringert werden soll.
   * @param i     Die Reihe des Eintrags in der Matrix.
   * @param j     Die Spalte des Eintrags in der Matrix.
   */
  private void decreaseWeightsDiff(Layer layer, int i, int j) {
    layer.getWeightsDiff().set(i, j,
        Math.max(layer.getWeightsDiff().get(i, j) * c_m, 0.000001));
  }


  /*
   * ///////////////////////////////////////// Getters und Setters
   * /////////////////////////////////////////
   */

  /**
   * Gibt alle aktuellen Trainingspatterns wieder. Ein Pattern besteht aus einem
   * Eingabevektor und einem Soll-Ausgabevektor.
   * 
   * @return Die aktuellen Trainingspatterns.
   */
  public DataPattern[] getPatterns() {
    return patterns;
  }


  /**
   * Legt die zu trainierenden Patterns fest. Ein Pattern besteht aus einem
   * Eingabevektor und einem Soll-Ausgabevektor.
   * 
   * @param patterns Die neuen Trainingspatterns.
   */
  public void setPatterns(DataPattern patterns[]) {
    this.patterns = patterns;
  }


  /**
   * Gibt die aktuelle Epoche zurück, also die bisherige Anzahl der
   * Wiederholungen des Trainings.
   * 
   * @return Die aktuelle Epoche.
   */
  public int getEpoch() {
    return epoch;
  }


  /**
   * Gibt die maximale Anzahl Epochen zurück, bis zu der trainiert werden soll.
   * 
   * @return Die maximale Anzahl Epochen für das Training.
   */
  public int getMaxEpochs() {
    return maxEpochs;
  }
}
