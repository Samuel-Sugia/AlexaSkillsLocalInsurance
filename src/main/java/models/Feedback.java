package models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "feedback")
public class Feedback {
	@JsonProperty("_id")
	private String id = null;
	private int skillRating;
	private int usabilityRating;
	private int contentRating;
	private int recommendationRating;
	private String feedbackText;
	
	public Feedback() {
		this.id = UUID.randomUUID().toString();
	}
	
	public int getSkillRating() {
		return skillRating;
	}

	public void setSkillRating(int skillRating) {
		this.skillRating = skillRating;
	}

	public int getUsabilityRating() {
		return usabilityRating;
	}

	public void setUsabilityRating(int usabilityRating) {
		this.usabilityRating = usabilityRating;
	}

	public int getContentRating() {
		return contentRating;
	}

	public void setContentRating(int contentRating) {
		this.contentRating = contentRating;
	}

	public int getRecommendationRating() {
		return recommendationRating;
	}

	public void setRecommendationRating(int recommendationRating) {
		this.recommendationRating = recommendationRating;
	}

	public String getFeedbackText() {
		return feedbackText;
	}

	public void setFeedbackText(String feedbackText) {
		this.feedbackText = feedbackText;
	}
	
	@Override
	public String toString() {
		return "skillRating="+this.skillRating+", usabilityRating="+this.usabilityRating+", contentRating="+this.contentRating+", recommendationRating"+this.recommendationRating+
				", feedbackText="+this.feedbackText;
	}
}
