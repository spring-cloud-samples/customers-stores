FROM    dsyer/maven

MAINTAINER dsyer@pivotal.io

USER user
WORKDIR /home/user/scripts

RUN sed -i -e 's/answer=false/answer=true/' /home/user/.gvm/etc/config && bash -lc 'gvm install springboot 1.1.5.RELEASE'

CMD bash -c 'echo "Hello World"'