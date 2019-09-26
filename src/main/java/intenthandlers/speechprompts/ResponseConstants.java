package intenthandlers.speechprompts;

public class ResponseConstants {
	private ResponseConstants() {}
	//Account Retrieval Error
	public static String ACCOUNT_RETRIEVAL_ERROR_SPEECH = "I'm sorry, I was not able to retrive your account information";
	public static String ACCOUNT_RETRIEVAL_ERROR_CARD="There was a problem retriving your account. Please call customer service.";
	//No claims
	public static String NO_CLAIMS_SPEECH="You have no claims of this type at this time";
	public static String NO_CLAIMS_CARD="No clams found";
	//Progressive Response
	public static String PROGRESSIVE_RESPONSE_1="Give me a moment as I look that up for you.";
	
	//Repompts
	public static String REPROMPT_TEXT = "I hope you are still with me ?";
}
