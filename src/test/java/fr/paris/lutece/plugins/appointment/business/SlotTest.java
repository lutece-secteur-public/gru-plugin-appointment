package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the Slot
 * 
 * @author Laurent Payen
 *
 */
public class SlotTest extends LuteceTestCase {

	public final static LocalDateTime STARTING_DATE_1 = LocalDateTime.parse("2017-01-27T09:00");
	public final static LocalDateTime STARTING_DATE_2 = LocalDateTime.parse("2017-01-28T09:30");
	public final static LocalDateTime ENDING_DATE_1 = LocalDateTime.parse("2017-01-27T09:30");
	public final static LocalDateTime ENDING_DATE_2 = LocalDateTime.parse("2017-01-28T10:00");
	public final static boolean IS_OPEN_1 = true;
	public final static boolean IS_OPEN_2 = false;
	public final static int NB_REMAINING_PLACES_1 = 1;
	public final static int NB_REMAINING_PLACES_2 = 2;

	/**
	 * Test method for the Slot (CRUD)
	 */
	public void testSlot() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a Slot
		Slot slot = buildSlot();
		slot.setIdForm(form.getIdForm());
		// Create the Slot in database
		SlotHome.create(slot);
		// Find the Slot created in database
		Slot slotStored = SlotHome.findByPrimaryKey(slot.getIdSlot());
		// Check Asserts
		checkAsserts(slotStored, slot);

		// Update the Slot
		slot.setStartingDateTime(STARTING_DATE_2);
		slot.setEndingDateTime(ENDING_DATE_2);
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

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Test delete cascade
	 */
	public void testDeleteCascade() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a Slot
		Slot slot = buildSlot();
		slot.setIdForm(form.getIdForm());
		// Create the Slot in database
		SlotHome.create(slot);
		// Find the Slot created in database
		Slot slotStored = SlotHome.findByPrimaryKey(slot.getIdSlot());
		assertNotNull(slotStored);
		// Delete the Form and by cascade the Slot
		FormHome.delete(form.getIdForm());
		slotStored = SlotHome.findByPrimaryKey(slot.getIdSlot());
		// Check the Slot has been removed from database
		assertNull(slotStored);
	}

	/**
	 * Test of findByIdFormAndDateRange
	 */
	public void testFindByIdFormAndDateRange() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a first Slot that matches
		Slot slot1 = buildSlot();
		slot1.setIdForm(form.getIdForm());
		// Create the Slot in database
		SlotHome.create(slot1);

		// Initialize a second slot that doesn't matche
		Slot slot2 = buildSlot2();
		slot2.setIdForm(form.getIdForm());
		// Create the Slot in database
		SlotHome.create(slot2);

		// Find the Slot created in database
		HashMap<LocalDateTime, Slot> listSlotStored = SlotHome.findByIdFormAndDateRange(form.getIdForm(), STARTING_DATE_1, ENDING_DATE_1);
		assertEquals(listSlotStored.size(), 1);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Test of findOpenSlotsByIdFormAndDateRange
	 */
	public void testFindOpenSlotsByIdFormAndDateRange() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a first Slot
		Slot slot1 = buildSlot();
		slot1.setIdForm(form.getIdForm());
		// Create the Slot in database
		SlotHome.create(slot1);

		// Initialize a second Slot closed
		Slot slot2 = buildClosedSlot();
		slot2.setIdForm(form.getIdForm());
		// Create the Slot in database
		SlotHome.create(slot2);
		// Find the Slot created in database
		List<Slot> listSlotStored = SlotHome.findOpenSlotsByIdFormAndDateRange(form.getIdForm(), STARTING_DATE_1,
				ENDING_DATE_2);
		assertEquals(listSlotStored.size(), 1);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Test of FindOpenSlotsByIdForm
	 */
	public void testFindOpenSlotsByIdForm() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a Slot
		Slot slot1 = buildSlot();
		slot1.setIdForm(form.getIdForm());
		// Create the Slot in database
		SlotHome.create(slot1);

		// Initialize a 2nd Slot
		Slot slot2 = buildSlot2();
		slot2.setIdForm(form.getIdForm());
		// Create the Slot in database
		SlotHome.create(slot2);

		// Initialize a 3th slot closed
		Slot slot3 = buildClosedSlot();
		slot3.setIdForm(form.getIdForm());
		// Create the Slot in database
		SlotHome.create(slot3);

		// Find the Slot created in database
		List<Slot> listSlotStored = SlotHome.findOpenSlotsByIdForm(form.getIdForm());
		assertEquals(listSlotStored.size(), 2);

		// Clean
		FormHome.delete(form.getIdForm());

	}

	/**
	 * Build a SLot Business Object
	 * 
	 * @return a slot
	 */
	public static Slot buildSlot() {
		Slot slot = new Slot();
		slot.setStartingDateTime(STARTING_DATE_1);
		slot.setEndingDateTime(ENDING_DATE_1);
		slot.setIsOpen(IS_OPEN_1);
		slot.setNbRemainingPlaces(NB_REMAINING_PLACES_1);
		return slot;
	}

	/**
	 * Build a SLot Business Object
	 * 
	 * @return a slot
	 */
	public static Slot buildSlot2() {
		Slot slot = new Slot();
		slot.setStartingDateTime(STARTING_DATE_2);
		slot.setEndingDateTime(ENDING_DATE_2);
		slot.setIsOpen(IS_OPEN_1);
		slot.setNbRemainingPlaces(NB_REMAINING_PLACES_2);
		return slot;
	}

	/**
	 * Build a SLot Business Object
	 * 
	 * @return a slot
	 */
	public static Slot buildClosedSlot() {
		Slot slot = new Slot();
		slot.setStartingDateTime(STARTING_DATE_2);
		slot.setEndingDateTime(ENDING_DATE_2);
		slot.setIsOpen(IS_OPEN_2);
		slot.setNbRemainingPlaces(NB_REMAINING_PLACES_2);
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
		assertEquals(slotStored.getStartingDateTime(), slot.getStartingDateTime());
		assertEquals(slotStored.getEndingDateTime(), slot.getEndingDateTime());
		assertEquals(slotStored.getIsOpen(), slot.getIsOpen());
		assertEquals(slotStored.getNbRemainingPlaces(), slot.getNbRemainingPlaces());
		assertEquals(slotStored.getIdForm(), slot.getIdForm());
	}

}
