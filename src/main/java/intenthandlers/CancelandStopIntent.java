package intenthandlers;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.display.Template;
import com.amazon.ask.request.Predicates;

import customPredicates.RequestPredicates;
import intenthandlers.speechprompts.GenericStatement;
import utils.DisplayUtils;

public class CancelandStopIntent implements RequestHandler {
	static Logger logger = LogManager.getLogger(CancelandStopIntent.class);
	//private static ObjectMapper mapper = new ObjectMapper();
	//private Map<String, Object> cache = null;

	public boolean canHandle(HandlerInput input) {
		// TODO Auto-generated method stub
		return input
				.matches(Predicates.intentName("AMAZON.StopIntent").or(Predicates.intentName("AMAZON.CancelIntent")));
	}

	public Optional<Response> handle(HandlerInput input) {
		if(input.matches(RequestPredicates.supportsDisplay())) {
			Template template = DisplayUtils.getBodyTemplate6("Local Insurance", GenericStatement.GOODBYE_SPEECH, null);
			return input.getResponseBuilder().withSpeech(GenericStatement.GOODBYE_SPEECH)
					.withSimpleCard("Local Insurance", GenericStatement.GOODBYE_SPEECH).addRenderTemplateDirective(template).withShouldEndSession(true).build();
		}
		return input.getResponseBuilder()
				.withSpeech(GenericStatement.GOODBYE_SPEECH)
				.withSimpleCard("Local Insurance", GenericStatement.GOODBYE_SPEECH).withShouldEndSession(true).build();
	}

}
