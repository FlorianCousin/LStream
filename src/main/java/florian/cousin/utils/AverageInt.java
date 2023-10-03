package florian.cousin.utils;

public class AverageInt {

  private long currentSum = 0;
  private long currentNumberOfValues = 0;

  public void addValue(int value) {
    currentSum += value;
    currentNumberOfValues++;
  }

  public double getAverage() {
    return currentNumberOfValues == 0 ? 0d : (double) currentSum / currentNumberOfValues;
  }
}
