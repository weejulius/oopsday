package com.fishstory.oopsday.infrustructure.tip
import javax.persistence.Persistence
import javax.persistence.EntityManager
import org.slf4j.LoggerFactory

trait Transactions {
  private val LOG = LoggerFactory.getLogger(classOf[Transactions])

  def start_transaction = {
    Transactions._transactions.set(Transactions._emf.createEntityManager())
    get().getTransaction().begin()
  }

  def get(): EntityManager = {
    Transactions._transactions.get()
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
      Transactions._transactions.set(null)
    }
  }
}

object Transactions {
  val _emf = Persistence.createEntityManagerFactory("tip")
  val _transactions: ThreadLocal[EntityManager] = new ThreadLocal[EntityManager]

}