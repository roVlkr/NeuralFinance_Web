package math;

import java.util.function.Function;


/**
 * Implementiert eine reellwertige Matrix im klassischen mathematischen Sinne.
 * 
 * @author Roland Völker.
 */
public class Matrix {

  /**
   * Die Werte der Matrix
   */
  public double data[][];


  /**
   * Erstellt eine Matrix mit den angegebenen Werten. Achtung: Die Werte werden
   * flach kopiert.
   * 
   * @param data Die Werte der Matrix.
   */
  public Matrix(double data[][]) {
    this.data = data;
  }


  /**
   * Füllt eine Matrix mit einem bestimmten Wert.
   * 
   * @param n    Anzahl der Reihen.
   * @param m    Anzahl der Spalten.
   * @param fill Der Wert, den jeder Eintrag der Matrix bekommt.
   */
  public Matrix(int n, int m, double fill) {
    this.data = new double[n][m];
    this.fill(fill);
  }


  /**
   * Erstellt eine Matrix der angegebenen Größe und füllt sie mit Nullen.
   * 
   * @param n Anzahl der Reihen.
   * @param m Anzahl der Spalten.
   */
  public Matrix(int n, int m) {
    this(n, m, 0);
  }


  /**
   * Ein klassischer Copy-Konstruktor.
   * 
   * @param mat Die zu kopierende Matrix.
   */
  public Matrix(Matrix mat) {
    int n = mat.getRows();
    int m = mat.getCols();

    this.data = new double[n][m];

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++)
        this.data[i][j] = mat.get(i, j);
    }
  }


  /**
   * @return Die Anzahl der Reihen der Matrix.
   */
  public int getRows() {
    return data.length;
  }


  /**
   * @return Die Anzahl der Spalten dieser Matrix.
   */
  public int getCols() {
    return data[0].length;
  }


  /**
   * @param i Die Zeile des Eintrags.
   * @param j Die Spalte des Eintrags
   * @return Den Eintrag an der Stelle ({@code i}, {@code j}).
   */
  public double get(int i, int j) {
    return data[i][j];
  }


  /**
   * @param i Die Zeile des Eintrags.
   * @param j Die Spalte des Eintrags
   * @param d Den Wert, der an der Stelle ({@code i}, {@code j}) gespeichert
   *          werden soll.
   */
  public void set(int i, int j, double d) {
    data[i][j] = d;
  }


  /**
   * Füllt die Matrix mit einem bestimmten Wert.
   * 
   * @param d Der Wert, mit dem die Matrix gefüllt werden soll.
   */
  public void fill(double d) {
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getCols(); j++)
        this.data[i][j] = d;
    }
  }


  /*
   * /////////////////////////////////////// Member-Operatoren (+=, -=, *=)
   * ///////////////////////////////////////
   */

  /**
   * Addiert zu dieser Matrix eine weitere.
   * 
   * @param mat Die Matrix, die auf diese addiert wird.
   * @return Das Ergebnis der Matrix-Addition.
   * @throws Exception Sollten beide Matrizen nicht die gleiche Anzahl an Reihen
   *                   und Spalten haben.
   */
  public Matrix add(Matrix mat) throws Exception {
    if (getRows() != mat.getRows() || getCols() != mat.getCols())
      throw new Exception("Dimension Error");

    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getCols(); j++)
        data[i][j] += mat.get(i, j);
    }

    return this;
  }


  /**
   * Subtrahiert eine Matrix von dieser Matrix.
   * 
   * @param mat Der Subtrahend in der Matrix-Subtraktion.
   * @return Das Ergebnis der Matrix-Subtraktion.
   * @throws Exception Sollten beide Matrizen nicht die gleiche Anzahl an Reihen
   *                   und Spalten haben.
   */
  public Matrix subtract(Matrix mat) throws Exception {
    if (getRows() != mat.getRows() || getCols() != mat.getCols())
      throw new Exception("Dimension Error");

    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getCols(); j++)
        data[i][j] += mat.get(i, j);
    }

    return this;
  }


  /**
   * Multipliziert auf diese Matrix (von rechts) eine weitere.
   * 
   * @param mat Die Matrix, die auf diese multipliziert wird (von rechts).
   * @return Das Ergebnis der Matrix-Multiplikation.
   * @throws Exception Sollte die Anzahl der Spalten dieser Matrix nicht der
   *                   Anzahl Reihen der anderen entsprechen.
   */
  public Matrix mult(Matrix mat) throws Exception {
    if (this.getCols() != mat.getRows())
      throw new Exception("Dimension Error");

    Matrix res = new Matrix(this.getRows(), mat.getCols());

    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < mat.getCols(); i++) {
        for (int k = 0; k < getCols(); k++)
          res.data[i][j] += data[i][k] * mat.data[k][j];
      }
    }

    this.data = res.data;
    return this;
  }


  /**
   * Multipliziert einen Vektor auf diese Matrix (von rechts).
   * 
   * @param v Der Vektor, der (von rechts) auf diese Matrix multipliziert wird.
   * @return Das Ergebnis der Matrix-Vektor-Multiplikation.
   * @throws Exception Sollte die Anzahl der Spalten dieser Matrix nicht der
   *                   Anzahl an Werten des Vektors entsprechen.
   */
  public Vektor mult(Vektor v) throws Exception {
    if (this.getCols() != v.getLength())
      throw new Exception("Dimension Error");

    Vektor res = new Vektor(getRows());

    for (int i = 0; i < getRows(); i++) {
      double sum = 0;

      for (int k = 0; k < getCols(); k++)
        sum += data[i][k] * v.get(k);

      res.set(i, sum);
    }

    return res;
  }


  /**
   * Multipliziert diese Matrix mit einem reellen Wert.
   * 
   * @param lambda Der reelle Wert, mit dem diese Matrix multipliziert wird.
   * @return Das Ergebnis der Matrix-Zahl-Multiplikation.
   */
  public Matrix mult(double lambda) {
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getCols(); j++)
        data[i][j] *= lambda;
    }

    return this;
  }


  /**
   * Extrahiert eine Reihe aus dieser Matrix und gibt sie als Vektor zurück.
   * 
   * @param r Index der Reihe, die extrahiert werden soll.
   * @return Die extrahierte Reihe als Vektor.
   */
  public Vektor extractRow(int r) {
    Vektor row = new Vektor(getCols());

    for (int i = 0; i < row.getLength(); i++)
      row.set(i, get(r, i));

    return row;
  }


  /**
   * Setzt eine gesamte Reihe dieser Matrix fest.
   * 
   * @param r Der Index der Reihe.
   * @param v Der Vektor mit den Reihenwerten.
   * @throws Exception Falls die Anzahl der Spalten (Reiheneinträge) nicht denen
   *                   der eingesetzten Reihe entsprechen.
   */
  public void setRow(int r, Vektor v) throws Exception {
    if (getCols() != v.getLength())
      throw new Exception("Dimension Error");

    for (int i = 0; i < getCols(); i++)
      data[r][i] = v.get(i);
  }


  /**
   * Wendet eine Funktion auf die gesamte Matrix an.
   * 
   * @param f Die Funktion, die auf alle Werte der Matrix angewendet werden
   *          soll.
   * @return Die Matrix, die die Ergebnisse der Funktion für die entsprechenden
   *         Werte aus dieser Matrix enthält.
   */
  public Matrix apply(Function<Double, Double> f) {
    Matrix res = new Matrix(this);

    for (int i = 0; i < getRows(); i++)
      for (int j = 0; j < getCols(); j++)
        res.data[i][j] = f.apply(res.data[i][j]);

    return res;
  }


  /**
   * Formatiert diese Matrix als String.
   */
  @Override
  public String toString() {
    String result = "";

    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getCols(); j++)
        if (j < getCols() - 1)
          result += data[i][j] + " ";
        else
          result += data[i][j];

      result += "\n";
    }

    return result;
  }


  /*
   * /////////////////////////////////////// Statische Operatoren (+, -, *)
   * ///////////////////////////////////////
   */

  /**
   * Addiert zwei Matrizen.
   * 
   * @param a Der linke Summand
   * @param b Der rechte Summand.
   * @return Das Ergebnis der Matrix-Addition.
   * @throws Exception Sollten beide Matrizen nicht die gleiche Anzahl an Reihen
   *                   und Spalten haben.
   */
  public static Matrix add(Matrix a, Matrix b) throws Exception {
    if (a.getRows() != b.getRows() || a.getCols() != b.getCols())
      throw new Exception("Dimension Error");

    Matrix res = new Matrix(a);

    for (int i = 0; i < a.getRows(); i++) {
      for (int j = 0; j < a.getCols(); j++)
        res.data[i][j] += b.get(i, j);
    }

    return res;
  }


  /**
   * Subtrahiert zwei Matrizen.
   * 
   * @param a Der Minuend.
   * @param b Der Subtrahend.
   * @return Das Ergebnis der Matrix-Subtraktion.
   * @throws Exception Sollten beide Matrizen nicht die gleiche Anzahl an Reihen
   *                   und Spalten haben.
   */
  public static Matrix subtract(Matrix a, Matrix b) throws Exception {
    if (a.getRows() != b.getRows() || a.getCols() != b.getCols())
      throw new Exception("Dimension Error");

    Matrix res = new Matrix(a);

    for (int i = 0; i < a.getRows(); i++) {
      for (int j = 0; j < a.getCols(); j++)
        res.data[i][j] += b.get(i, j);
    }

    return res;
  }


  /**
   * Multipliziert zwei Matrizen miteinander
   * 
   * @param a Der linke Faktor.
   * @param b Der rechte Faktor.
   * @return Das Ergebnis der Matrix-Multiplikation
   * @throws Exception Sollte die Anzahl der Spalten der linken Matrix nicht der
   *                   Anzahl Reihen der rechten entsprechen.
   */
  public static Matrix mult(Matrix a, Matrix b) throws Exception {
    if (a.getCols() != b.getRows())
      throw new Exception("Dimension Error");

    Matrix res = new Matrix(a.getRows(), b.getCols());

    for (int i = 0; i < a.getRows(); i++) {
      for (int j = 0; j < b.getCols(); i++) {
        for (int k = 0; k < a.getCols(); k++)
          res.data[i][j] += a.get(i, k) * b.get(k, j);
      }
    }

    return res;
  }


  /**
   * Multipliziert eine Matrix mit einem Vektor (von rechts).
   * 
   * @param mat Der linke Faktor.
   * @param v   Der rechte Faktor.
   * @return Das Ergebnis der Matrix-Vektor-Multiplikation.
   * @throws Exception Sollte die Anzahl der Spalten dieser Matrix nicht der
   *                   Anzahl an Werten des Vektors entsprechen.
   */
  public static Vektor mult(Matrix mat, Vektor v) throws Exception {
    if (mat.getCols() != v.getLength())
      throw new Exception("Dimension Error");

    Vektor res = new Vektor(mat.getRows());

    for (int i = 0; i < mat.getRows(); i++) {
      double sum = 0;

      for (int j = 0; j < mat.getCols(); j++)
        sum += mat.get(i, j) * v.get(j);

      res.set(i, sum);
    }

    return res;
  }


  /**
   * Multipliziert einen Vektor (von links) mit einer Matrix.
   * 
   * @param v   Der linke Faktor.
   * @param mat Der rechte Faktor.
   * @return Das Ergebnis der Vekor-Matrix-Multiplikation.
   * @throws Exception Sollte die Anzahl der Spalten dieser Matrix nicht der
   *                   Anzahl an Werten des Vektors entsprechen.
   */
  public static Vektor mult(Vektor v, Matrix mat) throws Exception {
    if (mat.getRows() != v.getLength())
      throw new Exception("Dimension Error");

    Vektor res = new Vektor(mat.getCols());

    for (int i = 0; i < mat.getCols(); i++) {
      double sum = 0;

      for (int j = 0; j < mat.getRows(); j++)
        sum += mat.get(j, i) * v.get(j);

      res.set(i, sum);
    }

    return res;
  }


  /**
   * Multipliziert zwei Matrizen koordinatenweise miteinander.
   * 
   * @param a Der linke Faktor.
   * @param b Der rechte Faktor
   * @return Das Ergebnis der koordinatenweisen Matrixmultiplikation.
   * @throws Exception Sollten beide Matrizen nicht die gleiche Anzahl an Reihen
   *                   und Spalten haben.
   */
  public static Matrix coordinateMult(Matrix a, Matrix b) throws Exception {
    if (a.getRows() != b.getRows() || a.getCols() != b.getCols())
      throw new Exception("Dimension Error");

    Matrix res = new Matrix(a);

    for (int i = 0; i < a.getRows(); i++) {
      for (int j = 0; j < a.getCols(); j++)
        res.data[i][j] *= b.get(i, j);
    }

    return res;
  }


  /**
   * Multipliziert eine Matrix mit einem reellen Wert.
   * 
   * @param mat    Die Matrix.
   * @param lambda Der reelle Wert.
   * @return Das Ergebnis der Matrix-Zahl-Multiplikation.
   */
  public static Matrix mult(Matrix mat, double lambda) {
    Matrix res = new Matrix(mat);

    for (int i = 0; i < res.getRows(); i++) {
      for (int k = 0; k < res.getCols(); k++)
        res.data[i][k] *= lambda;
    }

    return res;
  }
}
