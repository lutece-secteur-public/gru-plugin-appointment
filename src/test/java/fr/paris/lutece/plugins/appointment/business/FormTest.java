package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDate;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Form
 * @author Laurent Payen
 *
 */
public class FormTest extends LuteceTestCase {

	private final static String TITLE_FORM_1 = "TitreForm1";
	private final static String TITLE_FORM_2 = "TitreForm2";
	private final static String DESCRIPTION_FORM_1 = "DescriptionForm1";
	private final static String DESCRIPTION_FORM_2 = "DescriptionForm2";
	private final static String CATEGORY_FORM_1 = "CategoryForm1";
	private final static String CATEGORY_FORM_2 = "CategoryForm2";
	private final static LocalDate STARTING_VALIDITY_DATE_1 = LocalDate.parse("2017-01-24");
	private final static LocalDate STARTING_VALIDITY_DATE_2 = LocalDate.parse("2017-01-25");
	private final static LocalDate ENDING_VALIDITY_DATE_1 = LocalDate.parse("2017-02-28");
	private final static LocalDate ENDING_VALIDITY_DATE_2 = LocalDate.parse("2017-03-01");
	private final static boolean IS_ACTIVE1 = true;
	private final static boolean IS_ACTIVE2 = false;

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
	 * Build a Form Business Object
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
	}
	
}
