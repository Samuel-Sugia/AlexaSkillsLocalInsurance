package models.insurancePolicies;

import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.User;

public class DentalInsurance implements InsurancePolicy {
	@JsonProperty("policyNum")
	private String policyNum;
	@JsonProperty("policyType")
	private String policyType;
	@JsonProperty("deductible")
	private String deductible;
	@JsonProperty("outOfPocket")
	private String outOfPocket;
	@JsonProperty("familyPlan")
	private Boolean familyPlan;

	public DentalInsurance() {
	}

	public DentalInsurance(boolean filler) {
		Random rand = new Random();
		this.policyNum = "D" + rand.nextInt(3000);
		this.policyType = "Dental";
		this.deductible = String.valueOf(rand.nextInt(30)).concat("00");
		this.outOfPocket = String.valueOf(rand.nextInt(3000)) + String.valueOf(rand.nextDouble());
		this.familyPlan = rand.nextBoolean();
	}

	public DentalInsurance(String policyNum, String policyType, String deductible, String outOfPocket,
			boolean familyPlan, List<User> dependents) {
		super();
		this.policyNum = policyNum;
		this.policyType = policyType;
		this.deductible = deductible;
		this.outOfPocket = outOfPocket;
		this.familyPlan = familyPlan;
	}

	public String getPolicyNum() {
		return policyNum;
	}

	public void setPolicyNum(String policyNum) {
		this.policyNum = policyNum;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getDeductible() {
		return deductible;
	}

	public void setDeductible(String deductible) {
		this.deductible = deductible;
	}

	public String getOutOfPocket() {
		return outOfPocket;
	}

	public void setOutOfPocket(String outOfPocket) {
		this.outOfPocket = outOfPocket;
	}

	public Boolean getFamilyPlan() {
		return familyPlan;
	}

	public void setFamilyPlan(Boolean familyPlan) {
		this.familyPlan = familyPlan;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deductible == null) ? 0 : deductible.hashCode());
		result = prime * result + ((familyPlan == null) ? 0 : familyPlan.hashCode());
		result = prime * result + ((outOfPocket == null) ? 0 : outOfPocket.hashCode());
		result = prime * result + ((policyNum == null) ? 0 : policyNum.hashCode());
		result = prime * result + ((policyType == null) ? 0 : policyType.hashCode());
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
		DentalInsurance other = (DentalInsurance) obj;
		if (deductible == null) {
			if (other.deductible != null)
				return false;
		} else if (!deductible.equals(other.deductible))
			return false;
		if (familyPlan == null) {
			if (other.familyPlan != null)
				return false;
		} else if (!familyPlan.equals(other.familyPlan))
			return false;
		if (outOfPocket == null) {
			if (other.outOfPocket != null)
				return false;
		} else if (!outOfPocket.equals(other.outOfPocket))
			return false;
		if (policyNum == null) {
			if (other.policyNum != null)
				return false;
		} else if (!policyNum.equals(other.policyNum))
			return false;
		if (policyType == null) {
			if (other.policyType != null)
				return false;
		} else if (!policyType.equals(other.policyType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DentalInsurance [policyNum=");
		builder.append(policyNum);
		builder.append(", policyType=");
		builder.append(policyType);
		builder.append(", deductible=");
		builder.append(deductible);
		builder.append(", outOfPocket=");
		builder.append(outOfPocket);
		builder.append(", familyPlan=");
		builder.append(familyPlan);
		builder.append("]");
		return builder.toString();
	}
	
	
}
