# Claby UX

UX for the claby game (in repo mzero) in a browser. It allows human play (**Lapyrinthe**), or visualization of AI play (**AI world**)

The claby game: a simple game of eating fruits in a maze, avoiding unpasteurized cheese and moving enemies, with auto-generable maps & levels to clear.


## Setup

### Requirements ###
- Clojure & clojurescript
- Leiningen (who will take care of installing all other reqs)

For more info on requirements / dependencies and which version of what you need to install, see `project.clj`

### Installation
- Install by cloning this rep.


## Usage
- Start game server (backend) with ``lein run -m claby.ux.server/serve args`` where args are any kind of args that can be used to start a claby game in backend, see repo [mzero](https://github.com/sittingbull/mzero) for a list of those
- Start ux with niceties (sound, rabbits everywhere, animations) with ``lein fig:build-lapy``
- Start ux with minimal skin with ``lein fig:build-mini`` (intended for AI Game visualisation)

Both humans and AIs can play. By defaut AIs play. Use query parameter `player=human` to play yourself

### AI play
Press space bar to start / stop the AI player (chosen among multiple implementations in the game server args, see below)

### Human play
Move the player with arrow keys, or e - d - s - f keys. Game starts at level 1, and if the player clears all 6 predefined levels you will see the ending.

### Cheat codes
Cheat codes allow to start directly at a given level, or to slow down the enemies, by adding the query string `?cheatlev=X&tick=Y`

## Dev & deploy
UX tools and entry points for **lapyrinthe** are in ``claby.ux``

To get an interactive development environment run:

    lein fig:build-{mini|lapy}

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

	lein clean

To create a production build run:

	lein clean
	lein fig:prod


## Note - Mzero & Claby game
The code in this repo is a browser UX for the claby game in repo `mzero`. Check said repo for more info on the game.

Copyright © 2020 Philippe Rolet

Distributed under the Apache Public License 2.0
