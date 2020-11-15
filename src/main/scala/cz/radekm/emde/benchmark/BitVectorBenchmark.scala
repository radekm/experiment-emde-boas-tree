package cz.radekm.emde.benchmark

import java.util.concurrent.TimeUnit

import cz.radekm.emde._
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, OutputTimeUnit}

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
abstract class AbstractSuccessorBenchmark {
  protected def create(): PrioSet

  @Benchmark
  def successorFar(): Option[Int] = {
    val prioSet = create()
    prioSet.insert(1)
    prioSet.insert(15_000_000)
    prioSet.successor(1)
  }

  @Benchmark
  def successorClose(): Option[Int] = {
    val prioSet = create()
    prioSet.insert(1)
    prioSet.insert(2_000)
    prioSet.successor(1)
  }
}

class BitVectorSuccessorBenchmark extends AbstractSuccessorBenchmark {
  override protected def create(): PrioSet = BitVector(UniverseSizeLog)
}

class ClusteredBitVectorSuccessorBenchmark extends AbstractSuccessorBenchmark {
  override protected def create(): PrioSet = ClusteredBitVector(UniverseSizeLog, UniverseSizeLog / 2)
}

class RecursiveClusteredBitVectorSuccessorBenchmark extends AbstractSuccessorBenchmark {
  override protected def create(): PrioSet = RecursiveClusteredBitVector(UniverseSizeLog, UniverseSizeLog / 2)
}
