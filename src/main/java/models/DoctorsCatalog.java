package models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "doctorscatalog")
public class DoctorsCatalog {
	private List<Doctor> medicalDoctors;
	private List<Doctor> dentalDoctors;
	
	public List<Doctor> getMedicalDoctors() {
		return medicalDoctors;
	}
	public void setMedicalDoctors(List<Doctor> medicalDoctors) {
		this.medicalDoctors = medicalDoctors;
	}
	public List<Doctor> getDentalDoctors() {
		return dentalDoctors;
	}
	public void setDentalDoctors(List<Doctor> dentalDoctors) {
		this.dentalDoctors = dentalDoctors;
	}
}
