package customPredicates;

import java.util.function.Predicate;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;

public class CustomPredicates {
	private CustomPredicates() {
	}

	public static <T extends Request> Predicate<HandlerInput> slotName(String slotName) {
		return i -> i.getRequestEnvelope().getRequest() instanceof IntentRequest
				&& ((IntentRequest) i.getRequestEnvelope().getRequest()).getIntent().getSlots() != null
				&& ((IntentRequest) i.getRequestEnvelope().getRequest()).getIntent().getSlots().containsKey(slotName);

	}

	public static <T extends Request> Predicate<HandlerInput> supportsDisplay() {
		return i -> i.getRequestEnvelope().getContext().getSystem().getDevice().getSupportedInterfaces().getDisplay()!=null;
	}

}
