package com.abstractlayers.server

import java.io.File
import java.io.PrintWriter
import scalax.logging.Logging
import akka.actor.Actor
import com.abstractlayers.server.StreamingLogs.StartWriting

class LogWriter(logFileName:String) extends Actor {

  val writer = new PrintWriter(new File(logFileName))

  def receive = {
    case StartWriting() => {
      println("Msg received. Starting to write.....")
      var count = 0;
      while (true) {
        count += 1
        writer.write("line:" + count + ",some Text to log\n")
        writer.flush()
        Thread.sleep(500)
      }
      writer.close()
    }
  }

}