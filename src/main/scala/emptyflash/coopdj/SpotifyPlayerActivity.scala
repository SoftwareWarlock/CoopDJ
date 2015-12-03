package emptyflash.coopdj

import scala.concurrent.{ Future, Promise }
import scala.concurrent.ExecutionContext.Implicits.global

import org.scaloid.common._

import android.util.Log
import android.content.Intent

class SpotifyPlayerActivity extends SActivity {
  def getMessageWithoutHashtag(message: String, hashtag: String): String = {
    val hashtagWithSpace = hashtag + " "
    message.replace(hashtagWithSpace, "")
  }

  def setupMediaControlUI(player: QueuedPlayer) {
    setContentView(new SVerticalLayout {
      SButton("Back", player.previousSong())
      SButton("Play", player.play())
      SButton("Pause", player.pause())
      SButton("Next", player.nextSong())
    })
  }

  def beginPlayingTweetedSongs(hashtag: String, player: QueuedPlayer) = {
    val songSource = TwitterSongSource.startSongSource(hashtag, message => {
      val tweetedSong = getMessageWithoutHashtag(message, hashtag)
      player.addSongToQueue(tweetedSong)
    })
    onDestroy(TwitterSongSource.stopSongSource(songSource))
    setupMediaControlUI(player)
  }

  onCreate {
    val intent = getIntent()
    val token = intent.getExtras().getString("token")
    val hashtag = intent.getExtras().getString("hashtag")
    val queuedPlayerFuture: Future[QueuedPlayer] = SpotifyQueuedPlayer.initializeQueuedPlayer(token)
    queuedPlayerFuture.map(queuedPlayer => {
      beginPlayingTweetedSongs(hashtag, queuedPlayer)
      onDestroy(queuedPlayer.destroyPlayer())
    })
  }

}

