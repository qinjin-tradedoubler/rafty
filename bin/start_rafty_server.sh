#!/bin/bash
nohup java -jar rafty-server-1.0-SNAPSHOT.jar localhost:8521,localhost:8522,localhost:8523 1 1> rafty_server.out 2>&1 &
nohup java -jar rafty-server-1.0-SNAPSHOT.jar localhost:8522,localhost:8521,localhost:8523 2 1> rafty_server.out 2>&1 &
nohup java -jar rafty-server-1.0-SNAPSHOT.jar localhost:8523,localhost:8521,localhost:8522 3 1> rafty_server.out 2>&1 &