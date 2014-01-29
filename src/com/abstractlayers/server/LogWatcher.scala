package com.abstractlayers.server

import akka.actor.Actor
import java.io.File
import java.io.RandomAccessFile
import com.abstractlayers.server.StreamingLogs.Watch
import com.abstractlayers.server.StreamingLogs.LogMessages

class LogWatcher(logFile: String) extends Actor {

  println("Initializing log watcher with :" + logFile)
  val fileName = new File(logFile)
  var keepRunning = true
  var originalLength: Long = 0

  def getNewLinesFromFile(bytesToSkip: Int): List[String] = {
    val raf = new RandomAccessFile(fileName, "r");
    raf.skipBytes(bytesToSkip)
    val lines = Iterator.continually(raf.readLine()).takeWhile(_ != null).filter(s => (s.startsWith("line:"))).toList
    lines
  }

  
  def receive = {

    case Watch(msg) => {
      println("Msg recieved to watch.....")
      var bytesToSkip = 0
      while (keepRunning) {
        //println("original length=" + originalLength + "current length" + fileName.length())
        if (fileName.length() > originalLength) {
          val lines = getNewLinesFromFile(bytesToSkip)
          sender ! new LogMessages(lines)
          bytesToSkip = bytesToSkip +  lines.foldLeft(0) {(total,str)=> total + str.getBytes().length}
          originalLength = fileName.length()
        }
        Thread.sleep(5000)
      }
    }

    /*  case StopWatching => {
         keepRunning = false
    }*/
  }
}