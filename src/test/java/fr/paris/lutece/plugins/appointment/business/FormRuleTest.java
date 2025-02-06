/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
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
        Form form = FormTest.buildForm1( );
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
     * Test findByIdFOrm method
     */
    public void testFindByIdForm( )
    {
        Form form = FormTest.buildForm1( );
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
        FormRuleHome.delete( formRule.getIdFormRule( ) );
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
        assertEquals( formRuleStored.getIsCaptchaEnabled( ), formRule.getIsCaptchaEnabled( ) );
        assertEquals( formRuleStored.getIsMandatoryEmailEnabled( ), formRule.getIsMandatoryEmailEnabled( ) );
        assertEquals( formRuleStored.getIdForm( ), formRule.getIdForm( ) );
    }

}
