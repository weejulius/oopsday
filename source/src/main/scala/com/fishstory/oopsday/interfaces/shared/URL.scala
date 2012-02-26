package com.fishstory.oopsday.interfaces.shared

/**
 * User: Julius.Yu
 * Date: 12/28/11
 */

object URL {
  def decodeSpecialCharacters(a_string: String): String = {
    return a_string.replace("%20", " ")
  }
}