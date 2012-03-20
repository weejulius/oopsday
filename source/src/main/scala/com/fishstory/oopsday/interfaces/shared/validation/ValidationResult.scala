package com.fishstory.oopsday.interfaces.shared.validation

import collection.mutable.ListBuffer

/**
 * Used to place validation result shown to users
 */
class ValidationResult {

  type messageFormat = (String) => String
  type message = ValidationResult
  var messages: List[ListBuffer[Option[messageFormat]]] = List.empty

  def +(a: Option[messageFormat]) {
    messages.head += a
  }

  def clear {
    messages = List.empty
  }

  def clearRound {
    messages.head.clear()
  }

  def size(index: Int): Int = messages(index).size

  def isViolated(x: Int, y: Int): Boolean = x < messages.size && y < messages(0).size && messages(x)(y).isDefined

  def print(x: Int, y: Int, message: String): String = {
    var result = ""
    if (isViolated(x, y)) result = messages(x)(y).get(message)
    result
  }

  def registerNewRound() {
    var newRound: ListBuffer[Option[messageFormat]] = ListBuffer.empty
    messages = newRound :: messages
  }

}

