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
import com.fishstory.oopsday.infrustructure.tip.Transactions
import com.fishstory.oopsday.interfaces.shared.AbstractPlan
import com.fishstory.oopsday.interfaces.shared.InvalidArgumentException

class TipFace extends AbstractPlan {

  private val _tipRepository: TipRepository = new TipRepositoryJDBCImpl();

  override def delegate = {

    case req @ GET(Path("/tips")) =>

      start_transaction
      val tips = _tipRepository.find_all
      commit_and_close_transaction

      Scalate(req, "tip/tips.ssp", ("tips", tips.asScala.toList))

    case req @ GET(Path("/tips/new")) => editable_page(req, None)

    case req @ GET(Path(Seg("tips" :: id :: Nil))) =>

      validate_id(id)

      start_transaction
      var _tip: Option[Tip] = _tipRepository.find_by_id_is(id.toLong)
      commit_and_close_transaction

      if (_tip.isDefined) {
        Found ~> index_page(req, _tip.get)
      } else {
        NotFound ~> not_found_page(req)
      }

    case req @ GET(Path(Seg("tips" :: id :: "edit" :: Nil))) =>

      validate_id(id)

      start_transaction
      val _tip: Option[Tip] = _tipRepository.find_by_id_is(id.toLong)
      commit_and_close_transaction

      if (_tip.isDefined) {
        editable_page(req, _tip)
      } else {
        not_found_page(req)
      }

    case POST(Path("/tips")) & Params(params) =>

      var _tip_id: String = params("tip_id")(0)

      var _tip: Tip = null

      start_transaction
      if (_tip_id.isEmpty) {
        _tip = Tip.create(params("tip_title")(0), params("tip_content")(0), params("tip_author")(0))
        _tip_id = _tip.id.toString

      } else {
        _tip = _tipRepository.find_by_id_is(_tip_id.toLong).get;
        _tip.update_content(params("tip_content")(0))
      }

      _tipRepository.save_new_or_update(_tip)
      commit_and_close_transaction

      Redirect("/tips/" + _tip.id)
  }

  private def validate_id(a_id: String) = {
    if (!is_a_id(a_id)) {
      throw new InvalidArgumentException("access tip " + a_id, "failed", a_id + " is not a valid id")
    }
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

  def create: TipFace = new TipFace()

}