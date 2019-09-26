package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Claim implements Comparable<LocalDate>{
	private String claimNum;
	private String claimDescription;
	private String claimAmount;
	private String claimDate;
	private ClaimStatus claimStatus;

	public Claim() {
	}

	@JsonCreator
	public Claim(@JsonProperty("claimNum") String claimNum, @JsonProperty("claimDescription") String claimDescription,
			@JsonProperty("claimAmount") String claimAmount, @JsonProperty("claimDate") String claimDate,
			@JsonProperty("claimStatus") ClaimStatus claimStatus) {
		super();
		this.claimNum = claimNum;
		this.claimDescription = claimDescription;
		this.claimAmount = claimAmount;
		this.claimDate = claimDate;
		this.claimStatus = claimStatus;
	}

	public String getClaimNum() {
		return claimNum;
	}

	public void setClaimNum(String claimNum) {
		this.claimNum = claimNum;
	}

	public String getClaimDescription() {
		return claimDescription;
	}

	public void setClaimDescription(String claimDescription) {
		this.claimDescription = claimDescription;
	}

	public String getClaimAmount() {
		return claimAmount;
	}

	public void setClaimAmount(String claimAmount) {
		this.claimAmount = claimAmount;
	}

	public String getClaimDate() {
		return claimDate;
	}

	public void setClaimDate(String claimDate) {
		this.claimDate = claimDate;
	}

	public ClaimStatus getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(ClaimStatus claimStatus) {
		this.claimStatus = claimStatus;
	}

	@Override
	public String toString() {
		return "Claim [claimNum=" + claimNum + ", claimDescription=" + claimDescription + ", claimAmount=" + claimAmount
				+ ", claimDate=" + claimDate + ", claimStatus=" + claimStatus + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((claimAmount == null) ? 0 : claimAmount.hashCode());
		result = prime * result + ((claimDate == null) ? 0 : claimDate.hashCode());
		result = prime * result + ((claimDescription == null) ? 0 : claimDescription.hashCode());
		result = prime * result + ((claimNum == null) ? 0 : claimNum.hashCode());
		result = prime * result + ((claimStatus == null) ? 0 : claimStatus.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Claim other = (Claim) obj;
		if (claimAmount == null) {
			if (other.claimAmount != null)
				return false;
		} else if (!claimAmount.equals(other.claimAmount))
			return false;
		if (claimDate == null) {
			if (other.claimDate != null)
				return false;
		} else if (!claimDate.equals(other.claimDate))
			return false;
		if (claimDescription == null) {
			if (other.claimDescription != null)
				return false;
		} else if (!claimDescription.equals(other.claimDescription))
			return false;
		if (claimNum == null) {
			if (other.claimNum != null)
				return false;
		} else if (!claimNum.equals(other.claimNum))
			return false;
		if (claimStatus != other.claimStatus)
			return false;
		return true;
	}

	@Override
	public int compareTo(LocalDate o) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("YYYY-MM-DD");
		return LocalDate.parse(claimDate, format).compareTo(o);
	}

}
