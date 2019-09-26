package warehouse.memorystore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import models.Account;
import models.User;

public class AcctMemoryStoreImpl {
	private static final Logger logger = LogManager.getLogger(AcctMemoryStoreImpl.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private static Account objAccount;

	private AcctMemoryStoreImpl() {
	}
	
	public static Account getAccount() {
		if (objAccount == null) {
			objAccount = createAccount();
		}
		return objAccount;
	}

	public static void UpdateAccount(Account acct) {
		objAccount = acct;
	}

	private static Account createAccount() {
		Account account = new Account(true);
		User user = new User("Roger", "Wilcox", "80805 Crail Drive", "Paris", "Texas", "United States", "555-555-5555",
				"555-444-4444", "demo@example.com", 1234, "self", "Male", parseDate("12-10-1978"));
		account.addUser(new User("Mary", "Jane", "80805 Crail Drive", "Paris", "Texas", "United States", "555-555-5555",
				"123-234-3456", "demodependent@example.com", 1234, "self", "Female", parseDate("9-13-1979")));
		account.setPrimary(user);
		return account;
	}
	

	public static String parseDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		try {
			return LocalDate.parse(date, formatter).toString();
		} catch (Exception e) {
			return null;
		}
	}
	
}
