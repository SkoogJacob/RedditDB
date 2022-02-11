# RedditDB Loader

This repository contains source code for loading comment data
into a mariaDB database.

The program can be run to test loading times and output the results to a markdown file,
or it can be run to load a database and set up some views, procedures, and functions.

The program's execution is controlled by commandline arguments.

## Arguments

### `--schema`

This argument shows that the next argument is the intended name of the schema
in the database. If the schema doesn't exist it will be created.

**This argument is mandatory unless the first argument is ```--only-load-procedures```**.

### `--only-load-procedures`

Has to be given as the first argument to have effect. Indicates that the user only wants to
add procedures, views, and functions to an already existing schema with existing tables.

When the command is called with this flag as the initial flag the second argument must be the schema name,
and the second argument must be given.

If the schema doesn't exist the program will exit with an SQL error.

### `--test-output`

This argument tells the program that the next argument will be a file path to write test results to.
When this argument is given the program will run in testing mode, where it tries to load the data
multiple times using some different methods to compare the time taken and whether all data was added.

### Other Arguments

All other arguments that are not given together with any of the flags above are assumed to be file paths
to the files containing the reddit data. The file paths should be absolute or relative to `$PWD`.
