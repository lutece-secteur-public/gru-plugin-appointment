package fr.paris.lutece.plugins.appointment.business.planning;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * WorkingDay DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IWorkingDayDAO
{

    /**
     * The name of the bean of the DAO
     */
    static String BEAN_NAME = "appointment.workingDayDAO";

    /**
     * Generate a new primary key
     * 
     * @param plugin
     *            the Plugin
     * @return the new primary key
     */
    int getNewPrimaryKey( Plugin plugin );

    /**
     * Insert a new record in the table
     * 
     * @param workingDay
     *            instance of the WorkingDay object to insert
     * @param plugin
     *            the plugin
     */
    void insert( WorkingDay workingDay, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param workingDay
     *            the reference of the WorkingDay
     * @param plugin
     *            the plugin
     */
    void update( WorkingDay workingDay, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdWorkingDay
     *            identifier of the WorkingDay to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdWorkingDay, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdWorkingDay
     *            the identifier of the WorkingDay
     * @param plugin
     *            the plugin
     * @return the instance of the WorkingDay
     */
    WorkingDay select( int nIdWorkingDay, Plugin plugin );

    /**
     * Get all the working days of the weekdefinition given
     * 
     * @param nIdWeekDefinition
     *            the WeekDefinition id
     * @param plugin
     *            the Plugin
     * @return the list of all the working days of the weekdefinition
     */
    List<WorkingDay> findByIdWeekDefinition( int nIdWeekDefinition, Plugin plugin );

}
