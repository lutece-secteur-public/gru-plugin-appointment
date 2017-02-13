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

	public static void createForm(AppointmentForm appointmentForm) {
		Form form = FormService.generateForm(appointmentForm);
		int nIdForm = form.getIdForm();
		FormMessageService.createFormMessageWithDefaultValues(nIdForm);
		LocalDate dateNow = LocalDate.now();
		DisplayService.generateDisplay(appointmentForm, nIdForm);
		FormRuleService.generateFormRule(appointmentForm, nIdForm);
		ReservationRuleService.generateReservationRule(appointmentForm, nIdForm, dateNow);
		WeekDefinition weekDefinition = WeekDefinitionService.generateWeekDefinition(nIdForm, dateNow);
		int nIdWeekDefinition = weekDefinition.getIdWeekDefinition();
		LocalTime startingHour = LocalTime.parse(appointmentForm.getTimeStart());
		LocalTime endingHour = LocalTime.parse(appointmentForm.getTimeEnd());
		int nDuration = appointmentForm.getDurationAppointments();
		List<WorkingDay> listWorkingDay = new ArrayList<>();
		for (DayOfWeek dayOfWeek : WorkingDayService.getOpenDays(appointmentForm)) {
			listWorkingDay.add(WorkingDayService.generateWorkingDayAndListTimeSlot(nIdWeekDefinition, dayOfWeek,
					startingHour, endingHour, nDuration));
		}
		weekDefinition.setListWorkingDay(listWorkingDay);
	}

	public static List<AppointmentForm> buildAllAppointmentFormLight() {
		List<AppointmentForm> listAppointmentFormLight = new ArrayList<>();
		for (Form form : FormService.findAllForms()) {
			listAppointmentFormLight.add(buildAppointmentFormLight(form));
		}
		return listAppointmentFormLight;
	}

	private static AppointmentForm buildAppointmentFormLight(Form form) {
		AppointmentForm appointmentForm = new AppointmentForm();
		appointmentForm.setIdForm(form.getIdForm());
		appointmentForm.setTitle(form.getTitle());
		appointmentForm.setIsActive(form.isActive());
		return appointmentForm;
	}

	public static AppointmentForm buildAppointmentForm(int nIdForm) {
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

			LocalDate dateNow = LocalDate.now();
			ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndDateOfApply(nIdForm,
					dateNow);
			if (reservationRule != null) {
				fillAppointmentFormWithReservationRulePart(appointmentForm, reservationRule);
			}

			WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByFormIdAndDateOfApply(nIdForm,
					dateNow);
			if (weekDefinition != null) {
				fillAppointmentFormWithWeekDefinitionPart(appointmentForm, weekDefinition);
			}
		}
		return appointmentForm;
	}

	private static void fillAppointmentFormWithWeekDefinitionPart(AppointmentForm appointmentForm,
			WeekDefinition weekDefinition) {
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
		// We suppose that all the days have the same opening and closing hours
		LocalTime minStartingTime = null;
		LocalTime maxEndingTime = null;
		long lDurationAppointment = 0;
		if (weekDefinition != null && !weekDefinition.getListWorkingDay().isEmpty()) {
			for (WorkingDay workingDay : weekDefinition.getListWorkingDay()) {
				if (workingDay.getListTimeSlot() != null) {
					for (TimeSlot timeSlot : workingDay.getListTimeSlot()) {
						LocalTime startingHour = timeSlot.getStartingHour();
						LocalTime endingHour = timeSlot.getEndingHour();
						if (minStartingTime == null) {
							minStartingTime = startingHour;
						}
						if (startingHour.isBefore(minStartingTime)) {
							minStartingTime = startingHour;
						}
						if (maxEndingTime == null) {
							maxEndingTime = endingHour;
						}
						if (endingHour.isAfter(maxEndingTime)) {
							maxEndingTime = endingHour;
						}
						long lDurationTemp = startingHour.until(endingHour, ChronoUnit.MINUTES);
						if (lDurationAppointment == 0) {
							lDurationAppointment = lDurationTemp;
						}
						if (lDurationTemp < lDurationAppointment) {
							lDurationAppointment = lDurationTemp;
						}
					}
				}
			}			
		}
		appointmentForm.setTimeStart(minStartingTime.toString());
		appointmentForm.setTimeEnd(maxEndingTime.toString());
		appointmentForm.setDurationAppointments(toIntExact(lDurationAppointment));	
	}

	private static void fillAppointmentFormWithReservationRulePart(AppointmentForm appointmentForm,
			ReservationRule reservationRule) {
		appointmentForm.setMaxCapacityPerSlot(reservationRule.getMaxCapacityPerSlot());
		appointmentForm.setMaxPeoplePerAppointment(reservationRule.getMaxPeoplePerAppointment());
	}

	private static void fillAppointmentFormWithFormRulePart(AppointmentForm appointmentForm, FormRule formRule) {
		appointmentForm.setEnableCaptcha(formRule.isCaptchaEnabled());
		appointmentForm.setEnableMandatoryEmail(formRule.isMandatoryEmailEnabled());
	}

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

	private static void fillAppointmentFormWithDisplayPart(AppointmentForm appointmentForm, Display display) {
		appointmentForm.setDisplayTitleFo(display.isDisplayTitleFo());
		appointmentForm.setIcon(display.getIcon());
		appointmentForm.setNbWeeksToDisplay(display.getNbWeeksToDisplay());
		appointmentForm.setCalendarTemplateId(display.getIdCalendarTemplate());
	}

	public static Form generateForm(AppointmentForm appointmentForm) {
		Form form = new Form();
		form.setTitle(appointmentForm.getTitle());
		form.setDescription(appointmentForm.getDescription());
		form.setReference(appointmentForm.getReference());
		form.setCategory(appointmentForm.getCategory());
		form.setStartingValiditySqlDate(appointmentForm.getDateStartValidity());
		form.setEndingValiditySqlDate(appointmentForm.getDateEndValidity());
		form.setIsActive(appointmentForm.getIsActive());
		form.setIdWorkflow(appointmentForm.getIdWorkflow());
		FormHome.create(form);
		return form;
	}

	public static void removeForm(int nIdForm) {
		FormHome.delete(nIdForm);
	}

	public static List<Form> findAllForms() {
		return FormHome.findAllForms();
	}

	public static Form findFormByPrimaryKey(int nIdForm) {
		return FormHome.findByPrimaryKey(nIdForm);
	}

}
