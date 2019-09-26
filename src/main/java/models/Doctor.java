package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "doctor")
public class Doctor {
	@JsonProperty("_id")
	private String id = null;
	@JsonProperty("name")
	private String name;
	@JsonProperty("street")
	private String street;
	@JsonProperty("city")
	private String city;
	@JsonProperty("state")
	private String state;
	@JsonProperty("zipCode")
	private String zipCode;
	@JsonProperty("phone")
	private String phone;
	@JsonProperty("rating")
	private String rating;
	@JsonProperty("distance")
	private String distance;
	@JsonProperty("noOfPatients")
	private String noOfPatients;
	@JsonProperty("specialty")
	private String specialty;
	@JsonProperty("serviceType")
	private String serviceType;
	@JsonProperty("inNetwork")
	private boolean inNetwork;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getNoOfPatients() {
		return noOfPatients;
	}
	public void setNoOfPatients(String noOfPatients) {
		this.noOfPatients = noOfPatients;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public boolean isInNetwork() {
		return inNetwork;
	}
	public void setInNetwork(boolean inNetwork) {
		this.inNetwork = inNetwork;
	}
	@Override
	public String toString() {
		return "name="+this.name+", city="+this.city+", state=" + this.state + ", phone="+this.phone+", rating="+this.rating+", distance="+this.distance+
				", noOfPatients="+this.noOfPatients+", specialty="+this.specialty+", serviceType="+this.serviceType+
				", zipCode="+this.zipCode+", inNetwork="+this.inNetwork;
	}
}
