package com.fishstory.oopsday.infrustructure

import org.slf4j.LoggerFactory
import tip.entityManager
import javax.persistence.EntityManager

trait Transactions {
  private val LOG = LoggerFactory.getLogger(classOf[Transactions])

  def startTransaction = {
    entityManager._entityManager.set(entityManager._emf.createEntityManager())
    get().getTransaction().begin()
  }

  def get(): EntityManager = {
    entityManager._entityManager.get()
  }

  def commitAndCloseTransaction = {

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

  def transaction[A](a: => A): A = {
    startTransaction
    try {

      (a _)()

    } finally {
      commitAndCloseTransaction
    }
  }
}
