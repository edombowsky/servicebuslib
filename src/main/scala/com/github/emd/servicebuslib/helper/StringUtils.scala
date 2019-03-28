/*
 * Copyright (c) 2017-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.helper

import java.io.PrintWriter
import java.io.StringWriter
import java.time.Duration
import java.time.Instant

import com.github.emd.servicebuslib.helper.FileLogger.fileLogger
import ujson.StringRenderer
import scala.language.postfixOps
import scala.util.Random
import scala.xml.PrettyPrinter

import com.github.emd.servicebuslib.servicebus.helper.ServiceBusConstants

object StringUtils {

  /**
   * Confirm if a string is empty. That is, null or empty string.
   *
   * @param x string to be checked
   *
   * @return true if string is empty, false otherwise
   */
  def isEmpty(x: String): Boolean = x == null || x.trim.isEmpty

  /**
   * Convert a [[Duration]] to a string.
   *
   * @param d the [[Duration]] to be converted
   *
   * @return string representation of the input [[Duration]]
   */
  def Duration2String(d: Duration): String = {
      val s = d.getSeconds
      f"${s / 86400}%d:${s % 86400 / 3600}%02d:${s % 3600 / 60}%02d:${s % 60}%02d"
  }

  def generateMessageId(messageId: Option[String]): String = {
    val nowString = Instant.now.toString
    messageId match {
      case None => nowString
      case Some(str) => if (isEmpty(str)) nowString else s"$str-$nowString"
        //s"$nowString-$str"
    }
  }

  /**
   * Generate a string of a specified length containing random alphanumeric characters
   *
   * @param length the required string length
   *
   * @return requested string
   */
  def randomAlphanumeric(length: Int): String = {
    Random.alphanumeric take length mkString
  }

  /**
   * Generate a string of a specified length containing random alpha characters
   *
   * @param length the required string length
   *
   * @return requested string
   */
  def randomAlpha(length: Int): String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z')
    randomStringFromCharList(length, chars)
  }

  private def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
    val sb = new StringBuilder
    (1 to length).foreach(_ => sb.append(chars(Random.nextInt(chars.length))))
    sb.toString
  }

  /**
   * Replace tags on a string.
   *
   * @param string          string containing tags to be replaced
   * @param workOrderNumber work-order number
   * @param orderNumber     order number
   * @param priority        order priority
   * @param messageId       messageId prefix
   *
   * @return input string with tags replaced with values
   */
  def replaceTags(string: String,
      workOrderNumber: String,
      orderNumber: String,
      priority: String,
      messageIdPrefix: String): String = {

    val dateTimeString = Instant.now.toString
    val messageId = generateMessageId(Option(messageIdPrefix))
    val uuid = java.util.UUID.randomUUID().toString

    fileLogger.debug(s"""|\n
                     |Replacement patterns
                     |${ServiceBusConstants.WorkOrderNumberTag} -> $workOrderNumber
                     |${ServiceBusConstants.OrderNumberTag} -> $orderNumber
                     |${ServiceBusConstants.MessageIdTag} -> $messageId
                     |${ServiceBusConstants.DateTimeTag} -> $dateTimeString
                     |${ServiceBusConstants.UuidTag} -> $uuid
                  """.stripMargin)

    val replacementsMap = Map(ServiceBusConstants.WorkOrderNumberTag -> workOrderNumber,
      ServiceBusConstants.MessageIdTag -> messageId,
      ServiceBusConstants.OrderNumberTag -> orderNumber,
      ServiceBusConstants.DateTimeTag -> dateTimeString,
      ServiceBusConstants.UuidTag -> uuid,
      ServiceBusConstants.PriorityTag -> priority)

    replacementsMap.foldLeft(string) {
      case (accumulator, (target, replacement)) =>
       accumulator.replaceAllLiterally(target, replacement)
   }
  }

  /**
   * Pretty prints a Scala messages similar to its source representation.
   * Particularly useful for case classes.
   *
   * @param a                The messages to pretty print
   * @param indentSize       Number of spaces for each indent
   * @param maxElementWidth  Largest element size before wrapping
   * @param depth            Initial depth to pretty print indents
   *
   * @return String representation of a Scala object
   *
   * @see [[https://gist.github.com/carymrobbins/7b8ed52cd6ea186dbdf8]]
   */
  def prettyPrint(a: Any, indentSize: Int = 2, maxElementWidth: Int = 30, depth: Int = 0): String = {

    val indent = " " * depth * indentSize
    val fieldIndent = indent + (" " * indentSize)
    val thisDepth = prettyPrint(_: Any, indentSize, maxElementWidth, depth)
    val nextDepth = prettyPrint(_: Any, indentSize, maxElementWidth, depth + 1)

    a match {
      // Make Strings look similar to their literal form.
      case s: String =>
        val replaceMap = Seq(
          "\n" -> "\\n",
          "\r" -> "\\r",
          "\t" -> "\\t",
          "\"" -> "\\\""
        )
        '"' + replaceMap.foldLeft(s) { case (acc, (c, r)) => acc.replace(c, r) } + '"'

      // For an empty Seq just use its normal String representation.
      case xs: Seq[_] if xs.isEmpty => xs.toString()

      case xs: Seq[_] =>
        // If the Seq is not too long, pretty print on one line.
        val resultOneLine = xs.map(nextDepth).toString()
        if (resultOneLine.length <= maxElementWidth) return resultOneLine
        // Otherwise, build it with newlines and proper field indents.
        val result = xs.map(x => s"\n$fieldIndent${nextDepth(x)}").toString()
        result.substring(0, result.length - 1) + "\n" + indent + ")"

      // Product should cover case classes.
      case p: Product =>
        val prefix = p.productPrefix
        // We'll use reflection to get the constructor arg names and values.
        val cls = p.getClass
        val fields = cls.getDeclaredFields.filterNot(_.isSynthetic).map(_.getName)
        val values = p.productIterator.toSeq
        // If we weren't able to match up fields/values, fall back to toString.
        if (fields.length != values.length) return p.toString
        fields.zip(values).toList match {
          // If there are no fields, just use the normal String representation.
          case Nil => p.toString
          // If there is just one field, let's just print it as a wrapper.
          case (_, value) :: Nil => s"$prefix(${thisDepth(value)})"
          // If there is more than one field, build up the field names and values.
          case kvps =>
            val prettyFields = kvps.map { case (k, v) => s"$fieldIndent$k = ${nextDepth(v)}" }
            // If the result is not too long, pretty print on one line.
            val resultOneLine = s"$prefix(${prettyFields.mkString(", ")})"
            if (resultOneLine.length <= maxElementWidth) return resultOneLine
            // Otherwise, build it with newlines and proper field indents.
            s"$prefix(\n${prettyFields.mkString(",\n")}\n$indent)"
        }

      // If we haven't specialized this type, just use its toString.
      case _ => a.toString
    }
  }

  /**
   * Prepare a string that contains the stack trace from a throwable suitable
   * for logging to a log file or displaying to a dialog box.
   *
   * @param t throwable to prepare
   * @return string containing the [[Throwable]] stack trace
   */
  def getStackTraceAsString(t: Throwable): String = {
    val sw = new StringWriter
    t.printStackTrace(new PrintWriter(sw))
    sw.toString
  }

  /**
   * Tries to pretty print a string to either JSON or XML. If it can not
   * it will simply return the string un-touched.
   *
   * @param message string to be pretty printed
   *
   * @return pretty printed message
   */
  def renderJsonOrXml(message: String): String = {
    // FIXME:: Possibly do better than trying JSON first and then XML and then...
    try {
      ujson.transform(message, StringRenderer(indent = 3)).toString
    } catch {
      case _: Exception =>
        try {
          val p = new PrettyPrinter(80, 4)
          p.format(scala.xml.XML.loadString(message))
        } catch {
          case _: Exception => message
        }
    }
  }

}
