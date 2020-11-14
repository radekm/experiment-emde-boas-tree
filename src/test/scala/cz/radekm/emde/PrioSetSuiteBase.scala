package cz.radekm.emde

import minitest._

trait PrioSetSuiteBase extends SimpleTestSuite {
  type T <: PrioSet
  def create(): T

  val insideUniverse = Vector.range(0, 100) ++ Vector.range(UniverseSize - 10, UniverseSize)
  val outsideUniverse =
    Vector.range(Int.MinValue, Int.MinValue + 10) ++
      Vector.range(-100, 0) ++
      Vector.range(UniverseSize, UniverseSize + 10) ++
      Vector.range(Int.MaxValue - 10, Int.MaxValue) ++
      Vector(Int.MaxValue)

  test("empty vector has no members") {
    val v = create()
    val xs = insideUniverse ++ outsideUniverse
    xs.foreach { x => assert(!v.member(x)) }
  }

  test("inserted value is member") {
    val v = create()
    val xs = insideUniverse
    xs.foreach { x => v.insert(x) }
    xs.foreach { x => assert(v.member(x)) }
  }

  test("inserted value is member") {
    val v = create()
    val xs = insideUniverse
    xs.foreach { x => v.insert(x) }
    xs.foreach { x => assert(v.member(x)) }
  }

  test("values outside of universe cannot be inserted") {
    val v = create()
    val xs = outsideUniverse
    xs.foreach { x =>
      intercept[Exception] { v.insert(x) }
    }
  }

  test("empty vector contains no successors") {
    val v = create()
    val xs = insideUniverse ++ outsideUniverse
    xs.foreach { x => assert(v.successor(x).isEmpty) }
  }

  test("value is not member after delete") {
    val v = BitVector()
    val xs = insideUniverse.distinct
    xs.foreach { x => v.insert(x) }
    xs.indices.foreach { i =>
      v.delete(xs(i))
      assert(!v.member(xs(i)))
      // Remaining values are still members.
      (i + 1 until xs.size).foreach { j => assert(v.member(xs(j))) }
    }
  }

  test("in vector with two values higher is successor of lower and higher has no successor") {
    val v = create()
    val xs = insideUniverse.distinct.sorted
    xs.indices.foreach { i =>
      (i + 1 until xs.size).foreach { j =>
        val x = xs(i)
        val y = xs(j)

        v.insert(x)
        v.insert(y)
        assert(v.member(x))
        assert(v.member(y))

        assertEquals(v.successor(x), Some(y))
        assert(v.successor(y).isEmpty)

        v.delete(x)
        v.delete(y)
        assert(!v.member(x))
        assert(!v.member(y))
      }
    }
  }
}
