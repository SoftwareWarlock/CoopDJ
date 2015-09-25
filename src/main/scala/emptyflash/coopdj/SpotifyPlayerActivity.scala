package emptyflash.coopdj

import org.scaloid.common._

import android.util.Log
import android.content.Intent

class SpotifyPlayerActivity extends SActivity {
  def getMessageWithoutHashtag(message: String, hashtag: String) = {
    message.replace(hashtag + " ", "")
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
    val songSource: SongSource = new TwitterSongSource(hashtag)
    songSource.onSongSourceUpdated((message: String) => {
      val tweetedSong = getMessageWithoutHashtag(message, hashtag)
      player.addSongToQueue(tweetedSong)
    })
    songSource.startSource()
    onDestroy(songSource.stopSource())
    setupMediaControlUI(player)
  }

  def initializeQueuedPlayer(token: String, finishedCallback: QueuedPlayer => Unit) = {
    val queuedPlayer: QueuedPlayer = new SpotifyQueuedPlayer(token)
    queuedPlayer.initializePlayer(finishedCallback)
    onDestroy(queuedPlayer)
  }

  onCreate {
    lazy val intent = getIntent()
    lazy val token = intent.getExtras().getString("token")
    lazy val hashtag = intent.getExtras().getString("hashtag")
    initializeQueuedPlayer(token, beginPlayingTweetedSongs(hashtag, _))
  }

}

