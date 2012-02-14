package com.fishstory.oopsday.interfaces.shared.template

object Strings {

  def not_empty_then_return(a_string: String, a_value: String): String = {
    var result = ""
    if (a_string != null && !a_string.isEmpty()) {
      result = a_value
    }
    return result
  }

  def not_empty_then_return_orelse(a_string: String, a_value: String, another_value: String): String = {
    var result = another_value
    if (a_string != null && !a_string.isEmpty()) {
      result = a_value
    }
    return result
  }

  def not_empty(a_string: String): Boolean = (a_string != null && !a_string.isEmpty)

  def is_numeric(a_string: String): Boolean = a_string.forall(_.isDigit)

  def to_html(a_string: String): String = {
    a_string.replace("\n", "<br/>")
  }
}
