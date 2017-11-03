package fr.paris.lutece.plugins.appointment.service.listeners;

/**
 * Interface for listeners that should be notified when week definition has been changed or removed. <b>The listener must be a Spring bean.</b>
 * 
 * @author Laurent Payen
 * 
 */
public interface IWeekDefinitionListener
{

    /**
     * Notify the listener that a week definition has been changed
     * 
     * @param nIdWeekDefinition
     *            The id of the weekDefinition
     */
    void notifyWeekDefinitionChange( int nIdWeekDefinition );

    /**
     * Notify the listener that a new week definition has been created
     * 
     * @param nIdWeekDefinition
     *            The id of the weekDefinition
     */
    void notifyWeekDefinitionCreation( int nIdWeekDefinition );

    /**
     * Notify the listener that a week definition has been deleted
     * 
     * @param nIdWeekDefinition
     *            the id of the weekDefinition
     */
    void notifyWeekDefinitionRemoval( int nIdWeekDefinition );
}
