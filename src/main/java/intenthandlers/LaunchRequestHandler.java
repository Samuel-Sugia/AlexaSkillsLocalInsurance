package intenthandlers;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import intenthandlers.speechprompts.ResponseConstants;
import models.Account;
import warehouse.memorystore.AcctMemoryStoreImpl;

public class LaunchRequestHandler implements RequestHandler {
	public static transient String WELCOME_MESSAGE = "Welcome to Local Insurance. How can i help?";
	private static final Logger logger = LogManager.getLogger(LaunchRequest.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	//private static final AccountDB accountDb = new AcctMongoImpl();
	// For session attributes
	//private Map<String, Object> cache = new HashMap<String,Object>();;

	public boolean canHandle(HandlerInput input) {
		// Identify if you understand the incoming intent.
		mapper.enableDefaultTyping();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return input.matches(Predicates.requestType(LaunchRequest.class));

	}

	public Optional<Response> handle(HandlerInput input) {
		
		// Place Skill Welcome Message.
		// RequestEnvelope test = input.getRequestEnvelope();
		// Fetch Account from DB if account not in session
		Account currentAccount = null;
		
		try {
			//accountDb.CreateAccount(createAccount());
			//accountDoc = accountDb.FindByAccountNumber(LocalInsuranceStreamHandler.getDemoAccountNumber()).get().toJson();
			//logger.info(accountDoc);
			currentAccount = AcctMemoryStoreImpl.getAccount();
			logger.info("Account Object:" + currentAccount);
		} catch (Exception e) {
			
			logger.catching(e);
			return input.getResponseBuilder().withSpeech(ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_SPEECH)
					.withSimpleCard("Account Error", ResponseConstants.ACCOUNT_RETRIEVAL_ERROR_CARD)
					.withShouldEndSession(false).withReprompt(ResponseConstants.REPROMPT_TEXT).build();
		}
	
		return input.getResponseBuilder()
				.withSpeech("Hello " + currentAccount.getPrimary().getFname() + ", " + WELCOME_MESSAGE)
				.withSimpleCard("Welcome", "Hello " + currentAccount.getPrimary().getFname() + ", " + WELCOME_MESSAGE)
				.withReprompt(WELCOME_MESSAGE).withShouldEndSession(false).build();
	}
}
