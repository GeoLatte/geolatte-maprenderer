#!/bin/bash

REPO_NAME=mosaic-postgres

REPO=162510209540.dkr.ecr.eu-west-1.amazonaws.com
REPO_ARN=$REPO/rood/$REPO_NAME

docker rmi -f $REPO_ARN

cd "$(dirname "$0")" # maak van huidige dir working dir
cd ..

docker build -t $REPO_ARN docker/$REPO_NAME
