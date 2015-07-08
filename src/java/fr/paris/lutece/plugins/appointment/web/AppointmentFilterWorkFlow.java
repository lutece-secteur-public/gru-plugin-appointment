package fr.paris.lutece.plugins.appointment.web;

import java.util.Comparator;

import fr.paris.lutece.plugins.appointment.business.Appointment;

public class AppointmentFilterWorkFlow implements
		Comparator<Appointment> {
	private boolean _bOrder;
	@Override
	public int compare(Appointment o1, Appointment o2) {
			int nState = 0;
			if (o1!=null && o2!= null && o1.getState() != null && o2.getState()!=null)
			{
				if (_bOrder )
					nState = o1.getState().getName().compareTo(o2.getState().getName());
				else
					nState = o2.getState().getName().compareTo(o1.getState().getName());
			}
	        return nState;
	}
	AppointmentFilterWorkFlow ( boolean bOrder )
	{
		_bOrder = bOrder ; 
	}
	
}
