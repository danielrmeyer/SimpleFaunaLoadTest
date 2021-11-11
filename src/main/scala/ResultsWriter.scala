package example

import java.util.concurrent.ConcurrentLinkedQueue
import java.io.PrintWriter
import java.io.File
import java.util.NoSuchElementException

class ResultsWriter(val q: ConcurrentLinkedQueue[(Long, Long, Long, String, String)])
  extends Thread {

  override def run(): Unit = {
    println("starting the results writer Thread")

    val writer = new PrintWriter(new File("results.csv"))
    while (true) {
      try {
        //TODO find right way to unpack tuples
        var temp = q.remove()
        var agentid = temp._1
        var start = temp._2
        var latency = temp._3
        var t = temp._4
        var e = temp._5
        writer.write(
          f"$agentid,$start,$latency,$t,$e\n" //TODO find library to handle csv
        )
        writer.flush()
      }
      catch {
        case ex: NoSuchElementException => Thread.sleep(100) //According to docs this exception gets thrown when queue is empty.
      }
    }
  }
}
