package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.display.DisplayHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;

/**
 * Service class for the display
 * 
 * @author Laurent Payen
 *
 */
public final class DisplayService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private DisplayService( )
    {
    }

    /**
     * Fill a display object with the appointment form DTO
     * 
     * @param display
     *            the display object
     * @param appointmentForm
     *            the appointmentform DTO
     * @param nIdForm
     *            the form Id
     * @return the display overload
     */
    public static Display fillInDisplayWithAppointmentForm( Display display, AppointmentFormDTO appointmentForm, int nIdForm )
    {
        display.setDisplayTitleFo( appointmentForm.getDisplayTitleFo( ) );
        display.setIcon( appointmentForm.getIcon( ) );
        display.setNbWeeksToDisplay( appointmentForm.getNbWeeksToDisplay( ) );
        display.setIsDisplayedOnPortlet( appointmentForm.getIsDisplayedOnPortlet( ) );
        display.setIdCalendarTemplate( appointmentForm.getCalendarTemplateId( ) );
        display.setIdForm( nIdForm );
        return display;
    }

    /**
     * Create a display object from an appointment form DTO
     * 
     * @param appointmentForm
     *            the appointment form DTO
     * @param nIdForm
     *            the form Id
     * @return the display object created
     */
    public static Display createDisplay( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        Display display = new Display( );
        display = fillInDisplayWithAppointmentForm( display, appointmentForm, nIdForm );
        DisplayHome.create( display );
        return display;
    }

    /**
     * Save a display of a form
     * 
     * @param display
     *            the display to save
     */
    public static void saveDisplay( Display display )
    {
        DisplayHome.create( display );
    }

    /**
     * Update a display object with the values of an appointment form DTO
     * 
     * @param appointmentForm
     *            the appointment form DTO
     * @param nIdForm
     *            the form Id
     * @return the display object updated
     */
    public static Display updateDisplay( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        Display display = DisplayService.findDisplayWithFormId( nIdForm );
        display = fillInDisplayWithAppointmentForm( display, appointmentForm, nIdForm );
        DisplayHome.update( display );
        return display;
    }

    /**
     * Find the display of the form
     * 
     * @param nIdForm
     *            the form Id
     * @return the display of the form
     */
    public static Display findDisplayWithFormId( int nIdForm )
    {
        return DisplayHome.findByIdForm( nIdForm );
    }

}
