package emptyflash.coopdj

import org.scalatest.FlatSpec
import org.scalamock.scalatest.MockFactory

class TwitterSongSourceSpec extends FlatSpec with MockFactory {

  //  "An empty Set" should "have size 0" in {
  //    assert(Set.empty.size == 0)
  //  }
  "createSongSource" should "return an instance of StartedTwitterSongSource" in {
    TwitterSongSource.startSongSource("#test", tweetText => {
      assert(tweetText == "test tweet")
    })
  }

}
