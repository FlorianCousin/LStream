package florian.cousin.benchmarks;

import florian.cousin.LStream;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/*
 * Benchmark                      Mode  Cnt    Score    Error  Units
 * BigFilterMap.lstream           avgt   20   22.173 ±  1.157  ms/op
 * BigFilterMap.parallelStream    avgt   20  193.539 ± 22.434  ms/op
 * BigFilterMap.sequentialStream  avgt   20   20.194 ±  3.177  ms/op
 */

@State(Scope.Benchmark)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 10, time = 5)
@Measurement(iterations = 5, time = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BigFilterMap {

  private static final int NB_ELEMENTS = 1_000_000;
  private static final Random RANDOM = new Random(Instant.now().getNano());

  @Benchmark
  public void lstream(Blackhole blackhole) {

    List<Integer> generatedList =
        LStream.generate(() -> RANDOM.nextInt(-50, 51))
            .limit(NB_ELEMENTS)
            .filter(i -> i > 0)
            .map(i -> i - 1)
            .toList();

    blackhole.consume(generatedList);
  }

  @Benchmark
  public void sequentialStream(Blackhole blackhole) {

    List<Integer> generatedList =
        Stream.generate(() -> RANDOM.nextInt(-50, 51))
            .limit(NB_ELEMENTS)
            .filter(i -> i > 0)
            .map(i -> i - 1)
            .toList();

    blackhole.consume(generatedList);
  }

  @Benchmark
  public void parallelStream(Blackhole blackhole) {

    List<Integer> generatedList =
        Stream.generate(() -> RANDOM.nextInt(-50, 51))
            .parallel()
            .limit(NB_ELEMENTS)
            .filter(i -> i > 0)
            .map(i -> i - 1)
            .toList();

    blackhole.consume(generatedList);
  }
}
