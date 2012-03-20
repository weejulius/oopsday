package com.fishstory.oopsday.shared

import org.openqa.selenium.support.events.EventFiringWebDriver
import cucumber.annotation.After
import org.openqa.selenium.chrome.ChromeDriver


case class SharedDriver() extends EventFiringWebDriver(new ChromeDriver()) {

  @After
  override def close() {
    super.close();
  }
}