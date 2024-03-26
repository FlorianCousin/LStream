package cousin.florian.utils;

public class AverageLong {

  private long currentSum = 0;
  private long currentNumberOfValues = 0;

  public void addValue(long value) {
    currentSum += value;
    currentNumberOfValues++;
  }

  public double getAverage() {
    return currentNumberOfValues == 0 ? 0d : (double) currentSum / currentNumberOfValues;
  }
}
