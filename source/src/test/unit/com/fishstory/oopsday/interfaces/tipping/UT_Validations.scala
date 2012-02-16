package com.fishstory.oopsday.interfaces.tipping
import com.fishstory.oopsday.interfaces.shared.validation.Validations
import org.junit.Test
import com.fishstory.oopsday.interfaces.shared.validation.FAILURE
import com.fishstory.oopsday.interfaces.shared.validation.Validate
import com.fishstory.oopsday.interfaces.shared.validation.IsNotBlank
import com.fishstory.oopsday.interfaces.shared.validation.And
import com.fishstory.oopsday.interfaces.shared.validation.MaxLength

class UT_Validations {

  @Test
  def test_is_not_blank = {

    var params: Map[String, Seq[String]] = Map.empty
    params += ("tip_title"->List("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))

    Validations(params,
      Validate("tip_title", "title", IsNotBlank(), And(), MaxLength(20))).result match {
        case FAILURE(messages) => println(messages)
        case _ =>
      }
  }

}