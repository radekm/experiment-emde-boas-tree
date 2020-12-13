# Results of benchmarks

Van Emde Boas tree is a data structure which implements set with operations member, insert, delete and successor
over `m` bit integers. Required space depends on universe size `u = 2^m`,
and not how many elements are actually in the set.

We compared van Emde Boas tree to other data structures:
- `BitVector` - set is implemented as array of bytes, successor operation simply inspects bits
  until it finds one or reaches end of the bit vector.
- `BitVectorLong` - similar to `BitVector` but backed by array of longs (instead array of bytes).
- `BitVectorViaBitSet` - similar to `BitVector` but backed by `java.util.BitSet`.
- `ClusteredBitVector` - bit vector which is divided into `sqrt(u)` clusters. For each cluster it's remembered
  (in `summary` bit vector) whether it is empty or whether it contains any elements.
  This speeds up successor operation since empty clusters can be skipped without looking into them.
- `RecursiveClusteredBitVector` - similar to `ClusteredBitVector` but clusters and summary bit vectors are represented
  recursively by `RecursiveClusteredBitVector`.
- `ScalaTreeSet` - is backed by `scala.collection.mutable.TreeSet`.
- `TreeSet` - is backed by `java.util.TreeSet`.

We used 24 bit integers (universe 0...2^24-1) in benchmarks.

We're running benchmark in VM in Azure Cloud:

- VM: Standard A2 v2 (2 vcpus, 4 GiB memory)
- Ubuntu 18.04
- OpenJDK: 11.0.9.1 (build 11.0.9.1+1-Ubuntu-0ubuntu1.18.04)

We're using following SBT command:

```
jmh:run -i 20 -wi 20 -f3 -t1 .*
```

# Results for successor benchmark

Results are for the commit `bbd7cd89`.

## Small sets

Let's start with the set which contains only 2 elements (parameter `aSetSize`):

```
(aSetSize)  (distance)             (implementation)  Mode  Cnt        Score         Error  Units
         2       close                    BitVector  avgt   60       27.241 ±       0.064  ns/op
         2       close                BitVectorLong  avgt   60       29.826 ±       0.074  ns/op
         2       close           BitVectorViaBitSet  avgt   60       28.075 ±       0.190  ns/op
         2       close           ClusteredBitVector  avgt   60       51.021 ±       0.082  ns/op
         2       close  RecursiveClusteredBitVector  avgt   60       74.864 ±       0.192  ns/op
         2       close                 ScalaTreeSet  avgt   60       35.601 ±       0.212  ns/op
         2       close                      TreeSet  avgt   60       33.099 ±       0.103  ns/op
         2       close                      EmdeSet  avgt   60       47.274 ±       0.161  ns/op
         2      medium                    BitVector  avgt   60      491.066 ±       0.615  ns/op
         2      medium                BitVectorLong  avgt   60       56.049 ±       0.097  ns/op
         2      medium           BitVectorViaBitSet  avgt   60      123.256 ±       0.285  ns/op
         2      medium           ClusteredBitVector  avgt   60    10837.331 ±      17.146  ns/op
         2      medium  RecursiveClusteredBitVector  avgt   60    19900.014 ±     328.142  ns/op
         2      medium                 ScalaTreeSet  avgt   60       35.416 ±       0.068  ns/op
         2      medium                      TreeSet  avgt   60       32.966 ±       0.057  ns/op
         2      medium                      EmdeSet  avgt   60       49.530 ±       0.317  ns/op
         2         far                    BitVector  avgt   60  7273802.050 ± 1331912.253  ns/op
         2         far                BitVectorLong  avgt   60   249016.053 ±     336.710  ns/op
         2         far           BitVectorViaBitSet  avgt   60   550599.810 ±    1318.817  ns/op
         2         far           ClusteredBitVector  avgt   60    48326.256 ±    1183.653  ns/op
         2         far  RecursiveClusteredBitVector  avgt   60   101022.054 ±    1022.792  ns/op
         2         far                 ScalaTreeSet  avgt   60       35.169 ±       0.049  ns/op
         2         far                      TreeSet  avgt   60       33.748 ±       0.355  ns/op
         2         far                      EmdeSet  avgt   60       52.254 ±       0.283  ns/op
```

Since the performance of successor operation in bit-vector-based structures
depends on the distance of successor we have parameter `distance`:
- `close` - distance 3.
- `medium` - distance 2 thousand.
- `far` - distance 15 million.

When distance is `close` and set size is `2` the performance of all data structures is similar.
But when distance is increased the performance of bit-vector based structures except vEB gets awful.

Also notice the difference between `BitVector` and `BitVectorLong` on `medium` distance.
For `BitVector` we measured `491` ns and for `BitVectorLong` we measured only `56` ns.
It may seem like an accident, but it also happens for other set sizes. We don't have explanation for this.

## vEB vs TreeSet

Let's see how vEB compares to `TreeSet`s:

```
(aSetSize)  (distance)             (implementation)  Mode  Cnt        Score         Error  Units
         2         far                 ScalaTreeSet  avgt   60       35.169 ±       0.049  ns/op
         2         far                      TreeSet  avgt   60       33.748 ±       0.355  ns/op
         2         far                      EmdeSet  avgt   60       52.254 ±       0.283  ns/op
        20         far                 ScalaTreeSet  avgt   60       54.260 ±       0.071  ns/op
        20         far                      TreeSet  avgt   60       46.321 ±       0.056  ns/op
        20         far                      EmdeSet  avgt   60       52.528 ±       0.321  ns/op
       200         far                 ScalaTreeSet  avgt   60       80.283 ±       0.262  ns/op
       200         far                      TreeSet  avgt   60       59.449 ±       0.423  ns/op
       200         far                      EmdeSet  avgt   60       53.934 ±       0.366  ns/op
      2000         far                 ScalaTreeSet  avgt   60      105.729 ±       0.471  ns/op
      2000         far                      TreeSet  avgt   60       73.018 ±       0.337  ns/op
      2000         far                      EmdeSet  avgt   60       55.927 ±       0.353  ns/op
     20000         far                 ScalaTreeSet  avgt   60      144.949 ±       7.154  ns/op
     20000         far                      TreeSet  avgt   60       89.217 ±       1.388  ns/op
     20000         far                      EmdeSet  avgt   60       55.264 ±       0.451  ns/op
```

Successor operation in vEB has the same speed no matter how many elements are in the set.
Successor operation in `TreeSet`s is slower when the set is bigger.
vEB beats `TreeSet`s on sets with 200 or more elements.

## Summary

Performance of successor operation in van Emde Boas doesn't depend on the number of elements in the set.
When the represented set is small it's slower than `TreeSet`s. When the represented set contained 200
or more elements, successor in vEB was faster than successor in `TreeSet`s.

Unfortunately successor in Scala `TreeSet` is significantly slower than Java `TreeSet`.

Output of successor benchmark is available in [reports/successor-benchmark](reports/successor-benchmark).
