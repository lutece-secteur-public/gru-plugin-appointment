package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.FormRuleHome;

/**
 * Service class for the form rule
 * 
 * @author Laurent Payen
 *
 */
public class FormRuleService {

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
	public static void fillInFormRule(FormRule formRule, AppointmentForm appointmentForm, int nIdForm) {
		formRule.setIsCaptchaEnabled(appointmentForm.getEnableCaptcha());
		formRule.setIsMandatoryEmailEnabled(appointmentForm.getEnableMandatoryEmail());
		formRule.setNbDaysBeforeNewAppointment(appointmentForm.getNbDaysBeforeNewAppointment());
		formRule.setIdForm(nIdForm);
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
	public static FormRule createFormRule(AppointmentForm appointmentForm, int nIdForm) {
		FormRule formRule = new FormRule();
		fillInFormRule(formRule, appointmentForm, nIdForm);
		FormRuleHome.create(formRule);
		return formRule;
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
	public static FormRule updateFormRule(AppointmentForm appointmentForm, int nIdForm) {
		FormRule formRule = FormRuleService.findFormRuleWithFormId(nIdForm);
		fillInFormRule(formRule, appointmentForm, nIdForm);
		FormRuleHome.update(formRule);
		return formRule;
	}

	/**
	 * Find the rules of a form
	 * 
	 * @param nIdForm
	 *            the form Id
	 * @return the form rule
	 */
	public static FormRule findFormRuleWithFormId(int nIdForm) {
		return FormRuleHome.findByIdForm(nIdForm);
	}

}
