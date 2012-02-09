package com.fishstory.oopsday.infrustructure.tip
import com.fishstory.oopsday.domain.tip.TipRepository
import com.fishstory.oopsday.domain.tip.Tip
import scala.collection.mutable.HashMap
import javax.persistence.Persistence
import scala.collection.JavaConversions._
import javax.persistence.EntityManager
import com.fishstory.oopsday.interfaces.tipping.TipFace

class TipRepositoryJDBCImpl extends TipRepository with Transactions {

  private def entityManager: EntityManager = {
    get()
  }

  def find_by_id_is(id: Long): Option[Tip] = {
    var tip = entityManager.find(classOf[Tip], id);
    if (tip == null) {
      None
    } else {
      Some(tip)
    }
  }

  def find_all: java.util.List[Tip] = {
    return entityManager.createQuery("""
              SELECT tip
                FROM com.fishstory.oopsday.domain.tip.Tip tip           
      """, classOf[Tip]).getResultList();
  }

  def find_by_title_is(title: String): Option[Tip] = {
    var result = entityManager.createQuery("""
              SELECT tip
                FROM com.fishstory.oopsday.domain.tip.Tip tip
               WHERE tip.title=?1
      """, classOf[Tip]).setParameter(1, title).getResultList();
    if (result.isEmpty()) {
      None
    } else if (result.size() == 1) {
      Some(result.get(0));
    } else {
      throw new IllegalArgumentException("more than 1 tip are retrieved for the title " + title);
    }
  }

  def save_new_or_update(a_tip: Tip): Tip = {

    var tip: Option[Tip] = null

    if (a_tip.id > 0) {
      tip = find_by_id_is(a_tip.id)
    } else if (a_tip.title != null) {
      tip = find_by_title_is(a_tip.title)
    }

    if (tip.isEmpty) {
      entityManager.persist(a_tip)
      tip = Some(a_tip)
    } else {
      a_tip.id = tip.get.id
      entityManager.merge(a_tip)
    }
    tip.get
  }

}