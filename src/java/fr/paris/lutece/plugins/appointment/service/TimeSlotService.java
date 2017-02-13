package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
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
	
	public static List<TimeSlot> generateListTimeSlot(int nIdWorkingDay, LocalTime startingHour, LocalTime endingHour,
			int nDuration) {
		List<TimeSlot> listTimeSlot = new ArrayList<>();
		LocalTime tempStartingHour = startingHour;
		LocalTime tempEndingHour = startingHour.plus(nDuration, ChronoUnit.MINUTES);
		while (!tempEndingHour.isAfter(endingHour)) {
			listTimeSlot.add(generateTimeSlot(nIdWorkingDay, tempStartingHour, tempEndingHour, Boolean.TRUE.booleanValue()));
			tempStartingHour = tempEndingHour;
			tempEndingHour = tempEndingHour.plus(nDuration, ChronoUnit.MINUTES);
		}
		return listTimeSlot;
	}
	
	public static TimeSlot generateTimeSlot(int nIdWorkingDay, LocalTime startingHour, LocalTime endingHour,
			boolean isOpen) {
		TimeSlot timeSlot = new TimeSlot();
		timeSlot.setIdWorkingDay(nIdWorkingDay);
		timeSlot.setIsOpen(isOpen);
		timeSlot.setStartingHour(startingHour);
		timeSlot.setEndingHour(endingHour);
		TimeSlotHome.create(timeSlot);
		return timeSlot;
	}
	
	public static List<TimeSlot> findListTimeSlotByWorkingDay(int nIdWorkingDay) {		
		return TimeSlotHome.findByIdWorkingDay(nIdWorkingDay);		
	}
}
