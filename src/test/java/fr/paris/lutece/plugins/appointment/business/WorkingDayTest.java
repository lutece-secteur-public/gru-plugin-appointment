package fr.paris.lutece.plugins.appointment.business;

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

	private final static int DAY_OF_WEEK_1 = 1;
	private final static int DAY_OF_WEEK_2 = 2;

	/**
	 * Test method for a working day (CRUD)
	 */
	public void testWorkingDay() {
		// Initialize a WorkingDay
		WorkingDay workingDay = buildWorkingDay();
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
	}

	/**
	 * Build a WorkingDay Business Object
	 * 
	 * @return the working day
	 */
	public static WorkingDay buildWorkingDay() {
		WorkingDay workingDay = new WorkingDay();

		workingDay.setDayOfWeek(DAY_OF_WEEK_1);

		WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition();
		WeekDefinitionHome.create(weekDefinition);
		workingDay.setIdWeekDefinition(weekDefinition.getIdWeekDefinition());

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
