package data;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.json.JSONArray;

import math.MathHelper;
import math.Vektor;
import net.Sigmoider;
import util.BinaryConverter;
import util.ConvertingSequence;
import util.UnaryConverter;


/**
 * Die Hauptklasse zum Laden, Speichern und Verarbeiten der Eingabedaten.
 * 
 * @author Roland V�lker
 */
public class DataHandler {

  /**
   * Der eingelesene oder generierte Datensatz.
   */
  private Chart data;

  /**
   * Ein Converter, der die multiplikativen Zuw�chse der eingelesenen Daten
   * logarithmisiert. Diese Operation f�hrt die Untersuchung auf eine Standard-
   * Brownsche-Bewegung zur�ck.
   */
  private Logalizer logalizer;

  /**
   * Normalisiert die Eingabedaten, sodass alle Charts bez�glich des Netzes die
   * gleiche Genauigkeit der Ergebnisse erzielen.
   */
  private Normalizer normalizer;

  /**
   * Die Bezeichnung des Wertes, der gesch�tzt werden soll.
   */
  private String outputValue;


  /**
   * Erzeugt einen DataHandler. Dabei ist der Datensatz sowie der Pfad zur Datei
   * zun�chst auf null gesetzt. Der makeReadable-Converter wird als Logalizer
   * implementiert und der normalizer als Normalizer.
   */
  public DataHandler() {
    data = null;
  }


  /**
   * Liest die Daten aus einer Spalte einer CSV-Datei ein.
   * Die Daten werden im internen Datensatz der Klasse gespeichert.
   * 
   * @param fileName   Die CSV-Datei.
   * @param columnName Die Spalte, die eingelesen werden soll.
   * @throws FileNotFoundException Falls die Datei nicht gefunden wird.
   */
  public void readData(JSONArray jsonData, String outputValue) {
    data = new Chart();
    this.outputValue = outputValue;

    var keyList = new ArrayList<String>();

    for (int i = 0; i < jsonData.length(); i++) {
      var jsonPoint = jsonData.getJSONObject(i);
      var chartPoint = new ChartPoint();

      // Lege die Reihenfolge der Keys fest.
      if (i == 0) {
        for (var key : jsonPoint.keySet())
          keyList.add(key);
      }

      for (var key : keyList)
        chartPoint.put(key, jsonPoint.getDouble(key));

      data.add(chartPoint);
    }

    // Erstaunlich: Was hier herauskommt, sind ann�hernd normalverteilte Daten!
    logalizer = new Logalizer(data);

    // Der Normalizer �bernimmt die Standardabweichung und den Erwartungswert.
    normalizer = new Normalizer(logalizer.convertChart(data));
  }


  /**
   * Generiert aus den eingelesenen oder generierten Daten Trainingsmuster der
   * angegebenen Eingabe- und Ausgabel�nge, die der L�nge der Eingabe- und
   * Ausgabelayer des neuronalen Netzes entsprechen sollten.
   * 
   * @param inputLength  Die Eingabel�nge des Musters.
   * @param outputLength Die Ausgabel�nge des Musters.
   * @return Eine Liste von Trainingsmustern f�r das neuronale Netz.
   */
  public DataPattern[] getTrainingPatterns(int estimateLength) {
    int patternLength = 2 * estimateLength;
    int numberPatterns = data.size() - patternLength;
    DataPattern patterns[] = new DataPattern[numberPatterns];

    for (int i = 0; i < numberPatterns; i++) {
      // Ein Ausschnitt aus den Gesamtdaten
      var segment = data.subChart(i, i + patternLength + 1);

      Vektor input = makeInputNetReadable(
          segment.subChart(0, estimateLength + 1));

      Vektor output = makeOutputNetReadable(
          segment.subChart(estimateLength + 1), segment.get(estimateLength));

      patterns[i] = new DataPattern(input, output,
          Math.max(1, 2 * estimateLength / (numberPatterns - i)));
    }

    return patterns;
  }


  public double makeOutputHumanReadable(Vektor netOut, ChartPoint lastInput) {
    double estimateValue = netOut.get(0);

    estimateValue = MathHelper.sigmoidInv(estimateValue);
    estimateValue = normalizer.reconvert(estimateValue);
    estimateValue = logalizer.reconvert(lastInput.get(outputValue),
        estimateValue);

    return estimateValue;
  }


  public Vektor makeOutputNetReadable(Chart output, ChartPoint lastInput) {
    double estimateValue = output.last().get(outputValue);

    estimateValue = logalizer.convert(lastInput.get(outputValue),
        estimateValue);
    estimateValue = normalizer.convert(estimateValue);
    estimateValue = MathHelper.sigmoid(estimateValue);

    return new Vektor(estimateValue);
  }


  public Vektor makeInputNetReadable(Chart input) {
    var convertedChart = new Logalizer(input).convertChart(input);
    convertedChart = normalizer.convertChart(convertedChart);

    return new Vektor(convertedChart.flatten());
  }


  /**
   * Getter f�r den internen Datensatz.
   * 
   * @return Den internen Datensatz.
   */
  public Chart getData() {
    return data;
  }


  public String getOutputValue() {
    return outputValue;
  }
}
