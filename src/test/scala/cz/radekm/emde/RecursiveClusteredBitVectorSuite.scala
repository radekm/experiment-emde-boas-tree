package cz.radekm.emde

object RecursiveClusteredBitVectorSuite extends PrioSetSuiteBase {
  override type T = RecursiveClusteredBitVector
  override def create(): T = RecursiveClusteredBitVector(UniverseSizeLog, UniverseSizeLog / 2)
}
