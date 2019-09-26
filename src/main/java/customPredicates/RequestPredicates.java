package customPredicates;

import java.util.function.Predicate;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.slu.entityresolution.Resolution;
import com.amazon.ask.model.slu.entityresolution.StatusCode;

public class RequestPredicates {
	private RequestPredicates() {}
	
	
	public static <T extends Request> Predicate<HandlerInput> slotName(String slotName) {
		return i -> i.getRequestEnvelope().getRequest() instanceof IntentRequest
				&& ((IntentRequest) i.getRequestEnvelope().getRequest()).getIntent().getSlots() != null
				&& ((IntentRequest) i.getRequestEnvelope().getRequest()).getIntent().getSlots().containsKey(slotName);

	}

	public static <T extends Request> Predicate<HandlerInput> supportsDisplay() {
		return i -> (i.getRequestEnvelope().getContext().getDisplay() != null);
	}

	public static <T extends Request> Predicate<HandlerInput> supportsAPL(){
		return request -> request.getRequestEnvelope().getContext().getSystem().getDevice().getSupportedInterfaces()!=null;
	}
	
	public static Predicate<Resolution> intentUnderstands = (resolution) -> resolution.getStatus().getCode().equals(StatusCode.ER_SUCCESS_MATCH);
}
