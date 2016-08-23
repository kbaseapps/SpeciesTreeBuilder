package us.kbase.common.utils;

public class ClustalAlignment extends Alignment {
    public String infoLine;

    public ClustalAlignment(String[] protNames,String[] alignSeqs, String infoLine) {
        super(protNames, alignSeqs, infoLine.length());
        this.infoLine = infoLine;
    }
}
