/*
 * Copyright (c) 2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus

import scalafx.beans.property.StringProperty

/**
 * Storage for IMessage properties.
 *
 * @param propertyKey_   IMessage message property key
 * @param propertyValue_ IMessage message property value
 */
final case class IMsgProp(propertyKey_ : String, propertyValue_ : String) {
  val propertyKey = new StringProperty(this, "Key", propertyKey_)
  val propertyValue = new StringProperty(this, "Value", propertyValue_)

  override def toString: String = {
    s"""
       |Key:   $propertyKey_
       |Value: $propertyValue_
     """.stripMargin
  }
}
