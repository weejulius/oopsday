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
import com.fishstory.oopsday.shared.The

class TU_Tip() {

  @Test
  def it_should_be_able_to_create_tip_by_content{

    var expectedContent = "This is a tip"
    var tip = new Tip("Tip 1", "This is a tip", "jyu")

    The string (tip.content) should_equal_to (expectedContent);

    expectedContent = "This is another tip"
    tip = new Tip("Tip 1", "This is another tip", "jyu")

    The string (tip.content) should_equal_to (expectedContent);
  }

  @Test
  def it_should_be_able_to_update_content {

    var tip = new Tip("Tip 1", "This is a tip", "jyu")

    The.date(tip.modified_date).should_be_null_date

    tip.update_content("This is a modified tip")

    The string (tip.content) should_equal_to "This is a modified tip"

    The date (tip.created_date) should_be_now_approximately;
    The date (tip.modified_date) should_be_now_approximately;
  }

  @Test(expected = classOf[InvalidTipException])
  def the_content_should_less_than_maxNumberOfChar {

    entityValidationDef.tipMaxLengthOfContent = 20

    var tip = new Tip("Tip 1", "The tip is more than the max number of char,is it???????????????????????????", "jyu");

    fail("the tip should not more than the max number of char")
  }

  @Test
  def maxNumberOfChar_should_be_able_to_be_modified {

    entityValidationDef.tipMaxLengthOfContent = 80

    var tip = new Tip("Tip 1", "The tip is more than the max number of char,is it???????????????????????????", "jyu");

  }

  @Test
  def it_should_know_the_creation_date {

    var tip = new Tip("Tip 1", "This is a tip", "jyu")

    The date (tip.created_date) should_be_now_approximately;
    The date (tip.created_date) should_equal_to (tip.created_date);
  }

  @Test
  def it_should_have_author {

    var tip = new Tip("Tip 1", "this is a tip", "jyu")

    The string (tip.author) should_equal_to "jyu"

    tip = new Tip("Tip 1", "this is a tip", "sue")

    The string (tip.author) should_equal_to "sue"
  }

  @Test
  def it_should_have_title {
    var tip = new Tip("Tip 1", "this is a tip", "jyu")
    The string (tip.title) should_equal_to "Tip 1"
  }

  @Test
  def it_should_have_id {
    var tip = new Tip("Tip 1", "this is a tip", "jyu")
    The number (tip.id) should_be_greater_than_number -1
  }

}