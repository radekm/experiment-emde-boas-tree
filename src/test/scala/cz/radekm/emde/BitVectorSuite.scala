package cz.radekm.emde

object BitVectorSuite extends PrioSetSuiteBase {
  override type T = BitVector
  override def create(): T = BitVector()
}
