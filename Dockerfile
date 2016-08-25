FROM kbase/kbase:sdkbase.latest
MAINTAINER KBase Developer
# -----------------------------------------

# Insert apt-get instructions here to install
# any required dependencies for your module.

# RUN apt-get update
RUN mkdir -p /kb/deps
COPY ./deps/download_3rd_party_bins.sh /kb/deps/
WORKDIR /kb/deps
RUN ./download_3rd_party_bins.sh

RUN . /kb/dev_container/user-env.sh && \
  cd /kb/dev_container/modules && \
  rm -rf jars && \
  git clone https://github.com/kbase/jars && \
  rm -rf kb_sdk && \
  git clone https://github.com/kbase/kb_sdk -b develop && \
  cd /kb/dev_container/modules/jars && \
  make && make deploy && \
  cd /kb/dev_container/modules/kb_sdk && \
  make && make deploy && \
  echo 1000

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
