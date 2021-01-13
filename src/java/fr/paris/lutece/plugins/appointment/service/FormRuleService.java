/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.FormRuleHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;

/**
 * Service class for the form rule
 * 
 * @author Laurent Payen
 *
 */
public final class FormRuleService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private FormRuleService( )
    {
    }

    /**
     * Fill the form rule part with the appointment DTO
     * 
     * @param formRule
     *            the Form Rull to fill
     * @param appointmentForm
     *            the AppointmentForm DTO
     * @param nIdForm
     *            the Form Id
     */
    public static void fillInFormRule( FormRule formRule, AppointmentFormDTO appointmentForm, int nIdForm )
    {
        formRule.setIsCaptchaEnabled( appointmentForm.getEnableCaptcha( ) );
        formRule.setIsMandatoryEmailEnabled( appointmentForm.getEnableMandatoryEmail( ) );
        formRule.setIsActiveAuthentication( appointmentForm.getActiveAuthentication( ) );
        formRule.setNbDaysBeforeNewAppointment( appointmentForm.getNbDaysBeforeNewAppointment( ) );
        formRule.setMinTimeBeforeAppointment( appointmentForm.getMinTimeBeforeAppointment( ) );
        formRule.setNbMaxAppointmentsPerUser( appointmentForm.getNbMaxAppointmentsPerUser( ) );
        formRule.setNbDaysForMaxAppointmentsPerUser( appointmentForm.getNbDaysForMaxAppointmentsPerUser( ) );
        formRule.setBoOverbooking( appointmentForm.getBoOverbooking( ) );
        formRule.setIdForm( nIdForm );
    }

    /**
     * Create a form rule from an appointment form DTO
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
     * @param nIdForm
     *            the form Id
     * @return the FormRule created
     */
    public static FormRule createFormRule( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        FormRule formRule = new FormRule( );
        fillInFormRule( formRule, appointmentForm, nIdForm );
        FormRuleHome.create( formRule );
        return formRule;
    }

    /**
     * Save a form rule
     * 
     * @param formRule
     *            the form rule to save
     */
    public static void saveFormRule( FormRule formRule )
    {
        FormRuleHome.create( formRule );
    }

    /**
     * Update a form rule object with the values of an appointmentForm DTO
     * 
     * @param appointmentForm
     *            the apointmentForm DTO
     * @param nIdForm
     *            the form Id
     * @return the Form Rule updated
     */
    public static FormRule updateFormRule( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        FormRule formRule = FormRuleService.findFormRuleWithFormId( nIdForm );
        fillInFormRule( formRule, appointmentForm, nIdForm );
        FormRuleHome.update( formRule );
        return formRule;
    }

    /**
     * Find the rules of a form
     * 
     * @param nIdForm
     *            the form Id
     * @return the form rule
     */
    public static FormRule findFormRuleWithFormId( int nIdForm )
    {
        return FormRuleHome.findByIdForm( nIdForm );
    }

}
