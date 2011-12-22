package com.fishstory.oopsday.domain

import org.junit.Test
import org.junit.Before
import org.junit.Assert._
import com.fishstory.oopsday.domain.tip.Tip
import org.junit.rules.ExpectedException
import com.fishstory.oopsday.domain.tip.InvalidTipException
import com.fishstory.oopsday.domain.tip.Tip
import com.fishstory.oopsday.domain.tip.Tip
import org.joda.time.DateTime

class TU_Tip() {

  @Test
  def it_should_be_able_to_create_tip_by_content() = {

    var expectedContent = "This is a tip"
    var tip = new Tip(expectedContent, "jyu")
    assertEquals(expectedContent, tip.content)

    expectedContent = "This is another tip"
    tip = new Tip(expectedContent, "jyu")
    assertEquals(expectedContent, tip.content)
  }

  @Test
  def it_should_be_able_to_modify_content = {

    var tip = new Tip("This is a tip", "jyu")

    tip.set_content("This is a modified tip")

    assertEquals("This is a modified tip", tip.content)
  }

  @Test(expected = classOf[InvalidTipException])
  def the_content_should_less_than_maxNumberOfChar = {
    var tip = new Tip("The tip is more than the max number of char,is it???????????????????????????", "jyu");

    fail("the tip should not more than the max number of char")
  }

  @Test
  def maxNumberOfChar_should_be_able_to_be_modified = {

    Tip.set_maxNumberOfChar(80)

    var tip = new Tip("The tip is more than the max number of char,is it???????????????????????????", "jyu");

  }

  @Test
  def it_should_know_the_creation_date = {
    var tip = new Tip("This is a tip", "jyu")

    assertTrue("the creation date is now approximately", DateTime.now().getMillis() <= (tip.created_date.getTime() + 500))
    assertEquals("the creation date should not be different calling it", tip.created_date, tip.created_date);
  }

  @Test
  def it_should_has_author = {
    var tip = new Tip("this is a tip", "jyu")

    assertEquals("jyu", tip.author)

    tip = new Tip("this is a tip", "sue")

    assertEquals("sue", tip.author)
  }

}