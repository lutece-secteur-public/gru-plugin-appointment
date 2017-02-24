package fr.paris.lutece.plugins.appointment.service;

import static java.lang.Math.toIntExact;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class WorkingDayService {

	/**
	 * Name of the bean of the service
	 */
	public static final String BEAN_NAME = "appointment.workingDayService";

	/**
	 * Instance of the service
	 */
	private static volatile WorkingDayService _instance;

	/**
	 * Get an instance of the service
	 * 
	 * @return An instance of the service
	 */
	public static WorkingDayService getInstance() {
		if (_instance == null) {
			_instance = SpringContextService.getBean(BEAN_NAME);
		}

		return _instance;
	}

	/**
	 * 
	 * @param nIdWeekDefinition
	 * @param dayOfWeek
	 * @return
	 */
	public static WorkingDay generateWorkingDay(int nIdWeekDefinition, DayOfWeek dayOfWeek) {
		WorkingDay workingDay = new WorkingDay();
		workingDay.setIdWeekDefinition(nIdWeekDefinition);
		workingDay.setDayOfWeek(dayOfWeek.getValue());
		WorkingDayHome.create(workingDay);
		return workingDay;
	}

	/**
	 * 
	 * @param nIdWeekDefinition
	 * @param dayOfWeek
	 * @param startingTime
	 * @param endingTime
	 * @param nDuration
	 */
	public static void generateWorkingDayAndListTimeSlot(int nIdWeekDefinition, DayOfWeek dayOfWeek,
			LocalTime startingTime, LocalTime endingTime, int nDuration, int nMaxCapacity) {
		WorkingDay workingDay = generateWorkingDay(nIdWeekDefinition, dayOfWeek);
		TimeSlotService.generateListTimeSlot(workingDay.getIdWorkingDay(), startingTime, endingTime, nDuration,
				nMaxCapacity);
	}

	/**
	 * 
	 * @param appointmentForm
	 * @return
	 */
	public static List<DayOfWeek> getOpenDays(AppointmentForm appointmentForm) {
		List<DayOfWeek> openDays = new ArrayList<>();
		if (appointmentForm.getIsOpenMonday()) {
			openDays.add(DayOfWeek.MONDAY);
		}
		if (appointmentForm.getIsOpenTuesday()) {
			openDays.add(DayOfWeek.TUESDAY);
		}
		if (appointmentForm.getIsOpenWednesday()) {
			openDays.add(DayOfWeek.WEDNESDAY);
		}
		if (appointmentForm.getIsOpenThursday()) {
			openDays.add(DayOfWeek.THURSDAY);
		}
		if (appointmentForm.getIsOpenFriday()) {
			openDays.add(DayOfWeek.FRIDAY);
		}
		if (appointmentForm.getIsOpenSaturday()) {
			openDays.add(DayOfWeek.SATURDAY);
		}
		if (appointmentForm.getIsOpenSunday()) {
			openDays.add(DayOfWeek.SUNDAY);
		}
		return openDays;
	}

	/**
	 * 
	 * @param nIdWeekDefinition
	 * @return
	 */
	public static List<WorkingDay> findListWorkingDayByWeekDefinition(int nIdWeekDefinition) {
		List<WorkingDay> listWorkingDay = WorkingDayHome.findByIdWeekDefinition(nIdWeekDefinition);
		for (WorkingDay workingDay : listWorkingDay) {
			workingDay.setListTimeSlot(TimeSlotService.findListTimeSlotByWorkingDay(workingDay.getIdWorkingDay()));
		}
		return listWorkingDay;
	}

	/**
	 * l
	 * 
	 * @param listWorkingDay
	 */
	public static void deleteListWorkingDay(List<WorkingDay> listWorkingDay) {
		for (WorkingDay workingDay : listWorkingDay) {
			WorkingDayHome.delete(workingDay.getIdWorkingDay());
		}
	}

	public static WorkingDay findWorkingDayById(int nIdWorkingDay) {
		WorkingDay workingDay = WorkingDayHome.findByPrimaryKey(nIdWorkingDay);		
		return workingDay;
	}
	
	public static WorkingDay findWorkingDayWithListTimeSlotById(int nIdWorkingDay) {
		WorkingDay workingDay = WorkingDayHome.findByPrimaryKey(nIdWorkingDay);
		workingDay.setListTimeSlot(TimeSlotService.findListTimeSlotByWorkingDay(nIdWorkingDay));
		return workingDay;
	}

	public static LocalTime getMaxEndingTimeOfAWorkingDay(WorkingDay workingDay) {
		LocalTime endingTime = null;
		LocalTime endingTimeTemp;
		for (TimeSlot timeSlot : workingDay.getListTimeSlot()) {
			endingTimeTemp = timeSlot.getEndingTime(); 
			if (endingTime == null || endingTimeTemp.isAfter(endingTime)) {
				endingTime = endingTimeTemp;
			}			
		}
		return endingTime;
	}
	
	public static int getMinDurationTimeSlotOfAWorkingDay(WorkingDay workingDay){
		long lMinDuration = 0;
		LocalTime startingTimeTemp;
		LocalTime endingTimeTemp;
		long lDurationTemp;
		for (TimeSlot timeSlot : workingDay.getListTimeSlot()) {
			startingTimeTemp = timeSlot.getStartingTime();
			endingTimeTemp = timeSlot.getEndingTime();
			lDurationTemp = startingTimeTemp.until(endingTimeTemp, ChronoUnit.MINUTES);
			if (lMinDuration == 0 || lMinDuration > lDurationTemp) {
				lMinDuration = lDurationTemp;
			}			
		}
		return toIntExact(lMinDuration);
	}
}
