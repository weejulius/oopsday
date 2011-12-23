Feature: post a tip
   As a user I want to post a tip so I can share the tip
   
   Scenario: post a tip
   		Given the tip 1 is exiting
   		When the user open the link "tip/1"
   		Then the user see "This is a tip"  