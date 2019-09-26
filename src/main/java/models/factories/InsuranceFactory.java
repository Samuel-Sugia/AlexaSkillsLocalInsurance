package models.factories;

import models.insurancePolicies.DentalInsurance;
import models.insurancePolicies.InsurancePolicy;
import models.insurancePolicies.LifeInsurance;
import models.insurancePolicies.MedicalInsurance;
import models.insurancePolicies.VisionInsurance;

public class InsuranceFactory {

	public static String DENTAL = "dental";
	public static String MEDICAL = "medical";
	public static String LIFE = "life";
	public static String VISION = "vision";

	public static InsurancePolicy generate(int selector) {
		InsurancePolicy newPolicy = null;
		switch (selector) {
		case 1:
			newPolicy = new DentalInsurance(false);
			break;	

		case 2:
			newPolicy = new LifeInsurance(false);
			break;
		case 3:
			newPolicy = new MedicalInsurance(false);
			break;
		case 4:
			newPolicy = new VisionInsurance(false);
			break;
		}
		return newPolicy;
	}

	public static InsurancePolicy generate(String type) {
		InsurancePolicy newPolicy = null;
		switch (type.toLowerCase()) {
		case "dental":
			newPolicy = new DentalInsurance();
			break;
		case "medical":
			newPolicy = new MedicalInsurance();
			break;
		case "life":
			newPolicy = new LifeInsurance();
			break;
		case "lision":
			newPolicy = new VisionInsurance();
			break;
		}
		return newPolicy;
	}

}
