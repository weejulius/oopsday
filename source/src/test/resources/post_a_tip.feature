Feature: post a tip
   As a user I want to post a tip so I can share the tip
   
   Scenario: index a tip
   		Given the tip "This is the tip 0" is existing
   		When I am on the page "tips/0"
   		Then I should see the title "Tip 0"
   		Then I should see the content "This is the tip 0"

   		When I am on the page "tips/1"
   		Then I should see the NOT Found page

   		When I am on the page "tips/Tip 0"
   		Then I should see the content "This is the tip 0"

   Scenario: update a tip
        Given I am on the page "tips/0/edit"
        And I input the content "This is a modified tip 0"
        When I click the button
        Then I should see the content "This is a modified tip 0"

   Scenario: input a tip
        Given I am on the page "tips"
        And I input the content "I am the content of tip"
        When I click the button
        Then I should see the content "I am the content of tip"

