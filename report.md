# Reddit DB Write Testing

This test file records loading the database with the entries from RC_2007-10.
The unconstrained table loading loaded to tables entirely without any restraints or keys.

The constrained tables loaded to tables with primary and foreign keys, and added indexes to the columns after insertion.

The tests using staging first loaded the unconstrained table, just as in the unconstrained tests, and then queried the database to transfer all unique entries from it to the constrained tables.

## Test with unconstrained tables 1

150429 records were inserted in 2128613191 nanoseconds (2.128613 seconds).

## Test with unconstrained tables 2

150429 records were inserted in 1627273481 nanoseconds (1.627273 seconds).

## Test with unconstrained tables 3

150429 records were inserted in 1710439529 nanoseconds (1.710440 seconds).

## Test with constrained tables 1

150429 records were inserted in 5511912457 nanoseconds (5.511912 seconds).

## Test with constrained tables 2

150429 records were inserted in 5461463188 nanoseconds (5.461463 seconds).

## Test with constrained tables 3

150429 records were inserted in 5438563900 nanoseconds (5.438564 seconds).

## Test using staging to unconstrained tables 1

150429 records were inserted in 6721791454 nanoseconds (6.721791 seconds).

## Test using staging to unconstrained tables 2

150429 records were inserted in 6684522572 nanoseconds (6.684523 seconds).

## Test using staging to unconstrained tables 3

150429 records were inserted in 6690314982 nanoseconds (6.690315 seconds).

