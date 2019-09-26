package models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "feedbackcatlog")
public class FeedbackCatalog {
	private int skillRating;
	private int skillRatingCount;
	
	private int usabilityRating;
	private int usabilityRatingCount;
	
	private int contentRating;
	private int contentRatingCount;
	
	private int recommendationRating;
	private int recommendationRatingCount;
	
	List<Feedback> feedbackList;

	public FeedbackCatalog() {
		this.skillRating = 0;
		this.skillRatingCount = 0;
		this.usabilityRating = 0;
		this.usabilityRatingCount = 0;
		this.contentRating = 0;
		this.contentRatingCount = 0;
		this.recommendationRating = 0;
		this.recommendationRatingCount = 0;
		feedbackList = new ArrayList<Feedback>();
	}
	
	public int getSkillRating() {
		return skillRating;
	}

	public void setSkillRating(int skillRating) {
		this.skillRatingCount++;
		this.skillRating = (this.skillRating + skillRating) / skillRatingCount;
	}

	public int getSkillRatingCount() {
		return skillRatingCount;
	}

	public void setSkillRatingCount(int skillRatingCount) {
		this.skillRatingCount = skillRatingCount;
	}

	public int getUsabilityRating() {
		return usabilityRating;
	}

	public void setUsabilityRating(int usabilityRating) {
		this.usabilityRatingCount++;
		this.usabilityRating = (this.usabilityRating + usabilityRating) / this.usabilityRatingCount++;
	}

	public int getUsabilityRatingCount() {
		return usabilityRatingCount;
	}

	public void setUsabilityRatingCount(int usabilityRatingCount) {
		this.usabilityRatingCount = usabilityRatingCount;
	}

	public int getContentRating() {
		return contentRating;
	}

	public void setContentRating(int contentRating) {
		this.contentRatingCount++;
		this.contentRating = (this.contentRating + contentRating) / this.contentRatingCount;
	}

	public int getContentRatingCount() {
		return contentRatingCount;
	}

	public void setContentRatingCount(int contentRatingCount) {
		this.contentRatingCount = contentRatingCount;
	}

	public int getRecommendationRating() {
		return recommendationRating;
	}

	public void setRecommendationRating(int recommendationRating) {
		this.recommendationRatingCount++;
		this.recommendationRating = (this.recommendationRating + recommendationRating) / this.recommendationRatingCount;
	}

	public int getRecommendationRatingCount() {
		return recommendationRatingCount;
	}

	public void setRecommendationRatingCount(int recommendationRatingCount) {
		this.recommendationRatingCount = recommendationRatingCount;
	}

	public List<Feedback> getFeedbackList() {
		return feedbackList;
	}

	public void addFeedback(Feedback feedback) {
		this.feedbackList.add(feedback);
		setSkillRating(feedback.getSkillRating());
		setUsabilityRating(feedback.getUsabilityRating());
		setContentRating(feedback.getContentRating());
		setRecommendationRating(feedback.getRecommendationRating());
	}
	
}
