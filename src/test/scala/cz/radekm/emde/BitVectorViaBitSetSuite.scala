package cz.radekm.emde

object BitVectorViaBitSetSuite extends PrioSetSuiteBase {
  override type T = BitVectorViaBitSet
  override def create(): T = BitVectorViaBitSet(UniverseSizeLog)
}
