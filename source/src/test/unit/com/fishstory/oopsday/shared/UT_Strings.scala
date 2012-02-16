package com.fishstory.oopsday.shared
import org.junit.Test
import scala.util.Random

class UT_Strings {

  @Test
  def test_fixed_length_random_string = {
    var chars = ""
    for (i <- 0 until 150) {
      chars += (Random.nextPrintableChar())
      println(i)
    }
    print(chars)

  }

}