package fr.paris.lutece.plugins.appointment.business;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * The DTO for an appointment in front office
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentDTO extends Appointment implements Serializable
{

    private static final String PROPERTY_EMPTY_FIELD_FIRST_NAME = "appointment.validation.appointment.FirstName.notEmpty";
    private static final String PROPERTY_EMPTY_FIELD_LAST_NAME = "appointment.validation.appointment.LastName.notEmpty";
    private static final String PROPERTY_UNVAILABLE_EMAIL = "appointment.validation.appointment.Email.email";
    private static final String PROPERTY_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
    private static final String PROPERTY_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
    private static final String PROPERTY_UNVAILABLE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
    private static final String PROPERTY_EMPTY_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.notEmpty";
    private static final String PROPERTY_UNVAILABLE_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.error";
    private static final String PROPERTY_MAX_APPOINTMENT_PERIODE = "appointment.message.error.MaxAppointmentPeriode";
    private static final String PROPERTY_MAX_APPOINTMENT_PERIODE_BACK = "appointment.info.appointment.emailerror";
    private static final String PROPERTY_NB_DAY_BETWEEN_TWO_APPOINTMENTS = "appointment.validation.appointment.NbMinDaysBetweenTwoAppointments.error";
    public static final String PROPERTY_APPOINTMENT_STATUS_UNRESERVED = "appointment.message.labelStatusUnreserved";
    public static final String PROPERTY_APPOINTMENT_STATUS_RESERVED = "appointment.message.labelStatusReserved";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 703930649594406505L;

    /**
     * The Date of the appointment in string format
     */
    private String _strDateOfTheAppointment;

    /**
     * The starting date in LocalDateTime
     */
    private LocalDateTime _startingDateTime;

    /**
     * The starting time
     */
    private LocalTime _startingTime;

    /**
     * The ending time
     */
    private LocalTime _endingTime;

    /**
     * Tell if the appointment has been cancelled or not
     */
    private boolean _isCancelled;

    /**
     * The state of the appointment
     */
    private transient State _state;

    /**
     * The Form Id
     */
    private int _nIdForm;    

    /**
     * the number of booked seats for this appointment
     */
    private int _nNbBookedSeats;

    /**
     * The maximum number of seats the user can book
     */
    private int _nNbMaxPotentialBookedSeats;

    /**
     * The Map of the responses for the additional entries of the form
     */
    private Map<Integer, List<Response>> _mapResponsesByIdEntry = new HashMap<Integer, List<Response>>( );

    /**
     * The list of the responses for the additional entries of the form
     */
    private List<Response> _listResponse;

    /**
     * List of the available action of the workflow for this appointment
     */
    private transient Collection<Action> _listWorkflowActions;

    /**
     * Get the state of the appointment
     * 
     * @return the state of the appointment
     */
    public State getState( )
    {
        return _state;
    }

    /**
     * Set the state of the appointment
     * 
     * @param state
     *            the state to set
     */
    public void setState( State state )
    {
        this._state = state;
    }

    /**
     * Get the starting date time of the appointment
     * 
     * @return the starting date time
     */
    public LocalDateTime getStartingDateTime( )
    {
        return _startingDateTime;
    }

    /**
     * Set the starting date time of the appointment
     * 
     * @param startingDateTime
     *            the starting date time
     */
    public void setStartingDateTime( LocalDateTime startingDateTime )
    {
        this._startingDateTime = startingDateTime;
    }

    /**
     * Get the starting time of the appointment
     * 
     * @return the starting time
     */
    public LocalTime getStartingTime( )
    {
        return _startingTime;
    }

    /**
     * Set the starting time of the appointment
     * 
     * @param startingTime
     *            the starting time to set
     */
    public void setStartingTime( LocalTime startingTime )
    {
        this._startingTime = startingTime;
    }

    /**
     * Get the ending time of the appointment
     * 
     * @return the ending time
     */
    public LocalTime getEndingTime( )
    {
        return _endingTime;
    }

    /**
     * Set the ending time of the appointment
     * 
     * @param endingTime
     *            the ending time
     */
    public void setEndingTime( LocalTime endingTime )
    {
        this._endingTime = endingTime;
    }

    /**
     * Tell if the appointment is cancelled
     * 
     * @return true if the appointment is cancelled
     */
    public boolean getIsCancelled( )
    {
        return _isCancelled;
    }

    /**
     * Set if the appointment is cancelled
     * 
     * @param isCancelled
     *            the boolean value
     */
    public void setIsCancelled( boolean isCancelled )
    {
        this._isCancelled = isCancelled;
    }

    /**
     * Get the date of the appointment
     * 
     * @return the date of the appointment
     */
    public String getDateOfTheAppointment( )
    {
        return _strDateOfTheAppointment;
    }

    /**
     * Set the date of the appointment
     * 
     * @param strDateOfTheAppointment
     *            the date to set
     */
    public void setDateOfTheAppointment( String strDateOfTheAppointment )
    {
        this._strDateOfTheAppointment = strDateOfTheAppointment;
    }

    /**
     * Get the list of the responses of the additional entries of the form
     * 
     * @return the list of the responses
     */
    public List<Response> getListResponse( )
    {
        return _listResponse;
    }

    /**
     * Set the list of the responses of the additional entries of the form
     * 
     * @param listResponse
     *            the list of the responses to set
     */
    public void setListResponse( List<Response> listResponse )
    {
        this._listResponse = listResponse;
    }

    /**
     * Get the form Id
     * 
     * @return the form Id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the Form Id
     * 
     * @param nIdForm
     *            the form Id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }    

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
     * Set the number of booked seats for the appointment
     * 
     * @param nNumberOfPlacesReserved
     *            the number to set
     */
    public void setNbBookedSeats( int nNumberOfPlacesReserved )
    {
        this._nNbBookedSeats = nNumberOfPlacesReserved;
    }

    /**
     * Get the maximum number of booked seats the user can take
     * 
     * @return the max number of booked seats
     */
    public int getNbMaxPotentialBookedSeats( )
    {
        return _nNbMaxPotentialBookedSeats;
    }

    /**
     * Set the maximum number of booked seats the user can take
     * 
     * @param nNbMaxPotentialBookedSeats
     *            the maximum number to set
     */
    public void setNbMaxPotentialBookedSeats( int nNbMaxPotentialBookedSeats )
    {
        this._nNbMaxPotentialBookedSeats = nNbMaxPotentialBookedSeats;
    }

    /**
     * Get the available actions of the workflow for this appointment
     * 
     * @return the actions
     */
    public Collection<Action> getListWorkflowActions( )
    {
        return _listWorkflowActions;
    }

    /**
     * Set the available actions of the workflow for this appointment
     * 
     * @param listWorkflowActions
     */
    public void setListWorkflowActions( Collection<Action> listWorkflowActions )
    {
        this._listWorkflowActions = listWorkflowActions;
    }

    /**
     * Get the map of the responses of the additional entries of the form
     * 
     * @return the map of the responses
     */
    public Map<Integer, List<Response>> getMapResponsesByIdEntry( )
    {
        return _mapResponsesByIdEntry;
    }

    /**
     * Set the map of the responses of the addtional entries of the form to the appointment
     * 
     * @param mapResponsesByIdEntry
     *            the map to set
     */
    public void setMapResponsesByIdEntry( Map<Integer, List<Response>> mapResponsesByIdEntry )
    {
        this._mapResponsesByIdEntry = mapResponsesByIdEntry;
    }

    public void clearMapResponsesByIdEntry( )
    {
        this._mapResponsesByIdEntry.clear( );
    }

    /**
     * Get all the possible errors of the form
     * 
     * @param locale
     *            the locale
     * @return a list of all the possible errors of the form
     */
    public static List<String> getAllErrors( Locale locale )
    {
        List<String> listAllErrors = new ArrayList<String>( );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_FIELD_LAST_NAME, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_FIELD_FIRST_NAME, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_UNVAILABLE_EMAIL, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_MESSAGE_EMPTY_EMAIL, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_CONFIRM_EMAIL, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_UNVAILABLE_CONFIRM_EMAIL, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_NB_SEATS, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_UNVAILABLE_NB_SEATS, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_MAX_APPOINTMENT_PERIODE, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_MAX_APPOINTMENT_PERIODE_BACK, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_NB_DAY_BETWEEN_TWO_APPOINTMENTS, locale ) );
        return listAllErrors;
    }

}
