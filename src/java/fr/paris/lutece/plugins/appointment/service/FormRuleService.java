package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.FormRuleHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class FormRuleService {

	/**
	 * Name of the bean of the service
	 */
	public static final String BEAN_NAME = "appointment.formRuleService";
	
	/**
	 * Instance of the service
	 */
	private static volatile FormRuleService _instance;

	/**
	 * Get an instance of the service
	 * 
	 * @return An instance of the service
	 */
	public static FormRuleService getInstance() {
		if (_instance == null) {
			_instance = SpringContextService.getBean(BEAN_NAME);
		}

		return _instance;
	}
	
	public static FormRule generateFormRule(AppointmentForm appointmentForm, int nIdForm) {
		FormRule formRule = new FormRule();
		formRule.setIsCaptchaEnabled(appointmentForm.getEnableCaptcha());
		formRule.setIsMandatoryEmailEnabled(appointmentForm.getEnableMandatoryEmail());
		formRule.setIdForm(nIdForm);
		FormRuleHome.create(formRule);
		return formRule;
	}
	
	public static FormRule findFormRuleWithFormId(int nIdForm){
		return FormRuleHome.findByIdForm(nIdForm);
	}
	
}
