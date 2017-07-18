package fr.paris.lutece.plugins.appointment.service;

import java.util.TimerTask;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;

/**
 * Timer Task for a slot (Manage a lock the time the user fill the form
 * 
 * @author Laurent Payen
 *
 */
public class SlotEditTask extends TimerTask
{

    /**
     * Potentially number of places taken
     */
    private int nbPlacesTaken;

    /**
     * Id of the slot on which the user is taking an appointment
     */
    private int idSlot;

    @Override
    public void run( )
    {
        Slot slot = SlotService.findSlotById( idSlot );
        int nbPotentialRemainingPlaces = slot.getNbPotentialRemainingPlaces( );
        slot.setNbPotentialRemainingPlaces( nbPotentialRemainingPlaces + nbPlacesTaken );
        SlotService.updateSlot( slot );
    }

    /**
     * Get the number of places potentially taken
     * 
     * @return the number of places
     */
    public int getNbPlacesTaken( )
    {
        return nbPlacesTaken;
    }

    /**
     * Set the number of places potentially taken
     * 
     * @param nbPlacesTaken
     */
    public void setNbPlacesTaken( int nbPlacesTaken )
    {
        this.nbPlacesTaken = nbPlacesTaken;
    }

    /**
     * Get the id of the slot
     * 
     * @return the id of the slot
     */
    public int getIdSlot( )
    {
        return idSlot;
    }

    /**
     * Set the id of the slot
     * 
     * @param nIdSlot
     *            the id of the slot
     */
    public void setIdSlot( int nIdSlot )
    {
        this.idSlot = nIdSlot;
    }

}
