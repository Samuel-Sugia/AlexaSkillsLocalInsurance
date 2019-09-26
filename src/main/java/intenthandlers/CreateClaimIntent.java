package intenthandlers;

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

import customPredicates.CustomPredicates;
import intenthandlers.speechprompts.ResponseConstants;
import models.Account;
import models.Claim;
import utils.DisplayUtils;
import utils.RequestUtils;
import warehouse.memorystore.AcctMemoryStoreImpl;

public class CreateClaimIntent implements RequestHandler {
	private static final Logger logger = LogManager.getLogger(CreateClaimIntent.class);
	private static ObjectMapper mapper = new ObjectMapper();
	// Intent string constants
	private static final String INTENT_NAME = "CreateClaimIntent";
	private static final String CARD_TITLE = "Create Claim";
	private static final String SEARCHQUERY_SLOT_NAME = "CreateClaimDescription";
	private static final String NUMBER_SLOT_NAME = "CreateClaimAmount";
	// functionality not implemented yet
	// private static String ACTOR_SLOT_NAME = "CreateClaimType";

	@Override
	public boolean canHandle(HandlerInput input) {
		mapper.enableDefaultTyping();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return input.matches(Predicates.intentName(INTENT_NAME));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		Account currentAccount = null;
		//ResponseUtils.sendProgressiveResponse(input, ResponseConstants.PROGRESSIVE_RESPONSE_1);
		logger.info("fetching Account");
		try {
			currentAccount = getAccountDetails();
		} catch (Exception e) {
			logger.catching(e);
			return input.getResponseBuilder().withSpeech(ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_SPEECH)
					.withSimpleCard(CARD_TITLE, ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_CARD)
					.withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		}

		// getting slots
		Map<String, Slot> slots = RequestUtils.getSlots(input);
		String claimDesc = slots.get(SEARCHQUERY_SLOT_NAME).getValue();
		String claimAmmount = slots.get(NUMBER_SLOT_NAME).getValue();

		logger.info(currentAccount.addClaim(claimDesc, claimAmmount));

		Claim newClaim = currentAccount.getClaims().get(currentAccount.getClaims().size() - 1);
		String speechText = "Your claim for " + newClaim.getClaimAmount() + " dollars with the description of \""
				+ newClaim.getClaimDescription() + "\" was submitted on " + newClaim.getClaimDate()
				+ " and currently have a status of " + newClaim.getClaimStatus().getDesc() + ", the claim number is "
				+ newClaim.getClaimNum();
		String cardText = "\n" + newClaim.getClaimNum() + "\n" + newClaim.getClaimDate() + "\n"
				+ newClaim.getClaimDescription() + "\n" + newClaim.getClaimStatus().getDesc();

		// Updating account with new claim
		try {
			persistAccount(currentAccount);
		} catch (Exception e) {
			speechText = "I'm sorry there was an error while submitting your claim, please try again later";
			cardText = "Could not submit claim, please try again later.";
			logger.catching(e);
			return input.getResponseBuilder().withSpeech(speechText).withSimpleCard("Claim Error", cardText)
					.withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		}

		// Sending Response
		// Display Response
		if (input.matches(CustomPredicates.supportsDisplay())) {
			List<ListItem> itemList = new ArrayList<ListItem>();
			itemList.add(DisplayUtils.createListItemNoImage(CARD_TITLE, newClaim.getClaimNum(),
					newClaim.getClaimStatus().toString(), String.format("$%s", newClaim.getClaimAmount())));
			Template template = DisplayUtils.getListTemplate1(itemList, CARD_TITLE);
			return input.getResponseBuilder().withSpeech(speechText).withSimpleCard("Claim Submitted", cardText)
					.withShouldEndSession(false).addRenderTemplateDirective(template)
					.withReprompt(ResponseConstants.REPROMPT_TEXT).build();

		}
		// Non-Display Response
		return input.getResponseBuilder().withSpeech(speechText).withSimpleCard("Claim Submitted", cardText)
				.withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
	}
	
	private Account getAccountDetails() throws Exception {
		logger.debug("Into the getAccountDetails()");
		return AcctMemoryStoreImpl.getAccount();
	}
	private boolean persistAccount(Account objAccount) {
		logger.debug("Persisting the object:" + objAccount);
		AcctMemoryStoreImpl.UpdateAccount(objAccount);
		return true;
	}
}
