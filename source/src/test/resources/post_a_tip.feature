Feature: post a tip
   As a user I want to post a tip so I can share the tip
   
   Scenario: index a tip
   		Given the tip "This is the tip 1" is existing
   		When I am on the page "tips/1"
   		Then I should see the title "Tip 1"
   		Then I should see the content "This is the tip 1"

   		When I am on the page "tips/2"
   		Then I should see the Not Found page

   		When I am on the page "tips/Tip 1"
   		Then I should see the content "This is the tip 1"
   		
   		When I am on the page "tips/1/2"
   		Then I should see the Not Found page

   Scenario: update a tip
        Given I am on the page "tips/1/edit"
        And I input the content "This is a modified tip 1"
        When I click the button
        Then I should see the content "This is a modified tip 1"
        And the URL should be "tips/1"
        
        Given I am on the page "tips/2/edit"
        Then I should see the Not Found page

   Scenario: input a tip
        Given I am on the page "tips/new"
        And I input the title "a new tip"
        And I input the content "I am the content of tip"
        When I click the button
        Then I should see the tile "a new tip"
        Then I should see the content "I am the content of tip"
        And the URL should be "tips/2"
   
   Scenario: input a invalid tip
        Given I am on the page "tips/new"
        And I input the content "I am the content of tip"
        When I click the button
        Then I should see the message "Title is a must"
        
        When I input the title "this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title"
        And I submit
        Then I should see the message "Title should be less than 50"
        
        When I input the title " a new title"
        And I input the content "this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a t
        this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a t
        this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a t
        this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a t
        this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a t
        this is a title a title a title a title a title a title a title a title a title a title a title a title a title a title a title a t"
        Then I should see the message "Content should be less than 88
        
   Scenario: list tips
        Given I am on the page "tips"
        Then I should see the tip 1
        
        Given I am on the page "tips/new"
        And I input the title "a new tip"
        And I input the content "This is a new tip"
        And I submit
        When I am on the page "tips"
        Then I should see the tip "tip 1"
        And I should see the tip "a new tip"     

