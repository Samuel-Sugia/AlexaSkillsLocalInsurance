package intenthandlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.DialogState;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.interfaces.display.Template;
import com.amazon.ask.request.Predicates;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import intenthandlers.speechprompts.ResponseConstants;
import models.Account;
import models.User;
import utils.DisplayUtils;
import utils.RequestUtils;
import utils.SlotTypeUtils;
import warehouse.memorystore.AcctMemoryStoreImpl;


public class AddDepenIntent implements IntentRequestHandler {
	private static String INTENT_NAME = "AddDepenIntent";
	private static String CARD_TITLE = "Dependent Details";
	private static String RELATIONSHIP = "Relationship";
	private static String FIRST_NM = "FirstName";
	private static String LAST_NM = "LastName";
	private static String DOB = "DateOfBirth";
	private static String GENDER = "Gender";
	private static String PHONE = "Phone";
	private static String VOICE_PIN = "VoicePin";
	
	private static final Logger logger = LogManager.getLogger(AddDepenIntent.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private Map<String, Object> cache = null;
	
	@Override
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		mapper.enableDefaultTyping();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return input.matches(Predicates.intentName(INTENT_NAME));
	}

	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
		logger.info("AddDepenIntent.DialogState:" + intentRequest.getDialogState());
		
		if(intentRequest.getDialogState() == DialogState.STARTED || intentRequest.getDialogState() == DialogState.IN_PROGRESS) {

			Intent intent = intentRequest.getIntent();
			
			Map<String, Slot> slots = RequestUtils.getSlots(input);
			String strVoicePin = slots.get(VOICE_PIN).getValue();
			logger.debug("strVoicePin:" + strVoicePin);
			boolean bVerified = false;
			if(strVoicePin != null) {
				//if(strVoicePin.equals(currentAccount.getPrimary().getPin().toString())) {
				if(strVoicePin.equals("1234")) {	
					bVerified = true;
					logger.debug("bVerified:" + bVerified);
				} else {
			        Slot updateSlot = Slot.builder()
			                .withName(VOICE_PIN)
			                .withValue(null)
			                .build();

			        intent.getSlots().put(VOICE_PIN, updateSlot);
			        logger.debug("Voice Pin set to null");
				}
				if(!bVerified) {
					return input.getResponseBuilder().withSpeech("Verification denied, please provide your valid four digit pin for verification").addElicitSlotDirective(VOICE_PIN, intent).build();
				} 
			}

			return input.getResponseBuilder().addDelegateDirective(intent).build();

		} else {
			Template template = null;
			logger.debug("Into the Dialog completed stage");
			//ResponseUtils.sendProgressiveResponse(input, "Please wait while I save the information to your account");
			logger.debug("Progressive response sent");
			
			Account currentAccount = null;
			logger.debug("Retrieving account");
			try {
				currentAccount = getAccountDetails();
			} catch (Exception e) {
				logger.debug("Exception occurred while retrieving account");
				return input.getResponseBuilder().withSpeech(ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_SPEECH)
						.withSimpleCard(CARD_TITLE, ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_CARD)
						.withShouldEndSession(false).build();
			}
			logger.info("No of dependents before adding:" + currentAccount.getDependents().size());
			
			Map<String, Slot> slots = RequestUtils.getSlots(input);
			StringBuffer speechText = new StringBuffer();
			StringBuffer cardText = new StringBuffer();
			
			currentAccount = addDepdendentDetails(currentAccount, slots);
			if(currentAccount == null) {
				speechText.append("I'm sorry, I was not able to save your account information");
				cardText.append("There was a problem while saving your account. Please call customer service.");
				return input.getResponseBuilder().withSpeech(speechText.toString())
						.withSimpleCard(CARD_TITLE, cardText.toString())
						.withShouldEndSession(false).build();
			}
			
			speechText.append("I have successfully added the dependent to your account. Please refer to the companion app for the details.");
			cardText.append("I have successfully added the dependent to your account. Please refer to the companion app for the details.");
			
			template = DisplayUtils.getBodyTemplate1(CARD_TITLE, getLatestDependentDetails(currentAccount), "", "");		
			
			try {
				cache = input.getAttributesManager().getSessionAttributes();
				String accountJson = mapper.writeValueAsString(currentAccount);
				cache.put("curAcct", accountJson);
				input.getAttributesManager().setSessionAttributes(cache);
			} catch (JsonProcessingException e) {
				logger.info("Couldn't persist account");
				logger.catching(e);
			}
			
			logger.info("No of dependents After adding:" + currentAccount.getDependents().size());
			
			if(template == null) {
				return input.getResponseBuilder().withSpeech(speechText.toString()).withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
			} else {
				return input.getResponseBuilder().withSpeech(speechText.toString()).addRenderTemplateDirective(template)
						.withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
			}

		}
	}
	
	private Account addDepdendentDetails(Account currentAccount, Map<String, Slot> slots) {
		
		String strRelationship = SlotTypeUtils.getResolvedSlotValue(slots.get(RELATIONSHIP));
		logger.info("strRelationship:" + strRelationship);
		String strFirstName = slots.get(FIRST_NM).getValue();
		logger.info("strFirstName:" + strFirstName);
		String strLastName = slots.get(LAST_NM).getValue();
		logger.info("strLastName:" + strLastName);
		String strDateOfBirth = slots.get(DOB).getValue();
		logger.info("strDateOfBirth:" + strDateOfBirth);
		String strGender = SlotTypeUtils.getResolvedSlotValue(slots.get(GENDER));
		logger.info("strGender:" + strGender);
		String strPhone = slots.get(PHONE).getValue();
		logger.info("strPhone:" + strPhone);
		
		logger.debug("currentAccount.DOB:" + currentAccount.getPrimary().getDateOfBirth());
		//User primary = currentAccount.getPrimary();
		
		User dependent = new User();
		dependent.setFname(strFirstName);
		dependent.setLname(strLastName);
		dependent.setRelationship(strRelationship);
		
		try {
			Date dtDOB = new SimpleDateFormat("yyyy-MM-dd").parse(strDateOfBirth);
			logger.info("dtDOB:" + dtDOB);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			strDateOfBirth = sdf.format(dtDOB);
			dependent.setDateOfBirth(strDateOfBirth);
			logger.info("Setting date in object:" + strDateOfBirth);
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		dependent.setGender(strGender);
		dependent.setPhone(strPhone);
		
		currentAccount.addUser(dependent);
		boolean bSuccess = persistAccount(currentAccount);
		
		return bSuccess ? currentAccount : null;
	}
	
	private String getLatestDependentDetails(Account currentAccount) {
		StringBuffer sbDependent = new StringBuffer();
		User dependent = currentAccount.getDependents().get(currentAccount.getDependents().size()-1);
		
		//Date date = dependent.getDateOfBirth();  
		//DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");  
		//String strBirthDate = dateFormat.format(date); 
		
		sbDependent.append("First Name: " + dependent.getFname() + "<br/>");
		sbDependent.append("Last Name: " + dependent.getLname() + "<br/>");
		sbDependent.append("Date of Birth: " + dependent.getDateOfBirth() + "<br/>");
		sbDependent.append("Gender: " + dependent.getGender() + "<br/>");
		sbDependent.append("Relationship with primary: " + dependent.getRelationship() + "<br/>");
		sbDependent.append("Phone: +1 " + dependent.getPhone() + "<br/>");
		
		return sbDependent.toString();
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
