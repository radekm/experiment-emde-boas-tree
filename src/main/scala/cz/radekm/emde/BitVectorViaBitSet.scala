package cz.radekm.emde

import java.util.BitSet

object BitVectorViaBitSet {
  def apply(universeSizeLog: Int) = new BitVectorViaBitSet(universeSizeLog)
}

class BitVectorViaBitSet(override final val universeSizeLog: Int) extends PrioSet {
  override final def clusterSizeLog = 3
  override final def numClustersLog = universeSizeLog - clusterSizeLog

  private val bitSet = new BitSet(universeSize)

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
