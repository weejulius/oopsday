package com.fishstory.oopsday.interfaces.tipping

import unfiltered.request._
import com.fishstory.oopsday.domain.tip.Tip
import unfiltered.response.{ NotFound, Found }
import unfiltered.response.Redirect
import unfiltered.request.GET
import unfiltered.request.Seg
import com.fishstory.oopsday.infrustructure.tip.TipRepositoryJPAImpl
import com.fishstory.oopsday.domain.tip.TipRepository
import scala.collection.JavaConverters._
import com.fishstory.oopsday.interfaces.shared.Scalate
import com.fishstory.oopsday.interfaces.shared.AbstractPlan
import com.fishstory.oopsday.interfaces.shared.InvalidArgumentException
import unfiltered.request.Params.ParamMapper

class TipFace extends AbstractPlan {

  private val _tipRepository: TipRepository = new TipRepositoryJPAImpl();

  override def delegates = {

    case req @ GET(Path("/tips")) =>

      start_transaction
      val tips = _tipRepository.find_all
      commit_and_close_transaction

      Scalate(req, "tip/tips.ssp", ("tips", tips.asScala.toList))

    case req @ Path("/tips/new") => req match {

      case GET(_) => editable_page(req, None, Map.empty)
      case POST(_) & Params(params) => create_or_update_tip(req, params)
    }

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

    case req @ Path(Seg("tips" :: id :: "edit" :: Nil)) => req match {

      case GET(_) => {
        validate_id(id)

        start_transaction
        val _tip: Option[Tip] = _tipRepository.find_by_id_is(id.toLong)
        commit_and_close_transaction

        if (_tip.isDefined) {
          editable_page(req, _tip, Map.empty)
        } else {
          not_found_page(req)
        }
      }
      case POST(_) & Params(params) => create_or_update_tip(req, params)
    }
  }

  private def create_or_update_tip(req: HttpRequest[Any], params: Map[String, Seq[String]]) = {

    var validation_tip_message = Map.empty[String, String]

    var _tip_id: String = params("tip_id")(0)
    val _tip_title: String = params("tip_title")(0)
    val _tip_content: String = params("tip_content")(0)

    if (_tip_title.isEmpty()) {
      validation_tip_message += ("fail_tip_title" -> "the title is must")
    }

    if (_tip_content.isEmpty()) {
      validation_tip_message += ("fail_tip_content" -> "the content is must")
    }

    if (!validation_tip_message.isEmpty) {
       editable_page(req, None, validation_tip_message)
    } else {

      var _tip: Tip = null

      start_transaction
      if (_tip_id.isEmpty() || _tip_id.toLong <= 0) {
        _tip = Tip.create(_tip_title, _tip_content, "")
        _tip_id = _tip.id.toString

      } else {
        _tip = _tipRepository.find_by_id_is(_tip_id.toLong).get;
        _tip.update_content(params("tip_content")(0))
      }

      _tipRepository.save_new_or_update(_tip)
      commit_and_close_transaction

      Redirect("/tips/" + _tip.id)
    }
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

  private def editable_page(req: HttpRequest[Any], _tip: Option[Tip], _validation_tip_message: Map[String, String]) = {
    var tip: Tip = Tip.NullObject
    if (_tip.isDefined) {
      tip = _tip.get
    }
    Scalate(req, "tip/edit_tip.ssp", ("tip", tip), ("validation_tip_message", _validation_tip_message))
  }

}

object TipFace {

  def create: TipFace = new TipFace()

}