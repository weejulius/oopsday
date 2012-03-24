package com.fishstory.oopsday.infrustructure.tip

import com.fishstory.oopsday.domain.tip.Tip
import org.junit.Test
import org.junit.Assert._
import com.fishstory.oopsday.infrustructure.{Transactions, tipRepository}

/**
 * Created with IntelliJ IDEA.
 * User: julius.yu
 * Date: 3/23/12
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */

class UT_tipRepository extends Transactions {


  @Test
  def test {
    transaction {
      assertEquals(1, tipRepository.saveNewOrUpdate(new Tip("a", "b", "c")).id)
      assertEquals("b", tipRepository.column("title", "a").get content)
      assertEquals("b", tipRepository.id(1).get content)
      assertEquals(1, tipRepository.count())

      tipRepository.save(new Tip("abc", "b", "c")) inCaseOf column("title"), "b").isEmpty
    }
  }

}
