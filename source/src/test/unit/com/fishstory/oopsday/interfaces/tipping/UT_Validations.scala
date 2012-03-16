package com.fishstory.oopsday.interfaces.tipping

import com.fishstory.oopsday.interfaces.shared.validation.Validations
import com.fishstory.oopsday.interfaces.shared.validation.Validation
import org.junit.Test
import com.fishstory.oopsday.interfaces.shared.validation.FAILURE
import com.fishstory.oopsday.interfaces.shared.validation.IsNotBlank
import com.fishstory.oopsday.interfaces.shared.validation.And
import com.fishstory.oopsday.interfaces.shared.validation.MaxLength
import org.junit.Assert._

class UT_Validations extends Validation {

  @Test
  def test_is_not_blank {

    var params: Map[String, Seq[String]] = Map.empty
    params += ("tip_title" -> List("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))

    Validations(params,
      ("tip_title", "title", IsNotBlank() ::
        And() :: MaxLength(20) :: Nil)).result match {
      case FAILURE(messages) => println(messages)
      case _ =>
    }
  }

  //  @Test
  //  def test_another_style {
  //    var params: Map[String, Seq[String]] = Map.empty
  //    params += ("tip_title" -> List("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
  //
  //    assertEquals(2,
  //      validations {
  //        Result[String, String]().
  //          check(params.get, "tip_title").by(isNotBlank("the tip is must"), maxLength(20)("the length of tip is more than {}")).
  //          check(params.get, "tip_content").by(isNotBlank("the tip is must"), maxLength(20)("the length of tip is more than {}"))
  //      }.messages.size)
  //
  //    assertEquals(0,
  //      validations {
  //        Result[String, String]().
  //          check(params.get, "tip_content").by(isEmpty("the tip content is must"), isNumeric1("the length of tip is more than {}"))
  //      }.messages.size)
  //  }

  @Test
  def test_style_3 {
    val expression = _IsEmpty[String]("the tip id {} is must") || _IsNumeric("the tip id should be numeric")
    assertTrue(evaluation(expression, ""))
    assertTrue(evaluation(expression, "123"))
    assertFalse(evaluation(expression, "aaaa"))

    assertEquals(1, expression.head.messages.size(3))
  }

}