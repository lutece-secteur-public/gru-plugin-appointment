package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDate;
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
 * Test Class for the Form
 * 
 * @author Laurent Payen
 *
 */
public class FormTest extends LuteceTestCase {

	public final static String TITLE_FORM_1 = "TitreForm1";
	public final static String TITLE_FORM_2 = "TitreForm2";
	public final static String DESCRIPTION_FORM_1 = "DescriptionForm1";
	public final static String DESCRIPTION_FORM_2 = "DescriptionForm2";
	public final static String CATEGORY_FORM_1 = "CategoryForm1";
	public final static String CATEGORY_FORM_2 = "CategoryForm2";
	public final static LocalDate STARTING_VALIDITY_DATE_1 = LocalDate.parse("2017-01-24");
	public final static LocalDate STARTING_VALIDITY_DATE_2 = LocalDate.parse("2017-01-25");
	public final static LocalDate ENDING_VALIDITY_DATE_1 = LocalDate.parse("2017-02-28");
	public final static LocalDate ENDING_VALIDITY_DATE_2 = LocalDate.parse("2017-03-01");
	public final static boolean IS_ACTIVE1 = true;
	public final static boolean IS_ACTIVE2 = false;
	public final static int ID_WORKFLOW_1 = 1;
	public final static int ID_WORKFLOW_2 = 2;

	/**
	 * Test method for the Form (CRUD)
	 */
	public void testForm() {
		// Initialize a Form
		Form form = buildForm();
		// Create the Form in database
		FormHome.create(form);
		// Find the Form created in database
		Form formStored = FormHome.findByPrimaryKey(form.getIdForm());
		// Check Asserts
		checkAsserts(formStored, form);

		// Update the form
		form.setTitle(TITLE_FORM_2);
		form.setDescription(DESCRIPTION_FORM_2);
		form.setCategory(CATEGORY_FORM_2);
		form.setStartingValidityDate(STARTING_VALIDITY_DATE_2);
		form.setEndingValidityDate(ENDING_VALIDITY_DATE_2);
		form.setIsActive(IS_ACTIVE2);
		form.setIdWorkflow(ID_WORKFLOW_2);
		// Update the form in database
		FormHome.update(form);
		// Find the form updated in database
		formStored = FormHome.findByPrimaryKey(form.getIdForm());
		// Check Asserts
		checkAsserts(formStored, form);

		// Delete the form
		FormHome.delete(form.getIdForm());
		formStored = FormHome.findByPrimaryKey(form.getIdForm());
		// Check the form has been removed from database
		assertNull(formStored);
	}

	/**
	 * 
	 */
	public void testWeekDefinition() {
		Form form = buildForm();
		FormHome.create(form);

		WeekDefinition weekDefinition1 = new WeekDefinition();
		weekDefinition1.setDateOfApply(WeekDefinitionTest.DATE_OF_APPLY_1);
		weekDefinition1.setIdForm(form.getIdForm());
		WeekDefinitionHome.create(weekDefinition1);

		WorkingDay workingDay1 = new WorkingDay();
		workingDay1.setDayOfWeek(WorkingDayTest.DAY_OF_WEEK_1);
		workingDay1.setIdWeekDefinition(weekDefinition1.getIdWeekDefinition());
		WorkingDayHome.create(workingDay1);

		TimeSlot timeSlot1 = new TimeSlot();
		timeSlot1.setStartingHour(TimeSlotTest.STARTING_HOUR_1);
		timeSlot1.setEndingHour(TimeSlotTest.ENDING_HOUR_1);
		timeSlot1.setIsOpen(TimeSlotTest.IS_OPEN_1);
		timeSlot1.setIdWorkingDay(workingDay1.getIdWorkingDay());
		TimeSlotHome.create(timeSlot1);

		TimeSlot timeSlot2 = new TimeSlot();
		timeSlot2.setStartingHour(TimeSlotTest.STARTING_HOUR_2);
		timeSlot2.setEndingHour(TimeSlotTest.ENDING_HOUR_2);
		timeSlot2.setIsOpen(TimeSlotTest.IS_OPEN_2);
		timeSlot2.setIdWorkingDay(workingDay1.getIdWorkingDay());
		TimeSlotHome.create(timeSlot2);

		WorkingDay workingDay2 = new WorkingDay();
		workingDay2.setDayOfWeek(WorkingDayTest.DAY_OF_WEEK_2);
		workingDay2.setIdWeekDefinition(weekDefinition1.getIdWeekDefinition());
		WorkingDayHome.create(workingDay2);

		TimeSlot timeSlot3 = new TimeSlot();
		timeSlot3.setStartingHour(TimeSlotTest.STARTING_HOUR_1);
		timeSlot3.setEndingHour(TimeSlotTest.ENDING_HOUR_1);
		timeSlot3.setIsOpen(TimeSlotTest.IS_OPEN_1);
		timeSlot3.setIdWorkingDay(workingDay2.getIdWorkingDay());
		TimeSlotHome.create(timeSlot3);

		TimeSlot timeSlot4 = new TimeSlot();
		timeSlot4.setStartingHour(TimeSlotTest.STARTING_HOUR_2);
		timeSlot4.setEndingHour(TimeSlotTest.ENDING_HOUR_2);
		timeSlot4.setIsOpen(TimeSlotTest.IS_OPEN_2);
		timeSlot4.setIdWorkingDay(workingDay2.getIdWorkingDay());
		TimeSlotHome.create(timeSlot4);

		WeekDefinition weekDefinition2 = new WeekDefinition();
		weekDefinition2.setDateOfApply(WeekDefinitionTest.DATE_OF_APPLY_2);
		weekDefinition2.setIdForm(form.getIdForm());
		WeekDefinitionHome.create(weekDefinition2);

		WorkingDay workingDay3 = new WorkingDay();
		workingDay3.setDayOfWeek(WorkingDayTest.DAY_OF_WEEK_1);
		workingDay3.setIdWeekDefinition(weekDefinition2.getIdWeekDefinition());
		WorkingDayHome.create(workingDay3);

		TimeSlot timeSlot5 = new TimeSlot();
		timeSlot5.setStartingHour(TimeSlotTest.STARTING_HOUR_1);
		timeSlot5.setEndingHour(TimeSlotTest.ENDING_HOUR_1);
		timeSlot5.setIsOpen(TimeSlotTest.IS_OPEN_1);
		timeSlot5.setIdWorkingDay(workingDay3.getIdWorkingDay());
		TimeSlotHome.create(timeSlot5);

		TimeSlot timeSlot6 = new TimeSlot();
		timeSlot6.setStartingHour(TimeSlotTest.STARTING_HOUR_2);
		timeSlot6.setEndingHour(TimeSlotTest.ENDING_HOUR_2);
		timeSlot6.setIsOpen(TimeSlotTest.IS_OPEN_2);
		timeSlot6.setIdWorkingDay(workingDay3.getIdWorkingDay());
		TimeSlotHome.create(timeSlot6);

		WorkingDay workingDay4 = new WorkingDay();
		workingDay4.setDayOfWeek(WorkingDayTest.DAY_OF_WEEK_2);
		workingDay4.setIdWeekDefinition(weekDefinition2.getIdWeekDefinition());
		WorkingDayHome.create(workingDay4);

		TimeSlot timeSlot7 = new TimeSlot();
		timeSlot7.setStartingHour(TimeSlotTest.STARTING_HOUR_1);
		timeSlot7.setEndingHour(TimeSlotTest.ENDING_HOUR_1);
		timeSlot7.setIsOpen(TimeSlotTest.IS_OPEN_1);
		timeSlot7.setIdWorkingDay(workingDay4.getIdWorkingDay());
		TimeSlotHome.create(timeSlot7);

		TimeSlot timeSlot8 = new TimeSlot();
		timeSlot8.setStartingHour(TimeSlotTest.STARTING_HOUR_2);
		timeSlot8.setEndingHour(TimeSlotTest.ENDING_HOUR_2);
		timeSlot8.setIsOpen(TimeSlotTest.IS_OPEN_2);
		timeSlot8.setIdWorkingDay(workingDay4.getIdWorkingDay());
		TimeSlotHome.create(timeSlot8);

		List<WeekDefinition> listWeekDefinition = FormHome.getListWeekDefinition(form.getIdForm());
		assertEquals(listWeekDefinition.size(), 2);

		// Clean
		FormHome.delete(form.getIdForm());

	}

	/**
	 * Build a Form Business Object
	 * 
	 * @return a form
	 */
	public static Form buildForm() {
		Form form = new Form();
		form.setTitle(TITLE_FORM_1);
		form.setDescription(DESCRIPTION_FORM_1);
		form.setCategory(CATEGORY_FORM_1);
		form.setStartingValidityDate(STARTING_VALIDITY_DATE_1);
		form.setEndingValidityDate(ENDING_VALIDITY_DATE_1);
		form.setIsActive(IS_ACTIVE1);
		form.setIdWorkflow(ID_WORKFLOW_1);
		return form;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param formStored
	 *            the Form stored
	 * @param form
	 *            the Form created
	 */
	public void checkAsserts(Form formStored, Form form) {
		assertEquals(formStored.getTitle(), form.getTitle());
		assertEquals(formStored.getDescription(), form.getDescription());
		assertEquals(formStored.getCategory(), form.getCategory());
		assertEquals(formStored.getStartingValidityDate(), form.getStartingValidityDate());
		assertEquals(formStored.getEndingValidityDate(), form.getEndingValidityDate());
		assertEquals(formStored.isActive(), form.isActive());
		assertEquals(formStored.getIdWorkflow(), form.getIdWorkflow());
	}

}
