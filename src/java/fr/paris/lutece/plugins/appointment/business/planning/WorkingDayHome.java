package fr.paris.lutece.plugins.appointment.business.planning;

import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Working Day objects
 * 
 * @author Laurent Payen
 *
 */
public class WorkingDayHome
{

    // Static variable pointed at the DAO instance
    private static IWorkingDayDAO _dao = SpringContextService.getBean( IWorkingDayDAO.BEAN_NAME );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private WorkingDayHome( )
    {
    }

    /**
     * Create an instance of the WorkingDay class
     * 
     * @param workingDay
     *            The instance of the WorkingDay which contains the informations to store
     * @return The instance of the WorkingDay which has been created with its primary key.
     */
    public static WorkingDay create( WorkingDay workingDay )
    {
        _dao.insert( workingDay, _plugin );

        return workingDay;
    }

    /**
     * Update of the WorkingDay which is specified in parameter
     * 
     * @param workingDay
     *            The instance of the WorkingDay which contains the data to store
     * @return The instance of the WorkingDay which has been updated
     */
    public static WorkingDay update( WorkingDay workingDay )
    {
        _dao.update( workingDay, _plugin );

        return workingDay;
    }

    /**
     * Delete the WorkingDay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The WorkingDay Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of the WorkingDay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The WorkingDay primary key
     * @return an instance of the WorkingDay
     */
    public static WorkingDay findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Find the Working Day of the weekDefinition
     * 
     * @param nIdWeekDefinition
     *            the WeekDefinition Id
     * @return a list of the workingDay defined
     */
    public static List<WorkingDay> findByIdWeekDefinition( int nIdWeekDefinition )
    {
        return _dao.findByIdWeekDefinition( nIdWeekDefinition, _plugin );
    }

}
