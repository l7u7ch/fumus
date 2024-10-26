//> using dep "com.lihaoyi::upickle:4.0.2"
//> using dep "io.github.cdimascio:dotenv-java:3.0.2"
//> using dep "it.sauronsoftware.cron4j:cron4j:2.2.5"
//> using dep com.softwaremill.sttp.client4::core:4.0.0-M6

import io.github.cdimascio.dotenv.Dotenv
import it.sauronsoftware.cron4j.Scheduler
import java.time.format.DateTimeFormatter.ISO_INSTANT
import java.time.ZonedDateTime
import upickle.default.{ReadWriter, read}

@main def Main() = {
  val scheduler = new Scheduler()

  scheduler.schedule(
    "0 4 * * *",
    () => {
      val dotenv = Dotenv.load()

      val agent: Agent = Agent(service = dotenv.get("SERVICE"))
      agent.createSession(
        identifier = dotenv.get("IDENTIFIER"),
        password = dotenv.get("PASSWORD")
      )

      val authorFeed: String = agent.getAuthorFeed(
        limit = 100,
        cursor = ZonedDateTime.now().minusMonths(1).format(ISO_INSTANT).toString()
      )

      case class Viewer(repost: Option[String] = None) derives ReadWriter
      case class Post(uri: String, viewer: Viewer) derives ReadWriter
      case class Posts(post: Post) derives ReadWriter
      case class Feed(feed: List[Posts]) derives ReadWriter

      val rkeysPost: List[String] = read[Feed](authorFeed).feed.flatMap(_.post.uri.split("/").lastOption)
      val rkeysRepost: List[String] = read[Feed](authorFeed).feed.flatMap(_.post.viewer.repost).map(_.split("/").last)
      val rkeys: List[String] = rkeysPost ++ rkeysRepost

      if (0 < rkeys.length) {
        rkeysPost.foreach(agent.deleteRecord)
        rkeysRepost.foreach(agent.deleteRepost)
      }
    }
  )

  scheduler.start()

}
