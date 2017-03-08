package fr.paris.lutece.plugins.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;

public class TimeSlotService {

	/**
	 * 
	 * @param nIdWorkingDay
	 * @param startingTime
	 * @param endingTime
	 * @param nDuration
	 * @return
	 */
	public static List<TimeSlot> generateListTimeSlot(int nIdWorkingDay, LocalTime startingTime, LocalTime endingTime,
			int nDuration, int nMaxCapacity) {
		List<TimeSlot> listTimeSlot = new ArrayList<>();
		LocalTime tempStartingTime = startingTime;
		LocalTime tempEndingTime = startingTime.plus(nDuration, ChronoUnit.MINUTES);
		while (!tempEndingTime.isAfter(endingTime)) {
			listTimeSlot.add(generateTimeSlot(nIdWorkingDay, tempStartingTime, tempEndingTime,
					Boolean.TRUE.booleanValue(), nMaxCapacity));
			tempStartingTime = tempEndingTime;
			tempEndingTime = tempEndingTime.plus(nDuration, ChronoUnit.MINUTES);
		}
		return listTimeSlot;
	}

	/**
	 * 
	 * @param nIdWorkingDay
	 * @param startingTime
	 * @param endingTime
	 * @param isOpen
	 * @return
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
	 * 
	 * @param nIdWorkingDay
	 * @return
	 */
	public static List<TimeSlot> findListTimeSlotByWorkingDay(int nIdWorkingDay) {
		return TimeSlotHome.findByIdWorkingDay(nIdWorkingDay);
	}

	public static TimeSlot findTimeSlotById(int nIdTimeSlot) {
		return TimeSlotHome.findByPrimaryKey(nIdTimeSlot);
	}

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

	public static List<TimeSlot> findListTimeSlotAfterThisTimeSlot(TimeSlot timeSlot) {
		return TimeSlotService.findListTimeSlotByWorkingDay(timeSlot.getIdWorkingDay()).stream()
				.filter(x -> x.getStartingTime().isAfter(timeSlot.getStartingTime())).collect(Collectors.toList());
	}

	public static void deleteListTimeSlot(List<TimeSlot> listTimeSlot) {
		for (TimeSlot timeSlot : listTimeSlot) {
			TimeSlotHome.delete(timeSlot.getIdTimeSlot());
		}
	}

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

	public static List<TimeSlot> getSortedListTimeSlotAfterALocalTime(List<TimeSlot> listTimeSlot, LocalTime time) {
		return listTimeSlot.stream().filter(x -> x.getStartingTime().isAfter(time) || x.getStartingTime().equals(time))
				.sorted((e1, e2) -> e1.getStartingTime().compareTo(e2.getStartingTime())).collect(Collectors.toList());
	}

	public static TimeSlot getTimeSlotInListOfTimeSlotWithStartingTime(List<TimeSlot> listTimeSlot,
			LocalTime timeToSearch) {
		return listTimeSlot.stream().filter(x -> timeToSearch.equals(x.getStartingTime())).findFirst()
				.orElse(null);		
	}
}
