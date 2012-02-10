package com.fishstory.oopsday.interfaces.tipping

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.fishstory.oopsday.shared.The
import cucumber.annotation.Before
import cucumber.annotation.After
import cucumber.annotation.en.{ When, Given, Then }
import unfiltered.jetty.{ Server, Http }
import com.fishstory.oopsday.domain.tip.Tip
import org.openqa.selenium.{ By, WebDriver }
import org.openqa.selenium.WebElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.fishstory.oopsday.infrustructure.tip.Transactions
import com.fishstory.oopsday.domain.tip.TipRepository
import com.fishstory.oopsday.infrustructure.tip.TipRepositoryJDBCImpl

class Steps_Tip extends Transactions {
  private var _webDriver: WebDriver = new HtmlUnitDriver()
  private var _server: Server = Http(8080).plan(new TipFace)
  private val _log: Logger = LoggerFactory.getLogger(classOf[Steps_Tip])
  private val _tipRepository: TipRepository = new TipRepositoryJDBCImpl();

  @Before
  def startServer = {
    _server.run
  }

  @Given("^the tip \"([^\"]*)\" is existing$")
  def the_tip_is_existing(a_tip_id: String) {
    val tip = Tip.create("Tip 1", "This is the tip 1", "jyu")
    start_transaction
    _tipRepository.save_new_or_update(tip)
    commit_and_close_transaction
  }

  @Given("^I am on the page \"([^\"]*)\"$")
  def I_am_on_the_page(page: String) {
    _webDriver.get("http://localhost:8080/" + page)
  }

  @Given("^I input the content \"([^\"]*)\"$")
  def i_input_the_content(a_content: String) {
    _webDriver.findElement(By.id("tip_content")).clear()
    _webDriver.findElement(By.id("tip_content")).sendKeys(a_content)
  }

  @Given("^I input the title \"([^\"]*)\"$")
  def i_input_the_title(a_title: String) {
    _webDriver.findElement(By.id("tip_title")).clear()
    _webDriver.findElement(By.id("tip_title")).sendKeys(a_title)
  }

  @When("^I click the submit button$")
  def i_click_the_submit_button() {
    _webDriver.findElement(By.id("tip_submit")).submit()
  }

  @Then("^I should see the content \"([^\"]*)\"$")
  def i_should_see_the_content(a_content: String) {
    The list (getTextsFromElementsByClass("tip_content")) should_contain_the_element a_content
  }

  @Then("^I should see the title \"([^\"]*)\"$")
  def i_should_see_the_title(a_title: String) {

    The list (getTextsFromElementsByClass("tip_title")) should_contain_the_element a_title
  }

  @Then("^I should see the Not Found page$")
  def I_should_see_the_NOT_Found_page() {
    The string _webDriver.getPageSource should_contain "Not Found"
  }

  @Then("^the URL should be \"([^\"]*)\"$")
  def the_URL_should_be(a_url: String) {
    The string _webDriver.getCurrentUrl should_end_with a_url
  }

  @Then("^I should not be able to modify the author$")
  def i_should_not_be_able_to_modify_the_author() = {
    The string _webDriver.findElement(By.id("tip_author")).getAttribute("disabled") should_equal_to "true"
  }

  @After
  def stopServer = {
    _server.stop()
  }

  private def getElementByClassAndValue(a_class_name: String, a_value: String): Option[WebElement] = {
    var iterator = _webDriver.findElements(By.className(a_class_name)).iterator
    while (iterator.hasNext) {
      var el = iterator.next()
      if (a_value != null && a_value.equals(el.getText())) {
        return Some(el)
      }
    }
    None
  }

  private def getTextsFromElementsByClass(a_class_name: String): List[String] = {
    var _titles = List[String]();

    var iterator = _webDriver.findElements(By.className(a_class_name)).iterator
    while (iterator.hasNext) {
      _titles = iterator.next.getText :: _titles
    }
    return _titles
  }

}