Dépot du projet de Réseaux S8

- BARBARIN Paul
- COMITI Santinu
- DUCAMP Simon
- MOREL Mathieu
- MORENO CARPIO Kenzo


Lien du sujet :
https://docs.google.com/document/d/1BfxOFxFEg_u2H1JdQ1tFuR4dgp0Z8UvISHsogZ6ZslY/edit


Procédure pour tester l'échange pair à pair :
- Copier une deuxième version du projet pour avoir un deuxième client C2
- Supprimer le fichier testFile.tex de C1 (qui contient un Lorem Ipsum) : c'est ce fichier qu'on va essayer d'échanger
- Lancer le serveur via "make clean && make install && make run". S'il ne fonctionne pas, lancer le mock tracker depuis le dossier client avec "make run_mtracker"
- Lancer le client C2 avec "make run"
- Lancer le client C1 avec "make run"
- Sur C1, exécuter 'look [filename="testFile.tex"]', récupérer la clé $key
- Sur C1, exécuter 'getfile $key', cela connecte C1 à C2
- Sur C1, exécuter 'interested $key', cela télécharge les données du fichier
- Sur C1, vérifier le contenu du fichier