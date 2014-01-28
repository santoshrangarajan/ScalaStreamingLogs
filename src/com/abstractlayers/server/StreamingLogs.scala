package com.abstractlayers.server

import akka.actor.ActorSystem
import akka.actor.Props

import akka.actor.Actor

object StreamingLogs extends App {
  val system = ActorSystem("StreamingLogs")
  val logFileName = "/Users/admin/development/0117/ScalaStreamingLogs/src/com/abstractlayers/server/test.txt"
  val coordinator = system.actorOf(Props(new Coordinator(logFileName)), name = "co-ordinator")
  val logWriter = system.actorOf(Props(new LogWriter(logFileName)), name = "log-writer")

  logWriter ! StartWriting()
  coordinator ! StartCoordinator()

  case class StartWriting()
  case class StartCoordinator()
  case class StopCoordinator()
  case class LogMessages(ls: List[String])
  case class Watch(msg: String)
  case class StartServer(port: Int)
  case class StopServer()

  class Coordinator(logFileName: String) extends Actor {
    val tcpServer = system.actorOf(Props(new TCPServer()), name = "tcpServer")
    val logWatcher = system.actorOf(Props(new LogWatcher(logFileName)), name = "logWatcher")

    def receive = {

      case StartCoordinator() => {
        println("Starting the coordinator.....")
        tcpServer ! StartServer(8080)
        logWatcher ! Watch("start watching")
      }

      case StopCoordinator() => {
        println("Stopping the coordinator...")
      }

      case LogMessages(ls: List[String]) => {
        tcpServer ! new LogMessages(ls)
      }

    }
  }

}