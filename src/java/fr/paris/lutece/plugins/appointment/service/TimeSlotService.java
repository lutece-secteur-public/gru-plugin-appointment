package fr.paris.lutece.plugins.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;

/**
 * Service class for the time slot
 * 
 * @author Laurent Payen
 *
 */
public class TimeSlotService {

	/**
	 * Build a list of timeSlot Object from a starting time to an endingTime
	 * 
	 * @param nIdWorkingDay
	 *            the workingDay Id
	 * @param startingTime
	 *            the starting time
	 * @param endingTime
	 *            the ending time
	 * @param nDuration
	 *            the duration of the slot
	 * @param nMaxCapacity
	 *            the maximum capacity of the slot
	 * @return the list of TimeSlot built
	 */
	public static List<TimeSlot> generateListTimeSlot(int nIdWorkingDay, LocalTime startingTime, LocalTime endingTime,
			int nDuration, int nMaxCapacity) {
		List<TimeSlot> listTimeSlot = new ArrayList<>();
		LocalTime tempStartingTime = startingTime;
		LocalTime tempEndingTime = startingTime.plusMinutes(nDuration);
		while (!tempEndingTime.isAfter(endingTime)) {
			listTimeSlot.add(generateTimeSlot(nIdWorkingDay, tempStartingTime, tempEndingTime,
					Boolean.TRUE.booleanValue(), nMaxCapacity));
			tempStartingTime = tempEndingTime;
			tempEndingTime = tempEndingTime.plusMinutes(nDuration);
		}
		return listTimeSlot;
	}

	/**
	 * Build a timeSlot with all its values
	 * 
	 * @param nIdWorkingDay
	 *            the workingDay Id
	 * @param startingTime
	 *            the starting time
	 * @param endingTime
	 *            the ending time
	 * @param isOpen
	 *            true if the slot is open
	 * @param nMaxCapacity
	 *            the maximum capacity of the slot
	 * @return the timeSLot built
	 */
	public static TimeSlot generateTimeSlot(int nIdWorkingDay, LocalTime startingTime, LocalTime endingTime,
			boolean isOpen, int nMaxCapacity) {
		TimeSlot timeSlot = new TimeSlot();
		timeSlot.setIdWorkingDay(nIdWorkingDay);
		timeSlot.setIsOpen(isOpen);
		timeSlot.setStartingTime(startingTime);
		timeSlot.setEndingTime(endingTime);
		timeSlot.setMaxCapacity(nMaxCapacity);
		TimeSlotHome.create(timeSlot);
		return timeSlot;
	}

	/**
	 * Find the time slots of a working day
	 * 
	 * @param nIdWorkingDay
	 *            the working day Id
	 * @return the list of the timeSlot of this workingDay
	 */
	public static List<TimeSlot> findListTimeSlotByWorkingDay(int nIdWorkingDay) {
		return TimeSlotHome.findByIdWorkingDay(nIdWorkingDay);
	}

	/**
	 * Find a timeSlot with its primary key
	 * 
	 * @param nIdTimeSlot
	 *            the timeSlot Id
	 * @return the timeSlot found
	 */
	public static TimeSlot findTimeSlotById(int nIdTimeSlot) {
		return TimeSlotHome.findByPrimaryKey(nIdTimeSlot);
	}

	/**
	 * Update a timeSLot in database
	 * 
	 * @param timeSlot
	 *            the timeSlot to update
	 * @param bEndingTimeHasChanged
	 *            if the ending time has changed, need to regenerate and update
	 *            all the next time slots
	 */
	public static void updateTimeSlot(TimeSlot timeSlot, boolean bEndingTimeHasChanged) {
		if (bEndingTimeHasChanged) {
			WorkingDay workingDay = WorkingDayService.findWorkingDayById(timeSlot.getIdWorkingDay());
			int nDuration = WorkingDayService.getMinDurationTimeSlotOfAWorkingDay(workingDay);
			LocalTime maxEndingTime = WorkingDayService.getMaxEndingTimeOfAWorkingDay(workingDay);
			// Need to delete all the time slot after this one
			deleteListTimeSlot(findListTimeSlotAfterThisTimeSlot(timeSlot));
			// and to regenerate time slots after this one, with the good rules
			// for the slot capacity
			WeekDefinition weekDefinition = WeekDefinitionService
					.findWeekDefinitionLightById(workingDay.getIdWeekDefinition());
			ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply(
					weekDefinition.getIdForm(), weekDefinition.getDateOfApply());
			generateListTimeSlot(timeSlot.getIdWorkingDay(), timeSlot.getEndingTime(), maxEndingTime, nDuration,
					reservationRule.getMaxCapacityPerSlot());
		}
		TimeSlotHome.update(timeSlot);
	}

	/**
	 * Find the next time slots of a given time slot
	 * 
	 * @param timeSlot
	 *            the time slot
	 * @return a list of the next time slots
	 */
	public static List<TimeSlot> findListTimeSlotAfterThisTimeSlot(TimeSlot timeSlot) {
		return TimeSlotService.findListTimeSlotByWorkingDay(timeSlot.getIdWorkingDay()).stream()
				.filter(x -> x.getStartingTime().isAfter(timeSlot.getStartingTime())).collect(Collectors.toList());
	}

	/**
	 * Delete in database time slots
	 * 
	 * @param listTimeSlot
	 *            the list of time slots to delete
	 */
	public static void deleteListTimeSlot(List<TimeSlot> listTimeSlot) {
		for (TimeSlot timeSlot : listTimeSlot) {
			TimeSlotHome.delete(timeSlot.getIdTimeSlot());
		}
	}

	/**
	 * Get the time slots of a list of working days
	 * 
	 * @param listWorkingDay
	 *            the list of the working days
	 * @param dateInWeek
	 *            the date in the week
	 * @return the list of the time slots
	 */
	public static List<TimeSlot> getListTimeSlotOfAListOfWorkingDay(List<WorkingDay> listWorkingDay,
			LocalDate dateInWeek) {
		List<TimeSlot> listTimeSlot = new ArrayList<>();
		for (WorkingDay workingDay : listWorkingDay) {
			for (TimeSlot timeSlot : workingDay.getListTimeSlot()) {
				// Need to add the current date to the hour
				timeSlot.setStartingDateTime(
						dateInWeek.with(DayOfWeek.of(workingDay.getDayOfWeek())).atTime(timeSlot.getStartingTime()));
				timeSlot.setEndingDateTime(
						dateInWeek.with(DayOfWeek.of(workingDay.getDayOfWeek())).atTime(timeSlot.getEndingTime()));
				listTimeSlot.add(timeSlot);
			}
		}
		return listTimeSlot;
	}

	/**
	 * Return an ordered and filtered list of time slots after a given time
	 * 
	 * @param listTimeSlot
	 *            the list of time slot to sort and filter
	 * @param time
	 *            the time
	 * @return the list ordered and filtered
	 */
	public static List<TimeSlot> getSortedListTimeSlotAfterALocalTime(List<TimeSlot> listTimeSlot, LocalTime time) {
		return listTimeSlot.stream().filter(x -> x.getStartingTime().isAfter(time) || x.getStartingTime().equals(time))
				.sorted((e1, e2) -> e1.getStartingTime().compareTo(e2.getStartingTime())).collect(Collectors.toList());
	}

	/**
	 * Returns the time slot in a list of time slot with the given starting time
	 * 
	 * @param listTimeSlot
	 *            the list of time slots
	 * @param timeToSearch
	 *            the starting time to search
	 * @return the time slot found
	 */
	public static TimeSlot getTimeSlotInListOfTimeSlotWithStartingTime(List<TimeSlot> listTimeSlot,
			LocalTime timeToSearch) {
		return listTimeSlot.stream().filter(x -> timeToSearch.equals(x.getStartingTime())).findFirst().orElse(null);
	}
}
