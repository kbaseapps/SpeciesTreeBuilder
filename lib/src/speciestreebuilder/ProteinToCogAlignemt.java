package speciestreebuilder;

public class ProteinToCogAlignemt {
	private String cogCode;
	private String featureId;
	private int coverage;
	private double evalue;
	private double bitscore;
	private double identity;
	private String alignedCogConsensus;
	private String alignedFeatureSeq;
	private String trimmedFeatureSeq;
	
	public ProteinToCogAlignemt() {
	}

	public String getCogCode() {
		return cogCode;
	}

	public void setCogCode(String cogCode) {
		this.cogCode = cogCode;
	}

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public int getCoverage() {
		return coverage;
	}

	public void setCoverage(int coverage) {
		this.coverage = coverage;
	}

	public double getEvalue() {
		return evalue;
	}

	public void setEvalue(double evalue) {
		this.evalue = evalue;
	}

	public double getBitscore() {
		return bitscore;
	}

	public void setBitscore(double bitscore) {
		this.bitscore = bitscore;
	}

	public double getIdentity() {
		return identity;
	}

	public void setIdentity(double identity) {
		this.identity = identity;
	}

	public String getAlignedCogConsensus() {
		return alignedCogConsensus;
	}
	
	public void setAlignedCogConsensus(String alignedCogConsensus) {
		this.alignedCogConsensus = alignedCogConsensus;
	}
	
	public String getAlignedFeatureSeq() {
		return alignedFeatureSeq;
	}
	
	public void setAlignedFeatureSeq(String alignedFeatureSeq) {
		this.alignedFeatureSeq = alignedFeatureSeq;
	}
	
	public String getTrimmedFeatureSeq() {
		return trimmedFeatureSeq;
	}
	
	public void setTrimmedFeatureSeq(String trimmedSeq) {
		this.trimmedFeatureSeq = trimmedSeq;
	}
}
