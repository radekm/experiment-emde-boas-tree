package cz.radekm

package object emde {
  val UniverseSize = 1 << 24

  def throwOutOfRange(): Nothing = sys.error("Out of range")
}
