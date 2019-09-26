package customPredicates;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import models.Claim;
import models.ClaimStatus;

public class ClaimPredicates {
	private ClaimPredicates() {
	}
	public static Predicate<Claim> statusIsOpen = (claim) -> claim.getClaimStatus().equals(ClaimStatus.Open);
	public static Predicate<Claim> statusIsClose = (claim) -> claim.getClaimStatus().equals(ClaimStatus.Closed);
	public static Predicate<Claim> statusIsPending = (claim) -> claim.getClaimStatus().equals(ClaimStatus.Pending);

	public static Predicate<Claim> matchDate(String date) {
		return claim -> claim.getClaimDate().equals(date);
	}
	
	public static Predicate<Claim> inRange(String date){
		DateTimeFormatter format = DateTimeFormatter.ofPattern("YYYY-MM-DD");
		LocalDate locDate = LocalDate.parse(date, format);
		return claim -> (claim.compareTo(locDate)>-2 && claim.compareTo(locDate)<2) ;
	}

	public static Predicate<Claim> inLocalDateRange(LocalDate localdate){
		return claim -> (claim.compareTo(localdate)>-2 && claim.compareTo(localdate)<2) ;
	}
	
	public static Predicate<Claim> inRange(LocalDate date){
		return claim -> (claim.compareTo(date)>-2 && claim.compareTo(date)<2) ;
	}

	public static Predicate<Claim> statusIs(String type) {
		return claim -> claim.getClaimStatus().getDesc().equalsIgnoreCase(type);
	}

}
