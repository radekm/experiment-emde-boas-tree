package cz.radekm.emde

object RecursiveClusteredBitVector {
  def apply(universeSizeLog: Int,  clusterSizeLog: Int) = new RecursiveClusteredBitVector(universeSizeLog, clusterSizeLog)
}

class RecursiveClusteredBitVector(
  universeSizeLog: Int,
  clusterSizeLog: Int,
) extends ClusteredBitVector(
  universeSizeLog,
  clusterSizeLog,
  createPrioSet = universeSizeLog =>
    if (universeSizeLog >= 8) RecursiveClusteredBitVector(universeSizeLog, universeSizeLog / 2)
    else BitVector(universeSizeLog)
)
