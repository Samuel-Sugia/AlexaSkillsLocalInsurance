package utils;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.services.directive.Directive;
import com.amazon.ask.model.services.directive.Header;
import com.amazon.ask.model.services.directive.SendDirectiveRequest;
import com.amazon.ask.model.services.directive.SpeakDirective;

public class ResponseUtils {
	
	private ResponseUtils() {
		
	}
	
	/**
	 * This is a helper method to send a 
	 * easiest to use progressive response use this to send progressive response.
	 * @param input
	 * @param speech
	 */
	public static void sendProgressiveResponse(HandlerInput input, String speech) {
		input.getServiceClientFactory().getDirectiveService().enqueue(createSpeechDirective(input, speech));
	}
	
	/**
	 * This is helper method to send a progressive response
	 * @param input
	 * @param directive
	 */
	public static void sendProgressiveResponse(HandlerInput input, Directive directive) {
		String requestId = input.getRequestEnvelope().getRequest().getRequestId();
		Header header = Header.builder().withRequestId(requestId).build();
		SendDirectiveRequest sendDirectiveRequest = SendDirectiveRequest.builder().withDirective(directive).withHeader(header).build();
		input.getServiceClientFactory().getDirectiveService().enqueue(sendDirectiveRequest);
	}
	
	/**
	 * This is a helper method to create a SpeechDirective
	 * @param input
	 * @param speech
	 * @return
	 */
	public static SendDirectiveRequest createSpeechDirective(HandlerInput input, String speech) {
		String requestId = input.getRequestEnvelope().getRequest().getRequestId();
		Header header = Header.builder().withRequestId(requestId).build();
		Directive directive = SpeakDirective.builder().withSpeech(speech).build();
		return SendDirectiveRequest.builder().withDirective(directive).withHeader(header).build();
	}

}
