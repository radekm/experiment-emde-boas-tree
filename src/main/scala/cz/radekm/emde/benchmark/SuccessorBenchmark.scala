package cz.radekm.emde.benchmark

import java.util.concurrent.TimeUnit

import cz.radekm.emde._
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, OutputTimeUnit, State}
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup

object SuccessorBenchmark {
  val implementations = Map[String, () => PrioSet](
    "BitVector" -> (() => BitVector(UniverseSizeLog)),
    "BitVectorLong" -> (() => BitVectorLong(UniverseSizeLog)),
    "BitVectorViaBitSet" -> (() => BitVectorViaBitSet(UniverseSizeLog)),
    "ClusteredBitVector" -> (() => ClusteredBitVector(UniverseSizeLog, UniverseSizeLog / 2)),
    "RecursiveClusteredBitVector" -> (() => RecursiveClusteredBitVector(UniverseSizeLog, UniverseSizeLog / 2)),
    "ScalaTreeSet" -> (() => ScalaTreeSet(UniverseSizeLog)),
    "TreeSet" -> (() => TreeSet(UniverseSizeLog)),
    "EmdeSet" -> (() => EmdeSet(UniverseSizeLog, UniverseSizeLog / 2, 6)),
  )

  val distances = Map[String, Int](
    "close" -> 3,
    "medium" -> 2_000,
    "far" -> 15_000_000
  )
}

@State(Scope.Benchmark)
class SuccessorBenchmarkState {
  @Param(Array("2", "20", "200", "2000", "20000"))
  var aSetSize: Int = 0

  @Param(Array("close", "medium", "far"))
  var distance: String = ""

  @Param(Array(
    "BitVector", "BitVectorLong", "BitVectorViaBitSet",
    "ClusteredBitVector", "RecursiveClusteredBitVector",
    "ScalaTreeSet", "TreeSet", "EmdeSet"
  ))
  var implementation: String = ""

  var prioSet: PrioSet = null

  @Setup(Level.Trial)
  def setUp(): Unit = {
    prioSet = SuccessorBenchmark.implementations(implementation)()
    prioSet.insert(1)

    val d = SuccessorBenchmark.distances(distance)
    // We want to insert only `aSetSize - 1` elements since `1` is already there.
    (0 until aSetSize - 1).foreach { i =>
      prioSet.insert(d + i)
    }
  }
}

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
class SuccessorBenchmark {
  @Benchmark
  def successor(st: SuccessorBenchmarkState): Option[Int] = {
    st.prioSet.successor(1)
  }
}
