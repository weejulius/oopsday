Feature: Posting
  Scenario: Posting
    Given the visitor is on the index page
    When the visitor clicks the <share> link
    Then the visitor should see a text box to input tip

    Given the visitor inputs " This is a very useful tip"
    When the visitor clicks the <save> link
    Then the visitor can see a tip " This is a very useful tip"

    When the visitor clicks the <