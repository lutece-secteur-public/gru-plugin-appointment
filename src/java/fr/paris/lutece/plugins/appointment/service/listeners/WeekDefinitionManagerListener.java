package fr.paris.lutece.plugins.appointment.service.listeners;

import fr.paris.lutece.portal.service.spring.SpringContextService;

public final class WeekDefinitionManagerListener
{

    /**
     * Private default constructor
     */
    private WeekDefinitionManagerListener( )
    {
        // Nothing to do
    }

    /**
     * Notify listeners that a week definition has been created
     * 
     * @param nIdWeekDefinition
     *            The id of the week definition that has been created
     */
    public static void notifyListenersWeekDefinitionCreation( int nIdWeekDefinition )
    {
        for ( IWeekDefinitionListener weekDefinitionListener : SpringContextService.getBeansOfType( IWeekDefinitionListener.class ) )
        {
            weekDefinitionListener.notifyWeekDefinitionCreation( nIdWeekDefinition );
        }
    }

    /**
     * Notify listeners that a Week Definition has been changed
     * 
     * @param nIdWeekDefinition
     *            The id of the WeekDefinition that has been changed
     */
    public static void notifyListenersWeekDefinitionChange( int nIdWeekDefinition )
    {
        for ( IWeekDefinitionListener weekDefinitionListener : SpringContextService.getBeansOfType( IWeekDefinitionListener.class ) )
        {
            weekDefinitionListener.notifyWeekDefinitionChange( nIdWeekDefinition );
        }
    }

    /**
     * Notify listeners that a Week Definition is about to be removed
     * 
     * @param nIdWeekDefinition
     *            The id of the Week Definition that will be removed
     */
    public static void notifyListenersWeekDefinitionRemoval( int nIdWeekDefinition )
    {
        for ( IWeekDefinitionListener weekDefinitionListener : SpringContextService.getBeansOfType( IWeekDefinitionListener.class ) )
        {
            weekDefinitionListener.notifyWeekDefinitionRemoval( nIdWeekDefinition );
        }
    }

}
