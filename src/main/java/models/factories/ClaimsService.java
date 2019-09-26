package models.factories;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import models.Claim;
import models.ClaimStatus;

public class ClaimsService {

	private static transient int DEFAULT_CLAIM_COUNT = 6;
	private static ClaimsService instance = null;
	private static List<Claim> claims = null;

	private ClaimsService() {
		claims = new ArrayList<Claim>();

	}

	public static ClaimsService getInstance() {
		if (instance == null)
			instance = new ClaimsService();
		return instance;
	}

	private void populateClaims() {
		//Random statusSel = new Random();
		String claimNum, claimDescription, claimAmount, claimDate;
		List<Claim> newClaims = new ArrayList<Claim>();
		for (int i = 0; i < DEFAULT_CLAIM_COUNT; i++) {
			claimNum = generateClaimNum();
			claimDescription = new StringBuilder("This is demo claim ").append(i + 1).append(" of ")
					.append(DEFAULT_CLAIM_COUNT).toString();
			claimAmount = generateClaimAmount();
			claimDate = generateClaimDate();
			if (i % 2 ==0)
				newClaims.add(new Claim(claimNum, claimDescription, claimAmount, claimDate, ClaimStatus.Open));
			else if (i % 2 > 0)
				newClaims.add(new Claim(claimNum, claimDescription, claimAmount, claimDate, ClaimStatus.Pending));
			else
				newClaims.add(new Claim(claimNum, claimDescription, claimAmount, claimDate, ClaimStatus.Closed));
		}
		claims.addAll(newClaims);
	}

	public List<Claim> getClaims() {
		if (claims.isEmpty()) {
			populateClaims();
		}
		return claims;
	}

	public List<Claim> createClaim(String desc, String amount) {
		Calendar calDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM dd yyyy");
		String dateStr = formatter.format(calDate.getTime());
		Claim newClaim = new Claim(generateClaimNum(), desc, amount, dateStr, ClaimStatus.Open);
		if (claims.isEmpty() || claims == null) {
			claims = new ArrayList<Claim>();
			claims.add(newClaim);
			return claims;
		} else {
			claims.add(newClaim);
			return claims;
		}
	}

	public boolean addClaim(Claim newClaim) {
		return claims.add(newClaim);
	}

	private String generateClaimNum() {
		Random rand = new Random();
		return Integer.toString(rand.nextInt(3000));
	}

	private String generateClaimAmount() {
		Random rand = new Random();
		return Double.valueOf(rand.nextInt(3000) + rand.nextDouble()).toString();

	}

	private String generateClaimDate() {
		Random rand = new Random();
		int day = rand.nextInt(31);
		int month = rand.nextInt(12);
		boolean direction = rand.nextBoolean();
		day = (direction ? day : day * -1);
		month = (direction ? month : month * -1);
		Calendar calDate = Calendar.getInstance();
		calDate.add(Calendar.DAY_OF_MONTH, day);
		calDate.add(Calendar.MONTH, month);
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM dd yyyy");
		return formatter.format(calDate.getTime());
	}
}
