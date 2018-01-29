package fr.paris.lutece.plugins.appointment.service.listeners;

/**
 * Interface for listeners that should be notified when form has been changed, created or removed. <b>The listener must be a Spring bean.</b>
 * 
 * @author Laurent Payen
 * 
 */
public interface IFormListener
{

    /**
     * Notify the listener that a form has been changed
     * 
     * @param nIdForm
     *            The id of the form
     */
    void notifyFormChange( int nIdForm );

    /**
     * Notify the listener that a new form has been created
     * 
     * @param nIdForm
     *            The id of the form
     */
    void notifyFormCreation( int nIdForm );

    /**
     * Notify the listener that a form has been deleted
     * 
     * @param nIdForm
     *            the id of the form
     */
    void notifyFormRemoval( int nIdForm );

}
