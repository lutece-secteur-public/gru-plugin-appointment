package fr.paris.lutece.plugins.appointment.business;

import java.util.List;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.planningdefinition.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planningdefinition.WeekDefinitionHome;
import fr.paris.lutece.plugins.appointment.business.planningdefinition.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planningdefinition.WorkingDayHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class of the WorkingDay
 * 
 * @author Laurent Payen
 *
 */
public class WorkingDayTest extends LuteceTestCase {

	public final static int DAY_OF_WEEK_1 = 1;
	public final static int DAY_OF_WEEK_2 = 2;

	/**
	 * Test method for a working day (CRUD)
	 */
	public void testWorkingDay() {
		// Initialize a WorkingDay
		Form form = FormTest.buildForm();
		FormHome.create(form);
		WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition();
		weekDefinition.setIdForm(form.getIdForm());
		WeekDefinitionHome.create(weekDefinition);
		WorkingDay workingDay = buildWorkingDay();
		workingDay.setIdWeekDefinition(weekDefinition.getIdWeekDefinition());
		// Insert the WorkingDay in database
		WorkingDayHome.create(workingDay);
		// Find the workingDay created in database
		WorkingDay workingDayStored = WorkingDayHome.findByPrimaryKey(workingDay.getIdWorkingDay());
		// Check Asserts
		checkAsserts(workingDayStored, workingDay);

		// Update the WorkingDay
		workingDay.setDayOfWeek(DAY_OF_WEEK_2);
		// Update the WorkingDay in database
		WorkingDayHome.update(workingDay);
		// Get the workingDay in database
		workingDayStored = WorkingDayHome.findByPrimaryKey(workingDay.getIdWorkingDay());
		// Check asserts
		checkAsserts(workingDayStored, workingDay);

		// Delete the workingDay
		WorkingDayHome.delete(workingDay.getIdWorkingDay());
		workingDayStored = WorkingDayHome.findByPrimaryKey(workingDay.getIdWorkingDay());
		// Check the workingDay has been removed from database
		assertNull(workingDayStored);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Test delete cascade
	 */
	public void testDeleteCascade() {
		// Initialize a WorkingDay
		Form form = FormTest.buildForm();
		FormHome.create(form);
		WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition();
		weekDefinition.setIdForm(form.getIdForm());
		WeekDefinitionHome.create(weekDefinition);
		WorkingDay workingDay = buildWorkingDay();
		workingDay.setIdWeekDefinition(weekDefinition.getIdWeekDefinition());
		// Insert the WorkingDay in database
		WorkingDayHome.create(workingDay);
		// Find the workingDay created in database
		WorkingDay workingDayStored = WorkingDayHome.findByPrimaryKey(workingDay.getIdWorkingDay());
		assertNotNull(workingDayStored);
		// Delete the form and by cascade the workingDay
		FormHome.delete(form.getIdForm());
		workingDayStored = WorkingDayHome.findByPrimaryKey(workingDay.getIdWorkingDay());
		// Check the workingDay has been removed from database
		assertNull(workingDayStored);
	}

	/**
	 * Test of findByIdWeekDefinition
	 */
	public void testFindByIdWeekDefinition() {
		// Initialize a WorkingDay
		Form form = FormTest.buildForm();
		FormHome.create(form);
		WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition();
		weekDefinition.setIdForm(form.getIdForm());
		WeekDefinitionHome.create(weekDefinition);
		WorkingDay workingDay = buildWorkingDay();
		workingDay.setIdWeekDefinition(weekDefinition.getIdWeekDefinition());
		// Insert the WorkingDay in database
		WorkingDayHome.create(workingDay);
		// Find the workingDay created in database
		List<WorkingDay> listWorkingDayStored = WorkingDayHome
				.findByIdWeekDefinition(weekDefinition.getIdWeekDefinition());
		// Check Asserts
		assertEquals(listWorkingDayStored.size(), 1);
		checkAsserts(listWorkingDayStored.get(0), workingDay);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Build a WorkingDay Business Object
	 * 
	 * @return the working day
	 */
	public static WorkingDay buildWorkingDay() {
		WorkingDay workingDay = new WorkingDay();
		workingDay.setDayOfWeek(DAY_OF_WEEK_1);
		return workingDay;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param workingDayStored
	 *            the working day stored
	 * @param workingDay
	 *            the working day created
	 */
	public static void checkAsserts(WorkingDay workingDayStored, WorkingDay workingDay) {
		assertEquals(workingDayStored.getDayOfWeek(), workingDay.getDayOfWeek());
		assertEquals(workingDayStored.getIdWeekDefinition(), workingDay.getIdWeekDefinition());
	}
}
