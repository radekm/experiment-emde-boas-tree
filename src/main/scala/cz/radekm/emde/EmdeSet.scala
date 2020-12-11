package cz.radekm.emde

import java.util.BitSet

object EmdeSet {
  def apply(universeSizeLog: Int, clusterSizeLog: Int, nonUniverseSizeLog: Int): EmdeSet =
    if (universeSizeLog <= nonUniverseSizeLog) new EmdeSetNonRec(universeSizeLog)
    else new EmdeSetRec(universeSizeLog, clusterSizeLog, nonUniverseSizeLog)
}

abstract class EmdeSet extends PrioSet {
  def min: Int
  def max: Int
  final def isEmpty: Boolean = min > max
  final def isSingleton: Boolean = min == max
}

private class EmdeSetNonRec(
  override final val universeSizeLog: Int
) extends EmdeSet {
  private val bitSet = new BitSet(universeSize)

  override def min: Int = if (bitSet.isEmpty) Int.MaxValue else bitSet.nextSetBit(0)
  override def max: Int = if (bitSet.isEmpty) Int.MinValue else bitSet.previousSetBit(universeSize - 1)

  override final def clusterSizeLog = 6
  override final def numClustersLog = universeSizeLog - clusterSizeLog

  override def member(x: Int): Boolean =
    if (x < 0) false
    else bitSet.get(x)

  override def successor(x: Int): Option[Int] = {
    val bit =
      if (x < 0) bitSet.nextSetBit(0)
      else if (x == Int.MaxValue) -1 // Next case is not defined for `x == MaxValue`.
      else bitSet.nextSetBit(x + 1)
    if (bit == -1) None
    else Some(bit)
  }

  override def insert(x: Int): Unit =
    if (x < 0 || x >= universeSize) throwOutOfRange()
    else bitSet.set(x, true)

  override def delete(x: Int): Unit =
    if (x >= 0) {
      // FIXME Prevent resize by `recalculateWordsInUse`.
      bitSet.set(x, false)
    }
}

// Properties:
// - Empty `Emde` has `min == Int.MaxValue` and `max == Int.MinValue`.
// - `min` is not stored in `clusters` (ie. singleton set does not need `clusters` nor `summary`).
// - Cluster `h` is non-empty iff `summary.member(h)`.
// - If `summary` is non-empty then `summary.min` is the cluster with
//   the second smallest element of the whole `Emde`.
// - If `summary` is non-empty then `summary.max` is the cluster with
//   the biggest element of the whole `Emde`.
private class EmdeSetRec(
  override final val universeSizeLog: Int,
  override final val clusterSizeLog: Int,
  private val nonSplittableClusterSizeLog: Int,
) extends EmdeSet {
  override final def numClustersLog: Int = universeSizeLog - clusterSizeLog

  private var minHolder = Int.MaxValue
  private var maxHolder = Int.MinValue
  override def min: Int = minHolder
  override def max: Int = maxHolder
  def min_=(x: Int) = minHolder = x
  def max_=(x: Int) = maxHolder = x

  // `min` is never stored in `summary` and `clusters`
  // (ie. if the set is empty or singleton `summary` and `clusters` are not used).
  // Contains 1 if something is in the cluster.
  private var summary: EmdeSet = EmdeSet(numClustersLog, numClustersLog / 2, nonSplittableClusterSizeLog)
  // Contents of the cluster.
  private var clusters: Array[EmdeSet] = Array.fill(numClusters) { EmdeSet(clusterSizeLog, clusterSizeLog / 2, nonSplittableClusterSizeLog) }

  /**
   * Returns whether `i` is in the set.
   */
  override def member(x: Int): Boolean =
    !isEmpty && (min <= x && x <= max && (x == min || clusters(high(x)).member(low(x))))

  /**
   * Returns smallest `j: Int` such that `j > i` and `member(j)` returns `true`.
   */
  override def successor(x: Int): Option[Int] =
    if (isEmpty) None
    else if (x < min) Some(min)
    else if (x >= max) None
    else {
      // Here we know that set is not singleton (because we know `min < max`)
      // and that `x >= min && x < max` so we for sure will find a successor.
      val h = high(x)
      val l = low(x)
      val cl = clusters(h)
      // Successor is the same cluster as `x`.
      if (!cl.isEmpty && l < cl.max) Some(index(h, cl.successor(l).get))
      // Successor is in some cluster after cluster with `x`.
      else {
        // Cluster with successor.
        val h2 = summary.successor(h).get
        Some(index(h2, clusters(h2).min))
      }
    }

  /**
   * Following must hold: `0 <= i < universeSize` otherwise exception will be thrown.
   *
   * Updates set so that `member(i)` returns `true`.
   */
  override def insert(x: Int): Unit =
    if (x < 0) throwOutOfRange()
    else {
      if (high(x) >= numClusters) throwOutOfRange()
      if (isEmpty) {
        min = x
        max = x
      } else if (x == min && x == max) {
        // No need to do anything.
        // Set is singleton and it will stay singleton.
      } else {
        // Maybe it's singleton here but it won't stay singleton (because `x != min` or `x != max`).
        var reallyInsert = x
        if (x < min) {
          reallyInsert = min
          min = x
        } else if (x > max) {
          max = x
        }
        // Now we have `min < reallyInsert <= max`.
        val h = high(reallyInsert)
        val l = low(reallyInsert)
        val cl = clusters(h)
        if (cl.isEmpty) {
          // Insert into summary may take time but insert into `cl` will be constant time
          // (because it was empty and we need to set only `min` and `max`).
          summary.insert(h)
          cl.insert(l)
        } else {
          // No need to insert into `summary` (because `cl` is not empty so there's another item).
          cl.insert(l)
        }
      }
    }

  /**
   * Updates set so that `member(i)` returns `false`.
   */
  override def delete(x: Int): Unit =
    if (isEmpty || x < min || x > max) ()
    else if (isSingleton) {
      min = Int.MaxValue
      max = Int.MinValue
    } else {
      // Here we know that set contains at least 2 items.
      // And that `min <= x <= max`.

      if (x == min) {
        // Cluster with the second smallest element.
        val h = summary.min
        val cl = clusters(h)
        val l = cl.min
        // New `min` is the second smallest element.
        min = index(h, l)

        // Since `min` should not be in `summary` nor in `clusters` we have to remove it
        // (it is there because it was the second smallest element).
        cl.delete(l)
        if (cl.isEmpty) {
          summary.delete(h)
        }
      } else if (x == max) {
        val h = high(x)
        val cl = clusters(h)
        val l = low(x)

        cl.delete(l)
        if (cl.isEmpty) {
          summary.delete(h)
        }

        // Find new max.
        if (summary.isEmpty) {
          // Only element left is min.
          max = min
        } else {
          max = clusters(summary.max).max
        }
      } else {
        val h = high(x)
        val cl = clusters(h)
        val l = low(x)

        cl.delete(l)
        if (cl.isEmpty) {
          summary.delete(h)
        }
      }
    }
}
