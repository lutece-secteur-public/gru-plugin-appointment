package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDate;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.planningdefinition.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planningdefinition.WeekDefinitionHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class of the WeekDefinition
 * 
 * @author Laurent Payen
 *
 */
public class WeekDefinitionTest extends LuteceTestCase {

	private final static LocalDate DATE_OF_APPLY_1 = LocalDate.parse("2017-01-26");
	private final static LocalDate DATE_OF_APPLY_2 = LocalDate.parse("2017-01-27");

	/**
	 * Test method for the weekDefinition (CRUD)
	 */
	public void testWeekDefinition() {
		// Initialize a WeekDefinition
		WeekDefinition weekDefinition = buildWeekDefinition();
		// Insert the WeekDefinition in database
		WeekDefinitionHome.create(weekDefinition);
		// Find the weekDefinition created in database
		WeekDefinition weekDefinitionStored = WeekDefinitionHome.findByPrimaryKey(weekDefinition.getIdWeekDefinition());
		// Check Asserts
		checkAsserts(weekDefinitionStored, weekDefinition);

		// Update the weekDefinition
		weekDefinition.setDateOfApply(DATE_OF_APPLY_2);
		// Update the weekDefinition in database
		WeekDefinitionHome.update(weekDefinition);
		// Find the weekDefinition updated in database
		weekDefinitionStored = WeekDefinitionHome.findByPrimaryKey(weekDefinition.getIdWeekDefinition());
		// Check Asserts
		checkAsserts(weekDefinitionStored, weekDefinition);

		// Delete the weekDefinition
		WeekDefinitionHome.delete(weekDefinition.getIdWeekDefinition());
		weekDefinitionStored = WeekDefinitionHome.findByPrimaryKey(weekDefinition.getIdWeekDefinition());
		// Check the weekDefinition has been removed from database
		assertNull(weekDefinitionStored);
	}

	/**
	 * Build a WeekDefinition Business Object
	 * 
	 * @return the weekDefinition
	 */
	public static WeekDefinition buildWeekDefinition() {
		WeekDefinition weekDefinition = new WeekDefinition();
		weekDefinition.setDateOfApply(DATE_OF_APPLY_1);

		Form form = FormTest.buildForm();
		FormHome.create(form);
		weekDefinition.setIdForm(form.getIdForm());

		return weekDefinition;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param weekDefinitionStored
	 *            the weekDefinition stored
	 * @param weekDefinition
	 *            the week definition created
	 */
	public void checkAsserts(WeekDefinition weekDefinitionStored, WeekDefinition weekDefinition) {
		assertEquals(weekDefinitionStored.getDateOfApply(), weekDefinition.getDateOfApply());
		assertEquals(weekDefinitionStored.getIdForm(), weekDefinition.getIdForm());
	}
}
