 #!/bin/bash

# Définition du répertoire du projet et du répertoire de copie
ORIGINAL_DIR="/chemin/vers/le/projet_original"
CLONE_DIR="/chemin/vers/le/projet_clone"

# uncomment if you have problems like me
# pkill -9 "java"

# wipes the content of logging files from previous log, or creates it if doesn't exists
echo -n > "$ORIGINAL_DIR/client/src/logger/logging.log"


gnome-terminal --  /bin/bash -c "cd '$ORIGINAL_DIR/client'; make run_mtracker ; exec bash"

sleep 2
gnome-terminal --  /bin/bash -c "cd '$ORIGINAL_DIR/client'; make run ; exec bash"

gnome-terminal --  /bin/bash -c "cd '$CLONE_DIR/client'; make run ; exec bash"
