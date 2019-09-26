package intenthandlers;

import java.util.ArrayList;
import java.util.Iterator;
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

import customPredicates.CustomPredicates;
import intenthandlers.speechprompts.ResponseConstants;
import models.Account;
import models.Claim;
import utils.DisplayUtils;
import utils.RequestUtils;
import warehouse.memorystore.AcctMemoryStoreImpl;

public class SearchClaimIntent implements RequestHandler {
	private static final Logger logger = LogManager.getLogger(SearchClaimIntent.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	private static String INTENT_NAME = "SearchClaimIntent";
	private static String CARD_TITLE = "Search Claim";
	private static String SEARCHQUERY_SLOT_NAME = "SearchClaimText";

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
		try {
			currentAccount = getAccountDetails();
		} catch (Exception e) {
			logger.catching(e);
			return input.getResponseBuilder().withSpeech(ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_SPEECH)
					.withSimpleCard(CARD_TITLE, ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_CARD)
					.withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		}
		StringBuffer speechText = new StringBuffer();
		StringBuffer cardText = new StringBuffer();
		Claim claimResult = null;

		// Get
		Map<String, Slot> slots = RequestUtils.getSlots(input);
		String claimNum = (slots.containsKey(SEARCHQUERY_SLOT_NAME) ? slots.get(SEARCHQUERY_SLOT_NAME).getValue()
				: " ");
		logger.info("claim number: " + claimNum);
		//ResponseUtils.sendProgressiveResponse(input, ResponseConstants.PROGRESSIVE_RESPONSE_1);
		claimResult = findClaimNum(claimNum, currentAccount.getClaims());
		// logger.info("located claim: "+claimResult.toString());
		if (claimResult != null) {
			speechText.append("Claim number ").append(claimResult.getClaimNum()).append(" is currently ")
					.append(claimResult.getClaimStatus().getDesc());
			cardText.append("Claim number:" + claimResult.getClaimNum() + "\n")
					.append("Claim Description: " + claimResult.getClaimDescription() + "\n")
					.append("Claim Status: " + claimResult.getClaimStatus().getDesc() + "\n")
					.append("Claim submitted: " + claimResult.getClaimDate());
		} else {
			speechText.append("The requested claim, " + claimNum + " was not found");
			cardText.append(claimNum + "not found");
		}

		if (input.matches(CustomPredicates.supportsDisplay()) && claimResult != null) {
			List<ListItem> itemList = new ArrayList<ListItem>();
			itemList.add(
					DisplayUtils.createListItemNoImage(claimNum, claimResult.getClaimNum(), claimResult.getClaimDate(),
							String.format("$%.2f", Float.parseFloat(claimResult.getClaimAmount()))));
			Template template = DisplayUtils.getListTemplate1(itemList, CARD_TITLE);
			return input.getResponseBuilder().withSpeech(speechText.toString()).addRenderTemplateDirective(template)
					.withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false)
					.withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		}
		return input.getResponseBuilder().withSpeech(speechText.toString())
				.withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).build();

	}

	private Claim findClaimNum(String claimNum, List<Claim> claims) {
		Claim claim = null;
		Iterator<Claim> claimSearch = claims.iterator();
		while (claimSearch.hasNext()) {
			claim = claimSearch.next();
			if (claim.getClaimNum().compareTo(claimNum) == 0) {
				return claim;
			}
		}
		return null;
	}
	private Account getAccountDetails() throws Exception {
		logger.debug("Into the getAccountDetails()");
		return AcctMemoryStoreImpl.getAccount();
	}


}
