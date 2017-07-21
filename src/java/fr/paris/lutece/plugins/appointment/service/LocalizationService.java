package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.localization.Localization;
import fr.paris.lutece.plugins.appointment.business.localization.LocalizationHome;

/**
 * Service class for the localization
 * 
 * @author Laurent Payen
 *
 */
public final class LocalizationService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private LocalizationService( )
    {
    }

    /**
     * Fill a localization object with the appointment form DTO
     * 
     * @param localization
     *            the localization object
     * @param appointmentForm
     *            the appointmentform DTO
     * @param nIdForm
     *            the form Id
     * @return the localization overload
     */
    public static Localization fillInLocalizationWithAppointmentForm( Localization localization, AppointmentForm appointmentForm, int nIdForm )
    {
        localization.setLongitude( appointmentForm.getLongitude( ) );
        localization.setLatitude( appointmentForm.getLatitude( ) );
        localization.setAddress( appointmentForm.getAddress( ) );
        localization.setIdForm( nIdForm );
        return localization;
    }

    /**
     * Create a localization object from an appointment form DTO
     * 
     * @param appointmentForm
     *            the appointment form DTO
     * @param nIdForm
     *            the form Id
     * @return the display object created
     */
    public static Localization createLocalization( AppointmentForm appointmentForm, int nIdForm )
    {
        Localization localization = new Localization( );
        localization = fillInLocalizationWithAppointmentForm( localization, appointmentForm, nIdForm );
        LocalizationHome.create( localization );
        return localization;
    }

    /**
     * Update a localization object with the values of an appointment form DTO
     * 
     * @param appointmentForm
     *            the appointment form DTO
     * @param nIdForm
     *            the form Id
     * @return the localization object updated
     */
    public static Localization updateLocalization( AppointmentForm appointmentForm, int nIdForm )
    {
        Localization localization = LocalizationService.findLocalizationWithFormId( nIdForm );
        localization = fillInLocalizationWithAppointmentForm( localization, appointmentForm, nIdForm );
        LocalizationHome.update( localization );
        return localization;
    }

    /**
     * Find the Localization of the form
     * 
     * @param nIdForm
     *            the form Id
     * @return the Localization of the form
     */
    public static Localization findLocalizationWithFormId( int nIdForm )
    {
        return LocalizationHome.findByIdForm( nIdForm );
    }

}
