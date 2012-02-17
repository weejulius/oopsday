package com.fishstory.oopsday.interfaces.tipping

import org.junit.Test
import com.fishstory.oopsday.interfaces.shared.PageNavigation
import org.junit.Assert.assertEquals

/**
 * User: Julius Yu
 * Date: 2/17/12
 *
 */

class UT_PageNavigation {

  @Test
  def test_page_size={
    assertEquals(PageNavigation(1,0L,1).page_num,0)
    assertEquals(PageNavigation(1,1L,1).page_num,1)
    assertEquals(PageNavigation(1,2L,1).page_num,2)
    assertEquals(PageNavigation(1,5L,2).page_num,3)
    assertEquals(PageNavigation(1,7L,3).page_num,3)
  }

  @Test(expected=classOf[IllegalArgumentException])
  def test_invalid_count_and_page_size{
    PageNavigation(1,1,0).page_num
  }

}
