default : server

# Variables
DIST=dist
CC=gcc
CFLAGS= -Wall -I utils/ -I requests/

# Pattern rule for compiling .c files to .o files
$(DIST)/%.o : utils/%.c
	$(CC) $(CFLAGS) -c $< -o $@

$(DIST)/%.o : requests/%.c
	$(CC) $(CFLAGS) -c $< -o $@

$(DIST)/%.o : %.c
	$(CC) $(CFLAGS) -c $< -o $@ 

$(DIST)/%.o : log/%.c
	$(CC) $(CFLAGS) -c $< -o $@ 

# Target for building server executable
server: $(DIST)/server.o $(DIST)/verbose.o $(DIST)/peers.o $(DIST)/newClientListener.o $(DIST)/specificListener.o $(DIST)/waitingConnections.o $(DIST)/sigHandle.o $(DIST)/announce.o $(DIST)/any.o $(DIST)/look.o $(DIST)/stringUtils.o $(DIST)/fileAvailableList.o $(DIST)/getfile.o
	$(CC) $(CFLAGS) $^ -o $(DIST)/$@

install: server test
	mkdir -p install
	find $(DIST) -type f ! -name "*.o" -exec cp {} install \;

# Target for building test executable
test: testall

testall: $(DIST)/testall.o  $(DIST)/peers.o  $(DIST)/fileAvailableList.o
	$(CC) $(CFLAGS) $^ -o dist/$@

gdb: CFLAGS += -g
gdb: server

# Target for cleaning up object files
clean:
	rm -rf *.o $(DIST)/*.o
	rm -rf install
	rm -rf server $(DIST)/server

run: install
	./install/server

runtests: install
	./install/testall