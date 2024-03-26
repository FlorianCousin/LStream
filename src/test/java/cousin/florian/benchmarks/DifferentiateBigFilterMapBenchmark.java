package cousin.florian.benchmarks;

import cousin.florian.LStream;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/*
 * Benchmark                                           Mode  Cnt    Score   Error  Units
 * DifferentiateBigFilterMapBenchmark.lstreamGenerate  avgt   10   43.197 ± 0.771  ms/op
 * DifferentiateBigFilterMapBenchmark.lstreamFilter    avgt   10  103.327 ± 0.971  ms/op
 * DifferentiateBigFilterMapBenchmark.lstreamMap       avgt   10  114.149 ± 1.750  ms/op
 * DifferentiateBigFilterMapBenchmark.lstreamToList    avgt   10  177.028 ± 9.231  ms/op
 */

@State(Scope.Benchmark)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 5, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DifferentiateBigFilterMapBenchmark {

  private static final int NB_ELEMENTS = BigInteger.TEN.pow(7).intValueExact();
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
  public void lstreamGenerate(Blackhole blackhole) {

    LStream.from(testData).limit(NB_ELEMENTS).forEach(blackhole::consume);
  }

  @Benchmark
  public void lstreamFilter(Blackhole blackhole) {

    LStream.from(testData).filter(i -> i > 0).forEach(blackhole::consume);
  }

  @Benchmark
  public void lstreamMap(Blackhole blackhole) {

    LStream.from(testData).filter(i -> i > 0).map(i -> i - 1).forEach(blackhole::consume);
  }

  @Benchmark
  public void lstreamToList(Blackhole blackhole) {

    List<Integer> generatedList =
        LStream.from(testData).filter(i -> i > 0).map(i -> i - 1).toList();

    blackhole.consume(generatedList);
  }
}
