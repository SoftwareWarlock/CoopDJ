package emptyflash.coopdj

import collection.JavaConversions._
import collection.Seq

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.BasicClient
import com.twitter.hbc.httpclient.auth.Authentication
import com.twitter.hbc.httpclient.auth.OAuth1
import com.twitter.hbc.twitter4j.Twitter4jStatusClient
import com.twitter.hbc.twitter4j.handler.StatusStreamHandler
import com.twitter.hbc.twitter4j.message.DisconnectMessage

import com.twitter.hbc.twitter4j.message.StallWarningMessage
import twitter4j.StallWarning
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import twitter4j.StatusListener

import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

trait StartedSongSource
trait StoppedSongSource

trait SongSource {
  def startSongSource(hashtag: String, callback: String => Unit): StartedSongSource
  def stopSongSource(songSource: StartedSongSource): StoppedSongSource
}

case class StartedTwitterSongSource(hashtag: String, callback: String => Unit, client: BasicClient) extends StartedSongSource

case class StoppedTwitterSongSource(hashtag: String, callback: String => Unit, client: BasicClient) extends StoppedSongSource

object TwitterSongSource extends SongSource {
    def createStatusListener(callback: String => Unit): StatusListener = {
      new StatusStreamHandler {
        override def onStatus(status: Status) = {
          callback(status.getText())
        }

        override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) = {}
        override def onTrackLimitationNotice(limit: Int) = {}
        override def onScrubGeo(user: Long, upToStatus: Long) = {}
        override def onStallWarning(warning: StallWarning) = {}
        override def onException(e: Exception) = {}
        override def onDisconnectMessage(message: DisconnectMessage) = {}
        override def onStallWarningMessage(warning: StallWarningMessage) = {}
        override def onUnknownMessageType(s: String) = {}
      }
    }

    def buildClient(hashtag: String, callback: String => Unit): BasicClient = {
        val queue = new LinkedBlockingQueue[String](10000)
        val endpoint = new StatusesFilterEndpoint()
        endpoint.trackTerms(Seq(hashtag))

        val consumerKey = TwitterSettings.CONSUMER_KEY
        val consumerSecret = TwitterSettings.CONSUMER_SECRET
        val accessToken = TwitterSettings.ACCESS_TOKEN
        val accessTokenSecret = TwitterSettings.ACCESS_TOKEN_SECRET
        val auth = new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret)

        val client = new ClientBuilder()
          .hosts(Constants.STREAM_HOST)
          .endpoint(endpoint)
          .authentication(auth)
          .processor(new StringDelimitedProcessor(queue))
          .build()
        
        val listener = createStatusListener(callback)
        
        val numberOfThreads = 4
        val service = Executors.newFixedThreadPool(numberOfThreads)

        val t4jClient = new Twitter4jStatusClient(client, queue, Seq(listener), service)
        t4jClient.connect()
        (1 to numberOfThreads) foreach(index => t4jClient.process())

        client
    }

    def startSongSource(hashtag: String, callback: String => Unit): StartedSongSource = {
        val client = buildClient(hashtag, callback)
        StartedTwitterSongSource(hashtag, callback, client)
    }

    def stopSongSource(source: StartedSongSource): StoppedSongSource= {
        val StartedTwitterSongSource(hashtag, callback, client) = source
        client.stop()
        StoppedTwitterSongSource(hashtag, callback, client)
    }
}
