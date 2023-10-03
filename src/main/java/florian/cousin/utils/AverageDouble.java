package florian.cousin.utils;

public class AverageDouble {

  private double currentSum = 0;
  private long currentNumberOfValues = 0;

  public void addValue(double value) {
    currentSum += value;
    currentNumberOfValues++;
  }

  public double getAverage() {
    return currentNumberOfValues == 0 ? 0d : currentSum / currentNumberOfValues;
  }
}
