package cz.radekm.emde

object ClusteredBitVectorSuite extends PrioSetSuiteBase {
  override type T = ClusteredBitVector
  override def create(): T = ClusteredBitVector()
}
