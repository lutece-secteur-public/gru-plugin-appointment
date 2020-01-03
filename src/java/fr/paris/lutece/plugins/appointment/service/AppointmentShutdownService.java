package fr.paris.lutece.plugins.appointment.service;


import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.portal.service.init.ShutdownService;

public class AppointmentShutdownService implements ShutdownService {

	private static final String APPOINTMENT_SHUTDOWN="appointment";
	
	@Override
	public String getName() {
		
		return APPOINTMENT_SHUTDOWN;
	}

	/**
     * Shutdown the application
     */
	@Override
	public void process() {
		
		SlotHome.resetPotentialRemainingPlaces();
		
	}

}
