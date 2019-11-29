package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.portal.service.daemon.Daemon;

public class SlotDaemon extends Daemon {

	@Override
	public void run() {
		
		SlotSafeService.cleanSlotlist();
		
	}

}
