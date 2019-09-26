package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.ValueWrapper;

import customPredicates.RequestPredicates;

public class SlotTypeUtils {

	public static final String TELE_PATTERN = "(\\+1|)?(\\d{3})?(\\d{4})";
	public static final String TIME_PATTERN = "([01]?[0-9]|2[0-3]):([0-5][0-9])(?:(:[0-5][0-9])?)";
	public static final String TWO_CHAR_TIME_PATTERN = "(?=(MO|AF|EV|NI))\\1";
	public static final String DATE_PATTERN = "(?=(2[0-9] {3})-(0[1-9]|1[012])-([123]0|[012][1-9]|31))";
	public static final String WEEK_OF_YEAR_PATTERN = "(2[0-9]{3})-W(\\d{2})";
	public static final String WEEKEND_OF_YEAR_PATTERN = "(2[0-9]{3})-W(\\d{2})-WE";

	private SlotTypeUtils() {
	}

	/**
	 * Helper function to convert from duration(period) to LocalDate
	 * 
	 * @param duration
	 * @return
	 */
	public static LocalDate durationToLocalDate(String duration) {
		int day_of_year, year;
		Period period = Period.parse(duration);
		Integer days = (int) (period.getDays() + (period.getMonths() * 30) + period.getYears() * 365);
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, days);
		day_of_year = date.get(Calendar.DAY_OF_YEAR);
		year = date.get(Calendar.YEAR);
		return LocalDate.ofYearDay(year, day_of_year);
	}

	/**
	 * Helper function to Amazon date YYYY-MM-DD, YYYY-W##, YYYY-W##-WE return
	 * 
	 * @param input
	 * @return
	 */
	public static LocalDate dateToLocalDate(String input) {
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("YYYY-MM-DD");
		Matcher date = Pattern.compile(DATE_PATTERN).matcher(input);
		Matcher date1 = Pattern.compile(WEEK_OF_YEAR_PATTERN).matcher(input);
		Matcher date2 = Pattern.compile(WEEKEND_OF_YEAR_PATTERN).matcher(input);
		if (date.matches()) {
			return LocalDate.parse(input, format);
		} else if (date1.matches()) {
			cal.setWeekDate(Integer.parseInt(date1.group(1)), Integer.parseInt(date1.group(2)), Calendar.MONDAY);
			return LocalDate.ofYearDay(2019, cal.get(Calendar.DAY_OF_YEAR));
		} else if (date2.matches()) {
			cal.setWeekDate(Integer.parseInt(date1.group(1)), Integer.parseInt(date1.group(2)), Calendar.FRIDAY);
			return LocalDate.ofYearDay(2019, cal.get(Calendar.DAY_OF_YEAR));
		} else
			return null;
	}

	/**
	 * Helper method to handle Amazon Time format
	 * 
	 * @param input
	 * @return
	 */
	public static LocalTime timeToLocalTime(String input) {
		Matcher time = Pattern.compile(TIME_PATTERN).matcher(input);
		if (time.matches()) {
			return LocalTime.of(Integer.parseInt(time.group(1)), Integer.parseInt(time.group(2)));
		} else {
			switch (input) {
			case "MO":
				return LocalTime.of(07, 00);
			case "AF":
				return LocalTime.of(13, 00);
			case "EV":
				return LocalTime.of(17, 00);
			case "NI":
				return LocalTime.of(21, 00);
			default:
				return null;
			}
		}

	}

	/**
	 * Helper method to find the value from the entity resolution of an Actor slot type 
	 * @param actors slot of actor type;
	 * @param altValue alternate id of queried value
	 * @param idPrefix prefix of id value
	 * @return will return null if altId or idPrefix is not found
	 */
	public static String getActorValue(Slot actors,String altId,String idPrefix) {
		List<ValueWrapper> values = actors.getResolutions().getResolutionsPerAuthority().stream()
				.filter(RequestPredicates.intentUnderstands).findFirst().get().getValues();
		for (ValueWrapper value : values) {
			if (value.getValue().getId().contains(idPrefix) | value.getValue().getId().contains(altId))
				return value.getValue().getName();
		}
		return null;
	}
	
	/**
	 * Helper method to find the value from the entity resolution of a slot 
	 * @param objSlot slot of actor type;
	 * @return will return null if idPrefix is not found OR the first value if idPrefix is null
	 */
	public static String getResolvedSlotValue(Slot objSlot) {
		return getResolvedSlotValue(objSlot, null);
	}

	/**
	 * Helper method to find the value from the entity resolution of a slot
	 * @param objSlot slot of actor type;
	 * @param idPrefix prefix of id value
	 * @return will return null if idPrefix is not found OR the first value if idPrefix is null
	 */
	public static String getResolvedSlotValue(Slot objSlot, String idPrefix) {
		List<ValueWrapper> values = objSlot.getResolutions().getResolutionsPerAuthority().stream()
				.filter(RequestPredicates.intentUnderstands).findFirst().get().getValues();
		for (ValueWrapper value : values) {
			if (idPrefix == null || value.getValue().getId().contains(idPrefix))
				return value.getValue().getName();
		}
		return null;
	}
	
	/**
	 * Helper method to find the ID from the entity resolution of a slot 
	 * @param objSlot slot of actor type;
	 * @param idPrefix prefix of id value
	 * @return will return null if idPrefix is not found OR the first value if idPrefix is null
	 */
	public static String getResolvedSlotID(Slot objSlot) {
		List<ValueWrapper> values = objSlot.getResolutions().getResolutionsPerAuthority().stream()
				.filter(RequestPredicates.intentUnderstands).findFirst().get().getValues();
		if(values != null && values.size() > 0) {
			return values.get(0).getValue().getId();			
		}
		return null;
	}
	
}
