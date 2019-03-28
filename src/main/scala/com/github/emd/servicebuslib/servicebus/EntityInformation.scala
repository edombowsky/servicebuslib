/*
 * Copyright (c) 2017-2018 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus

import com.github.emd.servicebuslib.servicebus.helper.EntityType
import com.github.emd.servicebuslib.servicebus.helper.Role
import com.github.emd.servicebuslib.servicebus.helper.ServiceBusConstants
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder


/**
 * Captures information that defines an entity
 *
 * @param role             the roles this entity will play [[Role]]
 * @param entityType       the entityType type of this entity [[EntityType]]
 * @param keyName          name of the key for this entity
 * @param keyValue         the entities secret key
 * @param entity           the entities path
 * @param namespace        the namespace where the entity is contained
 * @param subscriptionName if this is a subscription this is its name
 */
final case class EntityInformation(
  role: Role,
  entityType: EntityType,
  keyName: String,
  keyValue: String,
  entity: String,
  namespace: String,
  namespaceConnection: String,
  subscriptionName: Option[String] = None) {

  private val entityPath =
    entityType match {
      case EntityType.DeadLetter =>
        ServiceBusConstants.DeadLetterPathPattern.format(entity)
      case EntityType.Queue | EntityType.Topic =>
        entity
      case EntityType.Subscription =>
        ServiceBusConstants.SubscriptionPathPattern
          .format(entity, subscriptionName.get)
      case EntityType.SubscriptionDeadLetter =>
        ServiceBusConstants.DeadLetterPathPattern
          .format(ServiceBusConstants.SubscriptionPathPattern
            .format(entity, subscriptionName.get))
    }

  /**
   * Sting used to connect to this entity.
   */
  val connectionString: String =
    new ConnectionStringBuilder(namespace, entityPath, keyName, keyValue).toString
}
