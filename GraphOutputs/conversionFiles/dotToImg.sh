#! /bin/bash

# Runs dot
# dotToImg [(rules||states),<filename.dot>]
# Alice Farquharson, 24/10/1010

# Get input from the command line, set up defaults
TOCONV="$1"
NULLVAL=""

# If no user input, convert all .dot files
if [ "$TOCONV" = "" ]; then
	TOCONV="all";
fi

# If 'all', convert all .dot files
if [ "$TOCONV" = "all" ]; then
	for file in $( find . -maxdepth 2 -name *.dot ) ; do 
		echo $file
		dot -Tpng -O $file
	done
	TOCONV="done"
fi

# If 'rules', convert all rule files (best guess)
if [ "$TOCONV" = "rules" ]; then
	for file in $( find . -maxdepth 2 -name *Action*.dot ) ; do 
		echo $file
		dot -Tpng -O $file
	done
	for file in $( find . -maxdepth 2 -name *Call*.dot ) ; do
		echo $file
		dot -Tpng -O $file
	done
	TOCONV="done"
fi

# If 'states', convert all state files (best guess)
if [ "$TOCONV" = "states" ]; then
	for file in $( find . -maxdepth 2 -name *state*.dot ) ; do 
		echo $file
		dot -Tpng -O $file
	done
	TOCONV="done"
fi

# If input matches a single filename, convert that
if [ "$TOCONV" != "done" ]; then
	file=$(find . -maxdepth 2 -name $TOCONV)
	dot -Tpng -O $file
	TOCONV="done"
fi

# Otherwise, print invalid error
if [ "$TOCONV" != "done" ]; then
	echo Use rules, states or <filename>.dot as an argument. To convert all
		 files, give no argument.
fi

exit 0

