package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "prescription")
public class Prescription {
	@JsonProperty("_id")
	private String id = null;
	private List<Medicine> medicineList;
	private String prescriptionDate;
	private String deliveryAddress;
	
	public Prescription() {
		this.id = UUID.randomUUID().toString();
		this.medicineList = new ArrayList<Medicine>();
	}
	
	public List<Medicine> getMedicineList() {
		return medicineList;
	}
	public String getPrescriptionDate() {
		return prescriptionDate;
	}
	public void setPrescriptionDate(String prescriptionDate) {
		this.prescriptionDate = prescriptionDate;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public void addMedicine(Medicine medicine) {
		this.medicineList.add(medicine);
	}
	
	@Override
	public String toString() {
		return "prescriptionDate=" + this.prescriptionDate + ", deliveryAddress=" + this.deliveryAddress + ", medicineList=" + this.medicineList;
	}
}
