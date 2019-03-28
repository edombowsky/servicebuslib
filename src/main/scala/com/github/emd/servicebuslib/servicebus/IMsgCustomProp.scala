/*
 * Copyright (c) 2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus

import scalafx.beans.property.StringProperty

/**
 * Storage for IMessage custom properties.
 *
 * @param propertyName_  IMessage message property name
 * @param propertyValue_ IMessage message property value
 */
final case class IMsgCustomProp(propertyName_ : String, propertyValue_ : String) {
  val propertyName = new StringProperty(this, "Name", propertyName_)
  val propertyValue = new StringProperty(this, "Value", propertyValue_)

  override def toString: String = {
    s"""
       |Name:  $propertyName_
       |Value: $propertyValue_
     """.stripMargin
  }
}
