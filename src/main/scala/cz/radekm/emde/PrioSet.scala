package cz.radekm.emde

/**
 * Represents subset of `0 until universeSize`.
 */
abstract class PrioSet {
  def universeSize: Int
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
