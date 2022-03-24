package net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import math.Vektor;


/**
 * Das neuronale Netz. Als dieses b�ndelt die Klasse mehrere Layer in einer
 * Liste und stellt entsprechende �bergeordnete Funktionen zur Kontrolle dieser
 * zur Verf�gung.
 * 
 * @author Roland V�lker
 */
public class Net implements Iterable<Layer> {

  /**
   * Liste der Layer. Der Inputlayer ohne eigene Gewichte ist nicht mit
   * inbegriffen, da er bereits vollst�ndig durch einen Vektor beschrieben wird.
   */
  private final List<Layer> layers;

  /**
   * Die Gr��en der Layer in einer Liste zusammengefasst. Dies bietet einen
   * leichteren Zugriff auf die Werte.
   */
  private final List<Integer> structure;

  /**
   * Dies ist der Zeitraum / die Anzahl der Datenpunkte, �ber die eine Sch�tzung
   * erfolgen soll.
   */
  private final int estimateLength;


  /**
   * Erstellt ein neuronales Netz.
   * 
   * @param estimateLength Die Dauer, �ber die gesch�tzt wird.
   * @param structure      Die von der graphischen Oberfl�che eingegebene
   *                       Struktur.
   */
  public Net(int estimateLength, List<Integer> hiddenLayers,
      int numberChartValues) {
    this.structure = new ArrayList<>(hiddenLayers);

    // Der InputLayer hat die L�nge estimateLength * die Anzahl der
    // Sch�tzungswerte, denn pro Chartpunkt gibt es dementsprechend
    // mehr Double-Werte.
    this.structure.add(0, estimateLength * numberChartValues);
    this.structure.add(1);
    this.estimateLength = estimateLength;

    layers = new ArrayList<>();
    for (int i = 1; i < this.structure.size(); i++)
      layers.add(new Layer(this.structure.get(i), this.structure.get(i - 1)));
  }


  /**
   * Schickt einen Eingabevektor durch das neuronale Netz und liefert dabei den
   * Ausgabevektor des letzten Layers.
   * 
   * @param input Der Eingabevektor, der durch das Netz geschickt werden soll.
   * @return Den Ausgabevektor des Netzes.
   * @throws Exception M�gliche Berechnungsfehler.
   */
  public synchronized Vektor feed(Vektor input) throws Exception {
    first().feed(input);

    for (int i = 1; i < layers.size(); i++)
      get(i).feed(get(i - 1).getOutput());

    return last().getOutput();
  }


  /**
   * Liefert einen vereinfachten Zugriff auf einen Layer.
   * 
   * @param i Der Index des Layers.
   * @return Den Layer mit dem angegebenen Index.
   */
  public Layer get(int i) {
    return layers.get(i);
  }


  /**
   * Gibt die Neuronenstruktur (also Anzahl der Neuronen pro Layer) zur�ck.
   * Dabei ist der letzte Wert anwendungsspezifisch immer 1.
   * 
   * @return Die Neuronenstruktur (also Anzahl der Neuronen pro Layer).
   */
  public List<Integer> getStructure() {
    return structure;
  }


  /**
   * Erm�glicht einen vereinfachten Zugriff auf den ersten Layer (nach dem
   * "klassischen" Inputlayer).
   * 
   * @return Den ersten Layer.
   */
  public Layer first() {
    return layers.get(0);
  }


  /**
   * Erm�glicht einen vereinfachten Zugriff auf den letzten Layer.
   * 
   * @return Den letzten Layer.
   */
  public Layer last() {
    return layers.get(layers.size() - 1);
  }


  /**
   * Erm�glicht einen vereinfachten Zugriff auf den ersten Layer und gibt einen
   * Iterator f�r Zugriffe auf folgende Layer zur�ck.
   * 
   * @return Einen Iterator, der beim ersten Layer (.next()) beginnt.
   */
  public ListIterator<Layer> begin() {
    return layers.listIterator(0);
  }


  /**
   * Erm�glicht einen vereinfachten Zugriff auf den letzten Layer und gibt einen
   * Iterator f�r Zugriffe auf vorige Layer zur�ck.
   * 
   * @return Einen Iterator, der beim letzten Layer (.previous()) beginnt.
   */
  public ListIterator<Layer> end() {
    return layers.listIterator(layers.size());
  }


  /**
   * Erm�glicht die Verwendung der eines Net-Objekts in einer foreach-Schleife.
   */
  @Override
  public Iterator<Layer> iterator() {
    return layers.iterator();
  }


  /**
   * Gibt den Zeitraum / die Anzahl der Datenpunkte, �ber die eine Sch�tzung
   * erfolgen soll, zur�ck.
   * 
   * @return Den Zeitraum / die Anzahl der Datenpunkte, �ber die eine Sch�tzung
   *         erfolgen soll.
   */
  public int getEstimateLength() {
    return estimateLength;
  }
}
