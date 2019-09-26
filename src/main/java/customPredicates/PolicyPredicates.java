package customPredicates;

import java.util.function.Predicate;

import models.PolicyType;
import models.insurancePolicies.InsurancePolicy;

public class PolicyPredicates {
	private PolicyPredicates() { }
	public static Predicate<InsurancePolicy> isMedical = (ipolicy) -> ipolicy.getPolicyType().toLowerCase() == PolicyType.Medical.getDesc();
	public static Predicate<InsurancePolicy> isDental = (ipolicy) -> ipolicy.getPolicyType().toLowerCase() == PolicyType.Dental.getDesc();
	public static Predicate<InsurancePolicy> isLife = (ipolicy) -> ipolicy.getPolicyType().toLowerCase() == PolicyType.Life.getDesc();
	public static Predicate<InsurancePolicy> isVision = (ipolicy) -> ipolicy.getPolicyType().toLowerCase() == PolicyType.Vision.getDesc();

}
