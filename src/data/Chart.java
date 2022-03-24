package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;


public class Chart extends ArrayList<ChartPoint> {

  /**
   * 
   */
  private static final long serialVersionUID = 6504588475610034761L;


  public Chart() {
    super();
  }


  public Chart(List<ChartPoint> points) {
    super(points);
  }


  public List<Double> get(String key) {
    return stream().map(map -> map.get(key)).collect(Collectors.toList());
  }


  public ChartPoint last() {
    return get(size() - 1);
  }


  public Set<String> keySet() {
    if (size() > 0)
      return get(0).keySet();
    else
      return null;
  }


  public Chart subChart(int fromIndex, int toIndex) {
    return new Chart(subList(fromIndex, toIndex));
  }


  public Chart subChart(int fromIndex) {
    return new Chart(subList(fromIndex, size()));
  }


  public Chart apply(UnaryOperator<Double> op) {
    return new Chart(
        this.stream().map(chartPoint -> ChartPoint.apply(op, chartPoint))
            .collect(Collectors.toList()));
  }


  public List<Double> flatten() {
    var res = new ArrayList<Double>();

    for (var point : this) {
      for (var key : point.keySet())
        res.add(point.get(key));
    }

    return res;
  }
}
