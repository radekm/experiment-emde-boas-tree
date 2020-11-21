package cz.radekm.emde

object ScalaTreeSetSuite extends PrioSetSuiteBase {
  override type T = ScalaTreeSet
  override def create(): T = ScalaTreeSet(UniverseSizeLog)
}
