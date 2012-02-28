package com.fishstory.oopsday.infrustructure.tip
import javax.persistence.Persistence
import javax.persistence.EntityManager
import org.slf4j.LoggerFactory

trait Transactions {
  private val LOG = LoggerFactory.getLogger(classOf[Transactions])

  def start_transaction = {
    entityManager._entityManager.set(entityManager._emf.createEntityManager())
    get().getTransaction().begin()
  }

  def get(): EntityManager = {
    entityManager._entityManager.get()
  }

  def commit_and_close_transaction = {

    try {
      if (get().getTransaction().isActive() && !get().getTransaction().getRollbackOnly()) {
        get.getTransaction().commit()
      } else {
        get().getTransaction().rollback()
      }
    } catch {
      case e: Exception => {
        LOG.warn("event:commit transaction,result:failed,reason:{}", e)
        get().getTransaction().rollback()
      }
    } finally {
      get().close()
      entityManager._entityManager.set(null)
    }
  }
}

object entityManager {
  val _emf = Persistence.createEntityManagerFactory("tip")
  val _entityManager: ThreadLocal[EntityManager] = new ThreadLocal[EntityManager]

}