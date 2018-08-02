#!/bin/bash

REPO_NAME=mosaic

REPO=162510209540.dkr.ecr.eu-west-1.amazonaws.com
REPO_ARN=$REPO/rood/$REPO_NAME


docker rmi -f $REPO_ARN

cd "$(dirname "$0")" # maak van huidige dir working dir
cd ..

sbt clean debian:packageBin
mkdir docker/$REPO_NAME/target
cp target/*deb docker/$REPO_NAME/target
docker build -t $REPO_ARN docker/$REPO_NAME
rm -rf docker/$REPO_NAME/target
