package fr.paris.lutece.plugins.appointment.service;

import static java.lang.Math.toIntExact;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.message.FormMessageHome;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class FormService {
	/**
	 * Name of the bean of the service
	 */
	public static final String BEAN_NAME = "appointment.formService";

	/**
	 * Instance of the service
	 */
	private static volatile FormService _instance;

	/**
	 * Get an instance of the service
	 * 
	 * @return An instance of the service
	 */
	public static FormService getInstance() {
		if (_instance == null) {
			_instance = SpringContextService.getBean(BEAN_NAME);
		}

		return _instance;
	}

	/**
	 * 
	 * @param nIdForm
	 * @param newNameForCopy
	 */
	public static void copyForm(int nIdForm, String newNameForCopy) {
		AppointmentForm appointmentForm = buildAppointmentForm(nIdForm, 0);
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
			if (listWorkingDay != null && !listWorkingDay.isEmpty()) {
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

	/**
	 * 
	 * @param form
	 * @return
	 */
	private static AppointmentForm buildAppointmentFormLight(Form form) {
		AppointmentForm appointmentForm = new AppointmentForm();
		appointmentForm.setIdForm(form.getIdForm());
		appointmentForm.setTitle(form.getTitle());
		appointmentForm.setIsActive(form.isActive());
		return appointmentForm;
	}

	/**
	 * 
	 * @param nIdForm
	 * @param nIdReservationRule
	 * @return
	 */
	public static AppointmentForm buildAppointmentForm(int nIdForm, int nIdReservationRule) {
		AppointmentForm appointmentForm = new AppointmentForm();
		Form form = FormService.findFormByPrimaryKey(nIdForm);
		if (form != null) {
			fillAppointmentFormWithFormPart(appointmentForm, form);
			Display display = DisplayService.findDisplayWithFormId(nIdForm);
			if (display != null) {
				fillAppointmentFormWithDisplayPart(appointmentForm, display);
			}
			FormRule formRule = FormRuleService.findFormRuleWithFormId(nIdForm);
			if (formRule != null) {
				fillAppointmentFormWithFormRulePart(appointmentForm, formRule);
			}
			ReservationRule reservationRule;
			LocalDate dateOfApply = LocalDate.now();
			if (nIdReservationRule > 0) {
				reservationRule = ReservationRuleService.findReservationRuleById(nIdReservationRule);
				dateOfApply = reservationRule.getDateOfApply();
			} else {
				reservationRule = ReservationRuleService.findReservationRuleByIdFormAndDateOfApply(nIdForm,
						dateOfApply);
			}
			if (reservationRule != null) {
				fillAppointmentFormWithReservationRulePart(appointmentForm, reservationRule);
			}
			WeekDefinition weekDefinition = WeekDefinitionService
					.findWeekDefinitionByFormIdAndClosestToDateOfApply(nIdForm, dateOfApply);
			if (weekDefinition != null) {
				fillAppointmentFormWithWeekDefinitionPart(appointmentForm, weekDefinition);
			}
		}
		return appointmentForm;
	}

	/**
	 * 
	 * @param appointmentForm
	 * @param weekDefinition
	 */
	private static void fillAppointmentFormWithWeekDefinitionPart(AppointmentForm appointmentForm,
			WeekDefinition weekDefinition) {
		if (weekDefinition != null && !weekDefinition.getListWorkingDay().isEmpty()) {			
			for (WorkingDay workingDay : weekDefinition.getListWorkingDay()) {
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
			LocalTime minStartingTime = null;
			LocalTime maxEndingTime = null;
			long lDurationAppointment = 0;
			for (WorkingDay workingDay : weekDefinition.getListWorkingDay()) {
				if (workingDay.getListTimeSlot() != null) {
					for (TimeSlot timeSlot : workingDay.getListTimeSlot()) {
						LocalTime startingTime = timeSlot.getStartingTime();
						LocalTime endingTime = timeSlot.getEndingTime();
						if (minStartingTime == null) {
							minStartingTime = startingTime;
						}
						if (startingTime.isBefore(minStartingTime)) {
							minStartingTime = startingTime;
						}
						if (maxEndingTime == null) {
							maxEndingTime = endingTime;
						}
						if (endingTime.isAfter(maxEndingTime)) {
							maxEndingTime = endingTime;
						}
						long lDurationTemp = startingTime.until(endingTime, ChronoUnit.MINUTES);
						if (lDurationAppointment == 0) {
							lDurationAppointment = lDurationTemp;
						}
						if (lDurationTemp < lDurationAppointment) {
							lDurationAppointment = lDurationTemp;
						}
					}
				}
			}
			appointmentForm.setTimeStart(minStartingTime.toString());
			appointmentForm.setTimeEnd(maxEndingTime.toString());
			appointmentForm.setDurationAppointments(toIntExact(lDurationAppointment));
		}
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
		Form form = FormService.findFormByPrimaryKey(appointmentForm.getIdForm());
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

	/**
	 * 
	 * @param nIdForm
	 * @return
	 */
	public static Form findFormByPrimaryKey(int nIdForm) {
		return FormHome.findByPrimaryKey(nIdForm);
	}

}
