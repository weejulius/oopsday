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


  @Test
  def test_style_3 {
    val expression = _IsEmpty[String]() || _IsNumeric()
    assertTrue(evaluation(expression, ""))
    assertTrue(evaluation(expression, "123"))
    assertFalse(evaluation(expression, "aaaa"))
    assertEquals(3, expression.head.messages.messages.size)
    assertEquals(1, expression.head.messages.size(0))
    assertEquals("aaaa is not numeric", expression.messages.print(0, 0, "<> is not numeric"))
  }

  @Test
  def test_validate_2_params {
    var expression = _NotBlank[String]() && _MaxLength(10)

    assertTrue(evaluation(expression, "1234"))
    assertFalse(evaluation(expression, "12333333333333333333"))
    expression retry (_NotBlank[String]() && _MaxLength(12))
    assertFalse(evaluation(expression, ""))
    assertFalse(evaluation(expression, "1222222222222222222222222"))

    assertEquals(4, expression.head.messages.messages.size)
    assertEquals("the title 1222222222222222222222222 is more than 12", expression.head.messages.print(0, 1, "the title <> is more than <>"))
    assertEquals("the title  is must", expression.head.messages.print(1, 0, "the title <> is must"))

    var expression1 = _NotBlank[Option[Seq[String]]]() && _MaxLength(12)
    var a: Option[Seq[String]] = Some(List[String]("").toSeq)
    assertFalse(evaluation[Option[Seq[String]]](expression1, a))
    assertEquals(1, expression1.head.messages.messages.size)
    assertEquals("the title  is must", expression1.head.messages.print(0, 0, "the title <> is must"))


  }

  @Test
  def test_message_template {
    val a = messageTemplate("a" :: "ba" :: Nil)(_)
    assertEquals("a is not ba", a("<> is not <>"))
  }

}