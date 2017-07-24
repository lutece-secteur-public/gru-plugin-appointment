package fr.paris.lutece.plugins.appointment.business.planning;

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * WeekDefinition DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IWeekDefinitionDAO
{

    /**
     * The name of the bean of the DAO
     */
    static String BEAN_NAME = "appointment.weekDefinitionDAO";

    /**
     * Insert a new record in the table
     * 
     * @param appointment
     *            instance of the WeekDefinition object to insert
     * @param plugin
     *            the plugin
     */
    void insert( WeekDefinition weekDefinition, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param weekDefinition
     *            the reference of the WeekDefinition
     * @param plugin
     *            the plugin
     */
    void update( WeekDefinition weekDefinition, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdWeekDefinition
     *            identifier of the WeekDefinition to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdWeekDefinition, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdWeekDefinition
     *            the identifier of the weekDefinition
     * @param plugin
     *            the plugin
     * @return the instance of the weekDefinition
     */
    WeekDefinition select( int nIdWeekDefinition, Plugin plugin );

    /**
     * Get all the week definitions of a form
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return a list of all the weekdefinitions of the form given
     */
    List<WeekDefinition> findByIdForm( int nIdForm, Plugin plugin );

    /**
     * Get the week definitions of a form for the date of apply
     * 
     * @param nIdForm
     *            the form id
     * @param dateOfApply
     *            the date of apply
     * @param plugin
     *            the plugin
     * @return the week definition
     */
    WeekDefinition findByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply, Plugin plugin );

    /**
     * Get the week definitions of a form for the closest date of apply
     * 
     * @param nIdForm
     *            the form id
     * @param dateOfApply
     *            the date of apply
     * @param plugin
     *            the plugin
     * @return the week definition
     */
    WeekDefinition findByIdFormAndClosestToDateOfApply( int nIdForm, LocalDate dateOfApply, Plugin plugin );
}
