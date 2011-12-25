package com.fishstory.oopsday.interfaces.tipping

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import com.fishstory.oopsday.shared.The
import cucumber.annotation.Before
import cucumber.annotation.After
import cucumber.annotation.en.{When, Given, Then}
import unfiltered.jetty.{Server, Http}

class Steps_Tip {
  private var _webDriver: WebDriver = null
  private var _tip: String = null
  private var _server: Server = null

  @Before
  def startServer = {
    _server = Http(8080).plan(new TipFace)
    _server.start
    _webDriver = new HtmlUnitDriver()
  }

  @Given("^the tip \"([^\"]*)\" is existing$")
  def the_tip_is_existing(a_tip: String) {
    _tip = a_tip
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

  @When("^I click the button$")
  def i_click_the_button() {
    _webDriver.findElement(By.id("tip_submit")).submit()
  }

  @Then("^I should see the content \"([^\"]*)\"$")
  def i_should_see_the_content(a_content: String) {
    The string (_webDriver.findElement(By.id("tip_content")).getText) should_equal_to a_content
  }

  @After
  def stopServer = {
    _server.stop()
    _webDriver.quit()
  }

}