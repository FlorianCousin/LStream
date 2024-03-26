package cousin.florian.benchmarks;

import cousin.florian.LStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/*
 * Benchmark                               Mode  Cnt    Score    Error  Units
 * BigFilterMapBenchmark.lstream           avgt   20  175.065 ± 10.409  ms/op
 * BigFilterMapBenchmark.sequentialStream  avgt   20  148.450 ± 20.376  ms/op
 * BigFilterMapBenchmark.parallelStream    avgt   20   36.511 ± 10.225  ms/op
 *
 * 2024-03-03
 * BigFilterMapBenchmark.lstream           avgt   20  164.060 ±  2.326  ms/op
 * BigFilterMapBenchmark.sequentialStream  avgt   20  171.586 ± 58.912  ms/op
 * BigFilterMapBenchmark.parallelStream    avgt   20   39.481 ±  5.593  ms/op
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
