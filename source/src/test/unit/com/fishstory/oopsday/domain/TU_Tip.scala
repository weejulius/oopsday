package com.fishstory.oopsday.domain

import org.junit.Test
import org.junit.Before
import org.junit.Assert._
import com.fishstory.oopsday.domain.tip.Tip
import org.junit.rules.ExpectedException
import com.fishstory.oopsday.domain.tip.InvalidTipException
import com.fishstory.oopsday.domain.tip.Tip
import com.fishstory.oopsday.domain.tip.Tip

class TU_Tip() {

  @Test
  def it_should_be_able_to_create_tip_by_content() = {
    
    var expectedContent = "This is a tip"
    var tip = new Tip(expectedContent)
    assertEquals(expectedContent, tip.content)

    expectedContent = "This is another tip"
    tip = new Tip(expectedContent)
    assertEquals(expectedContent, tip.content)
  }

  @Test
  def it_should_be_able_to_modify_content = {

    var tip = new Tip("This is a tip")
    
    tip.set_content("This is a modified tip")

    assertEquals("This is a modified tip", tip.content)
  }

  @Test (expected = classOf[ InvalidTipException])
  def the_content_should_less_than_maxNumberOfChar = {    
	var tip =  new Tip("The tip is more than the max number of char,is it???????????????????????????");
	
	fail("the tip should not more than the max number of char")	 
  }
  
  @Test
  def maxNumberOfChar_should_be_able_to_be_modified={
    
    Tip.set_maxNumberOfChar(80)
    
    var tip = new Tip("The tip is more than the max number of char,is it???????????????????????????");
     
  }

}