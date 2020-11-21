package cz.radekm.emde

object TreeSetSuite extends PrioSetSuiteBase {
  override type T = TreeSet
  override def create(): T = TreeSet(UniverseSizeLog)
}
