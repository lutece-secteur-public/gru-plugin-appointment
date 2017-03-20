package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Class of utilities
 * 
 * @author Laurent Payen
 *
 */
public class Utilities {

	public static final String FORMAT_DATE = "dd/MM/yyyy";
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE);

	/**
	 * Return the closest date in a list of date with the given date The return
	 * date will always be before the given date
	 * 
	 * @param listDate
	 *            the list of date
	 * @param dateToSearch
	 *            the date to search
	 * @return the closest date (not after the date to search)
	 */
	public static LocalDate getClosestDateInPast(List<LocalDate> listDate, LocalDate dateToSearch) {
		return listDate.stream().filter(x -> x.isBefore(dateToSearch) || x.isEqual(dateToSearch))
				.max(LocalDate::compareTo).orElse(null);
	}

	public static LocalDateTime getClosestDateTimeInFuture(List<LocalDateTime> listDateTime,
			LocalDateTime dateTimeToSearch) {
		return listDateTime.stream().filter(x -> x.isAfter(dateTimeToSearch) || x.isEqual(dateTimeToSearch))
				.min(LocalDateTime::compareTo).orElse(null);
	}

}
