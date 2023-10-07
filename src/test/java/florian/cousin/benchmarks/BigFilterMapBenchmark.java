package florian.cousin.benchmarks;

import florian.cousin.LStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/*
 * Benchmark                      Mode  Cnt    Score    Error  Units
 * BigFilterMapBenchmark.lstream           avgt   10  205.150 ± 33.819  ms/op
 * BigFilterMapBenchmark.sequentialStream  avgt   10  133.904 ± 33.752  ms/op
 * BigFilterMapBenchmark.parallelStream    avgt   10   42.398 ±  2.257  ms/op
 *
 * Benchmark                               Mode  Cnt    Score    Error  Units
 * BigFilterMapBenchmark.lstream           avgt   10  184.276 ± 21.508  ms/op
 * BigFilterMapBenchmark.sequentialStream  avgt   10  142.129 ± 38.148  ms/op
 * BigFilterMapBenchmark.parallelStream    avgt   10   39.669 ±  7.183  ms/op
 */

@State(Scope.Benchmark)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BigFilterMapBenchmark {

  private static final int NB_ELEMENTS = 10_000_000;
  private static final Random RANDOM = new Random(Instant.now().getNano());

  private static List<Integer> testData;

  @Setup(Level.Iteration)
  public void setUp() {
    testData = new ArrayList<>(NB_ELEMENTS);
    for (int i = 0; i < NB_ELEMENTS; i++) {
      testData.add(RANDOM.nextInt(-50, 51));
    }
  }

  @Benchmark
  public void lstream(Blackhole blackhole) {

    List<Integer> generatedList =
        LStream.from(testData).filter(i -> i > 0).map(i -> i - 1).toList();

    blackhole.consume(generatedList);
  }

  @Benchmark
  public void sequentialStream(Blackhole blackhole) {

    List<Integer> generatedList = testData.stream().filter(i -> i > 0).map(i -> i - 1).toList();

    blackhole.consume(generatedList);
  }

  @Benchmark
  public void parallelStream(Blackhole blackhole) {

    List<Integer> generatedList =
        testData.stream().parallel().filter(i -> i > 0).map(i -> i - 1).toList();

    blackhole.consume(generatedList);
  }
}
