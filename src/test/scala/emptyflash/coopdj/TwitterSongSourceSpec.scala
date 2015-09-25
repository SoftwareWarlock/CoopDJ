package emptyflash.coopdj

import org.scalatest.FlatSpec
import org.scalamock.scalatest.MockFactory

class TwitterSongSourceSpec extends FlatSpec with MockFactory {

  //  "An empty Set" should "have size 0" in {
  //    assert(Set.empty.size == 0)
  //  }
  "onSongSourceUpdated" should "append the function parameter to it's callback list" in {
    lazy val songSource = new TwitterSongSource("#test")
    assert(songSource.callbackFunctions.length == 0)
    songSource.onSongSourceUpdated((message: String) => {})
    assert(songSource.callbackFunctions.length == 1)
  }

  "applyMessagesToCallbacks" should "call every function in the callback list" in {
    lazy val songSource = new TwitterSongSource("#test")
    var called = 0
    val callback = (message: String) => {
      called += 1
    }
    songSource.callbackFunctions = List(callback, callback)
    songSource.applyMessageToCallbacks(songSource.callbackFunctions, "test")
    assert(called == 2)
  }

  it should "call the functions in the call back with messages as a parameter" in {
    lazy val songSource = new TwitterSongSource("#test")
    var receivedMessage = ""
    val callback = (message: String) => {
      receivedMessage = message
    }
    songSource.callbackFunctions = List(callback)
    songSource.applyMessageToCallbacks(songSource.callbackFunctions, "test")
    assert(receivedMessage == "test")
  }

  "startSource" should "set the client to the newly created client" in {
    lazy val songSource = new TwitterSongSource("#test")
    songSource.startSource()
    assert(songSource.client != null)
    songSource.stopSource()
  }
}
