package cz.radekm.emde

object BitVector {
  def apply(universeSizeLog: Int) = new BitVector(universeSizeLog)
}

class BitVector(override final val universeSizeLog: Int) extends PrioSet {
  override final def clusterSizeLog = 3
  override final def numClustersLog = universeSizeLog - clusterSizeLog

  private val arr = new Array[Byte](numClusters)

  private def getBit(byte: Byte, i: Int): Boolean = ((byte >> i) & 1) == 1
  private def setBit(byte: Byte, i: Int, bit: Boolean): Byte = {
    if (bit) byte | (1 << i)
    else byte & ~(1 << i)
  }.toByte

  override def member(x: Int): Boolean =
    if (x < 0) false
    else {
      val h = high(x)
      h < arr.length && getBit(arr(h), low(x))
    }

  override def successor(x: Int): Option[Int] = {
    var h = 0
    var l = 0

    if (x >= 0) {
      h = high(x)
      l = low(x) + 1
    }

    while (h < arr.length) {
      val byte = arr(h)
      // `byte != 0` is an optimization.
      while (byte != 0 && l < 8) {
        if (getBit(byte, l))
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
      if (h < arr.length) arr(h) = setBit(arr(h), low(x), true)
      else throwOutOfRange()
    }

  override def delete(x: Int): Unit =
    if (x >= 0) {
      val h = high(x)
      if (h < arr.length) arr(h) = setBit(arr(h), low(x), false)
    }
}
