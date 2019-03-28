/*
 * Copyright (c) 2017-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus

final case class SBContext(
  name: String,
  namespace: String,
  namespaceConnection: String,
  queueIn: String,
  queueInListenKey: String,
  queueInSendKey: String,
  eventHostSubscription: String,
  eventPushSubscription: String,
  eventDispatchSubscription: String,
  eventAlertSubscription: String,
  eventTopic: String,
  eventTopicListenKey: String,
  eventTopicSendKey: String,
  notificationTopic: String,
  notificationTopicSendKey: String,
  notificationTopicListenKey: String,
  notificationTopicHost: String) {

  override def toString: String =
    s"""
       |Name                             : $name
       |ServiceBusNamespace              : $namespace
       |ServiceBusNamespaceConnection    : $namespaceConnection
       |QueueIn                          : $queueIn
       |QueueInListenKey                 : *****
       |QueueInSendKey                   : *****
       |EventHostSubscription            : $eventHostSubscription
       |EventPushSubscription            : $eventPushSubscription
       |EventDispatchSubscription        : $eventDispatchSubscription
       |EventAlertSubscription           : $eventAlertSubscription
       |EventTopic                       : $eventTopic
       |EventTopicListenKey              : *****
       |EventTopicSendKey                : *****
       |NotificationTopic                : $notificationTopic
       |NotificationTopicSendKey         : *****
       |NotificationTopicListenKey       : *****
       |NotificationTopicHost            : $notificationTopicHost
    """.stripMargin
}
