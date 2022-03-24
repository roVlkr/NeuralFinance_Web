package data;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;


/**
 * Repräsentiert einen Datenpunkt eines Charts.
 * 
 * Die Wahl fällt auf die LinkedHashMap, da hier die Einträge in Insertion-Order
 * belassen werden.
 */
public class ChartPoint extends TreeMap<String, Double> {

  /**
   * Automatisch generierte serialVersionUID.
   */
  private static final long serialVersionUID = 4616504506997240220L;


  /**
   * Standard-Konstruktor.
   */
  public ChartPoint() {
    super();
  }


  /**
   * Copy-Konstruktor.
   * 
   * @param copy Davon soll eine Kopie angelegt werden.
   */
  public ChartPoint(ChartPoint copy) {
    super(copy);
  }


  /**
   * Erstellt einen ChartPoint mit geordneter keyList und zugehörigem
   * Werte-Array.
   * 
   * @param keyList Die (geordnete) Liste der Schlüssel.
   * @param values  Das Array der Werte.
   */
  public ChartPoint(Set<String> keySet, double values[]) {
    int index = 0;
    for (var key : keySet) {
      put(key, values[index]);
      index++;
    }
  }


  public double[] valueArray() {
    return this.values().stream().mapToDouble(d -> d).toArray();
  }


  public void apply(UnaryOperator<Double> op) {
    for (var key : keySet())
      put(key, op.apply(get(key)));
  }


  public static ChartPoint apply(BinaryOperator<Double> op, ChartPoint a,
      ChartPoint b) {
    var res = new ChartPoint();

    // Nur gleiche Schlüssel werden berücksichtigt.
    var intersection = new HashSet<String>(a.keySet());
    intersection.retainAll(b.keySet());

    for (var key : intersection)
      res.put(key, op.apply(a.get(key), b.get(key)));

    return res;
  }


  public static ChartPoint apply(UnaryOperator<Double> op, ChartPoint a) {
    var res = new ChartPoint(a);
    res.apply(op);

    return res;
  }


  @Override
  public String toString() {
    var result = "{";

    for (var key : keySet()) {
      result += "\"" + key + "\": " + get(key) + ", ";
    }

    // Das letzte ", " hinten fällt weg
    result = result.substring(0, result.length() - 2);

    return result + "}";
  }
}
