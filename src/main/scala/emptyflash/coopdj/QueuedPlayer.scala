package emptyflash.coopdj

import android.content.Context

trait QueuedPlayer {
  def initializePlayer(finishedCallback: QueuedPlayer => Unit)
  def play()
  def pause()
  def togglePlaying()
  def addSongToQueue(song: String)
  def nextSong()
  def previousSong()
  def clearQueue()
  def destroyPlayer()
}
