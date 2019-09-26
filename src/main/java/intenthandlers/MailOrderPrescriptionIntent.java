package intenthandlers;

import java.io.IOException;
import java.util.HashMap;
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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import intenthandlers.speechprompts.ResponseConstants;
import models.Account;
import models.Medicine;
import models.Prescription;
import utils.DisplayUtils;
import utils.RequestUtils;
import warehouse.memorystore.AcctMemoryStoreImpl;
import warehouse.memorystore.PrescriptionMemoryStoreImpl;
 
public class MailOrderPrescriptionIntent implements IntentRequestHandler {
	private static String INTENT_NAME = "MailOrderPrescriptionIntent";
	private static String CARD_TITLE = "Prescription Details";
	private static String PRESCRIPTION_DATE = "PrescriptionDate";
	private static String MEDICINE_NAME = "MedicineName";
	private static String MEDICINE_QTY = "MedicineQuantity";
	private static String CONTINUE_PRESCRIPTION = "ContinuePrescription";
	private static String DELIVERY_ADD_TYPE = "DeliveryAddressType";
	private static String DELIVERY_ADDRESS = "DeliveryAddress";
	
	private static String CACHE_KEY_MEDICINES = "PrescriptionMedicines";
	
	private static final Logger logger = LogManager.getLogger(MailOrderPrescriptionIntent.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private Map<String, Object> cache = null;
	
	@Override
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		mapper.enableDefaultTyping();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return input.matches(Predicates.intentName(INTENT_NAME));
	}

	@SuppressWarnings("unchecked")
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
		
		Map<String, String> hmMedicines = (HashMap<String, String>)readSessionAttributes(input, CACHE_KEY_MEDICINES);
		
		Map<String, Slot> slots = RequestUtils.getSlots(input);
		String strPrescriptionDate = slots.get(PRESCRIPTION_DATE).getValue();
		String strMedicineName = slots.get(MEDICINE_NAME).getValue();
		String strMedicineQty = slots.get(MEDICINE_QTY).getValue();
		String strContinue = slots.get(CONTINUE_PRESCRIPTION).getValue();
		String strDeliveryAddType = slots.get(DELIVERY_ADD_TYPE).getValue();
		String strDeliveryAddress = slots.get(DELIVERY_ADDRESS).getValue();
		logger.debug("Medicine Map retrieved from cache:"+hmMedicines);
		
		if(intentRequest.getDialogState() == DialogState.STARTED || intentRequest.getDialogState() == DialogState.IN_PROGRESS) {
			logger.info("MailOrderPrescriptionIntent.DialogState:" + intentRequest.getDialogState());
			Intent intent = intentRequest.getIntent();
			
			logger.debug("strPrescriptionDate:" + strPrescriptionDate);
			logger.debug("strMedicineName:" + strMedicineName);
			logger.debug("strMedicineQty:" + strMedicineQty);
			logger.debug("strContinue:" + strContinue);
			logger.debug("strDeliveryAddType:" + strDeliveryAddType);
			logger.debug("strDeliveryAddress:" + strDeliveryAddress);
			
			if(strPrescriptionDate == null || strPrescriptionDate.length() == 0) {
				//return input.getResponseBuilder().withSpeech(getPrompt(PRESCRIPTION_DATE)).addElicitSlotDirective(PRESCRIPTION_DATE, intent).build();
			} else if(strMedicineName == null || strMedicineName.length() == 0) {
				return input.getResponseBuilder().withSpeech(getPrompt(MEDICINE_NAME)).addElicitSlotDirective(MEDICINE_NAME, intent).build();
			} else if(strMedicineQty == null || strMedicineQty.length() == 0) {
				return input.getResponseBuilder().withSpeech(getPrompt(MEDICINE_QTY)).addElicitSlotDirective(MEDICINE_QTY, intent).build();
			} else if(strContinue == null || strContinue.length() == 0) {
				hmMedicines.put(strMedicineName, strMedicineQty);
				writeSessionAttributes(input, CACHE_KEY_MEDICINES, hmMedicines);
				return input.getResponseBuilder().withSpeech(getPrompt(CONTINUE_PRESCRIPTION)).addElicitSlotDirective(CONTINUE_PRESCRIPTION, intent).build();
			} else if("Yes".equalsIgnoreCase(strContinue)) {//If Continue, empty the medicine name & qty slots and restart the slot filling
		        Slot updateSlot = Slot.builder()
		                .withName(MEDICINE_NAME)
		                .withValue(null)
		                .build();
		        intent.getSlots().put(MEDICINE_NAME, updateSlot);
		        
		        Slot updateSlot2 = Slot.builder()
		                .withName(MEDICINE_QTY)
		                .withValue(null)
		                .build();
		        intent.getSlots().put(MEDICINE_QTY, updateSlot2);
		        
		        Slot updateSlot3 = Slot.builder()
		                .withName(CONTINUE_PRESCRIPTION)
		                .withValue(null)
		                .build();
		        intent.getSlots().put(CONTINUE_PRESCRIPTION, updateSlot3);
		        return input.getResponseBuilder().withSpeech(getPrompt(MEDICINE_NAME)).addElicitSlotDirective(MEDICINE_NAME, intent).build();
			} else if(strDeliveryAddType == null || strDeliveryAddType.length() == 0) {
				//return input.getResponseBuilder().withSpeech(getPrompt(DELIVERY_ADD_TYPE)).addElicitSlotDirective(DELIVERY_ADD_TYPE, intent).build();
			} else if(strDeliveryAddType.toLowerCase().contains("other") && (strDeliveryAddress == null || strDeliveryAddress.length() == 0)) {
				return input.getResponseBuilder().withSpeech(getPrompt(DELIVERY_ADDRESS)).addElicitSlotDirective(DELIVERY_ADDRESS, intent).build();
			}

			logger.debug("hmMedicines: " + hmMedicines);
			return input.getResponseBuilder().addDelegateDirective(intent).build();

		} else {
			
			StringBuffer speechText = new StringBuffer();
			StringBuffer cardText = new StringBuffer();
			
			Prescription objPrescription = new Prescription();
			objPrescription.setPrescriptionDate(strPrescriptionDate);
			
			Account objAccount = null;
			
			try {
				objAccount = getAccountDetails();
			} catch (Exception e) {
			}
			
			if(objAccount == null) {
				logger.debug("Exception occurred while retrieving account");
				return input.getResponseBuilder().withSpeech(ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_SPEECH)
						.withSimpleCard(CARD_TITLE, ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_CARD)
						.withShouldEndSession(false).build();
			}
			strDeliveryAddress = strDeliveryAddress == null ? objAccount.getPrimary().getAddress() : strDeliveryAddress;
			objPrescription.setDeliveryAddress(strDeliveryAddress);
			
			String strDisplayText = "Prescription Date: " + strPrescriptionDate + "<br/>";
			
			for(Map.Entry<String, String> entry : hmMedicines.entrySet()) {
			    strDisplayText += "Medicine Name: " + entry.getKey() + ", Quantity: " + entry.getValue() + "<br/>";
			    Medicine medicine = new Medicine();
			    medicine.setMedicineName(entry.getKey());
			    medicine.setMedicineQuantity(entry.getValue());
			    objPrescription.addMedicine(medicine);
			}
			
			strDisplayText += "Delivery Address: " + strDeliveryAddress;
			
			logger.debug("objPrescription: " + objPrescription);
			
			boolean bSaved = persistPrescription(objPrescription);
			
			
			if(bSaved) {
				speechText.append("I have successfully placed the order for your prescription, please refer to the companion app for details");
				cardText.append("I have successfully placed the order for your prescription, please refer to the companion app for details");

				Template template = DisplayUtils.getBodyTemplate1(CARD_TITLE, strDisplayText, "", "");
				return input.getResponseBuilder().withSpeech(speechText.toString()).addRenderTemplateDirective(template)
						.withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
			} else {
				speechText.append("There is some error occurred while saving the prescription, please try again after some time");
				cardText.append("There is some error occurred while saving the prescription, please try again after some time");

				return input.getResponseBuilder().withSpeech(speechText.toString()).withSimpleCard(CARD_TITLE, cardText.toString())
						.withShouldEndSession(false).build();
			}
		}
	}
	
	private void writeSessionAttributes(HandlerInput input, String strKey, Object object) {
		Map<String, Object> cacheMedicines = new HashMap<String, Object>();
		String json = null;
		try {
			json = mapper.writeValueAsString(object);
			logger.info(json);
		} catch (JsonProcessingException e) {
			logger.catching(e);
		}
		cacheMedicines.put(strKey, json);
		input.getAttributesManager().setSessionAttributes(cacheMedicines);
	}
	
	@SuppressWarnings("unchecked")
	private Object readSessionAttributes(HandlerInput input, String strKey) {
		Map<String, String> hmMedicines = null;
		if (input.getAttributesManager().getSessionAttributes().isEmpty()) {
			hmMedicines = new HashMap<String, String>();
		} else {
			cache = input.getAttributesManager().getSessionAttributes();
			Object objMedicines = cache.get(strKey);
			try {
				if(objMedicines != null) {
					hmMedicines = mapper.readValue((String) objMedicines, HashMap.class);
				} else {
					hmMedicines = new HashMap<String, String>();
				}
				
			} catch (JsonParseException e) {
				logger.catching(e);
			} catch (JsonMappingException e) {
				logger.catching(e);
			} catch (IOException e) {
				logger.catching(e);
			}
		}
		return hmMedicines;
	}
	
	private String getPrompt(String strSlotName) {
		StringBuffer sbPrompt = new StringBuffer();
		
		switch(strSlotName) {
			case "PrescriptionDate": 
				sbPrompt.append("Please provide prescription date");
				break;

			case "MedicineName": 
				sbPrompt.append("Please tell me the name of the medicine");
				break;
				
			case "MedicineQuantity": 
				sbPrompt.append("What is the quantity of the medicine you want?");
				break;
				
			case "ContinuePrescription": 
				sbPrompt.append("OK, i have added it to the prescription, do you want to add more medicines?");
				break;
				
			case "DeliveryAddressType": 
				sbPrompt.append("Where do you want us to send your medicines, to your registered address or any other address?");
				break;
				
			case "DeliveryAddress": 
				sbPrompt.append("Ok, then please tell me the address");
				break;
		}
		return sbPrompt.toString();
	}
	
	private boolean persistPrescription(Prescription objPrescription) {
		logger.debug("Persisting the object:" + objPrescription);
		PrescriptionMemoryStoreImpl objPrescriptionImpl = new PrescriptionMemoryStoreImpl();
		try {
			objPrescriptionImpl.CreatePrescription(objPrescription);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	private Account getAccountDetails() throws Exception {
		logger.debug("Into the getAccountDetails()");
		return AcctMemoryStoreImpl.getAccount();
	}
}
