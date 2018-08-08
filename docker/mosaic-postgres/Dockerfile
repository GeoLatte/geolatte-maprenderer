FROM 162510209540.dkr.ecr.eu-west-1.amazonaws.com/rood/postgres96

ENV PGPASSWORD docker

USER root

RUN /etc/init.d/postgresql start 9.6 &&\
    psql -U docker -d postgres -h 0.0.0.0 --command "CREATE USER mosaic WITH SUPERUSER PASSWORD 'mosaic';" &&\
    createdb -E UTF8 -U docker -h 0.0.0.0 -O mosaic mosaic --template template0  &&\
    psql -U docker -d mosaic -h 0.0.0.0 --command "create extension hstore; create extension postgis;" &&\
    createdb -E UTF8 -U docker -h 0.0.0.0 -O mosaic mosaic-test --template template0 &&\
    psql -U docker -d mosaic-test -h 0.0.0.0 --command "create extension hstore; create extension postgis;" &&\
    /etc/init.d/postgresql stop &&\
    rm -f /tmp/*gz

USER postgres

# Set the default command to run when starting the container
CMD ["/usr/lib/postgresql/9.6/bin/postgres", "-D", "/var/lib/postgresql/9.6/main", "-c", "config_file=/etc/postgresql/9.6/main/postgresql.conf"]
