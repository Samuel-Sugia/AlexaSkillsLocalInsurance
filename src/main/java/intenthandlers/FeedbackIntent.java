package intenthandlers;

import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.request.Predicates;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import intenthandlers.speechprompts.ResponseConstants;
import models.Feedback;
import utils.RequestUtils;
import warehouse.memorystore.FeedbackMemoryStoreImpl;



public class FeedbackIntent implements RequestHandler {
	private static String INTENT_NAME = "FeedbackIntent";
	private static String CARD_TITLE = "Feedback";
	private static String USABILITY_RATING = "UsabilityRating";
	private static String CONTENT_RATING = "ContentRating";
	private static String RECOMMENDATION_RATING = "RecommendRating";
	private static String SKILL_RATING = "SkillRating";
	private static String FEEDBACK_TEXT = "FeedbackText";
	
	private static final Logger logger = LogManager.getLogger(FeedbackIntent.class);
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
		
		logger.info("FeedbackIntent handler invoked");
		
		StringBuffer speechText = new StringBuffer();
		StringBuffer cardText = new StringBuffer();

		Map<String, Slot> slots = RequestUtils.getSlots(input);
		String strUsabilityRating = slots.get(USABILITY_RATING).getValue();
		String strContentRating = slots.get(CONTENT_RATING).getValue();
		String strRecommendationRating = slots.get(RECOMMENDATION_RATING).getValue();
		String strSkillRating = slots.get(SKILL_RATING).getValue();
		String strFeedbackText = slots.get(FEEDBACK_TEXT).getValue();
		
		logger.debug("strUsabilityRating:" + strUsabilityRating);
		logger.debug("strContentRating:" + strContentRating);
		logger.debug("strRecommendationRating:" + strRecommendationRating);
		logger.debug("strSkillRating:" + strSkillRating);
		logger.debug("strFeedbackText:" + strFeedbackText);
		
		Feedback objFeedback = new Feedback();
		try {
			objFeedback.setUsabilityRating(Integer.parseInt(strUsabilityRating));
		} catch (Exception e) {
		}
		try {
			objFeedback.setContentRating(Integer.parseInt(strContentRating));
		} catch (Exception e) {
		}
		try {
			objFeedback.setRecommendationRating(Integer.parseInt(strRecommendationRating));
		} catch (Exception e) {
		}
		try {
			objFeedback.setSkillRating(Integer.parseInt(strSkillRating));
		} catch (Exception e) {
		}
		objFeedback.setFeedbackText(strFeedbackText);
		
		boolean bSuccess = persistFeedback(objFeedback);
		
		if(bSuccess) {
			speechText.append("Thank you for your valuable feedback!");
			sendEmail(objFeedback);
		} else {
			speechText.append("Some error encountered while saving the feedback, please try again after some time");
		}
		
		return input.getResponseBuilder().withSpeech(speechText.toString()).withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
	}
	
	private boolean persistFeedback(Feedback objFeedback) {
		logger.debug("Persisting the object:" + objFeedback);

		FeedbackMemoryStoreImpl objFeedbackImpl = new FeedbackMemoryStoreImpl();
		try {
			objFeedbackImpl.CreateFeedback(objFeedback);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void sendEmail(Feedback objFeedback) {
		//TODO: Implement logic to send email to the Helpdesk.
	}
}
