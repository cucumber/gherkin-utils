Feature: With sections commented out

  Background: a background
    Given a step

  Scenario: A scenario
    Given a step
    # And another step
    And some other step
    # And yet another step

#  Scenario: A scenario
#    Given a step

  Rule: A rule

#    Background: a background
#      Given a step

    Scenario Outline: A scenario
      Given a step <a>

#      Examples: Example 1
#        | a |
#        | b |

      Examples: Example 2
        | a |
        | b |
