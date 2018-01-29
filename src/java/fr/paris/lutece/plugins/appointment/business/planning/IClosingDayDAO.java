package fr.paris.lutece.plugins.appointment.business.planning;

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * ClosingDay DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IClosingDayDAO
{

    /**
     * The name of the bean of the DAO
     */
    static String BEAN_NAME = "appointment.closingDayDAO";

    /**
     * Insert a new record in the table.
     * 
     * @param closingDay
     *            instance of the Closing Day object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( ClosingDay closingDay, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param closingDay
     *            the reference of the Closing Day
     * @param plugin
     *            the Plugin
     */
    void update( ClosingDay closingDay, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdClosingDay
     *            int identifier of the Closing Day to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nIdClosingDay, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdClosingDay
     *            The identifier of the Closing Day
     * @param plugin
     *            the Plugin
     * @return The instance of the Closing Day
     */
    ClosingDay select( int nIdClosingDay, Plugin plugin );

    /**
     * Return the closing day if exists
     * 
     * @param nIdForm
     *            the Form Id
     * @param dateOfCLosingDay
     *            the date of the closing day
     * @param plugin
     *            the plugin
     * @return the closing day if exists
     */
    ClosingDay findByIdFormAndDateOfClosingDay( int nIdForm, LocalDate dateOfCLosingDay, Plugin plugin );

    /**
     * Returns the closing days of a form
     * 
     * @param nIdForm
     *            the form Id
     * @param plugin
     *            the plugin
     * @return a list of the closing days of the form
     */
    List<ClosingDay> findByIdForm( int nIdForm, Plugin plugin );

    /**
     * Returns the closing days of the form on a period
     * 
     * @param nIdForm
     *            the form Id
     * @param startingDate
     *            the starting date
     * @param endingDate
     *            the ending date
     * @param plugin
     *            the plugin
     * @return the list of the closing days that matches the criteria
     */
    List<ClosingDay> findByIdFormAndDateRange( int nIdForm, LocalDate startingDate, LocalDate endingDate, Plugin plugin );
}
