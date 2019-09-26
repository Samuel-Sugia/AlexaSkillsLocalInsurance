package warehouse.memorystore;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import models.Feedback;

public class FeedbackMemoryStoreImpl {
	private static final Logger logger = LogManager.getLogger(FeedbackMemoryStoreImpl.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private static List<Feedback> lstFeedback;
	
	public FeedbackMemoryStoreImpl() {
		if(lstFeedback == null) {
			lstFeedback = new ArrayList<Feedback>();
		}
	}
	
	public void CreateFeedback(Feedback objFeedback) throws Exception{
		lstFeedback.add(objFeedback);
		logger.debug("Feedback object stored to memory:" + lstFeedback);
		logger.debug("Feedback list size:" + lstFeedback.size());
	}
}
