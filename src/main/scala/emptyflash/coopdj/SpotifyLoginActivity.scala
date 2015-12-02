package emptyflash.coopdj

import org.scaloid.common._

import android.content.Intent
import android.util.Log

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


class SpotifyLoginActivity extends SActivity {
  lazy val hashtagEditText = new SEditText() hint "#MyAwesomeParty" selectAllOnFocus true

  def openSpotifyAuthenticationLoginActivity() = {
    val builder = new AuthenticationRequest.Builder(SpotifySettings.CLIENT_ID, AuthenticationResponse.Type.TOKEN, SpotifySettings.REDIRECT_URL)
    builder setScopes Array[String]("streaming")
    val request = builder.build()
    AuthenticationClient.openLoginActivity(this, SpotifySettings.REQUEST_CODE, request)
  }

  onCreate {
    openSpotifyAuthenticationLoginActivity
    setContentView(new SVerticalLayout {
      SButton("Login in with Spotify", openSpotifyAuthenticationLoginActivity)
    })
  }

  def setupPostLoginUI(token: String) = {
      setContentView(new SVerticalLayout {
        STextView("Enter the hashtag for your DJ session")
        hashtagEditText
          .here
        SButton("Start playing music!", {
          lazy val hashtag = hashtagEditText.text.toString
          new Intent().put(token, hashtag).start[SpotifyPlayerActivity]
        })
      })
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
    lazy val response = AuthenticationClient.getResponse(resultCode, intent)
    response.getType() match {
      case AuthenticationResponse.Type.TOKEN => {
        lazy val token = response.getAccessToken()
        setupPostLoginUI(token)
      }
      case _ => Log.d("SpotifyLoginActivity", "Received bad authentication response")
    }
  }
}
