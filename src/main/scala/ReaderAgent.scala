package example

import java.util.concurrent.ConcurrentLinkedQueue
import faunadb.FaunaClient

class ReadingAgent(val q: ConcurrentLinkedQueue[(Long, Long, Long, String, String)],
                   val data: List[String],
                   val client: FaunaClient) extends Thread {
  override def run(): Unit = {
    val toInsert = scala.util.Random.shuffle(data.toList).head
    var error: String = ""
    val start: Long = System.currentTimeMillis()
    try {
      Utils.readRecordFromCollection(
        client, toInsert
      )
    } catch {
      case e: Exception => error = e.toString()
    }

    var latency = System.currentTimeMillis() - start

    q.add(
      (Thread.currentThread().getId(), start, latency, "read", error)
    )
  }
}
