package fr.paris.lutece.plugins.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.message.FormMessageHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;

/**
 * Service class for a form
 * 
 * @author Laurent Payen
 *
 */
public class FormService {

	/**
	 * Make a copy of form, with all its values
	 * 
	 * @param nIdForm
	 *            the Form Id to copy
	 * @param newNameForCopy
	 *            the new Name of the copy
	 */
	public static void copyForm(int nIdForm, String newNameForCopy) {
		AppointmentForm appointmentForm = buildAppointmentForm(nIdForm, 0, 0);
		appointmentForm.setTitle(newNameForCopy);
		appointmentForm.setIsActive(Boolean.FALSE);
		int nIdNewForm = createAppointmentForm(appointmentForm);
		FormMessage formMessage = FormMessageHome.findByPrimaryKey(nIdForm);
		formMessage.setIdForm(nIdNewForm);
		FormMessageHome.create(formMessage);
	}

	/**
	 * Create a form from an appointmentForm DTO
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @return the id of the form created
	 */
	public static int createAppointmentForm(AppointmentForm appointmentForm) {
		Form form = FormService.createForm(appointmentForm);
		int nIdForm = form.getIdForm();
		FormMessageService.createFormMessageWithDefaultValues(nIdForm);
		LocalDate dateNow = LocalDate.now();
		DisplayService.createDisplay(appointmentForm, nIdForm);
		FormRuleService.createFormRule(appointmentForm, nIdForm);
		ReservationRule reservationRule = ReservationRuleService.createReservationRule(appointmentForm, nIdForm,
				dateNow);
		int nMaxCapacity = reservationRule.getMaxCapacityPerSlot();
		WeekDefinition weekDefinition = WeekDefinitionService.createWeekDefinition(nIdForm, dateNow);
		int nIdWeekDefinition = weekDefinition.getIdWeekDefinition();
		LocalTime startingTime = LocalTime.parse(appointmentForm.getTimeStart());
		LocalTime endingTime = LocalTime.parse(appointmentForm.getTimeEnd());
		int nDuration = appointmentForm.getDurationAppointments();
		for (DayOfWeek dayOfWeek : WorkingDayService.getOpenDays(appointmentForm)) {
			WorkingDayService.generateWorkingDayAndListTimeSlot(nIdWeekDefinition, dayOfWeek, startingTime, endingTime,
					nDuration, nMaxCapacity);
		}
		return nIdForm;
	}

	/**
	 * Update a form with the new values of an appointmentForm DTO
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @param dateOfModification
	 *            the date of the update
	 */
	public static void updateAppointmentForm(AppointmentForm appointmentForm, LocalDate dateOfModification) {
		Form form = FormService.updateForm(appointmentForm);
		int nIdForm = form.getIdForm();
		DisplayService.updateDisplay(appointmentForm, nIdForm);
		FormRuleService.updateFormRule(appointmentForm, nIdForm);
		if (dateOfModification != null) {
			ReservationRule reservationRule = ReservationRuleService.updateReservationRule(appointmentForm, nIdForm,
					dateOfModification);
			int nMaxCapacity = reservationRule.getMaxCapacityPerSlot();
			WeekDefinition weekDefinition = WeekDefinitionService.updateWeekDefinition(nIdForm, dateOfModification);
			int nIdWeekDefinition = weekDefinition.getIdWeekDefinition();
			List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinition(nIdWeekDefinition);
			if (CollectionUtils.isNotEmpty(listWorkingDay)) {
				WorkingDayService.deleteListWorkingDay(listWorkingDay);
			}
			LocalTime startingHour = LocalTime.parse(appointmentForm.getTimeStart());
			LocalTime endingHour = LocalTime.parse(appointmentForm.getTimeEnd());
			int nDuration = appointmentForm.getDurationAppointments();
			for (DayOfWeek dayOfWeek : WorkingDayService.getOpenDays(appointmentForm)) {
				WorkingDayService.generateWorkingDayAndListTimeSlot(nIdWeekDefinition, dayOfWeek, startingHour,
						endingHour, nDuration, nMaxCapacity);
			}
		}
	}

	/**
	 * Build all the appointForm DTO of the database light because the
	 * appointFormDTO is only fill in with the form id, the title and if the
	 * form is active or not
	 * 
	 * @return the list of all the appointmentForm DTO
	 */
	public static List<AppointmentForm> buildAllAppointmentFormLight() {
		List<AppointmentForm> listAppointmentFormLight = new ArrayList<>();
		for (Form form : FormService.findAllForms()) {
			checkValidityDate(form);
			listAppointmentFormLight.add(buildAppointmentFormLight(form));
		}
		return listAppointmentFormLight;
	}

	/**
	 * Build all the active forms of the database
	 * 
	 * @return a list of appointmentForm DTO
	 */
	public static List<AppointmentForm> buildAllActiveAppointmentForm() {
		List<AppointmentForm> listAppointmentForm = new ArrayList<>();
		for (Form form : FormService.findAllActiveForms()) {
			listAppointmentForm.add(buildAppointmentForm(form.getIdForm(), 0, 0));
		}
		return listAppointmentForm;
	}

	/**
	 * Build an appointmentFormDTO light
	 * 
	 * @param form
	 *            the form object
	 * @return the appointmentForm DTO
	 */
	public static AppointmentForm buildAppointmentFormLight(Form form) {
		AppointmentForm appointmentForm = new AppointmentForm();
		fillAppointmentFormLightWithFormPart(appointmentForm, form);
		return appointmentForm;
	}

	/**
	 * Build an appointmentForm light
	 * 
	 * @param nIdForm
	 *            the form Id
	 * @return the appointmentForm DTO
	 */
	public static AppointmentForm buildAppointmentFormLight(int nIdForm) {
		Form form = FormService.findFormLightByPrimaryKey(nIdForm);
		return buildAppointmentFormLight(form);
	}

	/**
	 * Fill the appointmentForm DTO with the values of the form object
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @param form
	 *            the form Object
	 */
	private static void fillAppointmentFormLightWithFormPart(AppointmentForm appointmentForm, Form form) {
		appointmentForm.setIdForm(form.getIdForm());
		appointmentForm.setTitle(form.getTitle());
		appointmentForm.setIsActive(form.isActive());
		appointmentForm.setIdWorkflow(form.getIdWorkflow());
		if (form.getIdCategory() == null || form.getIdCategory() == 0) {
			appointmentForm.setIdCategory(-1);
		} else {
			appointmentForm.setIdCategory(form.getIdCategory());
		}
	}

	/**
	 * Build an appointmentForm DTO
	 * 
	 * @param nIdForm
	 *            the Form Id
	 * @param nIdReservationRule
	 *            the Reservation Rule Id
	 * @param nIdWeekDefinition
	 *            the WeekDefinition Id
	 * @return the apointmentForm DTO built
	 */
	public static AppointmentForm buildAppointmentForm(int nIdForm, int nIdReservationRule, int nIdWeekDefinition) {
		AppointmentForm appointmentForm = new AppointmentForm();
		Form form = FormService.findFormLightByPrimaryKey(nIdForm);
		fillAppointmentFormWithFormPart(appointmentForm, form);
		Display display = DisplayService.findDisplayWithFormId(nIdForm);
		fillAppointmentFormWithDisplayPart(appointmentForm, display);
		FormRule formRule = FormRuleService.findFormRuleWithFormId(nIdForm);
		fillAppointmentFormWithFormRulePart(appointmentForm, formRule);
		ReservationRule reservationRule = null;
		WeekDefinition weekDefinition = null;
		LocalDate dateOfApply = LocalDate.now();
		if (nIdReservationRule > 0) {
			reservationRule = ReservationRuleService.findReservationRuleById(nIdReservationRule);
			dateOfApply = reservationRule.getDateOfApply();
		}
		if (nIdWeekDefinition > 0) {
			weekDefinition = WeekDefinitionService.findWeekDefinitionById(nIdWeekDefinition);
			dateOfApply = weekDefinition.getDateOfApply();
		}
		if (reservationRule == null) {
			reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply(nIdForm,
					dateOfApply);
		}
		if (weekDefinition == null) {
			weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply(nIdForm,
					dateOfApply);
		}
		fillAppointmentFormWithReservationRulePart(appointmentForm, reservationRule);
		fillAppointmentFormWithWeekDefinitionPart(appointmentForm, weekDefinition);
		return appointmentForm;
	}

	/**
	 * Fill the appointmentForm DTO with the WeekDefinition
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @param weekDefinition
	 *            the week definition
	 */
	private static void fillAppointmentFormWithWeekDefinitionPart(AppointmentForm appointmentForm,
			WeekDefinition weekDefinition) {
		List<WorkingDay> listWorkingDay = weekDefinition.getListWorkingDay();
		for (WorkingDay workingDay : listWorkingDay) {
			DayOfWeek dayOfWeek = DayOfWeek.of(workingDay.getDayOfWeek());
			switch (dayOfWeek) {
			case MONDAY:
				appointmentForm.setIsOpenMonday(Boolean.TRUE);
				break;
			case TUESDAY:
				appointmentForm.setIsOpenTuesday(Boolean.TRUE);
				break;
			case WEDNESDAY:
				appointmentForm.setIsOpenWednesday(Boolean.TRUE);
				break;
			case THURSDAY:
				appointmentForm.setIsOpenThursday(Boolean.TRUE);
				break;
			case FRIDAY:
				appointmentForm.setIsOpenFriday(Boolean.TRUE);
				break;
			case SATURDAY:
				appointmentForm.setIsOpenSaturday(Boolean.TRUE);
				break;
			case SUNDAY:
				appointmentForm.setIsOpenSunday(Boolean.TRUE);
				break;
			}
		}
		// We suppose that all the days have the same opening and closing
		// hours (it can be modified after)
		LocalTime minStartingTime = WorkingDayService.getMinStartingTimeOfAListOfWorkingDay(listWorkingDay);
		LocalTime maxEndingTime = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay(listWorkingDay);
		int nDurationAppointment = WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay(listWorkingDay);
		appointmentForm.setTimeStart(minStartingTime.toString());
		appointmentForm.setTimeEnd(maxEndingTime.toString());
		appointmentForm.setDurationAppointments(nDurationAppointment);
	}

	/**
	 * Fill the appointmentForm DTO with the Reservation Rule
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @param reservationRule
	 *            the reservation rule
	 */
	private static void fillAppointmentFormWithReservationRulePart(AppointmentForm appointmentForm,
			ReservationRule reservationRule) {
		appointmentForm.setIdReservationRule(reservationRule.getIdReservationRule());
		appointmentForm.setMaxCapacityPerSlot(reservationRule.getMaxCapacityPerSlot());
		appointmentForm.setMaxPeoplePerAppointment(reservationRule.getMaxPeoplePerAppointment());
	}

	/**
	 * Fill the appointmentForm DTO with the form rule
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @param formRule
	 *            the form rule
	 */
	private static void fillAppointmentFormWithFormRulePart(AppointmentForm appointmentForm, FormRule formRule) {
		appointmentForm.setEnableCaptcha(formRule.isCaptchaEnabled());
		appointmentForm.setEnableMandatoryEmail(formRule.isMandatoryEmailEnabled());
		appointmentForm.setActiveAuthentication(formRule.isActiveAuthentication());
		appointmentForm.setNbDaysBeforeNewAppointment(formRule.getNbDaysBeforeNewAppointment());		
		appointmentForm.setMinTimeBeforeAppointment(formRule.getMinTimeBeforeAppointment());
		appointmentForm.setNbMaxAppointmentsPerUser(formRule.getNbMaxAppointmentsPerUser());
		appointmentForm.setNbDaysForMaxAppointmentsPerUser(formRule.getNbDaysForMaxAppointmentsPerUser());
	}

	/**
	 * Fill the appointmentForm DTO with the form
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @param form
	 *            the Form
	 */
	private static void fillAppointmentFormWithFormPart(AppointmentForm appointmentForm, Form form) {
		appointmentForm.setIdForm(form.getIdForm());
		appointmentForm.setTitle(form.getTitle());
		appointmentForm.setDescription(form.getDescription());
		appointmentForm.setReference(form.getReference());
		if (form.getIdCategory() == null || form.getIdCategory() == 0) {
			appointmentForm.setIdCategory(-1);
		} else {
			appointmentForm.setIdCategory(form.getIdCategory());
		}
		appointmentForm.setDateStartValidity(form.getStartingValiditySqlDate());
		appointmentForm.setDateEndValidity(form.getEndingValiditySqlDate());
		appointmentForm.setIdWorkflow(form.getIdWorkflow());
		appointmentForm.setIsActive(form.isActive());
	}

	/**
	 * Fill the appointmentForm DTO with the display
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @param display
	 *            the display
	 */
	private static void fillAppointmentFormWithDisplayPart(AppointmentForm appointmentForm, Display display) {
		appointmentForm.setDisplayTitleFo(display.isDisplayTitleFo());
		appointmentForm.setIcon(display.getIcon());
		appointmentForm.setNbWeeksToDisplay(display.getNbWeeksToDisplay());
		appointmentForm.setCalendarTemplateId(display.getIdCalendarTemplate());
	}

	/**
	 * Check the validity of the form and update it if necessary
	 * 
	 * @param form
	 *            the form to check
	 */
	private static void checkValidityDate(Form form) {
		LocalDate dateNow = LocalDate.now();
		if (form.getStartingValidityDate() != null && !form.isActive()
				&& (form.getStartingValidityDate().isBefore(dateNow))
				&& (form.getEndingValidityDate() == null || form.getEndingValidityDate().isAfter(dateNow))) {
			form.setIsActive(true);
			FormHome.update(form);

		} else if (form.getEndingValidityDate() != null && form.isActive()
				&& form.getEndingValidityDate().isBefore(dateNow)) {
			form.setIsActive(false);
			FormHome.update(form);
		}

	}

	/**
	 * Create a form from an appointmentForm DTO
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @return the Form created
	 */
	public static Form createForm(AppointmentForm appointmentForm) {
		Form form = new Form();
		form = fillInFormWithAppointmentForm(form, appointmentForm);
		FormHome.create(form);
		return form;
	}

	/**
	 * Update a form object with the values of the appointmentForm DTO
	 * 
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @return the Form object updated
	 */
	public static Form updateForm(AppointmentForm appointmentForm) {
		Form form = FormService.findFormLightByPrimaryKey(appointmentForm.getIdForm());
		form = fillInFormWithAppointmentForm(form, appointmentForm);
		FormHome.update(form);
		return form;
	}

	/**
	 * Fill the form object with the values of the appointmentForm DTO
	 * 
	 * @param form
	 *            the form object
	 * @param appointmentForm
	 *            the appointmentForm DTO
	 * @return the form completed
	 */
	public static Form fillInFormWithAppointmentForm(Form form, AppointmentForm appointmentForm) {
		form.setTitle(appointmentForm.getTitle());
		form.setDescription(appointmentForm.getDescription());
		form.setReference(appointmentForm.getReference());
		if (appointmentForm.getIdCategory() == -1) {
			form.setIdCategory(null);
		} else {
			form.setIdCategory(appointmentForm.getIdCategory());
		}
		form.setStartingValiditySqlDate(appointmentForm.getDateStartValidity());
		form.setEndingValiditySqlDate(appointmentForm.getDateEndValidity());
		form.setIsActive(appointmentForm.getIsActive());
		form.setIdWorkflow(appointmentForm.getIdWorkflow());
		return form;
	}

	/**
	 * Remove a Form from the database
	 * 
	 * @param nIdForm
	 *            the form id to remove
	 */
	public static void removeForm(int nIdForm) {
		AppointmentListenerManager.notifyListenersAppointmentFormRemoval(nIdForm);
		FormHome.delete(nIdForm);
	}

	/**
	 * Find all the forms in the database
	 * 
	 * @return a list of all the forms
	 */
	public static List<Form> findAllForms() {
		return FormHome.findAllForms();
	}

	/**
	 * Find all the active forms in database
	 * 
	 * @return a list of all the active forms
	 */
	public static List<Form> findAllActiveForms() {
		return FormHome.findActiveForms();
	}

	/**
	 * Find a form by its primary key
	 * 
	 * @param nIdForm
	 *            the form id
	 */
	public static Form findFormLightByPrimaryKey(int nIdForm) {
		return FormHome.findByPrimaryKey(nIdForm);
	}

}
