/*
 * Copyright (c) 2018-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.adapter

import java.util.concurrent.Executors

import scala.collection.immutable
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContextExecutorService
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.duration._
import java.util.concurrent.TimeoutException

import scala.util.Failure
import scala.util.Success

import com.github.emd.servicebuslib.servicebus.helper.ServiceBusConstants
import com.microsoft.azure.servicebus.IMessage
import com.microsoft.azure.servicebus.primitives.ServiceBusException
import enumeratum.Enum
import enumeratum.EnumEntry

import com.github.emd.servicebuslib.helper.FileLogger.fileLogger
import com.github.emd.servicebuslib.helper.Retry

/**
 * Service Bus entity adapter.
 * <p>
 * Uses the adapter pattern to abstract, simplify and unify functionality of
 * different Service Bus clients.
 *
 */
object EntityAdapter {

  /**
   * Types of Service Bus entities
   */
  sealed trait Type extends EnumEntry
  object Type extends Enum[Type] {

    val values: immutable.IndexedSeq[Type] = findValues

    case object Queue extends Type
    case object Topic extends Type
    case object Subscription extends Type
  }
}

trait EntityAdapter {
  /**
   * Sends message(s) to the Service Bus entity.
   *
   * @param messages      the collection of message(s) to send; if there's only
   *                      one message, then that message will be used to populate
   *                      any other parameters
   * @param sessionId     the session ID of the messages
   * @param correlationId the correlation ID of the messages
   */
  @throws(classOf[InterruptedException])
  @throws(classOf[ServiceBusException])
  @throws[TimeoutException]("if the sending operation exceeds [[com.github.emd.servicebuslib.servicebus.helper.ServiceBusConstants.MaxTransientExceptionAttempts]] seconds")
  def send(messages: scala.collection.immutable.List[IMessage],
           sessionId: String,
           correlationId: String): Unit = {

    val properties = new scala.collection.mutable.HashMap[String, String]

    messages match {
      case Nil =>
      case msg :: Nil =>
        fileLogger.info("EntityAdapter sending one message")
        MessageAdapter.addToProperties(msg,
                                       properties,
                                       scala.collection.mutable.Set(MessageAdapter.InfoType.All))
        doSend(msg :: Nil)
        fileLogger.info("EntityAdapter sent one message")

      case _ :: _ =>
        fileLogger.info("EntityAdapter sending more than one message")

        val msgIds = new scala.collection.mutable.ListBuffer[String]()

        messages.foreach { m =>
          if (m.getMessageId == null) msgIds += m.getMessageId
          else msgIds += "[NULL]" }

        properties.put("Message IDs", msgIds.mkString(","))
        properties.put("Session ID", Option(sessionId).getOrElse("[NULL]"))
        properties.put("Correlation ID", Option(correlationId).getOrElse("[NULL]"))

        val sendMessageLog = new StringBuilder

        sendMessageLog.append(s"Sending a batch of [${messages.length}] messages " +
                                s"with Session ID [$sessionId], Correlation ID [$correlationId]:")

        var mNum = 0

        messages.foreach { msg =>
          sendMessageLog.append(s"\nMessage [#${ mNum += 1; mNum }] - [#${msg.getMessageId}]")
        }

        fileLogger.info(sendMessageLog.toString)

        implicit val xc: ExecutionContextExecutorService =
          ExecutionContext fromExecutorService Executors.newSingleThreadExecutor

        val f: Future[Unit] = Retry.retry[Unit](ServiceBusConstants.MaxTransientExceptionAttempts)
          {
            doSend(messages)
          }

        val waitAtMost: Duration = 45.seconds
        Await.result(f, waitAtMost)
        f.onComplete {
                       case Success(_) =>
                         fileLogger.info(s"EntityAdapter sent batch of ${messages.length} messages")
                       case Failure(e) =>
                         fileLogger.error(s"EntityAdapter sent batch of ${messages.length} messages", e)
                         throw e
                     }
    }
  }

  /**
   * Called when {{{send(List, String, String)}}} is fired. Override this method
   * to implement the appropriate message sending functionality.
   *
   * @param messages the messages to send
   */
  @throws(classOf[InterruptedException])
  @throws(classOf[ServiceBusException])
  def doSend(messages: scala.collection.immutable.List[IMessage]): Unit

  /**
   * Reads messages from the Service Bus entity.
   *
   * @param numberOfMessages the number of messages to read
   *
   * @return List of messages read
   */
  @throws(classOf[InterruptedException])
  @throws(classOf[ServiceBusException])
  @throws(classOf[TimeoutException])
  def read(numberOfMessages: Int): List[IMessage] = {
    if (numberOfMessages < 1) {
      fileLogger.info("Didn't request any messages to be read!")
      List[IMessage]()
    } else {
      fileLogger.info(s"Try to read $numberOfMessages messages")

      val messages = doRead(numberOfMessages)

      fileLogger.info(s"Read ${messages.size} messages")
      messages
    }
  }

  /**
   * Called when {{{read(Int)}}} is fired. Override this method
   * to implement the appropriate message reading functionality.
   *
   * @param messageCount the number of messages to read
   *
   * @return List of messages read
   */
  @throws(classOf[InterruptedException])
  @throws(classOf[ServiceBusException])
  def doRead(messageCount: Int): List[IMessage]

  /**
   * Closes the connection to the Service Bus entity
   */
  @throws(classOf[ServiceBusException])
  def close(): Unit
}
