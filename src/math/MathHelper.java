package math;

/**
 * Stellt paketübergreifend Funktionen zur Verfügung, die der einfacheren
 * Bedienung in mathematischen Kontexten dienen.
 * 
 * @author Roland Völker
 */
public class MathHelper {

  /**
   * Erstellt eine Einheitsmatrix der angegebenen Größe.
   * 
   * @param n Anzahl Reihen / Spalten.
   * @return Eine Einheitsmatrix der angegebenen Größe.
   */
  public static Matrix id(int n) {
    Matrix res = new Matrix(n, n);

    for (int i = 0; i < n; i++)
      res.set(i, i, 1);

    return res;
  }


  /**
   * Erstellt eine Matrix der angegebenen Größe mit Zufallswerten im Bereich
   * (low, high).
   * 
   * @param n    Anzahl der Reihen.
   * @param m    Anzahl der Spalten.
   * @param low  Untere Bereichsgrenze für die Zufallszahlen.
   * @param high Obere Bereichsgrenze für die Zufallszahlen.
   * @return Eine Matrix mit Zufallswerten.
   */
  public static Matrix random(int n, int m, double low, double high) {
    Matrix res = new Matrix(n, m);

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++)
        res.set(i, j, Math.random() * (high - low) + low);
    }

    return res;
  }


  /**
   * Orthogonalisiert die Reihen einer Matrix.
   * 
   * @param mat Die Matrix, deren Reihen orthogonalisiert werden sollen.
   * @throws Exception Bei Berechnungsfehlern.
   */
  public static void orthogonalize(Matrix mat) throws Exception {
    int n = Math.min(mat.getRows(), mat.getCols());

    for (int r = 1; r < n; r++) {
      Vektor nextCol = mat.extractRow(r);

      for (int i = 0; i < r; i++) {
        Vektor passed = mat.extractRow(i);
        double lambda = Vektor.scp(nextCol, passed) / passed.norm2();

        nextCol.subtract(passed.mult(lambda));
      }

      mat.setRow(r, nextCol);
    }
  }


  /**
   * Die Sigmoid-Funktion x -> 1 / (1 + exp(-x)). Sie dient als
   * Aktivierungsfunktion der Neuronen.
   * 
   * @param x Ein reeller Wert.
   * @return Den Ausgabewert der Sigmoid-Funktion.
   */
  public static double sigmoid(double x) {
    return 1 / (1 + Math.exp(-x));
  }


  /**
   * Die inverse Sigmoid-Funktion log(y / (1 - y)). Ebenso wichtig wie die
   * Sigmoid-Funktion selbst, werden mit ihr die Werte des neuronalen Netzes
   * "rückübersetzt".
   * 
   * @param y Ein reeller Wert.
   * @return Den Ausgabewert der inversen Sigmoid-Funktion.
   */
  public static double sigmoidInv(double y) {
    return Math.log(y / (1 - y));
  }


  /**
   * Eine erweiterte Signum-Funktion, die neben 1 und -1 auch 0 angibt, falls x
   * == 0 ist.
   * 
   * @param x Ein reeller Wert.
   * @return Den Ausgabewert der Signum-Funktion.
   */
  public static double sgn(double x) {
    if (x == 0)
      return 0;

    return x > 0 ? 1 : -1;
  }


  /**
   * Eine erweiterte Rundungsfunktion, die auf bestimmte Nachkommastellen
   * rundet.
   * 
   * @param d             Die zu rundende Zahl.
   * @param decimalPlaces Die Anzahl der Nachkommastellen.
   * @return Die auf die angegebenen Nachkommastellen gerundete Zahl.
   */
  public static double round(double d, int decimalPlaces) {
    return Math.round(d * Math.pow(10, decimalPlaces))
        / Math.pow(10, decimalPlaces);
  }
}
