package cz.radekm.emde

object EmdeSetSuite extends PrioSetSuiteBase {
  override type T = EmdeSet
  override def create(): T = EmdeSet(UniverseSizeLog, UniverseSizeLog / 2, 6)
}
