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

public class FormService {

	/**
	 * 
	 * @param nIdForm
	 * @param newNameForCopy
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
	 * 
	 * @param appointmentForm
	 * @return
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
	 * 
	 * @param appointmentForm
	 * @param dateOfModification
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
	 * 
	 * @return
	 */
	public static List<AppointmentForm> buildAllAppointmentFormLight() {
		List<AppointmentForm> listAppointmentFormLight = new ArrayList<>();
		for (Form form : FormService.findAllForms()) {
			listAppointmentFormLight.add(buildAppointmentFormLight(form));
		}
		return listAppointmentFormLight;
	}

	public static List<AppointmentForm> buildAllActiveAppointmentForm() {
		List<AppointmentForm> listAppointmentForm = new ArrayList<>();
		for (Form form : FormService.findAllActiveForms()) {
			listAppointmentForm.add(buildAppointmentForm(form.getIdForm(), 0, 0));
		}
		return listAppointmentForm;
	}

	/**
	 * 
	 * @param form
	 * @return
	 */
	public static AppointmentForm buildAppointmentFormLight(Form form) {
		AppointmentForm appointmentForm = new AppointmentForm();
		fillAppointmentFormLightWithFormPart(appointmentForm, form);
		return appointmentForm;
	}

	public static AppointmentForm buildAppointmentFormLight(int nIdForm) {
		AppointmentForm appointmentForm = new AppointmentForm();
		Form form = FormService.findFormLightByPrimaryKey(nIdForm);
		fillAppointmentFormLightWithFormPart(appointmentForm, form);	
		Display display = DisplayService.findDisplayWithFormId(nIdForm);
		fillAppointmentFormWithDisplayPart(appointmentForm, display);	
		return appointmentForm;
	}

	private static void fillAppointmentFormLightWithFormPart(AppointmentForm appointmentForm, Form form) {
		appointmentForm.setIdForm(form.getIdForm());
		appointmentForm.setTitle(form.getTitle());
		appointmentForm.setIsActive(form.isActive());
	}

	/**
	 * 
	 * @param nIdForm
	 * @param nIdReservationRule
	 * @return
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
	 * 
	 * @param appointmentForm
	 * @param weekDefinition
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
		// hours
		LocalTime minStartingTime = WorkingDayService.getMinStartingTimeOfAListOfWorkingDay(listWorkingDay);
		LocalTime maxEndingTime = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay(listWorkingDay);
		int nDurationAppointment = WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay(listWorkingDay);
		appointmentForm.setTimeStart(minStartingTime.toString());
		appointmentForm.setTimeEnd(maxEndingTime.toString());
		appointmentForm.setDurationAppointments(nDurationAppointment);
	}

	/**
	 * 
	 * @param appointmentForm
	 * @param reservationRule
	 */
	private static void fillAppointmentFormWithReservationRulePart(AppointmentForm appointmentForm,
			ReservationRule reservationRule) {
		appointmentForm.setIdReservationRule(reservationRule.getIdReservationRule());
		appointmentForm.setMaxCapacityPerSlot(reservationRule.getMaxCapacityPerSlot());
		appointmentForm.setMaxPeoplePerAppointment(reservationRule.getMaxPeoplePerAppointment());
	}

	/**
	 * 
	 * @param appointmentForm
	 * @param formRule
	 */
	private static void fillAppointmentFormWithFormRulePart(AppointmentForm appointmentForm, FormRule formRule) {
		appointmentForm.setEnableCaptcha(formRule.isCaptchaEnabled());
		appointmentForm.setEnableMandatoryEmail(formRule.isMandatoryEmailEnabled());
		appointmentForm.setNbDaysBeforeNewAppointment(formRule.getNbDaysBeforeNewAppointment());
	}

	/**
	 * 
	 * @param appointmentForm
	 * @param form
	 */
	private static void fillAppointmentFormWithFormPart(AppointmentForm appointmentForm, Form form) {
		appointmentForm.setIdForm(form.getIdForm());
		appointmentForm.setTitle(form.getTitle());
		appointmentForm.setDescription(form.getDescription());
		appointmentForm.setReference(form.getReference());
		appointmentForm.setCategory(form.getCategory());
		appointmentForm.setDateStartValidity(form.getStartingValiditySqlDate());
		appointmentForm.setDateEndValidity(form.getEndingValiditySqlDate());
		appointmentForm.setIdWorkflow(form.getIdWorkflow());
		appointmentForm.setIsActive(form.isActive());
	}

	/**
	 * 
	 * @param appointmentForm
	 * @param display
	 */
	private static void fillAppointmentFormWithDisplayPart(AppointmentForm appointmentForm, Display display) {
		appointmentForm.setDisplayTitleFo(display.isDisplayTitleFo());
		appointmentForm.setIcon(display.getIcon());
		appointmentForm.setNbWeeksToDisplay(display.getNbWeeksToDisplay());
		appointmentForm.setCalendarTemplateId(display.getIdCalendarTemplate());
	}

	/**
	 * 
	 * @param appointmentForm
	 * @return
	 */
	public static Form createForm(AppointmentForm appointmentForm) {
		Form form = new Form();
		form = fillInFormWithAppointmentForm(form, appointmentForm);
		FormHome.create(form);
		return form;
	}

	/**
	 * 
	 * @param appointmentForm
	 * @return
	 */
	public static Form updateForm(AppointmentForm appointmentForm) {
		Form form = FormService.findFormLightByPrimaryKey(appointmentForm.getIdForm());
		form = fillInFormWithAppointmentForm(form, appointmentForm);
		FormHome.update(form);
		return form;
	}

	/**
	 * 
	 * @param form
	 * @param appointmentForm
	 * @return
	 */
	public static Form fillInFormWithAppointmentForm(Form form, AppointmentForm appointmentForm) {
		form.setTitle(appointmentForm.getTitle());
		form.setDescription(appointmentForm.getDescription());
		form.setReference(appointmentForm.getReference());
		form.setCategory(appointmentForm.getCategory());
		form.setStartingValiditySqlDate(appointmentForm.getDateStartValidity());
		form.setEndingValiditySqlDate(appointmentForm.getDateEndValidity());
		form.setIsActive(appointmentForm.getIsActive());
		form.setIdWorkflow(appointmentForm.getIdWorkflow());
		return form;
	}

	/**
	 * 
	 * @param nIdForm
	 */
	public static void removeForm(int nIdForm) {
		FormHome.delete(nIdForm);
	}

	/**
	 * 
	 * @return
	 */
	public static List<Form> findAllForms() {
		return FormHome.findAllForms();
	}

	public static List<Form> findAllActiveForms() {
		return FormHome.findActiveForms();
	}

	/**
	 * 
	 * @param nIdForm
	 * @return
	 */
	public static Form findFormLightByPrimaryKey(int nIdForm) {
		return FormHome.findByPrimaryKey(nIdForm);
	}

}
