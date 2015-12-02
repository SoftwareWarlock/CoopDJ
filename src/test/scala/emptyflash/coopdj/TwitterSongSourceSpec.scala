package emptyflash.coopdj

import org.scalatest.FlatSpec
import org.scalamock.scalatest.MockFactory

class TwitterSongSourceSpec extends FlatSpec with MockFactory {
  "startSongSource" should "return an instance of StartedTwitterSongSource" in {
    val startedSongSource = TwitterSongSource.startSongSource("#test", tweetText => {})
    assert(startedSongSource.getClass() == classOf[StartedTwitterSongSource])
  }

  "stopSongSource" should "take an instance of StartedTwitterSongSource and return an instance of StoppedTwitterSongSource" in {
    val startedSongSource = TwitterSongSource.startSongSource("#test", tweetText => {})
    val stoppedSongSource = TwitterSongSource.stopSongSource(startedSongSource)
    assert(stoppedSongSource.getClass() == classOf[StoppedSongSource])
  }

}
