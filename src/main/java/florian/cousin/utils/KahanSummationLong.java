package florian.cousin.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Implementation of the <a href="https://en.wikipedia.org/wiki/Kahan_summation_algorithm">Kahan
 * algorithm</a>
 */
@NoArgsConstructor
public final class KahanSummationLong {

  @Getter private double sum;
  private double error;

  public void add(double value) {
    double valueCompensated = value - error;
    double newSum = sum + valueCompensated;
    error = (newSum - sum) - valueCompensated;
    sum = newSum;
  }
}
