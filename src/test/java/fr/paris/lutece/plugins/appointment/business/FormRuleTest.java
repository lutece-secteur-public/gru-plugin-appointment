package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.FormRuleHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the FormRule
 * 
 * @author Laurent Payen
 *
 */
public final class FormRuleTest extends LuteceTestCase
{

    public static final boolean IS_CAPTCHA_ENABLED_1 = true;
    public static final boolean IS_CAPTCHA_ENABLED_2 = false;
    public static final boolean IS_MANDATORY_EMAIL_ENABLED_1 = true;
    public static final boolean IS_MANDATORY_EMAIL_ENABLED_2 = false;

    /**
     * Test method for the FormRule (CRUD)
     */
    public void testFormRule( )
    {
        Form form = FormTest.buildForm( );
        FormHome.create( form );

        // Initialize a FormRule
        FormRule formRule = buildFormRule( );
        formRule.setIdForm( form.getIdForm( ) );
        // Create the FormRule in database
        FormRuleHome.create( formRule );
        // Find the FormRule created in database
        FormRule formRuleStored = FormRuleHome.findByPrimaryKey( formRule.getIdFormRule( ) );
        // Check Asserts
        checkAsserts( formRuleStored, formRule );

        // Update the FormRule
        formRule.setIsCaptchaEnabled( IS_CAPTCHA_ENABLED_2 );
        formRule.setIsMandatoryEmailEnabled( IS_MANDATORY_EMAIL_ENABLED_2 );
        // Update the FormRule in database
        FormRuleHome.update( formRule );
        // Find the FormRule updated in database
        formRuleStored = FormRuleHome.findByPrimaryKey( formRule.getIdFormRule( ) );
        // Check Asserts
        checkAsserts( formRuleStored, formRule );

        // Delete the FormRule
        FormRuleHome.delete( formRule.getIdFormRule( ) );
        formRuleStored = FormRuleHome.findByPrimaryKey( formRule.getIdFormRule( ) );
        // Check the FormRule has been removed from database
        assertNull( formRuleStored );

        // Clean
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Test delete cascade
     */
    public void testDeleteCascade( )
    {
        Form form = FormTest.buildForm( );
        FormHome.create( form );

        // Initialize a FormRule
        FormRule formRule = buildFormRule( );
        formRule.setIdForm( form.getIdForm( ) );
        // Create the FormRule in database
        FormRuleHome.create( formRule );
        // Find the FormRule created in database
        FormRule formRuleStored = FormRuleHome.findByPrimaryKey( formRule.getIdFormRule( ) );
        assertNotNull( formRuleStored );
        // Delete the Form and by cascade the Rule
        FormHome.delete( form.getIdForm( ) );
        formRuleStored = FormRuleHome.findByPrimaryKey( formRule.getIdFormRule( ) );
        // Check the FormRule has been removed from database
        assertNull( formRuleStored );
    }

    /**
     * Test findByIdFOrm method
     */
    public void testFindByIdForm( )
    {
        Form form = FormTest.buildForm( );
        FormHome.create( form );

        // Initialize a FormRule
        FormRule formRule = buildFormRule( );
        formRule.setIdForm( form.getIdForm( ) );
        // Create the FormRule in database
        FormRuleHome.create( formRule );
        // Find the FormRule created in database
        FormRule formRuleStored = FormRuleHome.findByIdForm( form.getIdForm( ) );
        // Check Asserts
        checkAsserts( formRuleStored, formRule );

        // Clean
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Build a FormRule Business Object
     * 
     * @return the formRule
     */
    public FormRule buildFormRule( )
    {
        FormRule formRule = new FormRule( );
        formRule.setIsCaptchaEnabled( IS_CAPTCHA_ENABLED_1 );
        formRule.setIsMandatoryEmailEnabled( IS_MANDATORY_EMAIL_ENABLED_1 );
        return formRule;
    }

    /**
     * Check that all the asserts are true
     * 
     * @param formRuleStored
     *            the FormRule stored
     * @param formRule
     *            the FormRule created
     */
    public void checkAsserts( FormRule formRuleStored, FormRule formRule )
    {
        assertEquals( formRuleStored.isCaptchaEnabled( ), formRule.isCaptchaEnabled( ) );
        assertEquals( formRuleStored.isMandatoryEmailEnabled( ), formRule.isMandatoryEmailEnabled( ) );
        assertEquals( formRuleStored.getIdForm( ), formRule.getIdForm( ) );
    }

}
