# The Rabbit Game

### [The Rabbit Game](https://game.machine-zero.com) - A game cute for humans and tough for AIs

Discover various kinds of artificial intelligence algorithms by watching them play a simple game: a rabbit eating strawberries in a maze (humans can play too 😉). Play it [here](https://game.machine-zero.com).

**Note**: The game was tested on Chrome (& chromium-based) and firefox browsers; it is not yet mobile compliant unfortunately.

This repository contains all the frontend/backend for the game website. Core libs for the game engine, along with artificial players code and a CLI, can be found [here](https://github.com/philipperolet/the-rabbit-game-libs). Deep-learning based players (work in progress) are [here](https://github.com/philipperolet/trg-players).

## Purpose
The goal of The Rabbit Game is to demystify artificial intelligence
   a little, by watching how machines play the game. But first, you
   can play it a little yourself to see how it works, get a good score
   and try all the various levels.

Then, you can let artificial intelligences play. Check out the
    8 different artificial players; each has its own style determined
    by its stats--its strong and weak points (click on a stat to see
    what it means).

Have different AIs play on different levels. Each new level
   introduces something new that will make it tougher for AIs. See
   which ones get far, which one fail, and why they behave like
   this (click on \"Learn more about me\" to understand an AI's
   behaviour).

Additionnally, if you're a hacker, you can try to code an
    algorithm to go to the highest possible level. If it clears the
    last level, which is quite hard, you can win *a lot* of internet
    points (really awful lot).

Learn more about the story behind the game and why it's interesting for AI research [here](TODO)
## Setup

### Requirements ###
- Clojure & clojurescript v1.10.1 or above
- Leiningen v2.7.8 or above

Other requirements / dependencies will be installed by leiningen, see the [lein project file](project.clj)

### Installation
Meant for local use, not remote use.
```
git clone https://github.com/sittingbull/the-rabbit-game.git
cd the-rabbit-game
```
- Start game server (backend) with ``lein run -m claby.ux.server/serve args`` where args is a string of command-line args such as explaine in the  [TRG libs CLI](https://github.com/philipperolet/trg-libs);

- The following will launch a browser with the game frontend--it will only work if the backend has been started

```
lein fig:build-lapy
```
## Dev & deploy
GUI tools and entry points are in ``claby.ux``

To get an interactive development environment run:

    lein fig:build-lapy

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

## Licence
Copyright © 2020 Philippe Rolet

Distributed under the Apache Public License 2.0
