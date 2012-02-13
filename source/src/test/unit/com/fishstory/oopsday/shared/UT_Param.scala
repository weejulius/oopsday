package com.fishstory.oopsday.shared
import org.junit.Test
import org.junit.Assert

class UT_Param {
  
  @Test
  def test_parameter_is_not_existing={
    var params=Map.empty[String,Seq[String]]
    params += ("first" -> List("1"))
    params += ("second" -> Nil)
    params += ("third" -> List(""))
    
    Assert.assertTrue(params.get("fourth") + " is not null",params.get("fourth").isEmpty)
    
    Assert.assertFalse(params.get("fourth") + " is not null",params.get("second").isEmpty)
    
    Assert.assertFalse(params.get("fourth") + " is not null",params.get("third").isEmpty)
  }

}