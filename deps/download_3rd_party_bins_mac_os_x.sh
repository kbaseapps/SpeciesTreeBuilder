#!/bin/bash
mkdir -p ../bin
mkdir -p temp
cd ./temp

########### Blast #############
echo "Downloading blast..."
#curl -o blast.tar.gz 'ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/2.2.29/ncbi-blast-2.2.29+-universal-macosx.tar.gz'
tar -zxvf blast.tar.gz ncbi-blast-2.2.29+/bin/makeprofiledb ncbi-blast-2.2.29+/bin/rpsblast
mv ./ncbi-blast-2.2.29+/bin/makeprofiledb ../../bin/makeprofiledb.macosx
mv ./ncbi-blast-2.2.29+/bin/rpsblast ../../bin/rpsblast.macosx
rmdir ./ncbi-blast-2.2.29+/bin
rmdir ./ncbi-blast-2.2.29+
rm ./blast.tar.gz

########### FastTree ###########
echo "Downloading FastTree..."
curl -o FastTree.c 'http://www.microbesonline.org/fasttree/FastTree.c'
gcc -O3 -finline-functions -funroll-loops -Wall -o FastTree.macosx FastTree.c -lm
mv ./FastTree.macosx ../../bin/
rm ./FastTree.c

cd ..
rmdir ./temp
