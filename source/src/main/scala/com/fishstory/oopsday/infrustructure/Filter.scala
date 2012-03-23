package com.fishstory.oopsday.infrustructure


/**
 * Created with IntelliJ IDEA.
 * User: julius.yu
 * Date: 3/23/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */

//abstract class Filter[-A,-B] extends Transactions{
//  def doFilter():B
//
//  protected def nullToNone(a: Any) = if (a == null) None else Some(a)
//
//  protected def entityManager: EntityManager = get()
//
//
//}
//
//object singleResult {
//  def apply(result: List[Any]) = {
//    if (result.isEmpty) {
//      None
//    } else if (result.size == 1) {
//      Some(result.head)
//    } else {
//      throw new IllegalArgumentException("more than 1 record is retrieved in " + result.toString)
//    }
//  }
//}
//
//case class Id[A,Option[A]](id: Long) extends Filter[A,Option[A]] {
//  def doFilter():Option[A] = {
//    nullToNone(entityManager.find(classOf[A], id))
//  }
//}
//
//case class Column[A](column: String, value: String) extends Filter[A,Option[A]] {
//  def doFilter():Option[A] = {
//    singleResult(entityManager.createQuery("""
//              SELECT x
//                FROM """+classOf[A]+""" x
//               WHERE x.""" + column + "=?1", classOf[A]).setParameter(1, value).getResultList.asScala.toList
//    )
//  }
//}
//
//case class Page[A,List[A]](pageNumber: Int, size: Int = faceConfig.pageSize) extends Filter[A,List[A]]{
//  def doFilter():List[A] = {
//    entityManager.createQuery("""
//              SELECT x
//                FROM """+ classOf[A]+""" x
//      """, classOf[A]).setFirstResult((pageNumber - 1) * size).setMaxResults(size).getResultList()
//  }
//}
//
//case class Count[A,Long]() extends Filter[A,Long]{
//  def doFilter():Long = {
//    entityManager.createQuery("""
//              SELECT count(x)
//                FROM """ + classOf[A] + """ x
//      """, classOf[java.lang.Long]).getResultList.get(0).toLong
//  }
//}
