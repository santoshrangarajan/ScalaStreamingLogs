package com.abstractlayers.server

import akka.actor.Actor
import akka.actor.IOManager
import akka.actor.IO
import akka.util.ByteString
import java.net.InetSocketAddress
import akka.actor.ActorSystem
import akka.actor.Props
import java.util.ArrayList
import akka.actor.IO.SocketHandle
import akka.actor.IO.ReadHandle
import java.io.File
import java.io.RandomAccessFile
import com.abstractlayers.server.StreamingLogs.LogMessages
import com.abstractlayers.server.StreamingLogs.StartServer

class TCPServer() extends Actor {

  val socketConnections = new ArrayList[ReadHandle]()
  

  def receive = {
    case IO.NewClient(server) => {
      server.accept()
    }

    case IO.Read(rHandle, bytes) => {
      val byteString = ByteString("HTTP/1.1 200 OK\r\n\r\nOK")
      rHandle.asSocket.write(byteString)
      socketConnections.add(rHandle)
      println("Added new client.....")
    }

    case StartServer(port: Int) => {
      IOManager(context.system).listen(new InetSocketAddress(port))
      println("Server started on port " + port)
    }

    case LogMessages(ls) => {

      for (i <- 0 until socketConnections.size()) {
        val handle = socketConnections.get(i)
        println("sending streams to client.....")
        for (line <- ls) {
          val byteString = ByteString(line)
          handle.asSocket.write(byteString)
        }
      }
    }

  }

}
