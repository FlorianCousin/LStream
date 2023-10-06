package florian.cousin.benchmarks;

import florian.cousin.LStream;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/*
 * Benchmark                          Mode  Cnt    Score    Error  Units
 * DifferentiateBigFilterMapBenchmark.lstreamGenerate  avgt   10   99.931 ±  2.314  ms/op
 * DifferentiateBigFilterMapBenchmark.lstreamFilter    avgt   10  138.848 ±  5.723  ms/op
 * DifferentiateBigFilterMapBenchmark.lstreamMap       avgt   10  151.599 ±  4.873  ms/op
 * DifferentiateBigFilterMapBenchmark.lstreamToList    avgt   10  245.338 ± 14.463  ms/op
 */

@State(Scope.Benchmark)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DifferentiateBigFilterMapBenchmark {

  private static final int NB_ELEMENTS = BigInteger.TEN.pow(7).intValueExact();
  private static final Random RANDOM = new Random(Instant.now().getNano());

  @Benchmark
  public void lstreamGenerate(Blackhole blackhole) {

    LStream.generate(() -> RANDOM.nextInt(-50, 51)).limit(NB_ELEMENTS).forEach(blackhole::consume);
  }

  @Benchmark
  public void lstreamFilter(Blackhole blackhole) {

    LStream.generate(() -> RANDOM.nextInt(-50, 51))
        .limit(NB_ELEMENTS)
        .filter(i -> i > 0)
        .forEach(blackhole::consume);
  }

  @Benchmark
  public void lstreamMap(Blackhole blackhole) {

    LStream.generate(() -> RANDOM.nextInt(-50, 51))
        .limit(NB_ELEMENTS)
        .filter(i -> i > 0)
        .map(i -> i - 1)
        .forEach(blackhole::consume);
  }

  @Benchmark
  public void lstreamToList(Blackhole blackhole) {

    List<Integer> generatedList =
        LStream.generate(() -> RANDOM.nextInt(-50, 51))
            .limit(NB_ELEMENTS)
            .filter(i -> i > 0)
            .map(i -> i - 1)
            .toList();

    blackhole.consume(generatedList);
  }
}
