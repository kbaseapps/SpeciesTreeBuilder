FROM kbase/kbase:sdkbase.latest
MAINTAINER KBase Developer
# -----------------------------------------

# Insert apt-get instructions here to install
# any required dependencies for your module.

# RUN apt-get update
COPY ./deps/ /kb/deps
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
