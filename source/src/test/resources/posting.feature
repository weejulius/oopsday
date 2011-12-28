Feature: Posting
  Scenario: Posting
    Given the visitor is on the index page
    When the visitor clicks the <share> link
    Then the visitor should see a text box to input tip

    Given the visitor inputs " This is a very useful tip"
    When the visitor clicks the <save> link
    Then the visitor can see a tip " This is a very useful tip"

    When the visitor clicks the <

       Scenario: input a invalid tip
            Given I am on the page "tips/new"
            And I input the content "I am the content of tip"
            When I click the submit button
            Then I should see the message "Title is a must"

            When I input the title "this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title"
            And I click the submit button
            Then I should see the message "Title should be less than 50"

            When I input the title " a new title"
            And I input the content "this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a t  this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a t     this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a t"
            Then I should see the message "Content should be less than 88"

