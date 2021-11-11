package example

import java.util.concurrent.ConcurrentLinkedQueue
import faunadb.FaunaClient

class WriterAgent(val q: ConcurrentLinkedQueue[(Long, Long, Long, String, String)],
                  val client: FaunaClient) extends Thread {
  override def run(): Unit = {
    var error: String = ""
    val start: Long = System.currentTimeMillis()
    try {
      Utils.writeRecordToCollection(client)
    } catch {
      case e: Exception => error = e.toString()
    }

    var latency = System.currentTimeMillis() - start

    q.add(
      (Thread.currentThread().getId(), start, latency, "write", error)
    )
  }
}
