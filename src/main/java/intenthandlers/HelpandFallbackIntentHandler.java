package intenthandlers;

import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;

import intenthandlers.speechprompts.GenericStatement;
import intenthandlers.speechprompts.ResponseConstants;

public class HelpandFallbackIntentHandler implements RequestHandler {

	public boolean canHandle(HandlerInput input) {
		// Is the request coming in a "Help Request"
		return input.matches(Predicates.intentName("AMAZON.HelpIntent").or(Predicates.intentName("AMAZON.FallbackIntent")));
	}

	public Optional<Response> handle(HandlerInput input) {
		//How to use the skill logic.
		return input.getResponseBuilder()
				.withSimpleCard("How to use this skill",GenericStatement.HELP_SPEECH)
				.withSpeech(GenericStatement.HELP_SPEECH).addHintDirective("Try saying, \"Find a doctor\"").withReprompt(ResponseConstants.REPROMPT_TEXT).withShouldEndSession(false)
				.build();
	}

}
