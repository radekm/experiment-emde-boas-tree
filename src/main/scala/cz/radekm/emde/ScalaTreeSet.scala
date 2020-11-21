package cz.radekm.emde

object ScalaTreeSet {
  def apply(universeSizeLog: Int) = new ScalaTreeSet(universeSizeLog)
}

class ScalaTreeSet(override final val universeSizeLog: Int) extends PrioSet {
  override final def clusterSizeLog = universeSizeLog
  override final def numClustersLog = 0

  private val treeSet = new scala.collection.mutable.TreeSet[Int]()

  override def member(x: Int): Boolean = treeSet.contains(x)

  override def successor(x: Int): Option[Int] = treeSet.minAfter(x + 1)

  override def insert(x: Int): Unit =
    if (x < 0 || x >= universeSize) throwOutOfRange()
    else treeSet.add(x)

  override def delete(x: Int): Unit = treeSet.remove(x)
}
