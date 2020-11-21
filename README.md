# Results of benchmarks

I'm running benchmark in VM in Azure Cloud:

- VM: Standard A2 v2 (2 vcpus, 4 GiB memory)
- Ubuntu 18.04
- OpenJDK: 11.0.9.1 (build 11.0.9.1+1-Ubuntu-0ubuntu1.18.04)

I'm using following SBT command:

```
jmh:run -i 20 -wi 20 -f3 -t1 .*
```

# Latest results

For commit c1ad3626:

```
Benchmark                                                      Mode  Cnt   Score   Error  Units
BitVectorLongSuccessorBenchmark.successorClose                 avgt   60   0.536 ± 0.004  ms/op
BitVectorLongSuccessorBenchmark.successorFar                   avgt   60   0.814 ± 0.002  ms/op
BitVectorLongSuccessorBenchmark.successorMedium                avgt   60   0.530 ± 0.002  ms/op
BitVectorSuccessorBenchmark.successorClose                     avgt   60   0.530 ± 0.002  ms/op
BitVectorSuccessorBenchmark.successorFar                       avgt   60   8.344 ± 1.561  ms/op
BitVectorSuccessorBenchmark.successorMedium                    avgt   60   0.529 ± 0.001  ms/op
BitVectorViaBitSetSuccessorBenchmark.successorClose            avgt   60   0.531 ± 0.002  ms/op
BitVectorViaBitSetSuccessorBenchmark.successorFar              avgt   60   1.130 ± 0.003  ms/op
BitVectorViaBitSetSuccessorBenchmark.successorMedium           avgt   60   0.532 ± 0.001  ms/op
ClusteredBitVectorSuccessorBenchmark.successorClose            avgt   60   0.976 ± 0.002  ms/op
ClusteredBitVectorSuccessorBenchmark.successorFar              avgt   60   1.016 ± 0.002  ms/op
ClusteredBitVectorSuccessorBenchmark.successorMedium           avgt   60   0.990 ± 0.003  ms/op
RecursiveClusteredBitVectorSuccessorBenchmark.successorClose   avgt   60  12.772 ± 0.063  ms/op
RecursiveClusteredBitVectorSuccessorBenchmark.successorFar     avgt   60  13.658 ± 0.563  ms/op
RecursiveClusteredBitVectorSuccessorBenchmark.successorMedium  avgt   60  10.457 ± 0.599  ms/op
```

# Optimization of `BitVectorLong` using `Long.numberOfTrailingZeros`

Implementing `BitVectorLong.successor` by `Long.numberOfTrailingZeros` in c1ad3626.

Original version:

```scala
  override def successor(x: Int): Option[Int] = {
    var h = 0
    var l = 0

    if (x >= 0) {
      h = high(x)
      l = low(x) + 1
    }

    while (h < arr.length) {
      val cluster = arr(h)
      // `cluster != 0` is an optimization.
      while (cluster != 0 && l < 64) {
        if (getBit(cluster, l))
          return Some(index(h, l))
        l += 1
      }
      l = 0
      h += 1
    }
    None
  }
```

New version:

```scala
  override def successor(x: Int): Option[Int] = {
    var h = 0
    var cluster: Long = 0

    if (x >= 0) {
      h = high(x)
      val l = low(x) + 1

      if (h < arr.length && l < clusterSize) {
        cluster = (arr(h) >> l) << l
      }
    }

    while (true) {
      if (cluster != 0)
        return Some(index(h, java.lang.Long.numberOfTrailingZeros(cluster)))
      h += 1
      if (h < arr.length) {
        cluster = arr(h)
      } else {
        return None
      }
    }
    sys.error("Absurd")
  }
```

Original performance:

```
Benchmark                                                      Mode  Cnt   Score   Error  Units
BitVectorLongSuccessorBenchmark.successorClose                 avgt   60   0.533 ± 0.002  ms/op
BitVectorLongSuccessorBenchmark.successorFar                   avgt   60   0.980 ± 0.003  ms/op
BitVectorLongSuccessorBenchmark.successorMedium                avgt   60   0.532 ± 0.001  ms/op
```

New performance (only `successorFar` has better performance otherwise similar):

```
Benchmark                                                      Mode  Cnt   Score   Error  Units
BitVectorLongSuccessorBenchmark.successorClose                 avgt   60   0.536 ± 0.004  ms/op
BitVectorLongSuccessorBenchmark.successorFar                   avgt   60   0.814 ± 0.002  ms/op
BitVectorLongSuccessorBenchmark.successorMedium                avgt   60   0.530 ± 0.002  ms/op
```
