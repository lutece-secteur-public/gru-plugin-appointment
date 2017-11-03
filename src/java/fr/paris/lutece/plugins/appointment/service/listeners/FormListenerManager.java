package fr.paris.lutece.plugins.appointment.service.listeners;

import fr.paris.lutece.portal.service.spring.SpringContextService;

public final class FormListenerManager
{

    /**
     * Private default constructor
     */
    private FormListenerManager( )
    {
        // Nothing to do
    }

    /**
     * Notify listeners that a form has been changed
     * 
     * @param nIdForm
     *            The id of the form that has been changed
     */
    public static void notifyListenersFormCreation( int nIdForm )
    {
        for ( IFormListener formListener : SpringContextService.getBeansOfType( IFormListener.class ) )
        {
            formListener.notifyFormCreation( nIdForm );
        }
    }

    /**
     * Notify listeners that a form has been
     * 
     * @param nIdForm
     *            The id of the form that has been changed
     */
    public static void notifyListenersFormChange( int nIdForm )
    {
        for ( IFormListener formListener : SpringContextService.getBeansOfType( IFormListener.class ) )
        {
            formListener.notifyFormChange( nIdForm );
        }
    }

    /**
     * Notify listeners that a form is about to be removed
     * 
     * @param nIdForm
     *            The id of the form that will be removed
     */
    public static void notifyListenersFormRemoval( int nIdForm )
    {
        for ( IFormListener formListener : SpringContextService.getBeansOfType( IFormListener.class ) )
        {
            formListener.notifyFormRemoval( nIdForm );
        }
    }

}
