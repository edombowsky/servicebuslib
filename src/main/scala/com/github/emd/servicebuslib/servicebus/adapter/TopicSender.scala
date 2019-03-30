/*
 * Copyright (c) 2018-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.adapter

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

import com.github.emd.servicebuslib.helper.FileLogger.fileLogger
import com.github.emd.servicebuslib.helper.StringUtils
import com.github.emd.servicebuslib.servicebus.EntityInformation
import com.microsoft.azure.servicebus.IMessage
import com.microsoft.azure.servicebus.Message
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import com.microsoft.azure.servicebus.primitives.ServiceBusException
import com.microsoft.azure.servicebus.TopicClient

final case class TopicSender(entityInformation: EntityInformation) extends EntityAdapter {

  private lazy val connectionString: ConnectionStringBuilder =
    new ConnectionStringBuilder(entityInformation.connectionString, entityInformation.entity)
  private lazy val client: TopicClient = new TopicClient(connectionString)

  fileLogger.debug(s"Connection string for [${entityInformation.entity}]: $connectionString")

  /**
   * Sends a response message to inbound queue
   *
   * @param messages        list of messages to send
   * @param msgId           unique id for this message
   * @param sessionId       session id for this message
   * @param destinationApp  destination for the message (so it goes to the correct host subscription)
   *
   * @return true if a messages was sent, false otherwise
   */
  def sendMessage(
      messages: scala.collection.immutable.List[String],
      msgId: String,
      sessionId: String,
      destinationApp: String = "SBH")
  : Boolean = {

    val outboundMessages: ListBuffer[IMessage] = new scala.collection.mutable.ListBuffer[IMessage]

    if (messages.isEmpty) {
      fileLogger.info("No messages to send")
      true
    } else {
      fileLogger.info(s"Sending ${messages.length} messages to Queue")

      // Add the message properties to each message in the list
      messages.foreach { msg =>
        val outboundMessage = new Message(msg)
        val messageId = StringUtils.generateMessageId(Option(msgId))

        val msgProperties: Map[String, AnyRef] = Map("msgID" -> messageId)

        outboundMessage.setSessionId(sessionId)
        outboundMessage.setCorrelationId(messageId)
        outboundMessage.setMessageId(messageId)
        outboundMessage.setProperties(msgProperties.asJava)
        outboundMessage.setReplyTo(destinationApp)
        outboundMessage.setContentType("XML")

        outboundMessages += outboundMessage

        fileLogger.info(new MessageAdapter(Option(outboundMessage)).toString())
      }

      send(outboundMessages.toList, "session_id", "correlation_id")
      true
    }
  }

  /**
   * Send message to a queue.
   *
   * @param messages the messages to send
   */
  @throws[InterruptedException]
  @throws[ServiceBusException]
  override def doSend(messages: List[IMessage]): Unit = {
    messages match {
      case Nil =>
      case x :: Nil =>
        fileLogger.debug("TopicAdapter sending one message")
        client.sendAsync(x)
        fileLogger.debug("TopicAdapter sent one message")
      case _ =>
        fileLogger.debug(s"TopicAdapter sending a batch of ${messages.length} messages")
        client.sendBatchAsync(messages.asJava)
        fileLogger.debug(s"TopicAdapter sent a batch of ${messages.length} messages")
    }
  }

  /**
   * This Service Bus Entity does not support sending messages.
   *
   * @param messageCount the number of messages to read
   */
  @throws[InterruptedException]
  @throws[ServiceBusException]
  override def doRead(messageCount: Int): List[IMessage] = {
    throw new UnsupportedOperationException("This entity does not support receiving messages")
  }

  @throws[ServiceBusException]
  override def close(): Unit = {
    client.closeAsync()
    ()
  }
}
