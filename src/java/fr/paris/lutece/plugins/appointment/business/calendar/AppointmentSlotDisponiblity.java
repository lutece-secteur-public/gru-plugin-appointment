package fr.paris.lutece.plugins.appointment.business.calendar;

import java.sql.Timestamp;


public class AppointmentSlotDisponiblity  
	{
		  
	    private int _nIdSlot;
	    private AppointmentSlot _appointmentSlot ;
	    private Timestamp _freeDate;
	    private String _nIdSession;
	    
		public int getIdSlot() {
			return _nIdSlot;
		}
		public void setNIdSlot(int _nIdSlot) {
			this._nIdSlot = _nIdSlot;
		}
		public AppointmentSlot getAppointmentSlot() {
			return _appointmentSlot;
		}
		public void setAppointmentSlot(AppointmentSlot _appointmentSlot) {
			this._appointmentSlot = _appointmentSlot;
		}
		public Timestamp getFreeDate() {
			return _freeDate;
		}
		public void setFreeDate(Timestamp _blockDate) {
			this._freeDate = _blockDate;
		}
		public String getIdSession() {
			return _nIdSession;
		}
		public void setIdSession(String _nIdSession) {
			this._nIdSession = _nIdSession;
		}
}
