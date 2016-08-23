package speciestreebuilder;

import java.util.List;
import java.util.Map;

public class GenomeToCogsAlignment {
	private String genomeRef;
	private String genomeName;
	private Map<String, List<ProteinToCogAlignemt>> cogToProteins;
	
	public GenomeToCogsAlignment() {
	}

	public String getGenomeRef() {
		return genomeRef;
	}
	
	public void setGenomeRef(String genomeRef) {
		this.genomeRef = genomeRef;
	}
	
	public String getGenomeName() {
		return genomeName;
	}
	
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}
	
	public Map<String, List<ProteinToCogAlignemt>> getCogToProteins() {
		return cogToProteins;
	}
	
	public void setCogToProteins(
			Map<String, List<ProteinToCogAlignemt>> cogToProteins) {
		this.cogToProteins = cogToProteins;
	}
}
