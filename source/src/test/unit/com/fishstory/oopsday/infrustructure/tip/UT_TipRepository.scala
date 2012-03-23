package com.fishstory.oopsday.infrustructure.tip

import com.fishstory.oopsday.domain.tip.{Tip, TipRepository}
import org.junit.{Test, Before}
import org.junit.Assert._
import com.fishstory.oopsday.infrustructure.Repository

/**
 * Created with IntelliJ IDEA.
 * User: julius.yu
 * Date: 3/23/12
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */

class UT_TipRepository extends Repository {

  var repository: TipRepository = null

  @Before
  def setUp {
    repository = new TipRepositoryJPAImpl()
  }

  @Test
  def testFind {
    transaction {
      repository.save_new_or_update(new Tip("a", "b", "c"))
      assertEquals("b", column[Tip]("title", "a").get content)
      assertEquals("b", id[Tip](1).get content)
    }
  }

}
