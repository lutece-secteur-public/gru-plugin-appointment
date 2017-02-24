/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
package fr.paris.lutece.plugins.appointment.web;

import static java.lang.Math.toIntExact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentHoliDaysHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormService;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.appointment.service.TimeSlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.WorkingDayService;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.url.UrlItem;
/**
 * JspBean to manage calendar slots
 */
@Controller(controllerJsp = AppointmentSlotJspBean.JSP_MANAGE_APPOINTMENT_SLOTS, controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM)
public class AppointmentSlotJspBean extends MVCAdminJspBean {
	/**
	 * JSP of this JSP Bean
	 */
	public static final String JSP_MANAGE_APPOINTMENT_SLOTS = "ManageAppointmentSlots.jsp";

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 2376721852596997810L;

	// Messages
	private static final String MESSAGE_MANAGE_SLOTS_PAGE_TITLE = "appointment.manageCalendarSlots.pageTitle";
	private static final String MESSAGE_TYPICAL_WEEK_PAGE_TITLE = "appointment.typicalWeek.pageTitle";
	private static final String MESSAGE_MODIFY_SLOT_PAGE_TITLE = "appointment.modifyCalendarSlots.pageTitle";
	private static final String MESSAGE_WARNING_CHANGES_APPLY_TO_ALL = "appointment.modifyCalendarSlots.warningModifiyingEndingTime";
	private static final String MESSAGE_ERROR_DAY_HAS_APPOINTMENT = "appointment.modifyCalendarSlots.errorDayHasAppointment";
	private static final String MESSAGE_ERROR_FORM_NOT_ACTIVE = "appointment.message.error.formNotActive";
	private static final String MESSAGE_ERROR_TIME_END_BEFORE_TIME_START = "appointment.modifyCalendarSlots.errorTimeEndBeforeTimeStart";
	private static final String MESSAGE_ERROR_DURATION_MUST_BE_MULTIPLE_OF_REF_DURATION = "appointment.message.error.durationAppointmentSlotNotMultipleRef";
	private static final String MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM = "appointment.message.error.slotCanNotEndAfterDayOrForm";
	private static final String MESSAGE_INFO_SLOT_UPDATED = "appointment.modifyCalendarSlots.messageSlotUpdated";
	private static final String MESSAGE_MANAGE_HOLIDAYS_PAGE_TITLE = "appointment.manageHolidays.pageTitle";
	private static final String MESSAGE_ERROR_DATE_EXIST = "appointment.message.error.closeDayExist";
	private static final String MESSAGE_ERROR_EMPTY_DATE = "appointment.message.error.EmptycloseDay";
	private static final String MESSAGE_INFO_REMOVE_DATE = "appointment.info.appointmentform.closingDayRemoved";
	private static final String MESSAGE_INFO_ADD_DATE = "appointment.info.appointmentform.closingDayAdded";
	private static final String MESSAGE_INFO_IMPORTED_CLOSING_DAYS = "appointment.info.appointmentform.closingDayImport";
	private static final String MESSAGE_ERROR_EMPTY_FILE = "appointment.message.error.closingDayErrorImport";
	private static final String MESSAGE_ERROR_EXISTING_DATES = "appointment.message.error.closingDayErrorDaysExists";
	private static final String VIEW_MANAGE_HOLIDAYS = "viewManageHolidays";
	// Parameters
	private static final String PARAMETER_ID_FORM = "id_form";
	private static final String PARAMETER_ID_DAY = "id_day";
	private static final String PARAMETER_ID_SLOT = "id_slot";
	private static final String PARAMETER_ID_TIME_SLOT = "id_time_slot";
	private static final String PARAMETER_DAY_OF_WEEK = "dow";
	private static final String PARAMETER_DATE_OF_APPLY = "date_of_apply";
	private static final String PARAMETER_EVENTS = "events";
	private static final String PARAMETER_MIN_DURATION = "min_duration";
	private static final String PARAMETER_MIN_TIME = "min_time";
	private static final String PARAMETER_MAX_TIME = "max_time";
	private static final String PARAMETER_IS_OPEN = "is_open";
	private static final String PARAMETER_ENDING_TIME = "ending_time";
	private static final String PARAMETER_MAX_CAPACITY = "max_capacity";
	private static final String PARAMETER_APPOINTMENT_FORM = "appointmentform";
	private static final String PARAMETER_NB_WEEK = "nb_week";
	private static final String PARAMETER_ID_WEEK_DEFINITION = "id_week_definition";
	private static final String PARAMETER_ID_RESERVATION_RULE = "id_reservation_rule";

	// Marks
	private static final String MARK_SLOT = "slot";
	private static final String MARK_LOCALE = "language";
	private static final String MARK_HOLIDAY = "dateHoliday";
	private static final String MARK_LIST_DAYS = "listDays";
	private static final String MARK_COLUMN = "column";
	private static final String MARK_ROW = "row";
	private static final String MARK_EXCEL_EXTENSION_XLSX = "xlsx";
	private static final String MARK_FORMAT_DATE = "dd/MM/yyyy";
	private static final String MARK_FILE_CLOSING_DAYS = "fileHolidays";
	private static final String MARK_COLUMN_DAY = "appointment.manageHolidays.export_holidays.columnDay";
	private static final String MARK_COLUMN_MONTH = "appointment.manageHolidays.export_holidays.columnMonth";
	private static final String MARK_COLUMN_YEAR = "appointment.manageHolidays.export_holidays.columnYeay";
	private static final String MARK_COLUMN_DATE = "appointment.manageHolidays.export_holidays.columnShortDate";
	private static final String MARK_FORMAT_DATE_REGEX = "([0-9]{2})/([0-9]{2})/([0-9]{4})";
	private static final String MARK_ERROR_MSG = "appointment.manageHolidays.error.formatDate";
	private static final String MARK_ERROR_FORMAT_DATE = "appointment.manageHolidays.error.typeFormatNotValid";
	private static final String MARK_LIST_DATE_OF_MODIFICATION = "listDateOfModification";

	// Views
	private static final String VIEW_MANAGE_APPOINTMENT_SLOTS = "manageAppointmentSlots";
	private static final String VIEW_MANAGE_TYPICAL_WEEK = "manageTypicalWeek";
	private static final String VIEW_MODIFY_APPOINTMENT_SLOT = "viewModifySlots";

	// Actions
	private static final String ACTION_DO_CHANGE_SLOT_ENABLING = "doChangeSlotEnabling";
	private static final String ACTION_DO_MODIFY_SLOT = "doModifySlot";
	private static final String ACTION_DO_MODIFY_HOLIDAYS = "doModifyHolidays";

	// JSP URL
	private static final String JSP_URL_MANAGE_APPOINTMENT_SLOT = "jsp/admin/plugins/appointment/"
			+ JSP_MANAGE_APPOINTMENT_SLOTS;

	// Templates
	private static final String TEMPLATE_MANAGE_SLOTS = "admin/plugins/appointment/slots/manage_slots.html";
	private static final String TEMPLATE_MANAGE_TYPICAL_WEEK = "admin/plugins/appointment/slots/manage_typical_week.html";
	private static final String TEMPLATE_MODIFY_SLOT = "admin/plugins/appointment/slots/modify_slot.html";
	private static final String TEMPLATE_MANAGE_HOLIDAYS = "admin/plugins/appointment/slots/modify_appointmentform_holidays.html";
	// services
	private final AppointmentFormService _appointmentFormService = SpringContextService
			.getBean(AppointmentFormService.BEAN_NAME);

	@View(VIEW_MANAGE_HOLIDAYS)
	public String getManageHolidays(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);

		if (StringUtils.isNotEmpty(strIdForm) && StringUtils.isNumeric(strIdForm)) {
			if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
					AppointmentResourceIdService.PERMISSION_MODIFY_FORM, getUser())) {
				throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_MODIFY_FORM);
			}

			int nIdForm = Integer.parseInt(strIdForm);

			if (StringUtils.isNotEmpty(strIdForm) && StringUtils.isNumeric(strIdForm)) {
				nIdForm = Integer.parseInt(strIdForm);
			}

			AppointmentForm form = AppointmentFormHome.findByPrimaryKey(nIdForm);
			List<Date> listDays = AppointmentHoliDaysHome.findByIdForm(Integer.parseInt(strIdForm));

			Map<String, Object> model = getModel();
			model.put(MARK_LOCALE, getLocale());
			model.put(PARAMETER_APPOINTMENT_FORM, form);
			model.put(MARK_LIST_DAYS, listDays);
			AppointmentFormJspBean.addElementsToModelForLeftColumn(request, form, getUser(), getLocale(), model);

			return getPage(MESSAGE_MANAGE_HOLIDAYS_PAGE_TITLE, TEMPLATE_MANAGE_HOLIDAYS, model);
		}

		return redirect(request, AppointmentFormJspBean.getURLManageAppointmentForms(request));
	}

	@SuppressWarnings("deprecation")
	@Action(ACTION_DO_MODIFY_HOLIDAYS)
	public String doModifyHolidays(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);

		if (StringUtils.isEmpty(strIdForm) || !StringUtils.isNumeric(strIdForm)) {
			return redirect(request, AppointmentFormJspBean.getURLManageAppointmentForms(request));
		}

		int nIdForm = Integer.parseInt(strIdForm);
		List<Date> listHolidaysDb = AppointmentHoliDaysHome.findByIdForm(nIdForm);

		String strHoliday = (request.getParameter(MARK_HOLIDAY) == null) ? StringUtils.EMPTY
				: request.getParameter(MARK_HOLIDAY);
		String strDateDay = (request.getParameter("dateDay") == null) ? StringUtils.EMPTY
				: request.getParameter("dateDay");
		String strPathFile = StringUtils.EMPTY;

		MultipartHttpServletRequest mRequest;
		FileItem item = null;

		if (strDateDay.isEmpty() && strHoliday.isEmpty()) {
			mRequest = (MultipartHttpServletRequest) request;
			item = mRequest.getFile(MARK_FILE_CLOSING_DAYS);

			if (item != null) {
				if (StringUtils.isNotEmpty(item.getName())) {
					strPathFile = item.getName();
				}
			}
		}

		if (strHoliday.isEmpty() && strDateDay.isEmpty() && strPathFile.isEmpty()) {
			addError(MESSAGE_ERROR_EMPTY_DATE, getLocale());

			return redirect(request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, nIdForm);
		} else if (StringUtils.isNotEmpty(strHoliday)) {
			Date date = new Date(DateUtil.getDate(strHoliday).getTime());

			if (StringUtils.isNotEmpty(strIdForm) && StringUtils.isNumeric(strIdForm)) {
				if (listHolidaysDb.contains(date)) {
					addError(MESSAGE_ERROR_DATE_EXIST, getLocale());

					return redirect(request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, nIdForm);
				} else {
					AppointmentHoliDaysHome.create(date, nIdForm);
					addInfo(MESSAGE_INFO_ADD_DATE, getLocale());
				}
			}
		}

		if (StringUtils.isNotEmpty(strDateDay)) {
			Date dateDay = new Date(DateUtil.getDate(strDateDay).getTime());
			AppointmentHoliDaysHome.remove(dateDay, Integer.parseInt(strIdForm));
			addInfo(MESSAGE_INFO_REMOVE_DATE, getLocale());
		}

		if (StringUtils.isNotEmpty(strPathFile)) {
			List<Date> listImported = getImportClosingDays(item);

			if (listImported.size() == 0) {
				addError(MESSAGE_ERROR_EMPTY_FILE, getLocale());

				return redirect(request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, nIdForm);
			} else {
				if (listHolidaysDb.equals(listImported)) {
					addError(MESSAGE_ERROR_EXISTING_DATES, getLocale());

					return redirect(request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, nIdForm);
				} else {
					for (Date d : listImported) {
						if (!listHolidaysDb.contains(d)) {
							AppointmentHoliDaysHome.create(d, nIdForm);
						}
					}

					addInfo(MESSAGE_INFO_IMPORTED_CLOSING_DAYS, getLocale());
				}
			}
		}

		return redirect(request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, Integer.parseInt(strIdForm));
	}

	@SuppressWarnings("deprecation")
	private List<Date> getImportClosingDays(FileItem item) throws AccessDeniedException {
		List<Date> listDays = new ArrayList<Date>();
		FileInputStream fis = null;
		String strExtension = FilenameUtils.getExtension(item.getName());
		DateFormat dateFormat = new SimpleDateFormat(MARK_FORMAT_DATE);

		if (strExtension.equals(MARK_EXCEL_EXTENSION_XLSX)) {
			try {
				fis = (FileInputStream) item.getInputStream();

				// Using XSSF for xlsx format, for xls use HSSF
				Workbook workbook = new XSSFWorkbook(fis);

				int numberOfSheets = workbook.getNumberOfSheets();

				// looping over each workbook sheet
				for (int i = 0; i < numberOfSheets; i++) {
					Sheet sheet = workbook.getSheetAt(i);
					Iterator<Row> rowIterator = sheet.iterator();

					// iterating over each row
					while (rowIterator.hasNext()) {
						Row row = (Row) rowIterator.next();

						if (row.getRowNum() > 1) {
							Iterator<Cell> cellIterator = row.cellIterator();

							// Iterating over each cell (column wise) in a
							// particular row.
							while (cellIterator.hasNext()) {
								Cell cell = (Cell) cellIterator.next();

								// The Cell Containing String will is name.
								if (cell.getColumnIndex() == 3) {
									String strdate = StringUtils.EMPTY;

									if (cell.getCellType() == 0) {
										java.util.Date date = cell.getDateCellValue();

										strdate = dateFormat.format(date);
									}

									if (cell.getCellType() == 1) {
										strdate = cell.getStringCellValue();
									} else {
										AppLogService.error(MARK_ERROR_FORMAT_DATE + MARK_COLUMN + " : "
												+ (cell.getColumnIndex() + 1) + MARK_ROW + " : " + row.getRowNum());
									}

									if (StringUtils.isNotEmpty(strdate)) {
										if (strdate.matches(MARK_FORMAT_DATE_REGEX)) {
											Date date = new Date(DateUtil.getDate(strdate).getTime());
											listDays.add(date);
										} else {
											AppLogService.error(MARK_ERROR_MSG);
										}
									}
								}
							}
						}
					}
				}

				fis.close();
				workbook.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return listDays;
	}

	public String getExportClosingDays(HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);

		if (StringUtils.isEmpty(strIdForm) || !StringUtils.isNumeric(strIdForm)) {
			return redirect(request, AppointmentFormJspBean.getURLManageAppointmentForms(request));
		}

		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser())) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT);
		}

		int nIdForm = Integer.parseInt(strIdForm);
		AppointmentForm form = AppointmentFormHome.findByPrimaryKey(nIdForm);
		List<Date> listHolidays = AppointmentHoliDaysHome.findByIdForm(nIdForm);
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook
				.createSheet(I18nService.getLocalizedString("appointment.permission.label.resourceType", getLocale()));

		List<Object[]> tmpObj = new ArrayList<Object[]>();

		if (listHolidays != null) {
			Object[] strWriter = new String[1];
			strWriter[0] = form.getTitle();
			tmpObj.add(strWriter);

			Object[] strInfos = new String[4];
			strInfos[0] = I18nService.getLocalizedString(MARK_COLUMN_DAY, getLocale());
			strInfos[1] = I18nService.getLocalizedString(MARK_COLUMN_MONTH, getLocale());
			strInfos[2] = I18nService.getLocalizedString(MARK_COLUMN_YEAR, getLocale());
			strInfos[3] = I18nService.getLocalizedString(MARK_COLUMN_DATE, getLocale());

			tmpObj.add(strInfos);
		}

		if (listHolidays.size() > 0) {
			for (Date date : listHolidays) {
				Calendar cal = GregorianCalendar.getInstance(Locale.FRENCH);
				cal.setTime(date);

				int year = cal.get(Calendar.YEAR);
				String strmonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRENCH);
				int day = cal.get(Calendar.DAY_OF_MONTH);

				Object[] strWriter = new String[4];
				strWriter[0] = String.valueOf(day);
				strWriter[1] = strmonth;
				strWriter[2] = String.valueOf(year);
				strWriter[3] = DateUtil.getDateString(date, getLocale());
				tmpObj.add(strWriter);
			}
		}

		int nRownum = 0;

		for (Object[] myObj : tmpObj) {
			Row row = sheet.createRow(nRownum++);
			int nCellnum = 0;

			for (Object strLine : myObj) {
				Cell cell = row.createCell(nCellnum++);

				if (strLine instanceof String) {
					cell.setCellValue((String) strLine);
				} else if (strLine instanceof Boolean) {
					cell.setCellValue((Boolean) strLine);
				} else if (strLine instanceof Date) {
					cell.setCellValue((Date) strLine);
				} else if (strLine instanceof Double) {
					cell.setCellValue((Double) strLine);
				}
			}
		}

		try {
			String now = new SimpleDateFormat("ddMMyyyy-hhmm")
					.format(GregorianCalendar.getInstance(getLocale()).getTime()) + "_"
					+ I18nService.getLocalizedString("appointment.permission.label.resourceType", getLocale())
					+ DownloadConstants.EXCEL_FILE_EXTENSION;
			response.setContentType(DownloadConstants.EXCEL_MIME_TYPE);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + now + "\";");
			response.setHeader("Pragma", "public");
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate,post-check=0,pre-check=0");

			OutputStream os = response.getOutputStream();
			workbook.write(os);
			os.close();
			workbook.close();
		} catch (IOException e) {
			AppLogService.error(e);
		}

		return null;
	}

	/**
	 * Get the page to manage slots of a form or a day
	 * 
	 * @param request
	 *            The request
	 * @return The HTML content to display or the next URL to redirect to
	 * @throws AccessDeniedException
	 */
	@View(defaultView = true, value = VIEW_MANAGE_APPOINTMENT_SLOTS)
	public String getManageSlots(HttpServletRequest request) throws AccessDeniedException {
		AppointmentAsynchronousUploadHandler.getHandler().removeSessionFiles(request.getSession().getId());
		Map<String, Object> model = getModel();
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {
			return getPage(MESSAGE_MANAGE_SLOTS_PAGE_TITLE, TEMPLATE_MANAGE_SLOTS, model);
		}
		int nIdForm = Integer.parseInt(strIdForm);
		_appointmentFormService.removeAppointmentFromSession(request.getSession());
		_appointmentFormService.removeValidatedAppointmentFromSession(request.getSession());
		AppointmentForm form = FormService.buildAppointmentForm(nIdForm, 0);

		LocalDate dateNow = LocalDate.now();
		WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByFormIdAndClosestToDateOfApply(nIdForm,
				dateNow);
		List<WorkingDay> listWorkingDay = WorkingDayService
				.findListWorkingDayByWeekDefinition(weekDefinition.getIdWeekDefinition());
		LocalTime minStartingTime = null;
		LocalTime maxEndingTime = null;
		long lDurationAppointment = 0;
		List<TimeSlot> listTimeSlot = new ArrayList<>();
		List<String> listDayOfWeek = new ArrayList<>();
		for (WorkingDay workingDay : listWorkingDay) {
			listDayOfWeek.add(new Integer(workingDay.getDayOfWeek()).toString());
			if (workingDay.getListTimeSlot() != null) {
				for (TimeSlot timeSlot : workingDay.getListTimeSlot()) {
					LocalTime startingHour = timeSlot.getStartingTime();
					LocalTime endingHour = timeSlot.getEndingTime();
					// Need to add the current date to the hour
					LocalDateTime startingDateTime = dateNow.with(DayOfWeek.of(workingDay.getDayOfWeek()))
							.atTime(startingHour);
					LocalDateTime endingDateTime = dateNow.with(DayOfWeek.of(workingDay.getDayOfWeek()))
							.atTime(endingHour);
					timeSlot.setStartingDateTime(startingDateTime);
					timeSlot.setEndingDateTime(endingDateTime);
					listTimeSlot.add(timeSlot);
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
		model.put("dow", listDayOfWeek);
		model.put("events", listTimeSlot);
		model.put(PARAMETER_MIN_TIME, minStartingTime.toString());
		model.put(PARAMETER_MAX_TIME, maxEndingTime.toString());
		model.put("duration", "00:" + lDurationAppointment);
		model.put(MARK_LOCALE, getLocale());
		AppointmentFormJspBean.addElementsToModelForLeftColumn(request, form, getUser(), getLocale(), model);
		return getPage(MESSAGE_MANAGE_SLOTS_PAGE_TITLE, TEMPLATE_MANAGE_SLOTS, model);
	}

	@View(value = VIEW_MANAGE_TYPICAL_WEEK)
	public String getManageTypicalWeek(HttpServletRequest request) throws AccessDeniedException {
		Map<String, Object> model = getModel();
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {

		}
		String strIdReservationRule = request.getParameter(PARAMETER_ID_RESERVATION_RULE);
		int nIdReservationRule = 0;
		if (StringUtils.isNotEmpty(strIdReservationRule) && StringUtils.isNumeric(strIdReservationRule)) {
			nIdReservationRule = Integer.parseInt(strIdReservationRule);
		}
		LocalDate dateOfApply = LocalDate.now();
		int nIdForm = Integer.parseInt(strIdForm);
		ReservationRule reservationRule;
		if (nIdReservationRule != 0) {
			reservationRule = ReservationRuleService.findReservationRuleById(nIdReservationRule);
			dateOfApply = reservationRule.getDateOfApply();
		} else {
			reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply(nIdForm,
					dateOfApply);
		}
		AppointmentForm form = FormService.buildAppointmentForm(nIdForm, reservationRule.getIdReservationRule());
		WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByFormIdAndClosestToDateOfApply(nIdForm,
				dateOfApply);
		if (weekDefinition != null) {
			List<WorkingDay> listWorkingDay = WorkingDayService
					.findListWorkingDayByWeekDefinition(weekDefinition.getIdWeekDefinition());
			LocalTime minStartingTime = null;
			LocalTime maxEndingTime = null;
			long lDurationAppointment = 0;
			List<TimeSlot> listTimeSlot = new ArrayList<>();
			List<String> listDayOfWeek = new ArrayList<>();
			for (WorkingDay workingDay : listWorkingDay) {
				listDayOfWeek.add(new Integer(workingDay.getDayOfWeek()).toString());
				if (workingDay.getListTimeSlot() != null) {
					for (TimeSlot timeSlot : workingDay.getListTimeSlot()) {
						LocalTime startingHour = timeSlot.getStartingTime();
						LocalTime endingHour = timeSlot.getEndingTime();
						// Need to add the current date to the hour
						LocalDateTime startingDateTime = dateOfApply.with(DayOfWeek.of(workingDay.getDayOfWeek()))
								.atTime(startingHour);
						LocalDateTime endingDateTime = dateOfApply.with(DayOfWeek.of(workingDay.getDayOfWeek()))
								.atTime(endingHour);
						timeSlot.setStartingDateTime(startingDateTime);
						timeSlot.setEndingDateTime(endingDateTime);
						listTimeSlot.add(timeSlot);
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
			model.put(PARAMETER_DAY_OF_WEEK, listDayOfWeek);
			model.put(PARAMETER_EVENTS, listTimeSlot);
			model.put(PARAMETER_MIN_TIME, minStartingTime.toString());
			model.put(PARAMETER_MAX_TIME, maxEndingTime.toString());			
			model.put(PARAMETER_MIN_DURATION, LocalTime.of(0, toIntExact(lDurationAppointment)));
			model.put(PARAMETER_ID_WEEK_DEFINITION, weekDefinition.getIdWeekDefinition());
			model.put(PARAMETER_ID_RESERVATION_RULE, reservationRule.getIdReservationRule());
		}
		model.put(MARK_LIST_DATE_OF_MODIFICATION, ReservationRuleService.findAllDateOfReservationRule(nIdForm));
		model.put(PARAMETER_DATE_OF_APPLY, dateOfApply);
		model.put(PARAMETER_ID_FORM, nIdForm);
		model.put(MARK_LOCALE, getLocale());
		AppointmentFormJspBean.addElementsToModelForLeftColumn(request, form, getUser(), getLocale(), model);
		return getPage(MESSAGE_TYPICAL_WEEK_PAGE_TITLE, TEMPLATE_MANAGE_TYPICAL_WEEK, model);
	}

	/**
	 * Do change the enabling of a slot
	 * 
	 * @param request
	 *            The request
	 * @return The next URL to redirect to
	 */
	@Action(ACTION_DO_CHANGE_SLOT_ENABLING)
	public String doChangeSlotEnabling(HttpServletRequest request) {
		String strIdSlot = request.getParameter(PARAMETER_ID_SLOT);
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		String nb_week = request.getParameter(PARAMETER_NB_WEEK);

		int nNb_week = 0;

		int nIdForm = Integer.parseInt(strIdForm);

		if (StringUtils.isNotEmpty(strIdSlot) && StringUtils.isNumeric(strIdSlot)) {
			if (StringUtils.isNotEmpty(nb_week) && StringUtils.isNumeric(nb_week)) {
				nNb_week = Integer.parseInt(nb_week);
			}

			int nIdSlot = Integer.parseInt(strIdSlot);
			AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey(nIdSlot);

			if (slot != null) {
				if (slot.getIdDay() > 0) {
					AppointmentDay day = AppointmentDayHome.findByPrimaryKey(slot.getIdDay());

					if (day.getIsOpen()) {
						// we can only change enabling of opened days
						slot.setIsEnabled(!slot.getIsEnabled());
					}
				} else {
					AppointmentForm form = AppointmentFormHome.findByPrimaryKey(slot.getIdForm());

					// if (form.isDayOfWeekOpened(slot.getDayOfWeek())) {
					// we can only change enabling of opened days
					slot.setIsEnabled(!slot.getIsEnabled());
					// }
				}

				AppointmentSlotHome.update(slot);

				// even though only this slot has been modified
				// Notify for the whole form for simplicity
				AppointmentService.getInstance().notifyAppointmentFormModified(slot.getIdForm());

				if (slot.getIdDay() > 0) {
					return redirect(request, VIEW_MANAGE_APPOINTMENT_SLOTS, PARAMETER_ID_FORM, slot.getIdForm(),
							PARAMETER_NB_WEEK, nNb_week);
				}

				return redirect(request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, slot.getIdForm());
			} else {
				// form null
				// vérifier si le formulaire est activé
				AppointmentForm form = AppointmentFormHome.findByPrimaryKey(nIdForm);
				if (!form.getIsActive()) {
					addError(MESSAGE_ERROR_FORM_NOT_ACTIVE, getLocale());
					return redirect(request, VIEW_MANAGE_APPOINTMENT_SLOTS, PARAMETER_ID_FORM, form.getIdForm(),
							PARAMETER_NB_WEEK, nNb_week);
				}
			}
		}

		return redirect(request, AppointmentFormJspBean.getURLManageAppointmentForms(request));
	}

	/**
	 * Get the slot modification page
	 * 
	 * @param request
	 *            The request
	 * @return The HTML content to display
	 */
	@View(VIEW_MODIFY_APPOINTMENT_SLOT)
	public String getViewModifySlot(HttpServletRequest request) {
		String strIdSlot = request.getParameter(PARAMETER_ID_TIME_SLOT);
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		TimeSlot timeSlot = null;
		Map<String, Object> model = getModel();
		int nIdForm = 0;
		if (StringUtils.isNotEmpty(strIdForm) && StringUtils.isNumeric(strIdForm)) {
			nIdForm = Integer.parseInt(strIdForm);
			model.put(PARAMETER_APPOINTMENT_FORM, nIdForm);
		}
		if (StringUtils.isNotEmpty(strIdSlot) && StringUtils.isNumeric(strIdSlot)) {
			int nIdSlot = Integer.parseInt(strIdSlot);
			timeSlot = TimeSlotService.findTimeSlotById(nIdSlot);
		}
		if (timeSlot != null) {
			addInfo(MESSAGE_WARNING_CHANGES_APPLY_TO_ALL, getLocale());
			model.put(MARK_SLOT, timeSlot);
			model.put(MARK_LOCALE, getLocale());
			AppointmentForm appointmentForm = FormService.buildAppointmentForm(nIdForm, 0);
			AppointmentFormJspBean.addElementsToModelForLeftColumn(request, appointmentForm, getUser(), getLocale(),
					model);
			return getPage(MESSAGE_MODIFY_SLOT_PAGE_TITLE, TEMPLATE_MODIFY_SLOT, model);
		}
		return redirect(request, AppointmentFormJspBean.getURLManageAppointmentForms(request));
	}

	/**
	 * Do modify a slot
	 * 
	 * @param request
	 *            The request
	 * @return The next URL to redirect to
	 */
	/**
	 * @param request
	 * @return
	 */
	@Action(ACTION_DO_MODIFY_SLOT)
	public String doModifySlot(HttpServletRequest request) {
		int nIdTimeSlot = Integer.parseInt(request.getParameter(PARAMETER_ID_TIME_SLOT));
		boolean bIsOpen = Boolean.parseBoolean(request.getParameter(PARAMETER_IS_OPEN));
		int nMaxCapacity = Integer.parseInt(request.getParameter(PARAMETER_MAX_CAPACITY));
		LocalTime endingTime = LocalTime.parse(request.getParameter(PARAMETER_ENDING_TIME));
		TimeSlot timeSlotFromDb = TimeSlotService.findTimeSlotById(nIdTimeSlot);
		boolean endingTimeHasChanged = false;
		if (bIsOpen != timeSlotFromDb.getIsOpen()) {
			timeSlotFromDb.setIsOpen(bIsOpen);
		}
		if (nMaxCapacity != timeSlotFromDb.getMaxCapacity()) {
			timeSlotFromDb.setMaxCapacity(nMaxCapacity);
		}
		if (!endingTime.equals(timeSlotFromDb.getEndingTime())) {
			if (!checkEndingTime(endingTime, timeSlotFromDb)){
				return redirectView(request, VIEW_MODIFY_APPOINTMENT_SLOT);
			}
			timeSlotFromDb.setEndingTime(endingTime);
			endingTimeHasChanged = true;
		}
		TimeSlotService.updateTimeSlot(timeSlotFromDb, endingTimeHasChanged);
		addInfo(MESSAGE_INFO_SLOT_UPDATED, getLocale());
		return redirect(request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM,
				Integer.parseInt(request.getParameter(PARAMETER_ID_FORM)), PARAMETER_ID_WEEK_DEFINITION,
				Integer.parseInt(request.getParameter(PARAMETER_ID_WEEK_DEFINITION)));
	}

	private boolean checkEndingTime(LocalTime endingTime, TimeSlot timeSlot) {
		boolean bReturn = true;
		WorkingDay workingDay = WorkingDayService.findWorkingDayWithListTimeSlotById(timeSlot.getIdWorkingDay());
		if (endingTime.isAfter(WorkingDayService.getMaxEndingTimeOfAWorkingDay(workingDay))) {
			bReturn = false;
			addError(MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM, getLocale());
		}
		if (endingTime.isBefore(timeSlot.getStartingTime())) {
			bReturn = false;
			addError(MESSAGE_ERROR_TIME_END_BEFORE_TIME_START, getLocale());
		}
		return bReturn;
	}

	/**
	 * Get the URL to manage slots associated with a form
	 * 
	 * @param request
	 *            The request
	 * @param strIdForm
	 *            The id of the form
	 * @return The URL to manage slots
	 */
	public static String getUrlManageSlotsByIdForm(HttpServletRequest request, String strIdForm) {
		UrlItem urlItem = new UrlItem(AppPathService.getBaseUrl(request) + JSP_URL_MANAGE_APPOINTMENT_SLOT);
		urlItem.addParameter(PARAMETER_ID_FORM, strIdForm);

		return urlItem.getUrl();
	}

	/**
	 * Get the URL to manage slots associated with a day
	 * 
	 * @param request
	 *            The request
	 * @param nIdDay
	 *            The id of the day
	 * @return The URL to manage slots
	 */
	public static String getUrlManageSlotsByIdDay(HttpServletRequest request, int nIdDay) {
		return getUrlManageSlotsByIdDay(request, Integer.toString(nIdDay));
	}

	/**
	 * Get the URL to manage slots associated with a day
	 * 
	 * @param request
	 *            The request
	 * @param strIdDay
	 *            The id of the day
	 * @return The URL to manage slots
	 */
	public static String getUrlManageSlotsByIdDay(HttpServletRequest request, String strIdDay) {
		UrlItem urlItem = new UrlItem(AppPathService.getBaseUrl(request) + JSP_URL_MANAGE_APPOINTMENT_SLOT);
		urlItem.addParameter(MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENT_SLOTS);
		urlItem.addParameter(PARAMETER_ID_DAY, strIdDay);

		return urlItem.getUrl();
	}

	public static int getMaxWeek(int nbWeekToCreate, AppointmentForm form) {

		return nbWeekToCreate;

	}

}
