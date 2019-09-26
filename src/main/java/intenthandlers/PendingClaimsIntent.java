/**
 * @author Stephen Lovick
 */
package intenthandlers;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.interfaces.display.ListItem;
import com.amazon.ask.model.interfaces.display.Template;
import com.amazon.ask.request.Predicates;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import customPredicates.ClaimPredicates;
import customPredicates.CustomPredicates;
import intenthandlers.speechprompts.ResponseConstants;
import models.Account;
import models.Claim;
import utils.DisplayUtils;
import utils.RequestUtils;
import utils.SlotTypeUtils;
import warehouse.memorystore.AcctMemoryStoreImpl;

public class PendingClaimsIntent implements RequestHandler {
	private static Logger logger = LogManager.getLogger(PendingClaimsIntent.class);
	private static String INTENT_NAME = "PendingClaimsIntent";
	private static String CARD_TITLE = "Pending Claim";
	private static String DATE_SLOT_NAME = "PendingClaimDate";
	private static String DURATION_SLOT_NAME = "PendingClaimDuration";
	private static String ACTOR_SLOT_NAME = "PendingClaimType";
	private static String ACTOR_SLOT_ID_PREFIX = "PC";
	private static String ACTOR_SLOT_ALT_ID = "all";
	private static ObjectMapper mapper = new ObjectMapper();
	//private Map<String, Object> cache = null;

	@Override
	public boolean canHandle(HandlerInput input) {
		mapper.enableDefaultTyping();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return input.matches(Predicates.intentName(INTENT_NAME));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		Account currentAccount = null;
		StringBuffer speechText = new StringBuffer();
		StringBuffer cardText = new StringBuffer();
		String type = "pending";
		List<Claim> claimResults = null;
		
		//ResponseUtils.sendProgressiveResponse(input, ResponseConstants.PROGRESSIVE_RESPONSE_1);
		logger.info("fetching Account");
		try {
			currentAccount = getAccountDetails();
		}catch(Exception e) {
			logger.catching(e);
			return input.getResponseBuilder().withSpeech(ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_SPEECH)
					.withSimpleCard(CARD_TITLE, ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_CARD)
					.withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		}
		//getting slots
		Map<String, Slot> slots = RequestUtils.getSlots(input);
		Optional<Slot> claimDate = Optional.ofNullable(slots.get(DATE_SLOT_NAME));
		Optional<Slot> claimDuration = Optional.ofNullable(slots.get(DURATION_SLOT_NAME));
		Optional<Slot> claimType = Optional.ofNullable(slots.get(ACTOR_SLOT_NAME));
		
		logger.info("Claim Type present: " + claimType.isPresent());
		logger.info(SlotTypeUtils.getActorValue(claimType.get(), ACTOR_SLOT_ALT_ID, ACTOR_SLOT_ID_PREFIX));
		logger.info(claimDate.get().getValue() != null);
		logger.info("claims size: " +currentAccount.getClaims().size());
		// filter account claims
		if (claimDate.get().getValue() != null) {
			claimResults = searchDate(currentAccount, claimDate.get().getValue());
		} else if (claimDuration.get().getValue() != null) {
			claimResults = searchDuration(currentAccount, claimDate.get().getValue());
		} else if (claimType.get().getValue() != null) {
			type = SlotTypeUtils.getActorValue(claimType.get(), ACTOR_SLOT_ALT_ID, ACTOR_SLOT_ID_PREFIX);
			for (Claim claim : currentAccount.getClaims())
				logger.info(claim.getClaimStatus().getDesc());
			claimResults = searchType(currentAccount, type);
		} else
			claimResults = searchType(currentAccount, type);

		/*
		 * Searching for pending claims if claims are pending set SpeechText and
		 * CardText to "you have %d claims currently pending + (each pending claim
		 * information)
		 */
		logger.info("claimResults: " + claimResults.size());
		if (!claimResults.isEmpty()) {
			speechText.append("You have ").append(claimResults.size())
					.append(" claims currently " + type + ". please see the companion app for details");
			for(Claim claim: claimResults) {
				cardText.append(claim.getClaimNum()+"\n").append(String.format("$%s\n",claim.getClaimAmount())).append(claim.getClaimStatus().toString());
			}
			/*
			 * If not claims are pending set SpeechText & CardText to "You have no pending
			 * claims"
			 */
		} else {
			speechText.append(ResponseConstants.NO_CLAIMS_SPEECH);
			cardText.append(ResponseConstants.NO_CLAIMS_CARD);
		}
		
		// Returning response
		logger.info("claim results size: " + claimResults.size());
		logger.info("supports displays:" + input.matches(CustomPredicates.supportsDisplay()));
		
		//Response for Displays
		if (input.matches(CustomPredicates.supportsDisplay()) && !claimResults.isEmpty()) {
			Template template = claimsDisplayTemplate(claimResults);
			logger.info(template.toString());
			return input.getResponseBuilder().withSpeech(speechText.toString()).addRenderTemplateDirective(template)
					.withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		//response for non-display
		} else
			return input.getResponseBuilder().withSpeech(speechText.toString())
					.withSimpleCard(CARD_TITLE, cardText.toString()).withReprompt(ResponseConstants.REPROMPT_TEXT).withShouldEndSession(false).build();

	}

	/**
	 * Filters Account Claims using a date String (YYYY-MM-DD).
	 * 
	 * @param account
	 * @param date
	 * @return
	 */
	private List<Claim> searchDate(Account account, String date) {
		return account.getClaims().stream().filter(ClaimPredicates.matchDate(date)).collect(toList());
	}

	/**
	 * Filters account claims using a period string (P1Y1M1D)
	 * 
	 * @param account
	 * @param duration
	 * @return
	 */
	private List<Claim> searchDuration(Account account, String duration) {
		LocalDate date = SlotTypeUtils.durationToLocalDate(duration);
		return account.getClaims().stream().filter(ClaimPredicates.inLocalDateRange(date)).collect(toList());
	}

	/**
	 * Filters account claims to show the type queried
	 * 
	 * @param account
	 * @return
	 */
	private static List<Claim> searchType(Account account, String type) {
		return account.getClaims().stream().parallel().filter(ClaimPredicates.statusIs(type)).collect(toList());
	}

	private Template claimsDisplayTemplate(List<Claim> claims) {
		List<ListItem> list = new ArrayList<ListItem>();
		for (Claim claim : claims) {
			
			list.add(DisplayUtils.createListItemNoImage(CARD_TITLE, claim.getClaimNum(), String.format("$%.2f",Float.parseFloat(claim.getClaimAmount())),
					claim.getClaimStatus().toString()));
		}
		return DisplayUtils.getListTemplate1(list, CARD_TITLE);
	}
	
	private Account getAccountDetails() throws Exception {
		logger.debug("Into the getAccountDetails()");
		return AcctMemoryStoreImpl.getAccount();
	}

}
