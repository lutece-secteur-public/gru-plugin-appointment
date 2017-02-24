package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class TimeSlotService {

	/**
	 * Name of the bean of the service
	 */
	public static final String BEAN_NAME = "appointment.timeSlotService";

	/**
	 * Instance of the service
	 */
	private static volatile TimeSlotService _instance;

	/**
	 * Get an instance of the service
	 * 
	 * @return An instance of the service
	 */
	public static TimeSlotService getInstance() {
		if (_instance == null) {
			_instance = SpringContextService.getBean(BEAN_NAME);
		}

		return _instance;
	}

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
			WorkingDay workingDay = WorkingDayService.findWorkingDayWithListTimeSlotById(timeSlot.getIdWorkingDay());
			int nDuration = WorkingDayService.getMinDurationTimeSlotOfAWorkingDay(workingDay);
			LocalTime maxEndingTime = WorkingDayService.getMaxEndingTimeOfAWorkingDay(workingDay);
			// Need to delete all the time slot after this one 
			deleteListTimeSlot(getListTimeSlotAfterThisTimeSlot(timeSlot));
			// and to regenerate time slots after this one, with the god rules for the slot capacity
			
			WeekDefinition weekDefinition = WeekDefinitionService
					.findWeekDefinitionById(workingDay.getIdWeekDefinition());
			ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply(
					weekDefinition.getIdForm(), weekDefinition.getDateOfApply());
			generateListTimeSlot(timeSlot.getIdWorkingDay(), timeSlot.getEndingTime(),
					maxEndingTime,
					nDuration,
					reservationRule.getMaxCapacityPerSlot());
		}
		TimeSlotHome.update(timeSlot);
	}

	public static List<TimeSlot> getListTimeSlotAfterThisTimeSlot(TimeSlot timeSlot) {
		List<TimeSlot> listTimeSlotAfter = new ArrayList<>();
		LocalTime startingTimeRef = timeSlot.getStartingTime();
		for (TimeSlot timeSlotTemp : TimeSlotService.findListTimeSlotByWorkingDay(timeSlot.getIdWorkingDay())) {
			if (timeSlotTemp.getStartingTime().isAfter(startingTimeRef)) {
				listTimeSlotAfter.add(timeSlotTemp);
			}
		}
		return listTimeSlotAfter;
	}

	public static void deleteListTimeSlot(List<TimeSlot> listTimeSlot) {
		for (TimeSlot timeSlot : listTimeSlot) {
			TimeSlotHome.delete(timeSlot.getIdTimeSlot());
		}
	}
}
