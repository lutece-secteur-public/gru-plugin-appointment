package fr.paris.lutece.plugins.appointment.exception;

public class AppointmentSavedException extends RuntimeException {

	private static final long serialVersionUID = 9155769692111357148L;
	
	  /**
     * Constructor
     *
     * @param strMessage
     *            The error message
     */
	public AppointmentSavedException(String strMessage) {
		
		super( strMessage );
	}
	
    public AppointmentSavedException(String strMessage, Exception e) {
		
		super( strMessage, e );
	}
}
