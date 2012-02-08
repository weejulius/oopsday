package com.fishstory.oopsday.interfaces.tipping

import unfiltered.filter.Plan
import unfiltered.request._
import collection.mutable.HashMap
import com.fishstory.oopsday.domain.tip.Tip
import unfiltered.response.{ NotFound, Found, Html }
import unfiltered.response.Redirect
import com.fishstory.oopsday.interfaces.shared.URL
import unfiltered.request.GET
import unfiltered.request.Seg
import com.fishstory.oopsday.infrustructure.tip.TipRepositoryJDBCImpl
import com.fishstory.oopsday.domain.tip.TipRepository
import javax.persistence.Persistence
import javax.persistence.EntityManager
import scala.collection.JavaConverters._
import com.fishstory.oopsday.interfaces.shared.Scalate

class TipFace extends Plan {

  private val _tipRepository: TipRepository = new TipRepositoryJDBCImpl();

  TipFace.startTransaction
  _tipRepository.save_new_or_update(Tip.create("Tip 1", "This is the tip 1", "jyu"))
  TipFace.closeTransaction

  def intent = {

    case req @ GET(Path("/tips")) =>
      TipFace.startTransaction
      val tips = _tipRepository.find_all
      TipFace.closeTransaction
      Scalate(req, "tip/tips.ssp", ("tips", tips.asScala.toList))

    case req @ GET(Path("/tips/new")) => editable_page(req, None)

    case req @ GET(Path(Seg("tips" :: key :: Nil))) =>
      TipFace.startTransaction
      var _tip: Option[Tip] = None

      if (is_a_id(key)) {
        _tip = _tipRepository.find_by_id_is(key.toLong)
      } else {
        val parsedTitle: String = URL.decodeSpecialCharacters(key)
        _tip = _tipRepository.find_by_title_is(parsedTitle)
      }

      TipFace.closeTransaction

      if (_tip.isDefined) {
        Found ~> index_page(req, _tip.get)
      } else {
        NotFound ~> not_found_page(req)
      }

    case req @ GET(Path(Seg("tips" :: id :: "edit" :: Nil))) =>
      TipFace.startTransaction
      var tip = _tipRepository.find_by_id_is(id.toLong);
      TipFace.closeTransaction

      if (tip.isDefined) {

        editable_page(req, tip)

      } else {

        not_found_page(req)

      }

    case POST(Path("/tips")) & Params(params) =>

      var _tip_id: String = params("tip_id")(0)

      var _tip: Tip = null

      TipFace.startTransaction

      if (_tip_id.isEmpty) {
        _tip = Tip.create(params("tip_title")(0), params("tip_content")(0), params("tip_author")(0))
        _tip_id = _tip.id.toString

      } else {
        _tip = _tipRepository.find_by_id_is(_tip_id.toLong).get;
        _tip.update_content(params("tip_content")(0))
      }

      _tipRepository.save_new_or_update(_tip)
      TipFace.closeTransaction

      Redirect("/tips/" + _tip_id)

  }

  private def is_a_id(input: String): Boolean = input.forall(_.isDigit)

  private def not_found_page(req: HttpRequest[Any]) = {
    Scalate(req, "not_found.ssp")
  }

  private def index_page(req: HttpRequest[Any], _tip: Tip) = {
    Scalate(req, "tip/tip.ssp", ("_tip", _tip))
  }

  private def editable_page(req: HttpRequest[Any], _tip: Option[Tip]) = {
    var _tip_properties: List[String] = List("", "", "", "")
    if (_tip.isDefined) {
      _tip_properties = List(_tip.get.id.toString, _tip.get.title, _tip.get.content, _tip.get.author)
    }
    Scalate(req, "tip/edit_tip.ssp", ("_tip_properties", _tip_properties))
  }
}

object TipFace {
  val _emf = Persistence.createEntityManagerFactory("tip");
  val _transactions: ThreadLocal[EntityManager] = new ThreadLocal[EntityManager];

  def create: TipFace = new TipFace()

  def startTransaction = {
    _transactions.set(_emf.createEntityManager());
    _transactions.get().getTransaction().begin()
  }

  def closeTransaction = {
    try {
      _transactions.get().getTransaction().commit();
    } catch {
      case e: Exception => _transactions.get().getTransaction().rollback();
    } finally {
      _transactions.get().close();
    }

  }
}