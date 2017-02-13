package fr.paris.lutece.plugins.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
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
	
	public static WorkingDay generateWorkingDay(int nIdWeekDefinition, DayOfWeek dayOfWeek) {
		WorkingDay workingDay = new WorkingDay();
		workingDay.setIdWeekDefinition(nIdWeekDefinition);
		workingDay.setDayOfWeek(dayOfWeek.getValue());
		WorkingDayHome.create(workingDay);
		return workingDay;
	}
	
	public static WorkingDay generateWorkingDayAndListTimeSlot(int nIdWeekDefinition, DayOfWeek dayOfWeek, LocalTime startingHour,
			LocalTime endingHour, int nDuration) {
		WorkingDay workingDay = generateWorkingDay(nIdWeekDefinition, dayOfWeek);
		workingDay.setListTimeSlot(TimeSlotService.generateListTimeSlot(workingDay.getIdWorkingDay(), startingHour, endingHour, nDuration));
		return workingDay;
	}
	
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
	
	public static List<WorkingDay> findListWorkingDayByWeekDefinition(int nIdWeekDefinition){
		List<WorkingDay> listWorkingDay = WorkingDayHome.findByIdWeekDefinition(nIdWeekDefinition);
		for (WorkingDay workingDay : listWorkingDay){
			workingDay.setListTimeSlot(TimeSlotService.findListTimeSlotByWorkingDay(workingDay.getIdWorkingDay()));
		}
		return listWorkingDay;
	}
	
}
