# Reddit DB Write Testing

This file loaded the data from files RC\_2007-10 and RC\_2012-12.

Due to 26 million entries being several times more than the lock limit of around 6 million entries the tests using staging broke entirely as the database doesn't do any batching unless explicitly told to do so.

## Test with unconstrained tables 1

26 230 705 records were inserted in 344324259365 nanoseconds (344.324249 seconds).

## Test with unconstrained tables 2

26 230 705 records were inserted in 451 297 670 012 nanoseconds (451.297668 seconds).

## Test with unconstrained tables 3

26 230 705 records were inserted in 407 499 313 316 nanoseconds (407.499298 seconds).

## Test with constrained tables 1

26 230 705 records were inserted in 3 012 413 606 794 nanoseconds (3012.413574 seconds).

## Test with constrained tables 2

26 230 705 records were inserted in 3 057 637 266 909 nanoseconds (3057.637451 seconds).

## Test with constrained tables 3

26 230 705 records were inserted in 3 076 477 444 264 nanoseconds (3076.477539 seconds).

## Test using staging to unconstrained tables 1

0 records were inserted in 5 943 411 164 082 nanoseconds (5943.411133 seconds).

## Test using staging to unconstrained tables 2

0 records were inserted in 6 164 319 000 523 nanoseconds (6164.318848 seconds).

## Test using staging to unconstrained tables 3

0 records were inserted in 6 018 231 795 668 nanoseconds (6018.231934 seconds).

