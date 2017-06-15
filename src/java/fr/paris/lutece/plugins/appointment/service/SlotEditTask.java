package fr.paris.lutece.plugins.appointment.service;

import java.util.TimerTask;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;

public class SlotEditTask extends TimerTask {

	private int nbPlacesTaken;	
	private int nIdSlot;
	
	@Override
	public void run() {
		Slot slot = SlotService.findSlotById(nIdSlot);
		int nbPotentialRemainingPlaces = slot.getNbPotentialRemainingPlaces();
		slot.setNbPotentialRemainingPlaces(nbPotentialRemainingPlaces + nbPlacesTaken);
		SlotService.updateSlot(slot);
	}
	
	public int getNbPlacesTaken() {
		return nbPlacesTaken;
	}

	public void setNbPlacesTaken(int nbPlacesTaken) {
		this.nbPlacesTaken = nbPlacesTaken;
	}

	public int getnIdSlot() {
		return nIdSlot;
	}

	public void setnIdSlot(int nIdSlot) {
		this.nIdSlot = nIdSlot;
	}
	
	

}
