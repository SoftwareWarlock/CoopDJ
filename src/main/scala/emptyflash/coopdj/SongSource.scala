package emptyflash.coopdj

trait SongSource {
  def onSongSourceUpdated(callback: String => Unit): Unit
  def startSource()
  def stopSource()
}
