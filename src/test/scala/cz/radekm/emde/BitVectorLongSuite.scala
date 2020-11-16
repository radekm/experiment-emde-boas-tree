package cz.radekm.emde

object BitVectorLongSuite extends PrioSetSuiteBase {
  override type T = BitVectorLong
  override def create(): T = BitVectorLong(UniverseSizeLog)
}
