package fr.paris.lutece.plugins.appointment.business.form;

import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Form objects
 * 
 * @author Laurent Payen
 *
 */
public final class FormHome
{

    // Static variable pointed at the DAO instance
    private static IFormDAO _dao = SpringContextService.getBean( IFormDAO.BEAN_NAME );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private FormHome( )
    {
    }

    /**
     * Create an instance of the Form class
     * 
     * @param form
     *            The instance of the Form which contains the informations to store
     * @return The instance of the Form which has been created with its primary key.
     */
    public static Form create( Form form )
    {
        _dao.insert( form, _plugin );

        return form;
    }

    /**
     * Update of the Form which is specified in parameter
     * 
     * @param form
     *            The instance of the Form which contains the data to store
     * @return The instance of the Form which has been updated
     */
    public static Form update( Form form )
    {
        _dao.update( form, _plugin );

        return form;
    }

    /**
     * Delete the Form whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Form Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of the Form whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Form primary key
     * @return an instance of the Form
     */
    public static Form findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Returns an instance of the Form by its title
     * 
     * @param strTitle
     *            The Form title
     * @return a list of the forms with this title
     */
    public static List<Form> findByTitle( String strTitle )
    {
        return _dao.findByTitle( strTitle, _plugin );
    }

    /**
     * Returns all the active forms
     * 
     * @return a list of all the active forms
     */
    public static List<Form> findActiveForms( )
    {
        return _dao.findActiveForms( _plugin );

    }

    /**
     * Returns all the active and displayd on portlet forms
     * 
     * @return a list of all the active and displayed on portlet forms
     */
    public static List<Form> findActiveAndDisplayedOnPortletForms( )
    {
        return _dao.findActiveAndDisplayedOnPortletForms( _plugin );

    }

    /**
     * Returns all the forms
     * 
     * @return a list of all the forms
     */
    public static List<Form> findAllForms( )
    {
        return _dao.findAllForms( _plugin );

    }

    /**
     * Get all the week definitions of the form
     * 
     * @param nIdForm
     *            the FOrm Id
     * @return the list of all the week definitions of the form
     */
    public static List<WeekDefinition> getListWeekDefinition( int nIdForm )
    {
        List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm( nIdForm );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            List<WorkingDay> listWorkingDay = WorkingDayHome.findByIdWeekDefinition( weekDefinition.getIdWeekDefinition( ) );
            for ( WorkingDay workingDay : listWorkingDay )
            {
                List<TimeSlot> listTimeSlot = TimeSlotHome.findByIdWorkingDay( workingDay.getIdWorkingDay( ) );
                workingDay.setListTimeSlot( listTimeSlot );
            }
            weekDefinition.setListWorkingDay( listWorkingDay );
        }
        return listWeekDefinition;
    }
}
