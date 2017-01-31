package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDate;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.planningdefinition.ClosingDay;
import fr.paris.lutece.plugins.appointment.business.planningdefinition.ClosingDayHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the ClosingDay
 * 
 * @author Laurent Payen
 *
 */
public class ClosingDayTest extends LuteceTestCase {

	public static final LocalDate DATE_OF_CLOSING_DAY_1 = LocalDate.parse("2017-01-26");
	public static final LocalDate DATE_OF_CLOSING_DAY_2 = LocalDate.parse("2017-02-27");

	/**
	 * Test method for the ClosingDay (CRUD)
	 */
	public void testClosingDay() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a ClosingDay
		ClosingDay closingDay = buildClosingDay();
		closingDay.setIdForm(form.getIdForm());
		// Create the ClosingDay in database
		ClosingDayHome.create(closingDay);
		// Find the ClosingDay created in database
		ClosingDay closingDayStored = ClosingDayHome.findByPrimaryKey(closingDay.getIdClosingDay());
		// Check Asserts
		checkAsserts(closingDayStored, closingDay);

		// Update the ClosingDay
		closingDay.setDateOfClosingDay(DATE_OF_CLOSING_DAY_2);
		// Update the ClosingDay in database
		ClosingDayHome.update(closingDay);
		// Find the ClosingDay updated in database
		closingDayStored = ClosingDayHome.findByPrimaryKey(closingDay.getIdClosingDay());
		// Check Asserts
		checkAsserts(closingDayStored, closingDay);

		// Delete the ClosingDay
		ClosingDayHome.delete(closingDay.getIdClosingDay());
		closingDayStored = ClosingDayHome.findByPrimaryKey(closingDay.getIdClosingDay());
		// Check the ClosingDay has been removed from database
		assertNull(closingDayStored);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Test the delete cascade
	 */
	public void testDeleteCascade() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a ClosingDay
		ClosingDay closingDay = buildClosingDay();
		closingDay.setIdForm(form.getIdForm());
		// Create the ClosingDay in database
		ClosingDayHome.create(closingDay);
		// Find the ClosingDay created in database
		ClosingDay closingDayStored = ClosingDayHome.findByPrimaryKey(closingDay.getIdClosingDay());
		assertNotNull(closingDayStored);
		// Delete the Form and by cascade the ClosingDay
		FormHome.delete(form.getIdForm());
		closingDayStored = ClosingDayHome.findByPrimaryKey(closingDay.getIdClosingDay());
		// Check the ClosingDay has been removed from database
		assertNull(closingDayStored);
	}

	/**
	 * Test method for findByIdFormAndDateOfCLosingDay
	 */
	public void testFindByIdFormAndDateOfCLosingDay() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		// Initialize a ClosingDay
		ClosingDay closingDay = buildClosingDay();
		closingDay.setIdForm(form.getIdForm());
		// Create the ClosingDay in database
		ClosingDayHome.create(closingDay);

		// Find the ClosingDay
		ClosingDay closingDayStored = ClosingDayHome.findByIdFormAndDateOfCLosingDay(form.getIdForm(),
				DATE_OF_CLOSING_DAY_1);
		assertNotNull(closingDayStored);
		checkAsserts(closingDayStored, closingDay);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Build a ClosingDay Business Object
	 * 
	 * @return the closingDay
	 */
	public ClosingDay buildClosingDay() {
		ClosingDay closingDay = new ClosingDay();
		closingDay.setDateOfClosingDay(DATE_OF_CLOSING_DAY_1);
		return closingDay;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param closingDayStored
	 *            the ClosingDay stored
	 * @param closingDay
	 *            the ClosingDay created
	 */
	public void checkAsserts(ClosingDay closingDayStored, ClosingDay closingDay) {
		assertEquals(closingDayStored.getDateOfClosingDay(), closingDay.getDateOfClosingDay());
		assertEquals(closingDayStored.getIdForm(), closingDay.getIdForm());
	}
}
