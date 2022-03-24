package util;

import data.Chart;
import math.Vektor;


public abstract interface Converter {

  public Chart convertChart(Chart original);


  public Chart reconvertChart(Chart converted);


  public Vektor convertVektor(Vektor vektor);


  public Vektor reconvertVektor(Vektor vektor, String key);
}
