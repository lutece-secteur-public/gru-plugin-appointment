package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDateTime;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the Slot
 * @author Laurent Payen
 *
 */
public class SlotTest extends LuteceTestCase {

	private final static LocalDateTime STARTING_DATE_1 = LocalDateTime.parse("2017-01-27T09:00");
	private final static LocalDateTime STARTING_DATE_2 = LocalDateTime.parse("2017-01-28T09:30");
	private final static LocalDateTime ENDING_DATE_1 = LocalDateTime.parse("2017-01-27T09:30");
	private final static LocalDateTime ENDING_DATE_2 = LocalDateTime.parse("2017-01-28T10:00");
	private final static boolean IS_OPEN_1 = true;
	private final static boolean IS_OPEN_2 = false;
	private final static int NB_REMAINING_PLACES_1 = 1;
	private final static int NB_REMAINING_PLACES_2 = 2;

	/**
	 * Test method for the Slot (CRUD)
	 */
	public void testSlot() {
		// Initialize a Slot
		Slot slot = buildSlot();
		// Create the Slot in database
		SlotHome.create(slot);
		// Find the Slot created in database
		Slot slotStored = SlotHome.findByPrimaryKey(slot.getIdSlot());
		// Check Asserts
		checkAsserts(slotStored, slot);

		// Update the Slot
		slot.setStartingDate(STARTING_DATE_2);
		slot.setEndingDate(ENDING_DATE_2);
		slot.setIsOpen(IS_OPEN_2);
		slot.setNbRemainingPlaces(NB_REMAINING_PLACES_2);
		// Update the Slot in database
		SlotHome.update(slot);
		// Find the Slot updated in database
		slotStored = SlotHome.findByPrimaryKey(slot.getIdSlot());
		// Check Asserts
		checkAsserts(slotStored, slot);

		// Delete the Slot
		SlotHome.delete(slot.getIdSlot());
		slotStored = SlotHome.findByPrimaryKey(slot.getIdSlot());
		// Check the Slot has been removed from database
		assertNull(slotStored);
	}

	/**
	 * Build a SLot Business Object
	 * @return a slot
	 */
	public static Slot buildSlot() {
		Slot slot = new Slot();
		slot.setStartingDate(STARTING_DATE_1);
		slot.setEndingDate(ENDING_DATE_1);
		slot.setIsOpen(IS_OPEN_1);
		slot.setNbRemainingPlaces(NB_REMAINING_PLACES_1);

		Form form = FormTest.buildForm();
		FormHome.create(form);
		slot.setIdForm(form.getIdForm());

		return slot;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param slotStored
	 *            the Slot stored
	 * @param slot
	 *            the Slot created
	 */
	public void checkAsserts(Slot slotStored, Slot slot) {
		assertEquals(slotStored.getStartingDate(), slot.getStartingDate());
		assertEquals(slotStored.getEndingDate(), slot.getEndingDate());
		assertEquals(slotStored.isOpen(), slot.isOpen());
		assertEquals(slotStored.getNbRemainingPlaces(), slot.getNbRemainingPlaces());
		assertEquals(slotStored.getIdForm(), slot.getIdForm());
	}

}
