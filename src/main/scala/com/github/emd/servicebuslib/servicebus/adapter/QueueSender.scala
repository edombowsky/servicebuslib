/*
 * Copyright (c) 2018-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.adapter

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

import com.github.emd.servicebuslib.servicebus.helper.ServiceBusConstants
import com.microsoft.azure.servicebus.IMessage
import com.microsoft.azure.servicebus.Message
import com.microsoft.azure.servicebus.QueueClient
import com.microsoft.azure.servicebus.ReceiveMode
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import com.microsoft.azure.servicebus.primitives.ServiceBusException

import com.github.emd.servicebuslib.helper.FileLogger.fileLogger
import com.github.emd.servicebuslib.helper.StringUtils
import com.github.emd.servicebuslib.servicebus.EntityInformation

final case class QueueSender(entityInformation: EntityInformation) extends EntityAdapter {

  private val connectionString: ConnectionStringBuilder =
    new ConnectionStringBuilder(entityInformation.connectionString, entityInformation.entity)
  private lazy val client: QueueClient =
    new QueueClient(connectionString, ReceiveMode.PEEKLOCK)

  fileLogger.debug(s"Connection string for Queue [${entityInformation.entity}]: $connectionString")

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
        outboundMessage.setLabel(ServiceBusConstants.SendMessageLabel)
        outboundMessage.setContentType("XML")

        outboundMessages += outboundMessage

        fileLogger.info(new MessageAdapter(Option(outboundMessage)).toString())
      }

      send(outboundMessages.toList, "session_id", "correlation_id")
      true
    }
  }

  /**
   * Sends a message to a queue.
   *
   * @param messages the messages to send
   *
   * @throws InterruptedException
   * @throws ServiceBusException
   */
  override def doSend(messages: List[IMessage]): Unit = {
    messages match {
      case Nil =>
      case x :: Nil =>
        fileLogger.debug("QueueAdapter sending one message")
        //client.send(x)
        client.sendAsync(x)
        fileLogger.debug("QueueAdapter sent one message")
      case _ =>
        fileLogger.debug(s"QueueAdapter sending a batch of ${messages.length} messages")
        client.sendBatchAsync(messages.asJava)
        fileLogger.debug(s"QueueAdapter sent a batch of ${messages.length} messages")
    }
  }

  /**
   * This Service Bus Entity does not support reading messages.
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
    client.close()
  }
}
