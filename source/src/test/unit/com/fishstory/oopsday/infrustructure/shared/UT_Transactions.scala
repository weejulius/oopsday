package com.fishstory.oopsday.infrustructure.shared
import com.fishstory.oopsday.domain.tip.Tip
import org.junit.Test
import com.fishstory.oopsday.infrustructure.tip.TipRepositoryJPAImpl
import com.fishstory.oopsday.infrustructure.tip.Transactions
import com.fishstory.oopsday.domain.tip.TipRepository
import org.junit.Assert._

class UT_Transactions extends Transactions {
  private val repository: TipRepository = new TipRepositoryJPAImpl

  @Test
  def testTransaction {
    transaction {
      repository.save_new_or_update(new Tip("3", "4", "5"))
    }

    transaction {
      assertEquals("3",repository.find_by_title_is("3").get.title)
    }
  }
}