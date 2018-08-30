# --- !Ups

create extension if not exists hstore;

CREATE TABLE feedpointers
(
  feednaam VARCHAR NOT NULL PRIMARY KEY,
  entryid VARCHAR,
  pageurl VARCHAR
);

# --- !Downs

drop table feedpointers cascade;
