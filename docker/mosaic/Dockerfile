FROM 162510209540.dkr.ecr.eu-west-1.amazonaws.com/rood/java8

ENV DEBIAN_FRONTEND noninteractive

ADD powerUpWhenReady 	/
ADD target/*.deb        /tmp/app.deb

RUN chmod +x /powerUpWhenReady &&\
    dpkg -i /tmp/app.deb &&\
    rm -rf /tmp/* /var/tmp/*

EXPOSE 8080

CMD ["/powerUpWhenReady"]
