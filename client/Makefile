# Makefile for compiling Java files and running the main class

# Compiler
JC = javac

# Flags
JFLAGS = -d dist -cp "lib/commons-cli-1.6.0.jar"

# Source directories
SRCDIRS = src \
			src/client_class \
			src/client_class/classes \
			src/commands \
			src/config \
			src/connect \
			src/datatypes \
			src/logger \
			src/utils
			
TSTDIRS = tst \
			tst/mocks  \
			tst/datatypes  \
			tst/logger \
			tst/utils

# Source files
SRCS = $(wildcard $(addsuffix /*.java,$(SRCDIRS)))
MCKS = $(wildcard $(addsuffix /*.java,$(TSTDIRS)))

# Phony targets
.PHONY: all clean run doc

# Default target
all: 
	$(JC) $(JFLAGS) $(SRCS) $(MCKS)

# Run the main class
run: all
	java -cp dist:lib/commons-cli-1.6.0.jar src.PeerToPeerApp $(ARGS)

# Launches tests
test: all
	java -cp dist:lib/commons-cli-1.6.0.jar tst.Test $(ARGS)

# Runs mock tracker
run_mtracker: all
	java -cp dist:lib/commons-cli-1.6.0.jar tst.mocks.MockTracker

# Runs mock which doesn't answer
run_mnoans: all
	java -cp dist:lib/commons-cli-1.6.0.jar tst.mocks.MockTrackerNoAns

# Generate documentation
doc:
	doxygen Doxyfile

# Clean target
clean:
	$(RM) dist/*.class
