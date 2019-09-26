package intenthandlers;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.display.Template;
import com.amazon.ask.request.Predicates;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import intenthandlers.speechprompts.ResponseConstants;
import utils.DisplayUtils;

public class CustomerSupportIntent implements RequestHandler {

	private static String INTENT_NAME = "CustomerSupportIntent";
	private static String CARD_TITLE = "Customer Support";
	private static final Logger logger = LogManager.getLogger(FeedbackIntent.class);
	private static ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public boolean canHandle(HandlerInput input) {
		mapper.enableDefaultTyping();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return input.matches(Predicates.intentName(INTENT_NAME));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		
		logger.info("CustomerSupportIntent handler invoked");
		
		StringBuffer speechText = new StringBuffer();
		StringBuffer cardText = new StringBuffer();

		speechText.append("You can reach out to our customer support at 1-800-4357-4357, please refer to the companion app for more details");
		cardText.append("Customer Support (24x7): 1-800-4357-4357");
		
		String strTemplateText = "Contact (24x7): 1-800-4357-4357<br/>";
		strTemplateText += "Dial 1 for Policy details<br/>";
		strTemplateText += "Dial 2 for Claims<br/>";
		strTemplateText += "Dial 3 for any other service<br/>";
		strTemplateText += "<br/>Email: support@localinsurance.com";
		
		Template template = DisplayUtils.getBodyTemplate1(CARD_TITLE, strTemplateText, "", "");
		
		return input.getResponseBuilder().withSpeech(speechText.toString()).addRenderTemplateDirective(template)
				.withSimpleCard(CARD_TITLE, cardText.toString()).withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
	}
}
