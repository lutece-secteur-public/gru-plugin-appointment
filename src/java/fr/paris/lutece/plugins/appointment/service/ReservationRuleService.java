package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class ReservationRuleService {

	/**
	 * Name of the bean of the service
	 */
	public static final String BEAN_NAME = "appointment.reservationRuleService";
	
	/**
	 * Instance of the service
	 */
	private static volatile ReservationRuleService _instance;

	/**
	 * Get an instance of the service
	 * 
	 * @return An instance of the service
	 */
	public static ReservationRuleService getInstance() {
		if (_instance == null) {
			_instance = SpringContextService.getBean(BEAN_NAME);
		}

		return _instance;
	}
	
	public static ReservationRule generateReservationRule(AppointmentForm appointmentForm, int nIdForm, LocalDate dateOfApply) {
		ReservationRule reservationRule = new ReservationRule();
		reservationRule.setDateOfApply(dateOfApply);
		reservationRule.setMaxCapacityPerSlot(appointmentForm.getMaxCapacityPerSlot());
		reservationRule.setMaxPeoplePerAppointment(appointmentForm.getMaxPeoplePerAppointment());
		reservationRule.setIdForm(nIdForm);
		ReservationRuleHome.create(reservationRule);
		return reservationRule;
	}
	
	public static ReservationRule findReservationRuleByIdFormAndDateOfApply(int nIdForm, LocalDate dateOfApply) {		
		ReservationRule reservationRule = ReservationRuleHome.findByIdFormAndDateOfApply(nIdForm, dateOfApply);
		return reservationRule;
	}
	
}
