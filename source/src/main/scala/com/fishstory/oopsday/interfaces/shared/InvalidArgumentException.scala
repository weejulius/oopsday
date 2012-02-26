package com.fishstory.oopsday.interfaces.shared

import reflect.BeanProperty
import org.codehaus.jackson.map.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
 * User: Julius.Yu
 * Date: 2/11/12
 */

class InvalidArgumentException(message: String) extends RuntimeException(message) {
  def this(event: String, result: String, reason: Any) = this (Message(event, result, reason).toString)
}

case class Message(@BeanProperty var event: String, @BeanProperty var result: String, @BeanProperty var reason: Any) {
  override def toString: String = dto.toRaw(this)
}

object dto {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def toRaw(any: Any): String = {
    mapper.writeValueAsString(any)
  }
}