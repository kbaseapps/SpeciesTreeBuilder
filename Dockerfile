FROM kbase/kbase:sdkbase.latest
MAINTAINER KBase Developer
# -----------------------------------------

# Insert apt-get instructions here to install
# any required dependencies for your module.

# update java
RUN apt-get update --fix-missing \
	&& sudo apt-get install --reinstall ca-certificates \
	&& add-apt-repository ppa:openjdk-r/ppa \
	&& sudo apt-get update \
	&& sudo apt-get -y install openjdk-8-jdk \
	&& echo java versions: \
	&& java -version \
	&& javac -version \
	&& echo $JAVA_HOME \
	&& ls -l /usr/lib/jvm \
	&& cd /kb/runtime \
	&& rm java \
	&& ln -s /usr/lib/jvm/java-8-openjdk-amd64 java \
	&& ls -l

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

# update jars
RUN cd /kb/dev_container/modules/jars \
	&& git pull \
	&& . /kb/dev_container/user-env.sh \
	&& make deploy

RUN mkdir -p /kb/deps
COPY ./deps/download_3rd_party_bins.sh /kb/deps/
WORKDIR /kb/deps
RUN ./download_3rd_party_bins.sh

# -----------------------------------------

COPY ./ /kb/module
RUN mkdir -p /kb/module/work
RUN chmod 777 /kb/module

WORKDIR /kb/module

RUN mkdir -p bin
RUN cp /kb/deps/bin/* ./bin/

RUN make all

ENTRYPOINT [ "./scripts/entrypoint.sh" ]

CMD [ ]
