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
import unfiltered.response.ResponseWriter
import unfiltered.response.ResponseFunction
import com.fishstory.oopsday.interfaces.shared.Param

class TipFace extends AbstractPlan {

  private val _tipRepository: TipRepository = new TipRepositoryJPAImpl();

  override def delegates = {

    case req @ GET(Path("/tips")) => {

      start_transaction
      val tips = _tipRepository.find_all
      commit_and_close_transaction

      Scalate(req, "tip/tips.ssp", ("tips", tips.asScala.toList))
    }

    case req @ Path("/tips/new") => req match {

      case GET(_) => editable_page(req, None, Map.empty)
      case POST(_) & Params(params) => create_or_update_tip(req, params)
    }

    case req @ GET(Path(Seg("tips" :: id :: Nil))) => {

      validate_id(id)

      start_transaction
      var _tip: Option[Tip] = _tipRepository.find_by_id_is(id.toLong)
      commit_and_close_transaction

      if (_tip.isDefined) {
        Found ~> index_page(req, _tip.get)
      } else {
        NotFound ~> not_found_page(req)
      }
    }

    case req @ Path(Seg("tips" :: id :: "edit" :: Nil)) => req match {

      case POST(_) & Params(params) => create_or_update_tip(req, params)

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

    }
  }

  private def create_or_update_tip(req: HttpRequest[Any], params: Map[String, Seq[String]]): ResponseFunction[Any] = {

    var param = new Param().from(params).->("tip_id").is_empty_or_digit

    if (param.is_voilated) {
      return Scalate(req, "bad_user_request.ssp")
    }

    param = new Param().from(params)
      .->("tip_title").is_not_blank.otherwise.mark("fail_tip_title", "the title is must")
      .->("tip_content").is_not_blank.otherwise.mark("fail_tip_content", "the content is must")

    if (param.is_voilated) {
      return editable_page(req, None, param.validation_message)
    }

    var _tip_id: String = param <= "tip_id"
    val _tip_title: String = param <= "tip_title"
    val _tip_content: String = param <= "tip_content"

    var _tip: Option[Tip] = None

    start_transaction
    if (_tip_id.isEmpty() || _tip_id.toLong <= 0) {
      _tip = Some(Tip.create(_tip_title, _tip_content, ""))
      _tip_id = _tip.get.id.toString

    } else {
      _tip = _tipRepository.find_by_id_is(_tip_id.toLong);
      if (_tip.isDefined) {
        _tip.get.update_content(_tip_content)
      }
    }

    if (_tip.isDefined) {
      _tipRepository.save_new_or_update(_tip.get)
    }
    commit_and_close_transaction

    Redirect("/tips/" + _tip.get.id)

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