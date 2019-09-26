package utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.services.reminderManagement.AlertInfo;
import com.amazon.ask.model.services.reminderManagement.AlertInfoSpokenInfo;
import com.amazon.ask.model.services.reminderManagement.PushNotification;
import com.amazon.ask.model.services.reminderManagement.PushNotificationStatus;
import com.amazon.ask.model.services.reminderManagement.Recurrence;
import com.amazon.ask.model.services.reminderManagement.RecurrenceFreq;
import com.amazon.ask.model.services.reminderManagement.ReminderRequest;
import com.amazon.ask.model.services.reminderManagement.SpokenText;
import com.amazon.ask.model.services.reminderManagement.Trigger;
import com.amazon.ask.model.services.reminderManagement.TriggerType;

public class ReminderUtils {

	private ReminderUtils() {
	}

	public static ReminderRequest createRecurringRequest(String subject, String time, String date, String Timezone) {
		//TODO : Implement Reccuring Reminders
		return null;
	}

	public static ReminderRequest createReminderRequest(String subject, String time, String date, String timezone) {
		// logger.info(String.format("\nCreate Request\nSubject: %s\nTime: %s\nDate
		// %s\nTimezone: %s ", subject, time,
		// date, timezone));
		LocalDateTime locdt = createDateTime(time, date);
		OffsetDateTime offsetDT = OffsetDateTime.now(ZoneId.of(timezone));
		return ReminderRequest.builder().withRequestTime(offsetDT)
				.withTrigger(Trigger.builder().withScheduledTime(locdt).withTimeZoneId(timezone)
						.withType(TriggerType.SCHEDULED_ABSOLUTE).build())
				.withPushNotification(PushNotification.builder().withStatus(PushNotificationStatus.ENABLED).build())
				.withAlertInfo(
						AlertInfo.builder()
								.withSpokenInfo(AlertInfoSpokenInfo.builder()
										.addContentItem(SpokenText.builder()
												.withText("This is your reminder for " + subject).build())
										.build())
								.build())
				.build();

	}

	public static void createReminder(HandlerInput input) {

	}

	/**
	 * helper function to create a date time from a string.
	 * 
	 * @param time
	 * @param date
	 * @return
	 */
	private static LocalDateTime createDateTime(String time, String date) {
		Integer year, month, day, hour, minute;
		String[] dateFields = date.split("-");
		String[] timeFields = time.split(":");
		year = Integer.parseInt(dateFields[0]);
		month = Integer.parseInt(dateFields[1]);
		day = Integer.parseInt(dateFields[2]);
		hour = Integer.parseInt(timeFields[0]);
		minute = Integer.parseInt(timeFields[1]);
		return LocalDateTime.of(year, month, day, hour, minute);

	}

	/**
	 * helper function to create push notification.
	 * 
	 * @param enable
	 * @return
	 */
	public static PushNotification createPushNotification(boolean enable) {
		if (enable)
			return PushNotification.builder().withStatus(PushNotificationStatus.ENABLED).build();
		else
			return PushNotification.builder().withStatus(PushNotificationStatus.DISABLED).build();
	}

	/**
	 * helper function to create a daily or weekly reminder.
	 * 
	 * @param enable
	 * @return
	 */
	public static Recurrence createDailyReccurance(boolean enable) {
		if (enable)
			return Recurrence.builder().withFreq(RecurrenceFreq.DAILY).build();
		else
			return Recurrence.builder().withFreq(RecurrenceFreq.WEEKLY).build();
	}

	/**
	 * Helper function to create a trigger for reminder.
	 * 
	 * @param recurrence
	 * @param triggertype
	 * @param timezone
	 * @return
	 */
	public static Trigger createTrigger(Recurrence recurrence, TriggerType triggertype, String timezone) {
		return Trigger.builder().withRecurrence(recurrence).withType(triggertype).withTimeZoneId(timezone).build();
	}

	/**
	 * helper function to create an {@link AlertInfo}
	 * 
	 * @param text
	 * @return
	 */
	public static AlertInfo createSpokenAlertInfo(String text) {
		SpokenText contentItem = SpokenText.builder().withText(text).build();
		AlertInfoSpokenInfo spokenInfo = AlertInfoSpokenInfo.builder().addContentItem(contentItem).build();
		return AlertInfo.builder().withSpokenInfo(spokenInfo).build();
	}

}