#!/bin/bash
mkdir -p bin
mkdir -p temp
cd ./temp

########### Blast #############
echo "Downloading blast..."
curl -o blast.tar.gz 'ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/2.2.29/ncbi-blast-2.2.29+-x64-linux.tar.gz'
tar -zxvf blast.tar.gz ncbi-blast-2.2.29+/bin/makeprofiledb ncbi-blast-2.2.29+/bin/rpsblast
mv ./ncbi-blast-2.2.29+/bin/makeprofiledb ../bin/makeprofiledb.linux
mv ./ncbi-blast-2.2.29+/bin/rpsblast ../bin/rpsblast.linux
rmdir ./ncbi-blast-2.2.29+/bin
rmdir ./ncbi-blast-2.2.29+
rm ./blast.tar.gz

cd ..
rmdir ./temp

########### FastTree ###########
cd ./bin
echo "Downloading FastTree..."
curl -o FastTree.linux 'http://www.microbesonline.org/fasttree/FastTree'
chmod +x ./FastTree.linux
