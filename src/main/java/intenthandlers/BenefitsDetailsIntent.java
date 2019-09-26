package intenthandlers;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.interfaces.display.Template;
import com.amazon.ask.request.Predicates;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import intenthandlers.speechprompts.ResponseConstants;
import models.Account;
import models.insurancePolicies.InsurancePolicy;
import utils.DisplayUtils;
import utils.RequestUtils;
import warehouse.memorystore.AcctMemoryStoreImpl;


public class BenefitsDetailsIntent implements RequestHandler {
	private static String INTENT_NAME = "BenefitsDetailsIntent";
	private static String CARD_TITLE = "Benefits Detail";
	private static String ACTOR_SLOT_NAME = "PolicyDetailType";
	private static final Logger logger = LogManager.getLogger(BenefitsDetailsIntent.class);
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
		Template template = null;
		Account currentAccount = null;

		try {
			currentAccount = getAccountDetails();
		} catch (Exception e) {
			return input.getResponseBuilder().withSpeech(ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_SPEECH)
					.withSimpleCard(CARD_TITLE, ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_CARD)
					.withShouldEndSession(false).build();
		}
		
		StringBuffer speechText = new StringBuffer();
		StringBuffer cardText = new StringBuffer();
		List<InsurancePolicy> policyResults = null;
		//Getting Slots
		Map<String, Slot> slots = RequestUtils.getSlots(input);
		String policyType = slots.get(ACTOR_SLOT_NAME).getValue();
		//Filtering policies
		logger.info("policyType:" + policyType);
		policyResults = currentAccount.getPolicies().stream()
				.filter(p -> p.getPolicyType().toLowerCase().compareTo(policyType) == 0).collect(toList());
		if (policyResults.isEmpty() || policyResults == null) {
			speechText.append("You dont have any coverage for this policy type.");
		} else {
			speechText.append("I found the claim details about your " + policyType + " policy. ")
					.append("Check the companion app for details");
			for (InsurancePolicy policy : policyResults) {
				String strDeductible = policy.getDeductible();
				String strOutOfPocket = policy.getOutOfPocket();
				Random rand = new Random();
				
				logger.info("BenefitsDetailsIntent.strDeductible:" + strDeductible);
				logger.info("BenefitsDetailsIntent.strOutOfPocket:" + strOutOfPocket);
				
				int iDeductible = 3000;
				
				try {
					iDeductible = Integer.parseInt(strDeductible);
				} catch (Exception e) {
				}
				
				int iDeductibleUsed = rand.nextInt(4) * 200;
				if(iDeductible <= iDeductibleUsed) {
					iDeductibleUsed = iDeductible - 4;
				}
				int iDeductibleRemaining = iDeductible - iDeductibleUsed;
				
				int iOutOfPocket = 4500;
				
				try {
					iOutOfPocket = Integer.parseInt(strOutOfPocket);
				} catch (Exception e) {
				}
				
				int iOutOfPocketUsed = rand.nextInt(10) * 200;
				iOutOfPocketUsed = iOutOfPocketUsed == 0 ? 500 : iOutOfPocketUsed;
				
				if(iOutOfPocket <= iOutOfPocketUsed) {
					iOutOfPocketUsed = iOutOfPocket - 3;
				}
				
				int iOutOfPocketRemaining = iOutOfPocket - iOutOfPocketUsed;
				
				/*
				cardText.append("Policy number: " + policy.getPolicyNum() + " ")
						.append("Deductible used: $" + iDeductibleUsed + " ")
						.append("Deductible remaining: $" + iDeductibleRemaining + " ")
						.append("Out of Pocket used: $" + iOutOfPocketUsed + " ")
						.append("Out of Pocket remaining: $" + iOutOfPocketRemaining + " "); */
				
				String strPlanName = "Plan - ";
				if("dental".equalsIgnoreCase(policyType)) {
					strPlanName += "Dental Protection 1";
				} else if("vision".equalsIgnoreCase(policyType)) {
					strPlanName += "Vision Plus";
				} else if("life".equalsIgnoreCase(policyType)) {
					strPlanName += "Click2Protect";
				} else {
					strPlanName += "MediGold";
				}

				String strBenefitDetails = "Policy number:           " + policy.getPolicyNum() + "<br/>";
				strBenefitDetails += "Deductible used:         $" + iDeductibleUsed + "<br/>";
				strBenefitDetails += "Deductible remaining:    $" + iDeductibleRemaining + "<br/>";
				strBenefitDetails += "Out of Pocket used:      $" + iOutOfPocketUsed + "<br/>";
				strBenefitDetails += "Out of Pocket remaining: $" + iOutOfPocketRemaining + "<br/>";
				strBenefitDetails += "Copay - Primary care:    $25<br/>";
				strBenefitDetails += "Copay - Specialist:      $40";
				
				template = DisplayUtils.getBodyTemplate1(CARD_TITLE, strPlanName, strBenefitDetails, "");
				
			}

		}

		if(template == null) {
			return input.getResponseBuilder().withSpeech(speechText.toString()).withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		} else {
			return input.getResponseBuilder().withSpeech(speechText.toString()).addRenderTemplateDirective(template)
					.withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		}

	}
	
	private Account getAccountDetails() throws Exception {
		logger.debug("Into the getAccountDetails()");
		return AcctMemoryStoreImpl.getAccount();
	}
}
