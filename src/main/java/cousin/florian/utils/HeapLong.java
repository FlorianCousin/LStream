package cousin.florian.utils;

import lombok.Getter;

@Getter
public class HeapLong {

  private long value = 0;

  public void add(long addValue) {
    value += addValue;
  }
}
