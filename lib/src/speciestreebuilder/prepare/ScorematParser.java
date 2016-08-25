package speciestreebuilder.prepare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScorematParser {

    public static String getDomainTypeForScorematFile(File f) {
        String ret = f.getName();
        int pos = 0;
        while (Character.isLetter(ret.charAt(pos)))
            pos++;
        return ret.substring(0, pos);
    }

    public static List<String> readFile(File smpFile) throws IOException {
        List<String> ret = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(smpFile));
        while (true) {
            String l = br.readLine();
            if (l == null)
                break;
            ret.add(l);
        }
        br.close();
        return ret;
    }

    public static String searchTitle(List<String> scoremat) {
        return searchText(scoremat, "title", true);
    }

    public static String searchConsensus(File smpFile) throws IOException {
        return searchConsensus(readFile(smpFile));
    }

    public static String searchConsensus(List<String> scoremat) {
        return searchText(scoremat, "seq-data ncbieaa", false);
    }

    public static String searchText(List<String> scoremat, String prefix, boolean space) {
        StringBuilder ret = null;
        for (String line : scoremat) {
            line = line.trim();
            if (ret == null) {
                if (line.startsWith(prefix + " \"")) {
                    ret = new StringBuilder();
                    ret.append(line.substring(prefix.length() + 2));
                }
            } else {
                if (space)
                    ret.append(' ');
                ret.append(line);
            }
            if (ret != null && ret.length() > 0 && ret.charAt(ret.length() - 1) == '\"') {
                ret.deleteCharAt(ret.length() - 1);
                break;
            }
        }
        if (ret == null)
            throw new IllegalStateException("Can not find text for: " + prefix);
        return ret.toString().trim();
    }

}
