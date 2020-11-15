package cz.radekm.emde

/**
 * Represents subset of `0 until universeSize`.
 */
abstract class PrioSet {
  def universeSizeLog: Int
  def numClustersLog: Int
  def clusterSizeLog: Int

  require(0 <= universeSizeLog && universeSizeLog <= 32)
  require(0 <= numClustersLog)
  require(0 <= clusterSizeLog)
  require(numClustersLog + clusterSizeLog == universeSizeLog)

  final val universeSize: Int = 1 << universeSizeLog
  final val numClusters: Int = 1 << numClustersLog
  final val clusterSize: Int = 1 << clusterSizeLog

  protected final def high(x: Int) = x >> clusterSizeLog
  protected final def low(x: Int) = x & (clusterSize - 1)
  protected final def index(h: Int, l: Int) = (h << clusterSizeLog) | l

  /**
   * Returns whether `i` is in the set.
   */
  def member(x: Int): Boolean
  /**
   * Returns smallest `j: Int` such that `j > i` and `member(j)` returns `true`.
   */
  def successor(x: Int): Option[Int]
  /**
   * Following must hold: `0 <= i < universeSize` otherwise exception will be thrown.
   *
   * Updates set so that `member(i)` returns `true`.
   */
  def insert(x: Int): Unit
  /**
   * Updates set so that `member(i)` returns `false`.
   */
  def delete(x: Int): Unit
}
