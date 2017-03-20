package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.display.DisplayHome;

public class DisplayService {

	/**
	 * 
	 * @param display
	 * @param appointmentForm
	 * @param nIdForm
	 * @return
	 */
	public static Display fillInDisplayWithAppointmentForm(Display display, AppointmentForm appointmentForm,
			int nIdForm) {
		display.setDisplayTitleFo(appointmentForm.getDisplayTitleFo());
		display.setIcon(appointmentForm.getIcon());
		display.setNbWeeksToDisplay(appointmentForm.getNbWeeksToDisplay());
		display.setIdCalendarTemplate(appointmentForm.getCalendarTemplateId());
		display.setIdForm(nIdForm);
		return display;
	}

	/**
	 * 
	 * @param appointmentForm
	 * @param nIdForm
	 * @return
	 */
	public static Display createDisplay(AppointmentForm appointmentForm, int nIdForm) {
		Display display = new Display();
		display = fillInDisplayWithAppointmentForm(display, appointmentForm, nIdForm);
		DisplayHome.create(display);
		return display;
	}

	/**
	 * 
	 * @param appointmentForm
	 * @param nIdForm
	 * @return
	 */
	public static Display updateDisplay(AppointmentForm appointmentForm, int nIdForm) {
		Display display = DisplayService.findDisplayWithFormId(nIdForm);
		display = fillInDisplayWithAppointmentForm(display, appointmentForm, nIdForm);
		DisplayHome.update(display);
		return display;
	}

	/**
	 * 
	 * @param nIdForm
	 * @return
	 */
	public static Display findDisplayWithFormId(int nIdForm) {
		return DisplayHome.findByIdForm(nIdForm);
	}

}
