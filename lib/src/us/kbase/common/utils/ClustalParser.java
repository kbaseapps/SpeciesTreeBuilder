package us.kbase.common.utils;

import java.io.*;
import java.util.*;

public class ClustalParser {
	public static final String FORMAT_NAME = "CLUSTAL";

	public static String getFormatName() {
		return FORMAT_NAME;
	}

	public static Alignment parse(String text) throws Exception {
		return parse(text,FORMAT_NAME);
	}

	public static Alignment parse(String text, String startWord) throws Exception {
		return parse(new BufferedReader(new StringReader(text)), startWord);
	}

	public static Alignment parse(BufferedReader br, String startWord) throws Exception {
		try {
			for(;;) {
				String str = br.readLine();
				if (str == null)
					throw new Exception("Start word (" + startWord + ") is not found");
				if (startWord == null && str.trim().length() > 0)
					break;
				if (str.trim().toUpperCase().startsWith(startWord))
					break;
			}
			int captLen = -1;
			List<String> nameList = new ArrayList<String>();
			List<StringBuilder> seqList = new ArrayList<StringBuilder>();
			StringBuilder specSb = new StringBuilder();
			for (int blockNum = 0; ; blockNum++) {
				int readPos = 0;
				for (;;) {
					String line = br.readLine();
					if (line == null)
						break;
					if (line.trim().length() == 0 && readPos == 0)
						continue;
					if (captLen < 0) {
						captLen = line.indexOf(' ');
						while ((captLen < line.length()) && (line.charAt(captLen) == ' '))
							captLen++;
					}
					{
						if(line.length() <= captLen) 
							break;
						String protName = line.substring(0, captLen).trim();
						if (protName.length() == 0) {
							if (readPos == 0)
								throw new Exception("Empty protein name");
							if (readPos == nameList.size())
								specSb.append(line.substring(captLen));
							break;
						}
						String seqPart = line.substring(captLen).trim();
						if (seqPart.length() == 0)
							throw new Exception("No amino acids in the sequence");
						if (blockNum == 0) {
							nameList.add(protName);
							seqList.add(new StringBuilder(seqPart));
						} else {
							if (readPos >= nameList.size())
								throw new Exception("Different proteins quantity in blocks");
							if (!nameList.get(readPos).equals(protName))
								throw new Exception("Wrong order of proteins in blocks");
							seqList.get(readPos).append(seqPart);
						}
						readPos++;
					}
				}
				if (readPos == 0) 
					break;
				if (readPos != nameList.size())
					throw new Exception("Different proteins quantity in blocks");
			}
			String[] protNames = new String[nameList.size()];
			String[] alignSeqs = new String[nameList.size()];
			for (int i=0; i<protNames.length; i++) {
				protNames[i] = nameList.get(i);
				alignSeqs[i] = seqList.get(i).toString();
			}
			return new ClustalAlignment(protNames, alignSeqs, specSb.toString());
		} finally {
			br.close();
		}
	}
}
