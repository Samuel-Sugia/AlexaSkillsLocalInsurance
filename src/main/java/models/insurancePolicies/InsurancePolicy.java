package models.insurancePolicies;

//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
//@JsonSubTypes({ @Type(value = LifeInsurance.class, name = "LifeInsurance"),
//		@Type(value = MedicalInsurance.class, name = "MedicalInsurance"),
//		@Type(value = VisionInsurance.class, name = "VisionInsurance"),
//		@Type(value = DentalInsurance.class, name = "DentalInsurance") })
public interface InsurancePolicy {

	public String getPolicyNum();

	public String getPolicyType();

	public String getDeductible();

	public String getOutOfPocket();

}
