package warehouse.memorystore;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;

import models.Feedback;
import models.Prescription;

public class PrescriptionMemoryStoreImpl {
	private static final Logger logger = LogManager.getLogger(PrescriptionMemoryStoreImpl.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private static List<Prescription> lstPrescriptions;
	
	public PrescriptionMemoryStoreImpl() {
		if(lstPrescriptions == null) {
			lstPrescriptions = new ArrayList<Prescription>();
		}
	}
	
	public void CreatePrescription(Prescription objPrescription) throws Exception{
		lstPrescriptions.add(objPrescription);
		logger.debug("lstPrescriptions object stored to memory:" + lstPrescriptions);
		logger.debug("lstPrescriptions list size:" + lstPrescriptions.size());
	}
}
