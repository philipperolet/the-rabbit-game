# Mzero Game GUI

This is a ClojureScript GUI for the [Mzero Game](https://github.com/sittingbull/mzero-game). 

The GUI can be tried here: http://pyrinthe.filou.tech/index.html (chrome, chromium-based or firefox browsers, **not** mobile compliant).

For details about the game itself, please read the [Mzero Game README](https://github.com/sittingbull/mzero-game). 

## Setup

### Requirements ###
- Clojure & clojurescript v1.10.1 or above
- Leiningen v2.7.8 or above

Other requirements / dependencies will be installed by leiningen, see the [lein project file](project.clj)

### Installation
```
git clone https://github.com/sittingbull/mzero-game-gui.git
cd mzero-game-gui
```

## Usage
Meant for local use, not remote use.

- Start game server (backend) with ``lein run -m claby.ux.server/serve args`` where args is a string of command-line args such as those described for the [Mzero Game CLI](https://github.com/sittingbull/mzero-game);

- The following will launch a browser with the game frontend--it will only work if the backend has been started

```
lein fig:build-lapy  # nice GUI (with sounds, rabbits everywhere, animations)
lein fig:build-mini  # minimal, faster GUI ; intended for AI Game visualisation
```

Both humans and AIs can play. **By defaut AIs play**. Use query parameter `player=human` for human play.

### AI play : Spacebar & 'n' key
- Press `Spacebar` to start / stop the AI player (chose among multiple implementations via args given to the server, see above). 

- When stopped, press `n` to let it move step-by-step.

### Human play
See the [Mzero Game Intro](https://github.com/sittingbull/mzero-game) for rules & explanations.

## Code quality
The code in this repo won the 2020 [Pigsty Worst Practices Awards](pigsty-wpa.md).

## Dev & deploy
GUI tools and entry points for **lapyrinthe** are in ``claby.ux``

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

## Licence
Copyright © 2020 Philippe Rolet

Distributed under the Apache Public License 2.0
