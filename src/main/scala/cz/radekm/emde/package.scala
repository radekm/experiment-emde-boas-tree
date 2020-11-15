package cz.radekm

package object emde {
  val UniverseSizeLog = 24
  val UniverseSize = 1 << UniverseSizeLog

  def throwOutOfRange(): Nothing = sys.error("Out of range")
}
