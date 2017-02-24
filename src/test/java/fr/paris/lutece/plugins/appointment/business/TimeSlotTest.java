package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalTime;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the TimeSlot
 * 
 * @author Laurent Payen
 *
 */
public class TimeSlotTest extends LuteceTestCase {

	public final static LocalTime STARTING_TIME_1 = LocalTime.parse("09:00");
	public final static LocalTime STARTING_TIME_2 = LocalTime.parse("09:30");
	public final static LocalTime ENDING_TIME_1 = LocalTime.parse("09:30");
	public final static LocalTime ENDING_TIME_2 = LocalTime.parse("10:00");
	public final static boolean IS_OPEN_1 = true;
	public final static boolean IS_OPEN_2 = false;
	public final static int MAX_CAPACITY_1 = 1;
	public final static int MAX_CAPACITY_2 = 2;

	/**
	 * Test method for the TimeSlot (CRUD)
	 */
	public void testTimeSlot() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition();
		weekDefinition.setIdForm(form.getIdForm());
		WeekDefinitionHome.create(weekDefinition);

		WorkingDay workingDay = WorkingDayTest.buildWorkingDay();
		workingDay.setIdWeekDefinition(weekDefinition.getIdWeekDefinition());
		WorkingDayHome.create(workingDay);

		// Initialize a TimeSlot
		TimeSlot timeSlot = buildTimeSlot();
		timeSlot.setIdWorkingDay(workingDay.getIdWorkingDay());
		// Create the TimeSlot in database
		TimeSlotHome.create(timeSlot);

		// Find the TimeSlot created in database
		TimeSlot timeSlotStored = TimeSlotHome.findByPrimaryKey(timeSlot.getIdTimeSlot());
		// Check Asserts
		checkAsserts(timeSlotStored, timeSlot);

		// Update the timeSlot
		timeSlot.setStartingTime(STARTING_TIME_2);
		timeSlot.setEndingTime(ENDING_TIME_2);
		timeSlot.setIsOpen(IS_OPEN_2);
		timeSlot.setMaxCapacity(MAX_CAPACITY_2);
		// Update the timeSlot in database
		TimeSlotHome.update(timeSlot);
		// Find the timeSlot updated in database
		timeSlotStored = TimeSlotHome.findByPrimaryKey(timeSlot.getIdTimeSlot());
		// Check Asserts
		checkAsserts(timeSlotStored, timeSlot);

		// Delete the timeSlot
		TimeSlotHome.delete(timeSlot.getIdTimeSlot());
		timeSlotStored = TimeSlotHome.findByPrimaryKey(timeSlot.getIdTimeSlot());
		// Check the timeSlot has been removed from database
		assertNull(timeSlotStored);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Test delete cascade
	 */
	public void testDeleteCascade() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition();
		weekDefinition.setIdForm(form.getIdForm());
		WeekDefinitionHome.create(weekDefinition);

		WorkingDay workingDay = WorkingDayTest.buildWorkingDay();
		workingDay.setIdWeekDefinition(weekDefinition.getIdWeekDefinition());
		WorkingDayHome.create(workingDay);

		// Initialize a TimeSlot
		TimeSlot timeSlot = buildTimeSlot();
		timeSlot.setIdWorkingDay(workingDay.getIdWorkingDay());
		// Create the TimeSlot in database
		TimeSlotHome.create(timeSlot);

		// Find the TimeSlot created in database
		TimeSlot timeSlotStored = TimeSlotHome.findByPrimaryKey(timeSlot.getIdTimeSlot());
		assertNotNull(timeSlotStored);

		// Delete the Form and by cascade the timeSlot
		TimeSlotHome.delete(timeSlot.getIdTimeSlot());
		timeSlotStored = TimeSlotHome.findByPrimaryKey(timeSlot.getIdTimeSlot());
		// Check the timeSlot has been removed from database
		assertNull(timeSlotStored);

	}

	/**
	 * Test of findByIdWorkingDay
	 */
	public void testFindByIdWorkingDay() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition();
		weekDefinition.setIdForm(form.getIdForm());
		WeekDefinitionHome.create(weekDefinition);

		WorkingDay workingDay = WorkingDayTest.buildWorkingDay();
		workingDay.setIdWeekDefinition(weekDefinition.getIdWeekDefinition());
		WorkingDayHome.create(workingDay);

		// Initialize a TimeSlot
		TimeSlot timeSlot = buildTimeSlot();
		timeSlot.setIdWorkingDay(workingDay.getIdWorkingDay());
		// Create the TimeSlot in database
		TimeSlotHome.create(timeSlot);

		// Find the TimeSlot created in database
		List<TimeSlot> listTimeSlotStored = TimeSlotHome.findByIdWorkingDay(workingDay.getIdWorkingDay());
		assertEquals(listTimeSlotStored.size(), 1);
		checkAsserts(listTimeSlotStored.get(0), timeSlot);

		// Clean
		FormHome.delete(form.getIdForm());

	}

	/**
	 * build a TimeSlot Business Object
	 * 
	 * @return the timeSlot
	 */
	public static TimeSlot buildTimeSlot() {
		TimeSlot timeSlot = new TimeSlot();
		timeSlot.setStartingTime(STARTING_TIME_1);
		timeSlot.setEndingTime(ENDING_TIME_1);
		timeSlot.setIsOpen(IS_OPEN_1);
		timeSlot.setMaxCapacity(MAX_CAPACITY_1);
		return timeSlot;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param timeSlotStored
	 *            the timeSlot stored
	 * @param timeSlot
	 *            the timeSlot created
	 */
	public void checkAsserts(TimeSlot timeSlotStored, TimeSlot timeSlot) {
		assertEquals(timeSlotStored.getStartingTime(), timeSlot.getStartingTime());
		assertEquals(timeSlotStored.getEndingTime(), timeSlot.getEndingTime());
		assertEquals(timeSlotStored.getIsOpen(), timeSlot.getIsOpen());
		assertEquals(timeSlotStored.getMaxCapacity(), timeSlot.getMaxCapacity());
		assertEquals(timeSlotStored.getIdWorkingDay(), timeSlot.getIdWorkingDay());
	}

}
