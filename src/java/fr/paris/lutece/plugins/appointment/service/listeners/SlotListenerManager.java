package fr.paris.lutece.plugins.appointment.service.listeners;

import fr.paris.lutece.portal.service.spring.SpringContextService;

public final class SlotListenerManager
{

    /**
     * Private default constructor
     */
    private SlotListenerManager( )
    {
        // Nothing to do
    }

    /**
     * Notify listeners that a Slot has been created
     * 
     * @param nIdSlot
     *            The id of the Slot that has been created
     */
    public static void notifyListenersSlotCreation( int nIdSlot )
    {
        for ( ISlotListener slotListener : SpringContextService.getBeansOfType( ISlotListener.class ) )
        {
            slotListener.notifySlotCreation( nIdSlot );
        }
    }

    /**
     * Notify listeners that a Slot has been changed
     * 
     * @param nIdSlot
     *            The id of the Slot that has been changed
     */
    public static void notifyListenersSlotChange( int nIdSlot )
    {
        for ( ISlotListener slotListener : SpringContextService.getBeansOfType( ISlotListener.class ) )
        {
            slotListener.notifySlotChange( nIdSlot );
        }
    }

    /**
     * Notify listeners that a Slot is about to be removed
     * 
     * @param nIdSlot
     *            The id of the Slot that will be removed
     */
    public static void notifyListenersSlotRemoval( int nIdSlot )
    {
        for ( ISlotListener slotListener : SpringContextService.getBeansOfType( ISlotListener.class ) )
        {
            slotListener.notifySlotRemoval( nIdSlot );
        }
    }

}
