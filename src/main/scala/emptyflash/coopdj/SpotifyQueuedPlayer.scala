package emptyflash.coopdj

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

class SpotifyQueuedPlayer(token: String)(implicit context: Context) extends QueuedPlayer with PlayerNotificationCallback with ConnectionStateCallback {
  var spotifyPlayer: Player = null
  var spotifyService: SpotifyService = null
  var isPlaying: Boolean = false
  var hasSongsInQueue = false

  def initializePlayer(finishedCallback: QueuedPlayer => Unit) = {
    lazy val config = new Config(context, token, SpotifySettings.CLIENT_ID)
    config.useCache(false)
    spotifyPlayer = Spotify.getPlayer(config, this, new Player.InitializationObserver() {
      override def onInitialized(player: Player) {
        player.addConnectionStateCallback(SpotifyQueuedPlayer.this)
        player.addPlayerNotificationCallback(SpotifyQueuedPlayer.this)
        finishedCallback(SpotifyQueuedPlayer.this)
      }

      override def onError(thorwable: Throwable) {
      }
    })

    lazy val api = new SpotifyApi()
    api.setAccessToken(token)
    spotifyService = api.getService()
  }

  def searchForSongAndQueueFirst(song: String) {
    spotifyService.searchTracks(song, new SpotifyCallback[TracksPager] {
      override def success(tracksPage: TracksPager, response: Response) = {
        playOrQueueSong("spotify:track:" + tracksPage.tracks.items.get(0).id)
        toast(tracksPage.tracks.items.get(0).id)
      }
      override def failure(error: SpotifyError) {
      }
    })
  }

  def playOrQueueSong(songId: String) {
    if (!hasSongsInQueue) {
      spotifyPlayer.play(songId)
      hasSongsInQueue = true
    }
    else spotifyPlayer.queue(songId)
  }

  def addSongToQueue(song: String) {
    if (song contains "spotify:track:") playOrQueueSong(song) else searchForSongAndQueueFirst(song)
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
