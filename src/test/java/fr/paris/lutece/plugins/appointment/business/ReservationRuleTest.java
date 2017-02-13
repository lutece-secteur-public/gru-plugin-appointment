package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the ReservationRule
 * 
 * @author Laurent Payen
 *
 */
public class ReservationRuleTest extends LuteceTestCase {

	public static final LocalDate DATE_OF_APPLY_1 = LocalDate.parse("2017-01-27");
	public static final LocalDate DATE_OF_APPLY_2 = LocalDate.parse("2017-02-25");
	public static final int MAX_CAPACITY_PER_SLOT_1 = 1;
	public static final int MAX_CAPACITY_PER_SLOT_2 = 2;
	public static final int MAX_PEOPLE_PER_APPOINTMENT_1 = 1;
	public static final int MAX_PEOPLE_PER_APPOINTMENT_2 = 2;

	/**
	 * Test method for the ReservationRule (CRUD)
	 */
	public void testReservationRule() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a ReservationRule
		ReservationRule reservationRule = buildReservationRule();
		reservationRule.setIdForm(form.getIdForm());
		// Create the ReservationRule in database
		ReservationRuleHome.create(reservationRule);
		// Find the ReservationRule created in database
		ReservationRule reservationRuleStored = ReservationRuleHome
				.findByPrimaryKey(reservationRule.getIdReservationRule());
		// Check Asserts
		checkAsserts(reservationRuleStored, reservationRule);

		// Update the ReservationRule
		reservationRule.setDateOfApply(DATE_OF_APPLY_2);
		reservationRule.setMaxCapacityPerSlot(MAX_CAPACITY_PER_SLOT_2);
		reservationRule.setMaxPeoplePerAppointment(MAX_PEOPLE_PER_APPOINTMENT_2);
		// Update the ReservationRule in database
		ReservationRuleHome.update(reservationRule);
		// Find the ReservationRule updated in database
		reservationRuleStored = ReservationRuleHome.findByPrimaryKey(reservationRule.getIdReservationRule());
		// Check Asserts
		checkAsserts(reservationRuleStored, reservationRule);

		// Delete the ReservationRule
		ReservationRuleHome.delete(reservationRule.getIdReservationRule());
		reservationRuleStored = ReservationRuleHome.findByPrimaryKey(reservationRule.getIdReservationRule());
		// Check the ReservationRule has been removed from database
		assertNull(reservationRuleStored);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Test delete cascade
	 */
	public void testDeleteCascade() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a ReservationRule
		ReservationRule reservationRule = buildReservationRule();
		reservationRule.setIdForm(form.getIdForm());
		// Create the ReservationRule in database
		ReservationRuleHome.create(reservationRule);
		// Find the ReservationRule created in database
		ReservationRule reservationRuleStored = ReservationRuleHome
				.findByPrimaryKey(reservationRule.getIdReservationRule());
		assertNotNull(reservationRuleStored);
		// Delete the form and by cascade the ReservationRule
		FormHome.delete(form.getIdForm());
		reservationRuleStored = ReservationRuleHome.findByPrimaryKey(reservationRule.getIdReservationRule());
		// Check the ReservationRule has been removed from database
		assertNull(reservationRuleStored);
	}

	/**
	 * Test of findByIdForm method
	 */
	public void testFindByIdForm() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a ReservationRule
		ReservationRule reservationRule = buildReservationRule();
		reservationRule.setIdForm(form.getIdForm());
		// Create the ReservationRule in database
		ReservationRuleHome.create(reservationRule);
		// Find the ReservationRule created in database
		List<ReservationRule> listReservationRuleStored = ReservationRuleHome.findByIdForm(form.getIdForm());
		// Check Asserts
		assertEquals(listReservationRuleStored.size(), 1);
		checkAsserts(listReservationRuleStored.get(0), reservationRule);

		// Clean
		FormHome.delete(form.getIdForm());

	}

	/**
	 * Test of findByIdFormAndDateOfApply method
	 */
	public void findByIdFormAndDateOfApply() {
		Form form = FormTest.buildForm();
		FormHome.create(form);
		// Initialize a ReservationRule
		ReservationRule reservationRule1 = buildReservationRule();
		reservationRule1.setIdForm(form.getIdForm());
		// Create the ReservationRule in database
		ReservationRuleHome.create(reservationRule1);
		// Find the ReservationRule created in database
		ReservationRule reservationRuleStored = ReservationRuleHome
				.findByIdFormAndDateOfApply(form.getIdForm(), DATE_OF_APPLY_1);
		// Check Asserts		
		checkAsserts(reservationRuleStored, reservationRule1);
		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Build a ReservationRule Business Object
	 * 
	 * @return the reservationRule
	 */
	public ReservationRule buildReservationRule() {
		ReservationRule reservationRule = new ReservationRule();
		reservationRule.setDateOfApply(DATE_OF_APPLY_1);
		reservationRule.setMaxCapacityPerSlot(MAX_CAPACITY_PER_SLOT_1);
		reservationRule.setMaxPeoplePerAppointment(MAX_PEOPLE_PER_APPOINTMENT_1);
		return reservationRule;
	}

	/**
	 * Build a ReservationRule Business Object
	 * 
	 * @return the reservationRule
	 */
	public ReservationRule buildReservationRule2() {
		ReservationRule reservationRule = new ReservationRule();
		reservationRule.setDateOfApply(DATE_OF_APPLY_2);
		reservationRule.setMaxCapacityPerSlot(MAX_CAPACITY_PER_SLOT_2);
		reservationRule.setMaxPeoplePerAppointment(MAX_PEOPLE_PER_APPOINTMENT_2);
		return reservationRule;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param reservationRuleStored
	 *            the ReservationRule stored
	 * @param reservationRule
	 *            the ReservationRule created
	 */
	public void checkAsserts(ReservationRule reservationRuleStored, ReservationRule reservationRule) {
		assertEquals(reservationRuleStored.getDateOfApply(), reservationRule.getDateOfApply());
		assertEquals(reservationRuleStored.getMaxCapacityPerSlot(), reservationRule.getMaxCapacityPerSlot());
		assertEquals(reservationRuleStored.getMaxPeoplePerAppointment(), reservationRule.getMaxPeoplePerAppointment());
		assertEquals(reservationRuleStored.getIdForm(), reservationRule.getIdForm());
	}

}
