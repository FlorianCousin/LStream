package cousin.florian.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeapReference<T> {

  private T value = null;
}
