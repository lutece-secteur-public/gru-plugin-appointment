package fr.paris.lutece.plugins.appointment.web.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class AppointmentPackageDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7039306494521789575L;
	/**
     * the number of booked seats for this appointment package
    */
    private int _nNbBookedSeats;
	private Set<AppointmentDTO> _listAppointmentDTO = new HashSet<AppointmentDTO>( ) ;
	

	 /**
     * Get the number of booked seats for the appointment
     * 
     * @return the number of booked seats
     */
    public int getNbBookedSeats( )
    {
        return _nNbBookedSeats;
    }

    /**
     * Set the number of booked seats for the appointment package
     * 
     * @param nNumberOfPlacesReserved
     *            the number to set
     */
    public void setNbBookedSeats( int nNumberOfPlacesReserved )
    {
        _nNbBookedSeats = nNumberOfPlacesReserved;
    }
    
    /**
     * Get the list of the AppointmentDTO 
     * 
     * @return the list of the AppointmentDTO
     */
    public Set<AppointmentDTO> getListAppointmentDTO( )
    {
        return _listAppointmentDTO;
    }

    /**
     * Set the list of the AppointmentDTO
     * 
     * @param listAppointmentDTO
     *            the list of the AppointmentDTO to set
     */
    public void setListAppointmentDTO( Set<AppointmentDTO> listAppointmentDTO )
    {
        _listAppointmentDTO = listAppointmentDTO;
    }
    
    public void addAppointmentDto( AppointmentDTO appointmentDTO){
    	_listAppointmentDTO.add( appointmentDTO );
    }
}
