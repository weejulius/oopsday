Feature: Posting
  Scenario: Posting
    Given I have input a tip "This is a very useful tip"
    When I post the tip
    Then I can see the tip