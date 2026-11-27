Feature: Indenting a comment retains relative spacing

  Comments are indented by trimming the indentation of the entire block of
  consecutive comments. This preserve the relative spacing within the comment.
  This is important for diagrams and ascii art.

  Scenario Outline: ASCII art
    Given an ASCII art <animal>
#                 /\_/\
#    This is cat ( o.o )
#                 > ^ <
#
    Examples:
      | animal |
      | cat    |
