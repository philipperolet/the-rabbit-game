- Server should return a world

- NNs are 
- very good learning power for those
  - so 3 even if simple algo
- example of low sophistication but moderately high learning power
- speed
- ease of us





# Legacy
- update claby with last version of mzero-game
- get naming right
- option to see artificial players play
- option to on/off niceties (sound / transitions) 
- clean docs appropriately
  - README.md
  - project.clj
  - project.md

## Backlog
- option to store game & player data in a db for random player
- * for exhaustive player
- tools to inspect games

## Changelog
### 1.0.0 - UX for AI & Human gaming
- ref: remove code migrated to mzero and add mzero dependency
- feat: visualize basic claby games with ai players (fixed parameters)
- feat: ability to choose human / ai play
- feat: command-line like input to adjust params for ai players
- doc: clean readme
- feat: run the game auto at tick move. Enter used to start / pause. Space to move once when game paused.
- feat: visualize any claby games with ai players (incl. cheese & enemies)
- fix: auto speed increase / enemy movement (for human) / changed keys (for ai)


### v0.2.1
- refactor player cli options & player creation to fit new TE-player
+ added tree-exploration-player
- added modsubvec, check-spec & scaffold (to utils), get-player-senses (to player)

### AI World v0.2.0
+ refactor player dependency injection to not require any more coding, + docs
+ refactor world / main / game-runner to extract threaded-timing logic and doc
+ refactor tests to grasp default args and work on all game runners, + add tests for all game runners
+ add watcher-based runner
+ make parallel computing of experiment work
+ make script to run experiment with  many rand games, compare avg step number 
+ monothread runner (very fast) | x logging level works all the time
+ option to disable logging
+ player bug stops the game and shows error message
+ Make player implementation selection for the algorithm easy
+ Refactoring pass
+ ref: random player via an interface
+ add: non-random player - spans all the board algorithmically
+ Missteps in world state string
+ for non-interactive mode, adjustable log rate--defaulting to no log
- ref: full-state -> world-state 
- Documenter le fonctionnement souhaité dans README.md, main/world/player.clj
- préparer le backlog de la v0.2.0 pour qu'il aille à ce fonctionnement

### AI Game v0.1
#### v0.1.1 ####
- refactoring pour séparer setup & run et être plus FCIS
- régler le pb des tests

#### v0.1.0 - DONE
- possibilité de pauser / reprendre la partie, et de faire du step-by-step
- documenter et passer à la version suivante
- permettre l'ajustement de la quantité de murs
- lancer interactivement avec par défaut un affichage toutes les 2s
- mettre un log de début & un de fin
- passer l'affichage en log
- rationnaliser les args
- test & code d'une partie
- visualisation logguée clean du jeu
- jeu possible avec player aléatoire qui bouge toutes les 2 secondes
- créer la spec movement
- test & code d'une itération de jeu
- test & code d'une itération de joueur

#### base
- Document what it should do in README.md
- commande lein qui démarre le main thread
- démarre le game thread qui loggue l'état initial et affiche un message toutes les secondes

### Lapyrinthe v1
- Fix bug on level 3

### General
- séparer les codes de jeu humain vs jeu machine
- ajouter env d'exec mini
- test whether board creation is faster in java or clojure
- faire bouger les ennemis
- ajouter un level apéro, 1 apéro + fromage, 1 2* apéro + covid
- permettre la détection de fruits / lapins bloqués
- ajouter des ennemis statiques
- message de victoire
- gestion pour être toujours plein écran
- bloquer sur mobile
- affichage du level
- animation du message de niveaux
- cut game in multiple files (same namespace?)
- make tests faster
- instrument macro instruments only functions of namespace, not others
- board a ses éléments en fonction du niveau
- ajouter un système de passage de niveaux
- ajouter un level fromage
- victoire si tous fruits mangés
- animation de victoire
- mort si fromage & animation si mort
- possibilité de semer du fromage
- nouvelle case fromage, dans le html comme dans le compte
- scroll au bon moment
- passer le test get-html-for-state de cljs à clj
- centrer le lapin
- éviter 2e lancement musique
- message arrowkeys
- make it beautiful
- wall functions : generate random wall, add wall to board
- fruit functions : add random fruit, sow fruits accross board
- added create-nice-board
- refactoring of initial game state creation (incl. extract empty-board function)
- upgraded dependencies to later versions of clojure & reagent (req. to fix some issues)
- made generation.cljc working on cljs env
- made core.cljs compatible with reagent 0.10.0
- added find-in-board function 
- added "staging" build that does not include tests (dev build now not easily usable)
- split game in multiple files
- macro to run appropriately all spec checks
- wall generation & fruit sowing for board
- create game weird board is confined to tests
- score counter
- eat capability
- instrument functions during testing & dev
- prevent player from existing on wall on the board in specs
- wall capabilities and tests
- up action
- l,r,d action
- test all specs once in core
- remove game size from specs, put it as arg of game creation
- react component showing the state (incl. adding keys to table elts)
- refactor html-gen code to limit arg nb & add gen-html-cell
- turn matrix into HTML board
- setup testing workflow
- create game with empty board, working test
- allow magit auth with 2fa outside emacs
- Hello world in a browser

## Icebox
- remove libs from vcs to avoid useless commits / searchs?
- tab should autocomplete
- add walls & fruits with clicks
- let tests be pretty-printed
- convenient way of having tests in emacs
