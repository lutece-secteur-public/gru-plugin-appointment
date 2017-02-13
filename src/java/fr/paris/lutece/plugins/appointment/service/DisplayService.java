package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.display.DisplayHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class DisplayService {

	/**
	 * Name of the bean of the service
	 */
	public static final String BEAN_NAME = "appointment.displayService";
	
	/**
	 * Instance of the service
	 */
	private static volatile DisplayService _instance;

	/**
	 * Get an instance of the service
	 * 
	 * @return An instance of the service
	 */
	public static DisplayService getInstance() {
		if (_instance == null) {
			_instance = SpringContextService.getBean(BEAN_NAME);
		}

		return _instance;
	}
	
	public static Display generateDisplay(AppointmentForm appointmentForm, int nIdForm) {
		Display display = new Display();
		display.setDisplayTitleFo(appointmentForm.getDisplayTitleFo());
		display.setIcon(appointmentForm.getIcon());
		display.setNbWeeksToDisplay(appointmentForm.getNbWeeksToDisplay());
		display.setIdCalendarTemplate(appointmentForm.getCalendarTemplateId());
		display.setIdForm(nIdForm);
		DisplayHome.create(display);
		return display;
	}
	
	public static Display findDisplayWithFormId(int nIdForm){
		return DisplayHome.findByIdForm(nIdForm);
	}
	
}
