/*
 * Copyright (c) 2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.adapter

import scala.collection.JavaConverters._
import scala.collection.immutable
import scala.util.control.NonFatal
import scala.util.Try

import com.github.emd.servicebuslib.servicebus.helper.Role
import com.github.emd.servicebuslib.servicebus.helper.ServiceBusConstants
import com.microsoft.azure.servicebus.primitives.ServiceBusException
import com.microsoft.azure.servicebus.ReceiveMode
import com.microsoft.azure.servicebus.primitives.Util
import com.microsoft.azure.servicebus.ClientFactory
import com.microsoft.azure.servicebus.IMessage
import com.microsoft.azure.servicebus.IMessageReceiver
import com.microsoft.azure.servicebus.management.ManagementClient
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import enumeratum.Enum
import enumeratum.EnumEntry

import com.github.emd.servicebuslib.helper.FileLogger.fileLogger
import com.github.emd.servicebuslib.servicebus.EntityInformation


/**
 * Types of subscription receivers.
 */
sealed trait ReceiverName extends EnumEntry
object ReceiverName extends Enum[ReceiverName] {

  val values: immutable.IndexedSeq[ReceiverName] = findValues

  case object AlertEvent extends ReceiverName
  case object DispatchEvent extends ReceiverName
  case object PushEvent extends ReceiverName
  case object NotificationEvent extends ReceiverName

  case object AlertEventDL extends ReceiverName
  case object DispatchEventDL extends ReceiverName
  case object PushEventDL extends ReceiverName
  case object NotificationEventDL extends ReceiverName

  case object AlertEventPurger extends ReceiverName
  case object DispatchEventPurger extends ReceiverName
  case object PushEventPurger extends ReceiverName
  case object NotificationEventPurger extends ReceiverName

  case object OutboundNotification extends ReceiverName
  case object OutboundNotificationDL extends ReceiverName

  case object Inbound extends ReceiverName
  case object InboundDL extends ReceiverName
}

/**
 * Describes a receiver.
 *
 * @param entityInformation [[EntityInformation]] defining receiver
 */
final case class Receiver(entityInformation: EntityInformation) extends EntityAdapter {

  // The receiver
  private val receiver: IMessageReceiver =
    ClientFactory.createMessageReceiverFromConnectionString(
      entityInformation.connectionString, ReceiveMode.PEEKLOCK)

  // Required to get active and dead letter message counts
  private val namespaceConnectionStringBuilder = new ConnectionStringBuilder(entityInformation.namespaceConnection)
  private val managementClientSettings = Util.getClientSettingsFromConnectionStringBuilder(namespaceConnectionStringBuilder)
  private val client = new ManagementClient(namespaceConnectionStringBuilder.getEndpoint, managementClientSettings)

  /**
   * Return the active message count.
   *
   * @return the dead letter message count. If an exception is caught 0L will be returned
   */
  def activeMessageCount: Try[Long] = Try(
    entityInformation.subscriptionName match {
      case None =>
        client.getQueueRuntimeInfo(entityInformation.entity).getMessageCountDetails.getActiveMessageCount
      case Some(s) =>
        client.getSubscriptionRuntimeInfo(entityInformation.entity, s).getMessageCountDetails.getActiveMessageCount
    })

  /**
   * Return the dead letter message count.
   *
   * @return the dead letter message count. If an exception is caught 0L will be returned
   */
  def deadLetterMessageCount: Try[Long] = Try(
    entityInformation.subscriptionName match {
      case None =>
        client.getQueueRuntimeInfo(entityInformation.entity).getMessageCountDetails.getDeadLetterMessageCount
      case Some(s) =>
        client.getSubscriptionRuntimeInfo(entityInformation.entity, s).getMessageCountDetails.getDeadLetterMessageCount
    })

  /**
   * The role of this subscription receiver.
   *
   * @return the [[Role]] of this subscription receiver
   */
  def role: Role = entityInformation.role

  /**
   * This Service Bus Entity does not support sending messages.
   */
  @throws[InterruptedException]
  @throws[ServiceBusException]
  def doSend(messages: List[IMessage]): Unit = {
    throw new UnsupportedOperationException("This entity does not support sending messages")
  }

  /**
   * Reads next batch of active messages without changing the state of the
   * receiver or the message source.
   *
   * @param messageCount the number of messages to read
   *
   * @return batch of [[IMessage]] peeked
   */
  @throws[InterruptedException]("if the current thread was interrupted while waiting")
  @throws[ServiceBusException]("if peek failed")
  override def doRead(messageCount: Int): List[IMessage] = {

    fileLogger.debug(s"Try to read $messageCount messages")

    val messagesReceived = doReadRecursive(List.empty[IMessage], 0, messageCount)

    // If there were any messages read, abandon them so that they can be read again
    // without having to wait for lock duration to expire
    messagesReceived.foreach { msg =>
      if (msg.getLockToken != ServiceBusConstants.NullUUID) {
        fileLogger.debug(s"Abandon seq#: ${msg.getSequenceNumber}, lockToken: ${msg.getLockToken}")
        receiver.abandon(msg.getLockToken)
      }
    }

    messagesReceived
  }

  private def doReadRecursive(messages: List[IMessage], readSoFar: Int, total: Int): List[IMessage] = {

    try {
      val partialList: List[IMessage] = receiver.receiveBatch(total).asScala.toList

      if (partialList.nonEmpty && readSoFar <= total) {
        val plr = partialList.size
        fileLogger.debug(s"Read $plr messages this time, soFar ${readSoFar+plr}")

        val accumulatedMessages: List[IMessage] = partialList ::: messages
        fileLogger.debug(s"Read total of ${accumulatedMessages.size} messages")
        messages.foreach( msg => { fileLogger.debug(s"seq: ${msg.getSequenceNumber}, locktoken: ${msg.getLockToken}")})
        doReadRecursive(accumulatedMessages, accumulatedMessages.size, total)
      } else {
        fileLogger.debug(s"Nothing read this pass, let's call it quits and return what we have [${messages.size}]")
        messages
      }
    } catch {
      case NonFatal(e) =>
        fileLogger.warn("Exception caught, return what we have ", e)
        messages
    }
  }

  override def toString: String =
    s"""Receiver [${entityInformation.entity}]
       | connection string : ${entityInformation.connectionString},
       | role              : ${entityInformation.role},
       | mode              : ${ReceiveMode.PEEKLOCK}""".stripMargin

  @throws[ServiceBusException]
  override def close(): Unit = {

    entityInformation.subscriptionName match {
      case None =>
        fileLogger.debug(s"${entityInformation.entity} closing")
      case Some(s) =>
        fileLogger.debug(s"$s closing")
    }

    try { receiver.closeAsync() }
    catch { case NonFatal(_) => }

    ()
  }

  //@throws[RuntimeException]
  //def stopReceiver(): Unit = {
  //  try {
  //    receiver.close()
  //    fileLogger.debug(s"${entityInformation.subscriptionName} closed")
  //  } catch {
  //    case e: ServiceBusException =>
  //      fileLogger.warn(s"Error trying to close ${entityInformation.subscriptionName}", e)
  //  }
  //}
}
