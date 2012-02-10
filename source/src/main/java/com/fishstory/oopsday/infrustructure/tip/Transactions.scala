package com.fishstory.oopsday.infrustructure.tip
import javax.persistence.Persistence
import javax.persistence.EntityManager

trait Transactions {

  def start_transaction = {
    Transactions._transactions.set(Transactions._emf.createEntityManager());
    Transactions._transactions.get().getTransaction().begin()
  }

  def get(): EntityManager = {
    Transactions._transactions.get();
  }

  def commit_and_close_transaction = {
    try {
      Transactions._transactions.get().getTransaction().commit();
    } catch {
      case e: Exception => Transactions._transactions.get().getTransaction().rollback();
    } finally {
      Transactions._transactions.get().close();
      Transactions._transactions.set(null)
    }
  }
}

object Transactions {
  val _emf = Persistence.createEntityManagerFactory("tip");
  val _transactions: ThreadLocal[EntityManager] = new ThreadLocal[EntityManager];

}