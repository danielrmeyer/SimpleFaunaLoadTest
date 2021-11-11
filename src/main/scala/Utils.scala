package example

import faunadb._
import faunadb.query._
import java.util.UUID.randomUUID
import scala.io.Source
import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.concurrent.duration._

object Utils {

  import ExecutionContext.Implicits._

  def percentile(p: Int, seq: ListBuffer[Long]) = { //TODO Find library that does this
    val sorted = seq.sorted
    val k = math.ceil(
      (seq.length - 1) * (p / 100.0)
    ).toInt
    sorted(k)
  }

  def processResults(num_agents: Long) = {
    val results = Source.fromFile("results.csv")
    var latency = ListBuffer[Long]()

    for (line <- results.getLines()) {
      val cols = line.split(",").map(_.trim)
      latency += cols(2).toLong
    }

    val p99 = percentile(99, latency)
    val p95 = percentile(95, latency)
    val maximum = latency.max
    val average = latency.sum / latency.length //TODO find library routine
    val throughput = num_agents / (average / 1000.0) //Invoking Little's Law
    println(f"p99 latency (ms): $p99")
    println(f"p95 latency (ms): $p95")
    println(f"max latency (ms): $maximum")
    println(f"avg latency (ms): $average")
    println(f"throughput (req / sec): $throughput")
    results.close()
  }

  def populateCollection(client: FaunaClient, records: ListBuffer[String]): Unit = {
    println(
      Await.result(
        client.query(
          Map(
            records,
            Lambda { post_title =>
              Create(
                Collection("posts"),
                Obj("data" -> Obj("title" -> post_title)))
            }
          )), 10.seconds)
    )
  }

  def truncateCollection(client: FaunaClient) = {
    println(
      Await.result(
        client.query(
          Map(
            Paginate(
              Match(Index("posts_by_title"))
            ),
            Lambda("X", Delete(Var("X")))
          )
        ), 5.seconds
      )
    )
  }

  def writeRecordToCollection(client: FaunaClient) = {
    val toInsert = randomUUID().toString()
    val res = Await.result(
      client.query(
        Create(
          Collection("posts"),
          Obj("data" -> Obj("title" -> toInsert))
        )
      ), 5.seconds
    )
    val inserted: String = res("data")("title").get.toString()

    assert(inserted.stripPrefix('"'.toString()).stripSuffix('"'.toString()) == toInsert) //TODO clean this up
  }

  def readRecordFromCollection(client: FaunaClient, data: String) = {
    val res = Await.result(
      client.query(
        Get(
          Match(Index("posts_by_title"), data)
        )
      ), 5.seconds
    )
    val inserted: String = res("data")("title").get.toString()
    assert(inserted.stripPrefix('"'.toString()).stripSuffix('"'.toString()) == data)
  }

}
