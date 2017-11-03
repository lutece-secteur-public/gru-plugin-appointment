package fr.paris.lutece.plugins.appointment.service.listeners;

/**
 * Interface for listeners that should be notified when slot has been changed, created or removed. <b>The listener must be a Spring bean.</b>
 * 
 * @author Laurent Payen
 * 
 */
public interface ISlotListener
{

    /**
     * Notify the listener that a slot has been changed
     * 
     * @param nIdSlot
     *            The id of the slot
     */
    void notifySlotChange( int nIdSlot );

    /**
     * Notify the listener that a new week slot has been created
     * 
     * @param nIdSlot
     *            The id of the slot
     */
    void notifySlotCreation( int nIdSlot );

    /**
     * Notify the listener that a slot has been deleted
     * 
     * @param nIdSlot
     *            the id of the slot
     */
    void notifySlotRemoval( int nIdSlot );

}
