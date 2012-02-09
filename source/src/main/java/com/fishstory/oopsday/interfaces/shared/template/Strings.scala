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
}