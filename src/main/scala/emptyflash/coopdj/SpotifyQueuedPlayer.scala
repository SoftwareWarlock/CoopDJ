package emptyflash.coopdj

import scala.concurrent.{ Future, Promise }
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global

import android.content.Context
import org.scaloid.common._

import com.spotify.sdk.android.player.Spotify
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.ConnectionStateCallback
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerNotificationCallback
import com.spotify.sdk.android.player.PlayerState

import kaaes.spotify.webapi.android.models.TracksPager
import kaaes.spotify.webapi.android.SpotifyCallback
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyError

import retrofit.RetrofitError
import retrofit.client.Response

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

object SpotifyQueuedPlayer { 
  def getSpotifyPlayer(token: String)(implicit context: Context): Future[Player] = {
    val config = new Config(context, token, SpotifySettings.CLIENT_ID)
    config.useCache(false)
    val playerPromise = Promise[Player]()
    val self = this
    Spotify.getPlayer(config, this, new Player.InitializationObserver() {
      override def onInitialized(player: Player) = {
        player.addConnectionStateCallback(self)
        player.addPlayerNotificationCallback(self)
        playerPromise.success(player)
      }

      override def onError(thorwable: Throwable) = {
      }
    })
    playerPromise.future
  }

  def initializeQueuedPlayer(token: String)(implicit context: Context): Future[QueuedPlayer]  = {
    val spotifyPlayerFuture = getSpotifyPlayer(token)
    val queuedPlayerPromise = Promise[QueuedPlayer]()
    spotifyPlayerFuture.onComplete {
      case Success(spotifyPlayer) => {
        val api = new SpotifyApi()
        api.setAccessToken(token)
        val spotifyService = api.getService()
        queuedPlayerPromise.success(new SpotifyQueuedPlayer(spotifyPlayer, spotifyService))
      }
      case Failure(exception) => queuedPlayerPromise.failure(exception)
    }

    queuedPlayerPromise.future
  }
}

class SpotifyQueuedPlayer(spotifyPlayer: Player, spotifyService: SpotifyService)(implicit context: Context) extends QueuedPlayer with PlayerNotificationCallback with ConnectionStateCallback {
  var isPlaying = false
  var hasSongsInQueue = false

  def searchForSong(song: String): Future[String] = {
    val songPromise = Promise[String]()
    spotifyService.searchTracks(song, new SpotifyCallback[TracksPager] {
      override def success(tracksPage: TracksPager, response: Response) = {
        val songId = "spotify:track:" + tracksPage.tracks.items.get(0).id
        songPromise.success(songId)
        toast(songId)
      }
      override def failure(error: SpotifyError) {
        songPromise.failure(error)
      }
    })
    songPromise.future
  }

  def playOrQueueSong(songId: String): Future[String] = {
    if (!hasSongsInQueue) {
      spotifyPlayer.play(songId)
      hasSongsInQueue = true
    }
    else spotifyPlayer.queue(songId)
    Future[String](songId)
  }

  def addSongToQueue(song: String) {
    if (song contains "spotify:track:") playOrQueueSong(song) else searchForSong(song).flatMap(songId => playOrQueueSong(songId))
  }

  def nextSong = {
    spotifyPlayer.skipToNext()
  }

  def previousSong = {
    spotifyPlayer.skipToPrevious()
  }

  def clearQueue = {
    spotifyPlayer.clearQueue()
  }

  def play = {
    spotifyPlayer.resume()
  }

  def pause = {
    spotifyPlayer.pause()
  }

  def togglePlaying() {
    if (isPlaying) pause else play
  }

  def destroyPlayer() {
    Spotify.destroyPlayer(this)
  }

  override def onLoggedIn() {
  }

  override def onLoggedOut() {
  }

  override def onLoginFailed(error: Throwable) {
  }

  override def onTemporaryError() {
  }

  override def onConnectionMessage(message: String) {
  }
 
  override def onPlaybackEvent(eventType: PlayerNotificationCallback.EventType, playerState: PlayerState) {
    eventType match {
      case PlayerNotificationCallback.EventType.PLAY => isPlaying = playerState.playing 
      case PlayerNotificationCallback.EventType.PAUSE=> isPlaying = playerState.playing 
      case _ => {}
    }
  }

  override def onPlaybackError(errorType: PlayerNotificationCallback.ErrorType, errorDetails: String) {
  }
}
