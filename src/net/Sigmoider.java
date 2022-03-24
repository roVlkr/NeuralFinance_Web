package net;

import data.Chart;
import math.MathHelper;
import math.Vektor;
import util.UnaryConverter;


public class Sigmoider implements UnaryConverter {

  @Override
  public Chart convertChart(Chart original) {
    return original.apply(this::convert);
  }


  @Override
  public Chart reconvertChart(Chart converted) {
    return converted.apply(this::reconvert);
  }


  @Override
  public double convert(double d) {
    return MathHelper.sigmoid(d);
  }


  @Override
  public double reconvert(double d) {
    return MathHelper.sigmoidInv(d);
  }


  @Override
  public Vektor convertVektor(Vektor vektor) {
    return vektor.apply(this::convert);
  }


  @Override
  public Vektor reconvertVektor(Vektor vektor, String key) {
    return vektor.apply(this::reconvert);
  }
}
