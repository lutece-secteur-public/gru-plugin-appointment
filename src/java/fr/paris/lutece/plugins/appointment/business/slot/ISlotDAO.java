package fr.paris.lutece.plugins.appointment.business.slot;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Slot DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface ISlotDAO
{

    /**
     * The name of the bean of the DAO
     */
    static String BEAN_NAME = "appointment.slotDAO";

    /**
     * Insert a new record in the table
     * 
     * @param slot
     *            instance of the Slot object to insert
     * @param plugin
     *            the plugin
     */
    void insert( Slot slot, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param slot
     *            the reference of the Slot
     * @param plugin
     *            the plugin
     */
    void update( Slot slot, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdSlot
     *            identifier of the Slot to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdSlot, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdSlot
     *            the identifier of the Slot
     * @param plugin
     *            the plugin
     * @return the instance of the Slot
     */
    Slot select( int nIdSlot, Plugin plugin );

    /**
     * Returns all the slot for the date range
     * 
     * @param nIdForm
     *            the Form Id
     * @param startingDateTime
     *            the starting date
     * @param endingDateTime
     *            the ending date
     * @param plugin
     *            the plugin
     * @return a list of slots whose dates are included in the given period
     */
    HashMap<LocalDateTime, Slot> findByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin );

    /**
     * Returns all the slots of a form
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return a list of all the slots of the form
     */
    List<Slot> findByIdForm( int nIdForm, Plugin plugin );

    /**
     * Returns all the open slots for the given date range
     * 
     * @param nIdForm
     *            the Form Id
     * @param startingDateTime
     *            the starting date
     * @param endingDateTime
     *            the ending date
     * @param plugin
     *            the plugin
     * @return a list of open slots whose dates are included in the given period
     */
    List<Slot> findOpenSlotsByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin );

    /**
     * Returns all the open slots
     * 
     * @param nIdForm
     *            the Form Id
     * @param plugin
     *            the plugin
     * @return a list of open slots
     */
    List<Slot> findOpenSlotsByIdForm( int nIdForm, Plugin plugin );

}
