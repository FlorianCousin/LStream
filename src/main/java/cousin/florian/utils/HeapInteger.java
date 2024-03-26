package cousin.florian.utils;

import lombok.Getter;

@Getter
public class HeapInteger {

  private int value = 0;

  public void add(int addValue) {
    value += addValue;
  }
}
