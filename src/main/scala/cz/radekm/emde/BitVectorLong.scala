package cz.radekm.emde


object BitVectorLong {
  def apply(universeSizeLog: Int) = new BitVectorLong(universeSizeLog)
}

class BitVectorLong(override final val universeSizeLog: Int) extends PrioSet {
  override final def clusterSizeLog = 6
  override final def numClustersLog = universeSizeLog - clusterSizeLog

  private val arr = new Array[Long](numClusters)

  private def getBit(cluster: Long, i: Int): Boolean = ((cluster >> i) & 1L) == 1
  private def setBit(cluster: Long, i: Int, bit: Boolean): Long =
    if (bit) cluster | (1L << i)
    else cluster & ~(1L << i)

  override def member(x: Int): Boolean =
    if (x < 0) false
    else {
      val h = high(x)
      h < arr.length && getBit(arr(h), low(x))
    }

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

  override def insert(x: Int): Unit =
    if (x < 0) throwOutOfRange()
    else {
      val h = high(x)
      if (h < arr.length) arr(h) = setBit(arr(h), low(x), true)
      else throwOutOfRange()
    }

  override def delete(x: Int): Unit =
    if (x >= 0) {
      val h = high(x)
      if (h < arr.length) arr(h) = setBit(arr(h), low(x), false)
    }
}
