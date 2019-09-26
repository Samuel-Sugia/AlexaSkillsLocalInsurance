package utils;

import java.util.Map;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Slot;

public class RequestUtils {
	
	private RequestUtils() {}
	/**
	 * Helper function to get Slots from request
	 *
	 * @param input
	 * @return
	 */
	public static Map<String,Slot> getSlots(HandlerInput input) {
		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		Map<String,Slot> slots = intentRequest.getIntent().getSlots();
		return slots;
	}
	

}
