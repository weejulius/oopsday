package com.fishstory.oopsday.infrustructure.tip

import javax.persistence.Persistence
import javax.persistence.EntityManager


object entityManager {
  val _emf = Persistence.createEntityManagerFactory("tip")
  val _entityManager: ThreadLocal[EntityManager] = new ThreadLocal[EntityManager]

}