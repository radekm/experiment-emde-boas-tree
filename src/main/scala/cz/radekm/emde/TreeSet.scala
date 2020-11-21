package cz.radekm.emde

import java.util

object TreeSet {
  def apply(universeSizeLog: Int) = new TreeSet(universeSizeLog)
}

class TreeSet(override final val universeSizeLog: Int) extends PrioSet {
  override final def clusterSizeLog = universeSizeLog
  override final def numClustersLog = 0

  private val treeSet = new util.TreeSet[Int]()

  override def member(x: Int): Boolean = treeSet.contains(x)

  override def successor(x: Int): Option[Int] = Option(treeSet.higher(x))

  override def insert(x: Int): Unit =
    if (x < 0 || x >= universeSize) throwOutOfRange()
    else treeSet.add(x)

  override def delete(x: Int): Unit = treeSet.remove(x)
}
