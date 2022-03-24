package util;

import java.util.ArrayList;
import java.util.List;

import data.Chart;


/**
 * Enthält auf geordnete Art und Weise die Converter, die in dieser Reihenfolge
 * auf Daten angewendet werden sollen.
 */
public class ConvertingSequence extends ArrayList<Converter> {

  /**
   * Automatisch generierte serialVersionUID.
   */
  private static final long serialVersionUID = -5252327337504136788L;


  public ConvertingSequence() {
    super();
  }


  public ConvertingSequence(List<Converter> converters) {
    super(converters);
  }


  public Chart applyInOrder(Chart c) {
    var result = new Chart(c);

    for (int i = 0; i < size(); i++)
      result = convert(result, i);

    return result;
  }


  public Chart applyInReverse(Chart c) {
    var result = new Chart(c);

    for (int i = size() - 1; i >= 0; i--)
      result = reconvert(result, i);

    return result;
  }


  public Chart convert(Chart c, int index) {
    return get(index).convertChart(c);
  }


  public Chart reconvert(Chart c, int index) {
    return get(index).reconvertChart(c);
  }


  public ConvertingSequence subSequence(int fromIndex) {
    return new ConvertingSequence(subList(fromIndex, size()));
  }
}
