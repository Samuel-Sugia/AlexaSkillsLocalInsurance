package intenthandlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

public class UpdatePersonalInfoIntent implements IntentRequestHandler {

	private static String INTENT_NAME = "UpdatePersonalInfoIntent";
	private static String CARD_TITLE = "Update Personal Details";
	private static String FIRST_NM = "FirstName";
	private static String PARAM_KEY = "PersonalInfo";
	private static String PARAM_VALUE = "ParamValue";
	private static String VOICE_PIN = "VoicePin";
	
	private static final Logger logger = LogManager.getLogger(FeedbackIntent.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private Map<String, Object> cache = null;
	
	@Override
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		mapper.enableDefaultTyping();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return input.matches(Predicates.intentName(INTENT_NAME));
	}

	@Override
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
		
		
		logger.info("UpdatePersonalInfoIntent.DialogState:" + intentRequest.getDialogState());
		
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
			//ResponseUtils.sendProgressiveResponse(input, "Please wait while I save the information to your account");
			
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
			
			Map<String, Slot> slots = RequestUtils.getSlots(input);
			StringBuffer speechText = new StringBuffer();
			StringBuffer cardText = new StringBuffer();
			
			User objUpdatedUser = updatePersonalInfo(currentAccount, slots);
			if(objUpdatedUser == null) {
				speechText.append("I'm sorry, I was not able to save your account information");
				cardText.append("There was a problem while saving your account. Please call customer service.");
				return input.getResponseBuilder().withSpeech(speechText.toString())
						.withSimpleCard(CARD_TITLE, cardText.toString())
						.withShouldEndSession(false).build();
			}
			
			speechText.append("I have successfully updated your personal details to your account. Please refer to the companion app for the details.");
			cardText.append("I have successfully updated your personal details to your account. Please refer to the companion app for the details.");
			
			String strPrimaryText = getLatestUserDetails(objUpdatedUser);
			
			Template template = DisplayUtils.getBodyTemplate1(CARD_TITLE, strPrimaryText, "", "");		
			
			try {
				cache = input.getAttributesManager().getSessionAttributes();
				String accountJson = mapper.writeValueAsString(currentAccount);
				cache.put("curAcct", accountJson);
				input.getAttributesManager().setSessionAttributes(cache);
			} catch (JsonProcessingException e) {
				logger.info("Couldn't persist account");
				logger.catching(e);
			}
			logger.debug("sending resonse back");
			return input.getResponseBuilder().withSpeech(speechText.toString()).addRenderTemplateDirective(template)
					.withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		}
	}
	
	private Account getAccountDetails() throws Exception {
		logger.debug("Into the getAccountDetails()");
		return AcctMemoryStoreImpl.getAccount();
	}
	
	private String getLatestUserDetails(User objUser) {
		StringBuffer sbUser = new StringBuffer();
		
		//Date date = dependent.getDateOfBirth();  
		//DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");  
		//String strBirthDate = dateFormat.format(date); 
		
		sbUser.append("First Name: " + objUser.getFname() + "<br/>");
		sbUser.append("Last Name: " + objUser.getLname() + "<br/>");
		sbUser.append("Date of Birth: " + objUser.getDateOfBirth() + "<br/>");
		sbUser.append("Gender: " + objUser.getGender() + "<br/>");
		sbUser.append("Relationship with primary: " + objUser.getRelationship() + "<br/>");
		sbUser.append("Phone: +1 " + objUser.getPhone() + "<br/>");
		
		return sbUser.toString();
	}
	
	private User updatePersonalInfo(Account currentAccount, Map<String, Slot> slots) {
		
		logger.debug("slots:" + slots);
		String strFirstName = slots.get(FIRST_NM).getValue();
		logger.info("strFirstName:" + strFirstName);
		
		//String strParamKey = slots.get(PARAM_KEY).getValue();
		String strParamKey = SlotTypeUtils.getResolvedSlotValue(slots.get(PARAM_KEY));
		logger.info("strParamKey:" + strParamKey);
		
		String strParamValue = slots.get(PARAM_VALUE).getValue();
		logger.info("strParamValue:" + strParamValue);
		
		User objUserToUpdate = null;
		
		if(currentAccount.getPrimary().getFname().equalsIgnoreCase(strFirstName)) {
			objUserToUpdate = updateUserProperty(currentAccount.getPrimary(), strParamKey, strParamValue);
			currentAccount.setPrimary(objUserToUpdate);
		} else {
			List<User> lstDependents = currentAccount.getDependents();
			int index = 0;
			for (User user : lstDependents) {
				if(user.getFname().equalsIgnoreCase(strFirstName)) {
					objUserToUpdate = updateUserProperty(user, strParamKey, strParamValue);
					lstDependents.set(index, objUserToUpdate);
					break;
				}
				index++;
			}
		}
		logger.info("Updated Account:" + currentAccount);
		
		boolean bPersistSuccess = persistAccount(currentAccount);
		
		if(bPersistSuccess) {
			return objUserToUpdate;
		} else {
			return null;
		}
	}
	
	private User updateUserProperty(User objUser, String strParamKey, String strParamValue) {
		switch(strParamKey.toLowerCase()) {
		case "first name": 
			objUser.setFname(strParamValue);
			break;

		case "1st name": 
			objUser.setFname(strParamValue);
			break;
			
		case "last name": 
			objUser.setLname(strParamValue);
			break;

		case "date of birth": 
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
				strParamValue = strParamValue.replace("second", "2");
				String strTemp = strParamValue.replaceAll("(?<=\\d)(st|nd|rd|th)", "");
				logger.debug("strTemp:" + strTemp);
				Date dtDOB = dateFormat.parse(strTemp);
				logger.info("Parsed date:" + dtDOB);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				strParamValue = sdf.format(dtDOB);
				objUser.setDateOfBirth(strParamValue);
				logger.info("Setting date in object:" + strParamValue);
			} catch(Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}

			break;
			
		case "gender": 
			objUser.setGender(strParamValue);
			break;
			
		case "relationship": 
			objUser.setRelationship(strParamValue);
			break;
			
		case "phone": 
			objUser.setPhone(strParamValue);
			break;
		}
		
		return objUser;
	}
	
	private boolean persistAccount(Account objAccount) {
		logger.debug("Persisting the object:" + objAccount);
		AcctMemoryStoreImpl.UpdateAccount(objAccount);
		return true;
	}
}
