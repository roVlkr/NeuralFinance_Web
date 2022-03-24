package net;

import math.MathHelper;
import math.Matrix;
import math.Vektor;
import util.BoundedQueue;


/**
 * Bündelt die Idee einer Schicht im neuronalen Netz. Dabei gibt es einen
 * Eingabevektor, der als von außen gegeben betrachtet werden soll und einen
 * Ausgabevektor, der den Zustand der Neuronen dieses Layers darstellen soll.
 * 
 * Hinzu kommen einige Matrizen zur Berechnung der Gewichte.
 * 
 * @author Roland Völker
 */
public class Layer {

  /**
   * Der Ausgabevektor. Dieser legt auch die Größe des Layers fest.
   */
  private Vektor output;

  /**
   * Der Eingabevektor.
   */
  private Vektor input;

  /**
   * Die Gewichtsmatrix.
   */
  private Matrix weights;

  /**
   * Die Änderungsmatrix für die Gewichtsmatrix.
   */
  private Matrix weightsDiff;

  /**
   * Die (begrenzte) Liste der Gradientenmatrizen. Enthält drei Matrizen, von
   * denen die älteste beim Einfügen einer neuen gelöscht wird.
   */
  private BoundedQueue<Matrix> gradients;

  /**
   * Enthält die Deltawerte des Layers. Diese sind beim Training und der
   * Fehlerrückübertragung relevant.
   */
  private Vektor delta;


  /**
   * Erstellt einen Layer. Der Eingabevektor besitzt eine um 1 erhöhte Länge, da
   * er später erweitert wird (siehe extendInput). Die Gewichtsmatrix wird mit
   * zufälligen Werten zwischen -0.01 und 0.01 gefüllt, danach werden ihre
   * Zeilen orthogonalisiert. Außerdem wird die Liste der Gradienten auf die
   * Länge 3 gestellt, dies ist notwendig für das RPROP-Training (siehe Klasse
   * Training).
   * 
   * @param outputLength Länge des Outputvektors
   * @param inputLength  Länge des Inputvektors (ohne Biaserweiterung)
   */
  public Layer(int outputLength, int inputLength) {
    output = new Vektor(outputLength);
    input = new Vektor(inputLength + 1);

    weights = MathHelper.random(outputLength, inputLength + 1, -0.01, 0.01);
    try {
      MathHelper.orthogonalize(weights);
    } catch (Exception e) {
      e.printStackTrace();
    }

    weightsDiff = new Matrix(outputLength, inputLength + 1, 0);
    gradients = new BoundedQueue<>(3);
    gradients.push(new Matrix(outputLength, inputLength + 1, 0));
  }


  /**
   * Erweitert den Eingabevektor um eine angehängte 1. Diese ist zur
   * Einbeziehung der Biaswerte wichtig.
   * 
   * @param in Der zu erweiternde Vektor.
   */
  private void extendInput(Vektor in) {
    input = new Vektor(in.getLength() + 1);

    for (int i = 0; i < in.getLength(); i++)
      input.set(i, in.get(i));

    input.set(in.getLength(), 1); // Erweitert
  }


  /**
   * Schickt den Eingabevektor durch das neuronale Netz.
   * 
   * @param in Der Eingabevektor.
   * @throws Exception Mögliche Berechnungsfehler.
   */
  public void feed(Vektor in) throws Exception {
    extendInput(in);
    output = weights.mult(this.input);
    output = output.apply(MathHelper::sigmoid);
  }


  /**
   * Gibt die Größe des Layers aus. Diese wird mit der Länge des Ausgabevektors
   * gleichgesetzt.
   * 
   * @return Die Größe des Layers.
   */
  public int getSize() {
    return output.getLength();
  }


  /**
   * Der Ausgabevektor des Layers, möglichst nach einer Eingabe.
   * 
   * @return Der Ausgabevektor des Layers.
   */
  public Vektor getOutput() {
    return output;
  }


  /**
   * Der von außen oder einem vorigen Layer eingegebene Vektor, der der
   * Berechnung der Ausgabe dient.
   * 
   * @return Der Eingabevektor des Layers.
   */
  public Vektor getInput() {
    return input;
  }


  /**
   * Berechnet den Lambdawert des Layers. Dies ist die Ableitung der
   * Sigmoid-Funktion, die die Form des Ausgabevektors annimmt, mit anderen
   * Worten: OutputVektor * (1 - OutputVektor) [komponentenweise Multiplikation]
   * 
   * @return Den Lambdawert des Layers.
   * @throws Exception Mögliche Berechnungsfehler.
   */
  public Vektor getLambda() throws Exception {
    return Vektor.mult(output, new Vektor(getSize(), 1).subtract(output));
  }


  /**
   * Berechnet die Deltawerte des Layers. Diese bilden einen Teil der
   * Fehlerrückübertragung. Deltawerte werden in Bezug auf den nächsten Layer
   * gebildet.
   * 
   * @param next Der im Netz nächste Layer.
   * @return Einen Vektor, der alle Deltawerte enthält.
   */
  public Vektor calcDelta(Layer next) {
    delta = new Vektor(getSize());

    // Für jedes Neuron in diesem Layer
    for (int u = 0; u < getSize(); u++) {
      double sum = 0;

      // Summiere über die Neuronen der nachfolgenden Schicht
      for (int succ = 0; succ < next.getSize(); succ++)
        sum += next.weights.get(succ, u) * next.delta.get(succ);

      delta.set(u, sum * output.get(u) * (1 - output.get(u)));
    }

    return delta;
  }


  /**
   * Dies ist die Berechnungsvariante der Deltawerte für den Outputlayer.
   * 
   * @param trainingOut Der Soll-Ausgabevektor des Trainingpatterns.
   * @return Einen Vektor, der alle Deltawerte enthält.
   */
  public Vektor calcDelta(Vektor trainingOut) {
    delta = new Vektor(getSize());

    for (int u = 0; u < getSize(); u++)
      delta.set(u, (trainingOut.get(u) - output.get(u)) * output.get(u)
          * (1 - output.get(u)));

    return delta;
  }


  /**
   * Gibt die Matrix der Gewichtswerte zurück. Diese ergeben, multipliziert mit
   * dem erweiterten Eingabevektor, den Ausgabevektor.
   * 
   * @return Die Gewichtsmatrix.
   */
  public Matrix getWeights() {
    return weights;
  }


  /**
   * Gibt die Matrix der Gewichtsveränderungen zurück. Diese Veränderungen
   * werden auf die Gewichtswerte addiert.
   * 
   * @return Die Matrix der Gewichtsveränderungen.
   */
  public Matrix getWeightsDiff() {
    return weightsDiff;
  }


  /**
   * Setzt die Matrix der Gewichtsveränderungen neu.
   * 
   * @param weightsDiff Die neue Matrix der Gewichtsveränderungen.
   */
  public void setWeightsDiff(Matrix weightsDiff) {
    this.weightsDiff = new Matrix(weightsDiff);
  }


  /**
   * Gibt den angegebenen Gradienten zurück. Der Gradient ist eine Matrix der
   * Gradienten der jeweiligen Gewichtswerte. Die Gradienten sind von aktuell
   * bis alt geordnet.
   * 
   * @param i Ein Integer-Wert zwischen 0 und 2, für einen der drei
   *          gespeicherten Gradienten.
   * @return Gibt den i-ten Gradienten zurück.
   */
  public Matrix getGradient(int i) {
    return gradients.get(i);
  }


  /**
   * Addiert einen Teilbetrag auf den aktuellen Gradienten.
   * 
   * @param g Teilbetrag (Matrix), der auf den aktuellen Gradienten addiert
   *          wird.
   * @throws Exception Mögliche Berechnungsfehler.
   */
  public void addGradient(Matrix g) throws Exception {
    gradients.get(0).add(g);
  }


  /**
   * Addiert die Gewichtsveränderungsmatrix unter der Berücksichtigung des
   * aktuellen Gradienten (siehe Wikipedia) auf die Gewichtsmatrix. "Schiebt"
   * eine neue Matrix in die Reihe der Gradienten.
   * 
   * @throws Exception Mögliche Berechnungsfehler.
   */
  public void applyWeightChanges() throws Exception {
    weights.add(Matrix.coordinateMult(weightsDiff,
        getGradient(0).apply(MathHelper::sgn)));

    gradients.push(new Matrix(weights.getRows(), weights.getCols()));
  }
}
