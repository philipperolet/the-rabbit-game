#!/bin/bash
lein run -m claby.ux.server/serve 2>> ~/server-err.log >> ~/server-out.log </dev/null &

tail -f ~/server-out.log ~/server-err.log 
