#!/bin/bash
nohup java -jar rafty-runtest-1.0-SNAPSHOT.jar localhost:8521,localhost:8522,localhost:8523 100 1> rafty_test.out 2>&1 &