package net;

import math.MathHelper;
import math.Matrix;
import math.Vektor;
import util.BoundedQueue;


/**
 * B�ndelt die Idee einer Schicht im neuronalen Netz. Dabei gibt es einen
 * Eingabevektor, der als von au�en gegeben betrachtet werden soll und einen
 * Ausgabevektor, der den Zustand der Neuronen dieses Layers darstellen soll.
 * 
 * Hinzu kommen einige Matrizen zur Berechnung der Gewichte.
 * 
 * @author Roland V�lker
 */
public class Layer {

  /**
   * Der Ausgabevektor. Dieser legt auch die Gr��e des Layers fest.
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
   * Die �nderungsmatrix f�r die Gewichtsmatrix.
   */
  private Matrix weightsDiff;

  /**
   * Die (begrenzte) Liste der Gradientenmatrizen. Enth�lt drei Matrizen, von
   * denen die �lteste beim Einf�gen einer neuen gel�scht wird.
   */
  private BoundedQueue<Matrix> gradients;

  /**
   * Enth�lt die Deltawerte des Layers. Diese sind beim Training und der
   * Fehlerr�ck�bertragung relevant.
   */
  private Vektor delta;


  /**
   * Erstellt einen Layer. Der Eingabevektor besitzt eine um 1 erh�hte L�nge, da
   * er sp�ter erweitert wird (siehe extendInput). Die Gewichtsmatrix wird mit
   * zuf�lligen Werten zwischen -0.01 und 0.01 gef�llt, danach werden ihre
   * Zeilen orthogonalisiert. Au�erdem wird die Liste der Gradienten auf die
   * L�nge 3 gestellt, dies ist notwendig f�r das RPROP-Training (siehe Klasse
   * Training).
   * 
   * @param outputLength L�nge des Outputvektors
   * @param inputLength  L�nge des Inputvektors (ohne Biaserweiterung)
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
   * Erweitert den Eingabevektor um eine angeh�ngte 1. Diese ist zur
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
   * @throws Exception M�gliche Berechnungsfehler.
   */
  public void feed(Vektor in) throws Exception {
    extendInput(in);
    output = weights.mult(this.input);
    output = output.apply(MathHelper::sigmoid);
  }


  /**
   * Gibt die Gr��e des Layers aus. Diese wird mit der L�nge des Ausgabevektors
   * gleichgesetzt.
   * 
   * @return Die Gr��e des Layers.
   */
  public int getSize() {
    return output.getLength();
  }


  /**
   * Der Ausgabevektor des Layers, m�glichst nach einer Eingabe.
   * 
   * @return Der Ausgabevektor des Layers.
   */
  public Vektor getOutput() {
    return output;
  }


  /**
   * Der von au�en oder einem vorigen Layer eingegebene Vektor, der der
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
   * @throws Exception M�gliche Berechnungsfehler.
   */
  public Vektor getLambda() throws Exception {
    return Vektor.mult(output, new Vektor(getSize(), 1).subtract(output));
  }


  /**
   * Berechnet die Deltawerte des Layers. Diese bilden einen Teil der
   * Fehlerr�ck�bertragung. Deltawerte werden in Bezug auf den n�chsten Layer
   * gebildet.
   * 
   * @param next Der im Netz n�chste Layer.
   * @return Einen Vektor, der alle Deltawerte enth�lt.
   */
  public Vektor calcDelta(Layer next) {
    delta = new Vektor(getSize());

    // F�r jedes Neuron in diesem Layer
    for (int u = 0; u < getSize(); u++) {
      double sum = 0;

      // Summiere �ber die Neuronen der nachfolgenden Schicht
      for (int succ = 0; succ < next.getSize(); succ++)
        sum += next.weights.get(succ, u) * next.delta.get(succ);

      delta.set(u, sum * output.get(u) * (1 - output.get(u)));
    }

    return delta;
  }


  /**
   * Dies ist die Berechnungsvariante der Deltawerte f�r den Outputlayer.
   * 
   * @param trainingOut Der Soll-Ausgabevektor des Trainingpatterns.
   * @return Einen Vektor, der alle Deltawerte enth�lt.
   */
  public Vektor calcDelta(Vektor trainingOut) {
    delta = new Vektor(getSize());

    for (int u = 0; u < getSize(); u++)
      delta.set(u, (trainingOut.get(u) - output.get(u)) * output.get(u)
          * (1 - output.get(u)));

    return delta;
  }


  /**
   * Gibt die Matrix der Gewichtswerte zur�ck. Diese ergeben, multipliziert mit
   * dem erweiterten Eingabevektor, den Ausgabevektor.
   * 
   * @return Die Gewichtsmatrix.
   */
  public Matrix getWeights() {
    return weights;
  }


  /**
   * Gibt die Matrix der Gewichtsver�nderungen zur�ck. Diese Ver�nderungen
   * werden auf die Gewichtswerte addiert.
   * 
   * @return Die Matrix der Gewichtsver�nderungen.
   */
  public Matrix getWeightsDiff() {
    return weightsDiff;
  }


  /**
   * Setzt die Matrix der Gewichtsver�nderungen neu.
   * 
   * @param weightsDiff Die neue Matrix der Gewichtsver�nderungen.
   */
  public void setWeightsDiff(Matrix weightsDiff) {
    this.weightsDiff = new Matrix(weightsDiff);
  }


  /**
   * Gibt den angegebenen Gradienten zur�ck. Der Gradient ist eine Matrix der
   * Gradienten der jeweiligen Gewichtswerte. Die Gradienten sind von aktuell
   * bis alt geordnet.
   * 
   * @param i Ein Integer-Wert zwischen 0 und 2, f�r einen der drei
   *          gespeicherten Gradienten.
   * @return Gibt den i-ten Gradienten zur�ck.
   */
  public Matrix getGradient(int i) {
    return gradients.get(i);
  }


  /**
   * Addiert einen Teilbetrag auf den aktuellen Gradienten.
   * 
   * @param g Teilbetrag (Matrix), der auf den aktuellen Gradienten addiert
   *          wird.
   * @throws Exception M�gliche Berechnungsfehler.
   */
  public void addGradient(Matrix g) throws Exception {
    gradients.get(0).add(g);
  }


  /**
   * Addiert die Gewichtsver�nderungsmatrix unter der Ber�cksichtigung des
   * aktuellen Gradienten (siehe Wikipedia) auf die Gewichtsmatrix. "Schiebt"
   * eine neue Matrix in die Reihe der Gradienten.
   * 
   * @throws Exception M�gliche Berechnungsfehler.
   */
  public void applyWeightChanges() throws Exception {
    weights.add(Matrix.coordinateMult(weightsDiff,
        getGradient(0).apply(MathHelper::sgn)));

    gradients.push(new Matrix(weights.getRows(), weights.getCols()));
  }
}
