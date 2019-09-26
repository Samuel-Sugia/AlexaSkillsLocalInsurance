package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import models.factories.ClaimsService;
import models.factories.InsuranceFactory;
import models.insurancePolicies.InsurancePolicy;

@JsonDeserialize(builder = Account.Builder.class)
public class Account {
	@JsonProperty("_id")
	private String id;
	@JsonProperty("acctNumber")
	private String acctNumber;
	@JsonProperty("primary")
	private User primary = null;
	@JsonProperty("dependents")
	private List<User> dependents;
	@JsonProperty("policies")
	private List<InsurancePolicy> policies;
	@JsonProperty("claims")
	private List<Claim> claims;

	public static Builder builder() {
		return new Builder();
	}
	
	public Account() {}
	
	public Account(boolean filler) {
		this.acctNumber = "A" + String.valueOf(new Random().nextInt(3000));
		this.id = this.acctNumber;
		this.policies = new ArrayList<InsurancePolicy>();
		this.dependents = new ArrayList<User>();
		policies.add(InsuranceFactory.generate(1));
		policies.add(InsuranceFactory.generate(3));
		this.claims = ClaimsService.getInstance().getClaims();
	}

	private Account(Builder builder) {
		this.id = String.format("%s", builder.policyNumber);
		this.acctNumber = builder.policyNumber;
		this.primary = builder.accountHolder;
		this.dependents = builder.dependents;
		this.policies = builder.policies;
		this.claims = builder.claims;

	}

	public boolean addPolicy(InsurancePolicy policy) {
		return policies.add(policy);
	}

	public boolean addClaim(String desc, String amount) {
		int claimsLen = claims.size();
		this.claims = ClaimsService.getInstance().createClaim(desc, amount);
		return claimsLen < claims.size();

	}
	public void addUser(User user) {
		this.dependents.add(user);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAcctNumber() {
		return acctNumber;
	}

	public void setAcctNumber(String acctNumber) {
		this.acctNumber = acctNumber;
	}

	public User getPrimary() {
		return primary;
	}

	public void setPrimary(User primary) {
		this.primary = primary;
	}

	public List<User> getDependents() {
		return dependents;
	}

	public void setDependents(List<User> dependents) {
		this.dependents = dependents;
	}

	public List<InsurancePolicy> getPolicies() {
		return policies;
	}

	public void setPolicies(List<InsurancePolicy> policies) {
		this.policies = policies;
	}

	public List<Claim> getClaims() {
		return claims;
	}

	public void setClaims(List<Claim> claims) {
		this.claims = claims;
	}

	@Override
	public String toString() {
		StringBuilder builder2 = new StringBuilder();
		builder2.append("Account2 [id=");
		builder2.append(id);
		builder2.append(", acctNumber=");
		builder2.append(acctNumber);
		builder2.append(", primary=");
		builder2.append(primary);
		builder2.append(", dependents=");
		builder2.append(dependents);
		builder2.append(", policies=");
		builder2.append(policies);
		builder2.append(", claims=");
		builder2.append(claims);
		builder2.append("]");
		return builder2.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acctNumber == null) ? 0 : acctNumber.hashCode());
		result = prime * result + ((claims == null) ? 0 : claims.hashCode());
		result = prime * result + ((dependents == null) ? 0 : dependents.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((policies == null) ? 0 : policies.hashCode());
		result = prime * result + ((primary == null) ? 0 : primary.hashCode());
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
		Account other = (Account) obj;
		if (acctNumber == null) {
			if (other.acctNumber != null)
				return false;
		} else if (!acctNumber.equals(other.acctNumber))
			return false;
		if (claims == null) {
			if (other.claims != null)
				return false;
		} else if (!claims.equals(other.claims))
			return false;
		if (dependents == null) {
			if (other.dependents != null)
				return false;
		} else if (!dependents.equals(other.dependents))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (policies == null) {
			if (other.policies != null)
				return false;
		} else if (!policies.equals(other.policies))
			return false;
		if (primary == null) {
			if (other.primary != null)
				return false;
		} else if (!primary.equals(other.primary))
			return false;
		return true;
	}

	public static class Builder {
		@SuppressWarnings("unused")
		private String id = null;
		private String policyNumber = null;
		private User accountHolder = null;
		private List<Claim> claims = null;
		private List<User> dependents = null;
		private List<InsurancePolicy> policies = null;

		private Builder() {
		}
		
		@JsonProperty("_id")
		public Builder withObjectID(String id) {
			this.id = id;
			return this;
		}

		@JsonProperty("acctNumber")
		public Builder withAcctNumber(String policyNumber) {
			this.policyNumber = policyNumber.replaceAll("-", "0");
			return this;
		}

		@JsonProperty("primary")
		public Builder withPrimary(User user) {
			this.accountHolder = user;
			return this;
		}

		@JsonProperty("dependents")
		public Builder withDependents(List<User> dependents) {
			this.dependents = dependents;
			return this;
		}

		@JsonProperty("policies")
		public Builder withPolicies(List<InsurancePolicy> policies) {
			this.policies = policies;
			return this;
		}
		
		@JsonProperty("claims")
		public Builder withClaims(List<Claim> claims) {
			this.claims = claims;
			return this;
		}
		public Account build() {
			if (policies == null) {
				policies = new ArrayList<InsurancePolicy>();
			}
			if(dependents==null) {
				dependents = new ArrayList<User>();
			}
			return new Account(this);
		}

	}

}
