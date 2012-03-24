package com.fishstory.oopsday.infrustructure

import javax.persistence.EntityManager
import com.fishstory.oopsday.interfaces.shared.faceConfig
import scala.collection.JavaConverters._
import com.fishstory.oopsday.domain.tip.Tip
import com.fishstory.oopsday.domain.DomainEntity
import com.fishstory.oopsday.domain.tag.Tag

/**
 * Created with IntelliJ IDEA.
 * User: julius.yu
 * Date: 3/23/12
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */

trait Repository[A <: DomainEntity] extends Transactions {

  protected def nullToNone[A](a: A) = if (a == null) None else Some(a)

  abstract class Filter

  protected def entityManager: EntityManager = get()

  object singleResult {
    def apply(result: List[Any]) = {
      if (result.isEmpty) {
        None
      } else if (result.size == 1) {
        Some(result.head)
      } else {
        throw new IllegalArgumentException("more than 1 record is retrieved in " + result.toString)
      }
    }
  }

  case object id extends Filter {
    def apply(id: Long)(implicit m: scala.reflect.Manifest[A]): Option[A] = {
      nullToNone(entityManager.find(m.erasure, id)).asInstanceOf[Option[A]]
    }
  }

  case object saveNewOrUpdate {
    def apply(entity: A)(implicit m: scala.reflect.Manifest[A]): A = {
      var savedEntity: Option[A] = None

      if (entity.id > 0) {
        savedEntity = id(entity.id)
      }

      if (savedEntity.isEmpty) {
        entityManager.persist(entity)
        savedEntity = Some(entity)
      } else {
        savedEntity = Some(entityManager.merge(entity))
      }
      savedEntity.get
    }
  }

  case object column extends Filter {

    def apply(column: String, value: String)(implicit m: scala.reflect.Manifest[A]): Option[A] = {
      singleResult(entityManager.createQuery("""
              SELECT x
                FROM """ + m.erasure.getName + """ x
               WHERE x.""" + column + "=?1", m.erasure
      ).setParameter(1, value).getResultList.asScala.toList
      ).asInstanceOf[Option[A]]
    }
  }

  case object page extends Filter {
    def apply(pageNumber: Int, size: Int = faceConfig.pageSize)(implicit m: scala.reflect.Manifest[A]): List[A] = {
      entityManager.createQuery("""
              SELECT x
                FROM """ + m.erasure.getName + """ x
      """, m.erasure).setFirstResult((pageNumber - 1) * size).setMaxResults(size).getResultList()
    }.asInstanceOf[List[A]]
  }

  case object count {
    def apply()(implicit m: scala.reflect.Manifest[A]): Long = {
      entityManager.createQuery("""
              SELECT count(x)
                FROM """ + m.erasure.getName + """ x
      """, classOf[java.lang.Long]).getResultList.get(0).toString.toLong
    }
  }

}


object tipRepository extends Repository[Tip]

object tagRepository extends Repository[Tag] {
}
