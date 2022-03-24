package math;

import java.util.List;
import java.util.function.Function;


public class Vektor {

  private double data[];


  /**
   * Erstellt einen Vektor im Double-Wertebereich.
   * 
   * @param data Stellt die zu kopierenden Daten zur Verfügung. Achtung: Hiervon
   *             wird eine flache Kopie erstellt!
   */
  public Vektor(double... data) {
    this.data = data;
  }


  public Vektor(List<Double> dataList) {
    data = new double[dataList.size()];
    for (int i = 0; i < dataList.size(); i++)
      data[i] = dataList.get(i);
  }


  /**
   * Erstellt einen Vektor im Double-Wertebereich.
   * 
   * @param n    Gibt die Anzahl der Werte an.
   * @param fill Gibt den Standardwert für die Einträge des Vektors an.
   */
  public Vektor(int n, double fill) {
    this.data = new double[n];
    for (int i = 0; i < n; i++)
      data[i] = fill;
  }


  /**
   * Erstellt einen Vektor im Double-Wertebereich und füllt ihn mit Nullen.
   * 
   * @param n Gibt die Anzahl der Werte an.
   */
  public Vektor(int n) {
    this(n, 0);
  }


  public Vektor(Vektor v) {
    this.data = new double[v.getLength()];
    for (int i = 0; i < data.length; i++)
      data[i] = v.get(i);
  }


  public double get(int i) {
    return data[i];
  }


  public void set(int i, double d) {
    data[i] = d;
  }


  public double[] getData() {
    return data;
  }


  public int getLength() {
    return data.length;
  }


  public Vektor dropLast() {
    double newData[] = new double[data.length - 1];

    for (int i = 0; i < newData.length; i++)
      newData[i] = data[i];

    data = newData;
    return this;
  }


  public Vektor apply(Function<Double, Double> f) {
    Vektor res = new Vektor(this);

    for (int i = 0; i < data.length; i++) {
      res.data[i] = f.apply(data[i]);
    }

    return res;
  }


  @Override
  public String toString() {
    String res = "(";

    for (double d : data)
      res += d + " ";

    res += ")";
    return res;
  }


  public double norm2() {
    double res = 0;

    for (int i = 0; i < data.length; i++) {
      res += data[i] * data[i];
    }

    return res;
  }


  public Vektor add(Vektor v) throws Exception {
    if (v.getLength() != data.length)
      throw new Exception("Dimension Error");

    for (int i = 0; i < data.length; i++)
      data[i] += v.data[i];

    return this;
  }


  public Vektor subtract(Vektor v) throws Exception {
    if (v.getLength() != data.length)
      throw new Exception("Dimension Error");

    for (int i = 0; i < data.length; i++)
      data[i] -= v.data[i];

    return this;
  }


  public Vektor mult(double lambda) {
    for (int i = 0; i < data.length; i++)
      data[i] *= lambda;

    return this;
  }


  /**
   * @return Das Skalarprodukt der Vektoren.
   */
  public double scp(Vektor v) throws Exception {
    if (v.getLength() != data.length)
      throw new Exception("Dimension Error");

    double sum = 0;

    for (int i = 0; i < data.length; i++)
      sum += data[i] * v.get(i);

    return sum;
  }


  /**
   * @return Das komponentenweise Produkt der Vektoren.
   */
  public Vektor mult(Vektor v) throws Exception {
    if (v.getLength() != data.length)
      throw new Exception("Dimension Error");

    for (int i = 0; i < data.length; i++)
      data[i] *= v.get(i);

    return this;
  }


  public Matrix dyadicProduct(Vektor v) {
    Matrix res = new Matrix(this.getLength(), v.getLength());

    for (int i = 0; i < this.getLength(); i++)
      for (int j = 0; j < v.getLength(); j++)
        res.set(i, j, this.get(i) * v.get(j));

    return res;
  }


  public static Vektor add(Vektor a, Vektor b) throws Exception {
    if (a.getLength() != b.getLength())
      throw new Exception("Dimension Error");

    Vektor v = new Vektor(a);

    for (int i = 0; i < a.getLength(); i++)
      v.data[i] += b.data[i];

    return v;
  }


  public static Vektor subtract(Vektor a, Vektor b) throws Exception {
    if (a.getLength() != b.getLength())
      throw new Exception("Dimension Error");

    Vektor v = new Vektor(a);

    for (int i = 0; i < a.getLength(); i++)
      v.data[i] -= b.data[i];

    return v;
  }


  public static Vektor mult(Vektor v, double lambda) {
    Vektor res = new Vektor(v);

    for (int i = 0; i < res.getLength(); i++)
      res.data[i] *= lambda;

    return res;
  }


  /**
   * @return Das Skalarprodukt der Vektoren.
   */
  public static double scp(Vektor a, Vektor b) throws Exception {
    if (a.getLength() != a.getLength())
      throw new Exception("Dimension Error");

    double sum = 0;

    for (int i = 0; i < a.getLength(); i++)
      sum += a.get(i) * b.get(i);

    return sum;
  }


  /**
   * @return Das komponentenweise Produkt der Vektoren.
   */
  public static Vektor mult(Vektor a, Vektor b) throws Exception {
    if (a.getLength() != b.getLength())
      throw new Exception("Dimension Error");

    Vektor v = new Vektor(a);

    for (int i = 0; i < a.getLength(); i++)
      v.data[i] *= b.get(i);

    return v;
  }


  public static Matrix dyadicProduct(Vektor a, Vektor b) {
    Matrix res = new Matrix(a.getLength(), b.getLength());

    for (int i = 0; i < a.getLength(); i++)
      for (int j = 0; j < b.getLength(); j++)
        res.set(i, j, a.get(i) * b.get(j));

    return res;
  }


  public void concat(Vektor output) {
    double newData[] = new double[data.length + output.getLength()];

    for (int i = 0; i < data.length; i++) {
      newData[i] = data[i];
    }

    for (int i = 0; i < output.getLength(); i++) {
      newData[data.length + i] = output.get(i);
    }

    data = newData;
  }
}
