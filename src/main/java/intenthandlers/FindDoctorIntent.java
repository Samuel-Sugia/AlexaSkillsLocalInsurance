package intenthandlers;

import java.util.ArrayList;
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
import com.amazon.ask.model.interfaces.display.ListItem;
import com.amazon.ask.model.interfaces.display.Template;
import com.amazon.ask.request.Predicates;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.Doctor;
import utils.DisplayUtils;
import utils.RequestUtils;
import utils.SlotTypeUtils;
import warehouse.memorystore.DocMemoryStoreImpl;



public class FindDoctorIntent implements IntentRequestHandler  {
	private static String INTENT_NAME = "FindDoctorIntent";
	private static String CARD_TITLE = "Doctor Details";
	private static String MEDICAL_TYPE = "PolicyDetailType";
	private static String MEDICAL_SPECIALITY = "MedicalSpeciality";
	private static String DENTAL_SPECIALITY = "DentalSpeciality";
	private static String ZIP_CODE = "ZipCode";
	private static String IN_OUT_NETWORK = "InOutNetwork";
	private static String SORT_PREFERENCE = "SortPreference";
	
	private static final Logger logger = LogManager.getLogger(FindDoctorIntent.class);
	private static ObjectMapper mapper = new ObjectMapper();
	//private Map<String, Object> cache = null;

	@Override
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		mapper.enableDefaultTyping();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return input.matches(Predicates.intentName(INTENT_NAME));
	}

	@Override
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {

		logger.info("FindDoctorIntent.intentRequest.getDialogState():" + intentRequest.getDialogState());
		
		if(intentRequest.getDialogState() == DialogState.STARTED || intentRequest.getDialogState() == DialogState.IN_PROGRESS) {
			Map<String, Slot> slots = RequestUtils.getSlots(input);
			String strMedicalType = slots.get(MEDICAL_TYPE).getValue();
			logger.info("FindDoctorIntent.strMedicalType:" + strMedicalType);
			String strMedicalSpeciality = slots.get(MEDICAL_SPECIALITY).getValue();
			logger.info("FindDoctorIntent.strMedicalSpeciality:" + strMedicalSpeciality);
			Intent intent = intentRequest.getIntent();
			if(strMedicalType != null && "Dental".equalsIgnoreCase(strMedicalType)) {
				 
		        Slot updateSlot = Slot.builder()
		                .withName(MEDICAL_SPECIALITY)
		                .withValue("NOT_APPLICABLE")
		                .build();

		        // Push the updated slot into the intent object
		        intent.getSlots().put(MEDICAL_SPECIALITY, updateSlot);
		        logger.info("Speciality Slot updated!");
			} else {
				if(strMedicalType == null || strMedicalType.length() == 0) {
					if(strMedicalSpeciality != null) {
						if("Dentist".equalsIgnoreCase(strMedicalSpeciality)) {
					        Slot updateSlot = Slot.builder()
					                .withName(MEDICAL_TYPE)
					                .withValue("dental")
					                .build();
					        intent.getSlots().put(MEDICAL_TYPE, updateSlot);
						} else {
					        Slot updateSlot = Slot.builder()
					                .withName(MEDICAL_TYPE)
					                .withValue("medical")
					                .build();
					        intent.getSlots().put(MEDICAL_TYPE, updateSlot);
					        Slot updateSlot2 = Slot.builder()
					                .withName(DENTAL_SPECIALITY)
					                .withValue("NOT_APPLICABLE")
					                .build();
					        intent.getSlots().put(DENTAL_SPECIALITY, updateSlot2);
						}
				        // Push the updated slot into the intent object
				        
				        logger.info("MedicalType Slot updated!");
					}
				} else if ("medical".equalsIgnoreCase(strMedicalType)){
			        Slot updateSlot2 = Slot.builder()
			                .withName(DENTAL_SPECIALITY)
			                .withValue("NOT_APPLICABLE")
			                .build();
			        intent.getSlots().put(DENTAL_SPECIALITY, updateSlot2);
				}
			}
			return input.getResponseBuilder().addDelegateDirective(intent).build();
			
		} else {
			StringBuffer speechText = new StringBuffer();
			StringBuffer cardText = new StringBuffer();

			//Getting Slots
			Map<String, Slot> slots = RequestUtils.getSlots(input);
			String strMedicalType = slots.get(MEDICAL_TYPE).getValue();
			String strMedicalSpeciality = slots.get(MEDICAL_SPECIALITY).getValue();
			String strDentalSpeciality = slots.get(DENTAL_SPECIALITY).getValue();
			String strZipCode = slots.get(ZIP_CODE).getValue();
			String strInOutNetwork =  slots.get(IN_OUT_NETWORK).getValue();
			String strSortPreference = SlotTypeUtils.getResolvedSlotValue(slots.get(SORT_PREFERENCE));
			
			logger.info("FindDoctorIntent.strMedicalType:" + strMedicalType);
			logger.info("FindDoctorIntent.strMedicalSpeciality:" + strMedicalSpeciality);
			logger.info("FindDoctorIntent.strZipCode:" + strZipCode);
			logger.info("FindDoctorIntent.inout tostring:" + strInOutNetwork);
			logger.info("FindDoctorIntent.inout strSortPreference:" + strSortPreference);
			
			//Optional<Slot> inOutNW = Optional.ofNullable(slots.get(IN_OUT_NETWORK));
			String InOutNWType = SlotTypeUtils.getResolvedSlotValue(slots.get(IN_OUT_NETWORK));
			
			logger.info("FindDoctorIntent.InOutNWType::-" + InOutNWType);
			
			speechText.append("I found some " + InOutNWType + " doctors in this area, ")
			.append("check the companion app for details");
			
			Template template = getDoctorTemplateResponse(strZipCode, "Dental".equalsIgnoreCase(strMedicalType) ? strDentalSpeciality : strMedicalSpeciality, strSortPreference, "In Network".equalsIgnoreCase(InOutNWType));
			logger.info(template.toString());
			return input.getResponseBuilder().withSpeech(speechText.toString()).addRenderTemplateDirective(template)
					.withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).build();

		}
	}
	
	private Template getDoctorTemplateResponse(String strZip, String strSpecialty, String strSortKey, boolean inNetwork) {
		logger.info("strZip:" + strZip);
		logger.info("strSpecialty:" + strSpecialty);
		logger.info("strSortKey:" + strSortKey);
		logger.info("inNetwork:" + inNetwork);
		
		List<ListItem> lstDisplayTemp = new ArrayList<ListItem>();
		DocMemoryStoreImpl objDocMemoryStoreImpl = new DocMemoryStoreImpl();
		List<Doctor> lstDoctors = objDocMemoryStoreImpl.findDoctors(strZip, strSpecialty, strSortKey, inNetwork);
		for (Doctor doctor : lstDoctors) {
			String strSecondaryText = doctor.getStreet();
			strSecondaryText += ", "+ doctor.getCity() + " " + doctor.getZipCode();
			strSecondaryText += "(" + doctor.getDistance() + " mi)";
			logger.info("strSecondaryText:" + strSecondaryText);
			lstDisplayTemp.add(DisplayUtils.createListItemNoImage(CARD_TITLE, "<font size=\"1\">" + doctor.getName() + "; Ph: " + doctor.getPhone() + "</font>", "<font size=\"1\">" + strSecondaryText + "</font>", "<font size=\"2\">" + doctor.getRating() + " / 5</font>", true));
		}
		return DisplayUtils.getListTemplate1(lstDisplayTemp, CARD_TITLE);
	}

}
