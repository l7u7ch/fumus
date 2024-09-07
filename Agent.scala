import upickle.default.{ReadWriter, read, write}
import sttp.client4.quick.*

case class Agent(private val service: String) {

  private var accessJwt: String = ""
  private var handle: String = ""
  private var did: String = ""

  // https://docs.bsky.app/docs/api/com-atproto-server-create-session
  def createSession(identifier: String, password: String): String = {

    case class Payload(identifier: String, password: String) derives ReadWriter

    val payload: Payload = Payload(identifier = identifier, password = password)

    val response: String = quickRequest
      .post(uri"${service}/xrpc/com.atproto.server.createSession")
      .header("Content-Type", "application/json")
      .header("Accept", "application/json")
      .body(write(payload))
      .send()
      .body

    case class CreateSession(accessJwt: String, handle: String, did: String) derives ReadWriter

    val createSession: CreateSession = read[CreateSession](response)

    accessJwt = createSession.accessJwt
    handle = createSession.handle
    did = createSession.did

    response
  }

  // https://docs.bsky.app/docs/api/com-atproto-repo-create-record
  def createRecord(msg: String): String = {

    case class Payload(repo: String, collection: String, record: Map[String, String]) derives ReadWriter

    val payload: Payload = Payload(
      repo = handle,
      collection = "app.bsky.feed.post",
      record = Map("text" -> msg, "createdAt" -> java.time.Instant.now().toString)
    )

    val response: String = quickRequest
      .post(uri"${service}/xrpc/com.atproto.repo.createRecord")
      .header("Content-Type", "application/json")
      .header("Accept", "application/json")
      .header("Authorization", "Bearer " + accessJwt)
      .body(write(payload))
      .send()
      .body

    response
  }

  // https://docs.bsky.app/docs/api/com-atproto-repo-delete-record
  def deleteRecord(rkey: String): String = {

    case class Payload(repo: String, collection: String, rkey: String) derives ReadWriter

    val payload: Payload = Payload(repo = handle, collection = "app.bsky.feed.post", rkey = rkey)

    val response: String = quickRequest
      .post(uri"${service}/xrpc/com.atproto.repo.deleteRecord")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + accessJwt)
      .body(write(payload))
      .send()
      .body

    response
  }

  // https://docs.bsky.app/docs/api/com-atproto-repo-delete-record
  def deleteRepost(rkey: String): String = {

    case class Payload(repo: String, collection: String, rkey: String) derives ReadWriter

    val payload: Payload = Payload(
      repo = handle,
      collection = "app.bsky.feed.repost",
      rkey = rkey
    )

    val response: String = quickRequest
      .post(uri"${service}/xrpc/com.atproto.repo.deleteRecord")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + accessJwt)
      .body(write(payload))
      .send()
      .body

    response
  }

  // https://docs.bsky.app/docs/api/app-bsky-feed-get-author-feed
  def getAuthorFeed(limit: Int = 50, cursor: String = ""): String = {

    val response: String = quickRequest
      .get(uri"${service}/xrpc/app.bsky.feed.getAuthorFeed?actor=${handle}&limit=${limit}&cursor=${cursor}")
      .header("Accept", "application/json")
      .header("Authorization", "Bearer " + accessJwt)
      .send()
      .body

    response
  }
}
