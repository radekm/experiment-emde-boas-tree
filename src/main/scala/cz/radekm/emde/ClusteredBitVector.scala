package cz.radekm.emde

object ClusteredBitVector {
  def apply(universeSizeLog: Int,  clusterSizeLog: Int) = new ClusteredBitVector(universeSizeLog, clusterSizeLog)
}

class ClusteredBitVector(
  override final val universeSizeLog: Int,
  override final val clusterSizeLog: Int,
  private val createPrioSet: Int => PrioSet = universeSizeLog => BitVector(universeSizeLog),
) extends PrioSet {

  override final def numClustersLog: Int = universeSizeLog - clusterSizeLog

  // Contains 1 if something is in the cluster.
  private val summary = createPrioSet(numClustersLog)
  // Contents of the cluster.
  private val clusters = Array.fill(numClusters) { createPrioSet(clusterSizeLog) }
  private val numItemsInCluster = Array.fill(numClusters) { 0 }

  override def member(x: Int): Boolean =
    if (x < 0) false
    else {
      val h = high(x)
      h < numClusters && clusters(h).member(low(x))
    }

  // This is same as `BitVector` but instead of bytes we have clusters.
  override def successor(x: Int): Option[Int] = {
    var h = 0
    var l = 0

    if (x >= 0) {
      h = high(x)
      l = low(x) + 1
    }

    while (h < numClusters) {
      val cluster = clusters(h)
      // `summary.member(h)` is an optimization.
      while (summary.member(h) && l < clusterSize) {
        if (cluster.member(l))
          return Some(index(h, l))
        l += 1
      }
      l = 0
      h += 1
    }
    None
  }

  override def insert(x: Int): Unit =
    if (x < 0) throwOutOfRange()
    else {
      val h = high(x)
      // If this succeeds it means that cluster `h` exists.
      summary.insert(h)
      // TODO Could be simpler if insert was returning whether the item
      //      was really inserted or was already there.
      if (!clusters(h).member(low(x))) {
        clusters(h).insert(low(x))
        numItemsInCluster(h) += 1
      }
    }

  override def delete(x: Int): Unit =
    if (x >= 0) {
      if (member(x)) {
        val h = high(x)
        clusters(h).delete(low(x))
        numItemsInCluster(h) -= 1
        if (numItemsInCluster(h) == 0) {
          summary.delete(h)
        }
      }
    }
}
