package us.kbase.common.utils;

public class Alignment {
    public String[] protNames;
    public String[] alignSeqs;
    public int alignLen;

    public Alignment(String[] protNames, String[] alignSeqs, int alignLen) {
        this.protNames = protNames;
        this.alignSeqs = alignSeqs;
        this.alignLen = alignLen;
        checkSequencesLengthEquality();
    }

    public Alignment(String[] protNames,String[] alignSeqs) {
        this(protNames, alignSeqs, alignSeqs[0].length());
        checkSequencesLengthEquality();
    }

    private void checkSequencesLengthEquality() {
        // Check for equality of aligned sequences
        for(int i=1;i< alignSeqs.length;i++) {
            if(alignSeqs[i].length()!= alignSeqs[0].length())
                throw new RuntimeException("Alignment has sequences of different length");
        }
    }
}
