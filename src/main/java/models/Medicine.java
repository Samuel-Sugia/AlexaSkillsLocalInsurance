package models;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "medicine")
public class Medicine {
	private String medicineName;
	private String medicineQuantity;
	
	public String getMedicineName() {
		return medicineName;
	}
	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}
	public String getMedicineQuantity() {
		return medicineQuantity;
	}
	public void setMedicineQuantity(String medicineQuantity) {
		this.medicineQuantity = medicineQuantity;
	}
	@Override
	public String toString() {
		return "medicineName=" + this.medicineName + ", medicineQuantity=" + this.medicineQuantity;
	}
}
