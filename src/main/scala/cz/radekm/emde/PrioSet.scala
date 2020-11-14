package cz.radekm.emde

abstract class PrioSet {
  /**
   * Returns whether `i` is in the set.
   */
  def member(i: Int): Boolean
  /**
   * Returns smallest `j: Int` such that `j > i` and `member(j)` returns `true`.
   */
  def successor(i: Int): Option[Int]
  /**
   * Following must hold: `0 <= i < UniverseSize` otherwise exception will be thrown.
   *
   * Updates set so that `member(i)` returns `true`.
   */
  def insert(i: Int): Unit
  /**
   * Updates set so that `member(i)` returns `false`.
   */
  def delete(i: Int): Unit
}
