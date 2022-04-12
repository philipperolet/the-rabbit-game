#!/bin/bash
install_dir="/home/ubuntu/the-rabbit-game"
cd $install_dir
lein run -m claby.ux.server/serve 2>> /home/ubuntu/the-rabbit-game-server.err >> /home/ubuntu/the-rabbit-game-server.log </dev/null &
sudo shutdown -h +60

