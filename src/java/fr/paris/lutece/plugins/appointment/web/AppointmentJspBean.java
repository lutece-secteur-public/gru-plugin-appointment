/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.Appointment.Status;
import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.AppointmentFilter;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormMessages;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormMessagesHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.ResponseRecapDTO;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormService;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.addon.AppointmentAddOnManager;
import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.csv.CSVReaderService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.util.LocalizedDelegatePaginator;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpRequest;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.naming.RefAddr;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;


/**
 * This class provides the user interface to manage Appointment features (
 * manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageAppointments.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentJspBean extends MVCAdminJspBean
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1978001810468444844L;
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR = "/admin/plugins/appointment/appointment/manage_appointments_calendar.html";
    private static final String TEMPLATE_CREATE_APPOINTMENT = "/admin/plugins/appointment/appointment/create_appointment.html";
    private static final String TEMPLATE_MANAGE_APPOINTMENTS = "/admin/plugins/appointment/appointment/manage_appointments.html";
    private static final String TEMPLATE_VIEW_APPOINTMENT = "/admin/plugins/appointment/appointment/view_appointment.html";
    private static final String TEMPLATE_APPOINTMENT_FORM_CALENDAR = "/admin/plugins/appointment/appointment/appointment_form_calendar.html";
    private static final String TEMPLATE_APPOINTMENT_FORM_RECAP = "/admin/plugins/appointment/appointment/appointment_form_recap.html";
    private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/appointment/appointment/tasks_form_workflow.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS = "appointment.manage_appointments.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS_CALENDAR = "appointment.manage_appointment_calendar.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT = "appointment.create_appointment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT = "appointment.view_appointment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_APPOINTMENT_CALENDAR = "appointment.appointmentCalendar.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT = "appointment.appointmentApp.recap.title";
    private static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "appointment.taskFormWorkflow.pageTitle";
    private static final String PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT = "appointment.form.nbWeekToCreate";
    // Parameters
    private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_DATE_MIN = "allDates";
    private static final String PARAMETER_FIRST_NAME = "firstname";
    private static final String PARAMETER_LAST_NAME = "lastname";
    private static final String PARAMETER_NB_WEEK = "nb_week";
    private static final String PARAMETER_MAX_WEEK = "max_week";
    private static final String PARAMETER_ID_SLOT = "idSlot";
    private static final String PARAMETER_BACK = "back";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_NEW_STATUS = "new_status";
    private static final String PARAMETER_ORDER_BY = "orderBy";
    private static final String PARAMETER_ORDER_ASC = "orderAsc";
    private static final String PARAMETER_SAVE_AND_BACK = "saveAndBack";
    private static final String PARAMETER_ID_ADMIN_USER = "idAdminUser";
    private static final String PARAMETER_ID_RESPONSE = "idResponse";
    private static final String PARAMETER_ID_TIME="time";
    private static final String PARAMETER_ID_APPOINTMENT_DELETE = "apmt";
    private static final String PARAMETER_DELETE_AND_BACK =  "eraseAll";
    private static final String PARAMETER_LIM_DATES = "bornDates";
    private static final String PARAMETER_SEEK = "Rechercher";
    private static final String PARAMETER_INDX = "page_index";
    private static final String PARAMETER_MARK_FORCE = "force";
    

    // Markers
    private static final String MARK_APPOINTMENT_LIST = "appointment_list";
    private static final String MARK_APPOINTMENT = "appointment";
    private static final String MARK_APPOINTMENTSLOT = "appointmentSlot";
    private static final String MARK_APPOINTMENTSLOTDAY = "appointmentSlotDay";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_FORM_MESSAGES = "formMessages";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_FORM = "form";
    private static final String MARK_STATUS = "libelled_status";
    private static final String MARK_FORM_ERRORS = "form_errors";
    private static final String MARK_LIST_DAYS = "listDays";
    private static final String MARK_LIST_TIME_BEGIN = "list_time_begin";
    private static final String MARK_MIN_DURATION_APPOINTMENT = "min_duration_appointments";
    private static final String MARK_SLOT = "slot";
    private static final String MARK_LIST_DAYS_OF_WEEK = "list_days_of_week";
    private static final String MARK_RIGHT_CREATE = "rightCreate";
    private static final String MARK_RIGHT_MODIFY = "rightModify";
    private static final String MARK_RIGHT_DELETE = "rightDelete";
    private static final String MARK_RIGHT_VIEW = "rightView";
    private static final String MARK_RIGHT_CHANGE_STATUS = "rightChangeStatus";
    private static final String MARK_DAY = "day";
    private static final String MARK_FILTER = "filter";
    private static final String MARK_REF_LIST_STATUS = "refListStatus";
    private static final String MARK_REF_LIST_EXPORT = "refListExports";
    private static final String MARK_FILTER_FROM_SESSION = "loadFilterFromSession";
    private static final String MARK_TASKS_FORM = "tasks_form";
    private static final String MARK_STATUS_VALIDATED = "status_validated";
    private static final String MARK_STATUS_REJECTED = "status_rejected";
    private static final String MARK_RESOURCE_HISTORY = "resource_history";
    private static final String MARK_LIST_ADMIN_USERS = "list_admin_users";
    private static final String MARK_ADMIN_USER = "admin_user";
    private static final String MARK_ADDON = "addon";
    private static final String MARK_LIST_RESPONSE_RECAP_DTO = "listResponseRecapDTO";
    private static final String MARK_LANGUAGE = "language";
    private static final String MARK_ALLDATES = "allDates";
    

    // JSPhttp://localhost:8080/lutece/jsp/site/Portal.jsp?page=appointment&action=doCancelAppointment&dateAppointment=16/04/15&refAppointment=2572c82f
    private static final String JSP_MANAGE_APPOINTMENTS = "jsp/admin/plugins/appointment/ManageAppointments.jsp";
    private static final String ERROR_MESSAGE_SLOT_FULL = "appointment.message.error.slotFull";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENT = "appointment.message.confirmRemoveAppointment";
    private static final String MESSAGE_CONFIRM_REMOVE_MASSAPPOINTMENT = "appointment.message.confirmRemoveMassAppointment";
    private static final String MESSAGE_LABEL_STATUS_VALIDATED = "appointment.message.labelStatusValidated";
    private static final String MESSAGE_LABEL_STATUS_NOT_VALIDATED = "appointment.message.labelStatusNotValidated";
    private static final String MESSAGE_LABEL_STATUS_REJECTED = "appointment.message.labelStatusRejected";
    private static final String MESSAGE_APPOINTMENT_WITH_NO_ADMIN_USER = "appointment.manage_appointment.labelAppointmentWithNoAdminUser";
    /** Infos error WorkFlow */
    private static final String INFO_APPOINTMENT_STATE_ERROR = "appointment.info.appointment.etatinitial";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE = "appointment.listAppointments.itemsPerPage";

    // Views
    private static final String VIEW_MANAGE_APPOINTMENTS = "manageAppointments";
    private static final String VIEW_CREATE_APPOINTMENT = "createAppointment";
    private static final String VIEW_GET_APPOINTMENT_CALENDAR = "getAppointmentCalendar";
    private static final String VIEW_MODIFY_APPOINTMENT = "modifyAppointment";
    private static final String VIEW_VIEW_APPOINTMENT = "viewAppointment";
    private static final String VIEW_DISPLAY_RECAP_APPOINTMENT = "displayRecapAppointment";
    private static final String VIEW_CALENDAR_MANAGE_APPOINTMENTS = "viewCalendarManageAppointment";
    private static final String VIEW_WORKFLOW_ACTION_FORM = "viewWorkflowActionForm";

    // Actions
    private static final String ACTION_DO_VALIDATE_FORM = "doValidateForm";
    private static final String ACTION_REMOVE_APPOINTMENT = "removeAppointment";
    private static final String ACTION_REMOVE_MASSAPPOINTMENT = "removeMassAppointment";
    private static final String ACTION_CONFIRM_REMOVE_APPOINTMENT = "confirmRemoveAppointment";
    private static final String ACTION_CONFIRM_REMOVE_MASS_APPOINTMENT = "confirmRemoveMassAppointment";
    private static final String ACTION_DO_MAKE_APPOINTMENT = "doMakeAppointment";
    private static final String ACTION_DO_PROCESS_WORKFLOW_ACTION = "doProcessWorkflowAction";
    private static final String ACTION_DO_CHANGE_APPOINTMENT_STATUS = "doChangeAppointmentStatus";

    // Infos
    private static final String INFO_APPOINTMENT_CREATED = "appointment.info.appointment.created";
    private static final String INFO_APPOINTMENT_UPDATED = "appointment.info.appointment.updated";
    private static final String INFO_APPOINTMENT_REMOVED = "appointment.info.appointment.removed";
    private static final String INFO_APPOINTMENT_MASSREMOVED = "appointment.info.appointment.removed";
    private static final String INFO_APPOINTMENT_EMAIL_ERROR = "appointment.info.appointment.emailerror";

    // Session keys
    private static final String SESSION_CURRENT_PAGE_INDEX = "appointment.session.currentPageIndex";
    private static final String SESSION_ITEMS_PER_PAGE = "appointment.session.itemsPerPage";
    private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";

    
    
    // Messages
    private static final String[] MESSAGE_LIST_DAYS_OF_WEEK = AppointmentService.getListDaysOfWeek(  );

    // Constants
    private static final int STATUS_CODE_ZERO = 0;
    private static final int STATUS_CODE_ONE = 1;
    private static final int STATUS_CODE_TWO = 2;
    private static final String DEFAULT_CURRENT_PAGE = "1";
    private static final String CONSTANT_SPACE = " ";
    private static final String CONSTANT_ZERO = "0";
    private final AppointmentFormService _appointmentFormService = SpringContextService.getBean( AppointmentFormService.BEAN_NAME );
    private final StateService _stateService  = SpringContextService.getBean( StateService.BEAN_SERVICE );
    private final fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService _stateServiceWorkFlow  = SpringContextService.getBean( fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService.BEAN_SERVICE );

    // Session variable to store working values
    private int _nDefaultItemsPerPage;
    private AppointmentFilter _filter;

    /**
     * Status of appointments that have not been validated yet, validate or rejected
     */
    public enum ExportFilter {
     	DAY_ONLY (STATUS_CODE_ZERO, "appointment.manage_appointments.daytitle"),
    	FROM_NOWDAY(STATUS_CODE_ONE, 	"appointment.manage_appointments.lighttitle" ),
    	ALL_DAYS (STATUS_CODE_TWO, "appointment.manage_appointments.fulltitle");
    	
    	private final int nValue;
        private final String strLibelle;
        
        ExportFilter (int nValeur, String strMessage)
        {
        	this.nValue = nValeur;
        	this.strLibelle = strMessage;
        }
        
        public int getValeur(){ return this.nValue; }
        public String getLibelle(){ return this.strLibelle; }
    }
    
    
    /**
     * Default constructor
     */
    public AppointmentJspBean(  )
    {
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE, 10 );
    }
    
    /**
     * Get Sattus for CSV Writer
     * @return
     */
    private Hashtable<Integer, String> getStatus ( Locale myLocale )
    {
    	Status[] mich = Appointment.Status.values();
       	Hashtable<Integer, String> myStatus= new Hashtable<Integer, String>();
       	for (Status tmpStatus: mich)
       		myStatus.put(tmpStatus.getValeur(), I18nService.getLocalizedString( tmpStatus.getLibelle(),  myLocale ));
       	return myStatus;
    }
    
    /**
     * Get Admin for CSV Writer
     * @return
     */
    private static Hashtable<Integer, String> getAdmins ( )
    {
    	Collection<AdminUser> listAdminUser = AdminUserHome.findUserList(  );
        Hashtable<Integer, String> myStatus= new Hashtable<Integer, String>();
       	for (AdminUser tmpUser: listAdminUser)
       		myStatus.put(tmpUser.getUserId(), tmpUser.getFirstName(  ) + CONSTANT_SPACE + tmpUser.getLastName(  ));
       	return myStatus;
    }
    /**
    * Do download a file from an appointment response
    * @param request The request
    * @param httpResponse The response
    * @return nothing.
    * @throws AccessDeniedException If the user is not authorized to access
    *             this feature
    */
   public String getDownloadFileAppointment( HttpServletRequest request, HttpServletResponse response )
       throws AccessDeniedException
   {
       String strIdResponse = request.getParameter( PARAMETER_ID_FORM );
       if ( StringUtils.isEmpty( strIdResponse ) || !StringUtils.isNumeric( strIdResponse ) )
       {
       	return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
       }
       if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdResponse,
               AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser(  ) ) )
       {
       	throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT );
   		}
       AppointmentFilter filter = (AppointmentFilter) request.getSession().getAttribute(MARK_FILTER);
        
       List<Object[]> tmpObj = new ArrayList<Object[]>();
       List<Integer> listIdAppointments = AppointmentHome.getAppointmentIdByFilter( filter );
       AppointmentForm tmpForm = AppointmentFormHome.findByPrimaryKey(Integer.valueOf(strIdResponse));
       XSSFWorkbook workbook = new XSSFWorkbook();
       XSSFSheet sheet = workbook.createSheet(I18nService.getLocalizedString( "appointment.permission.label.resourceType", getLocale() ));

       if ( tmpForm!= null )
       {
    	   Object[] strWriter = new String[1];
    	   strWriter[0] = tmpForm.getTitle();
    	   tmpObj.add( strWriter );
    	   Object[] strInfos= new String[8];
    	   strInfos[0] = I18nService.getLocalizedString( "appointment.manage_appointments.columnLastName", getLocale() );
    	   strInfos[1] = I18nService.getLocalizedString( "appointment.manage_appointments.columnFirstName", getLocale());
    	   strInfos[2] = I18nService.getLocalizedString( "appointment.manage_appointments.columnEmail", getLocale()    );
    	   strInfos[3] = I18nService.getLocalizedString( "appointment.manage_appointments.columnDateAppointment", getLocale() );
    	   strInfos[4] = I18nService.getLocalizedString( "appointment.model.entity.appointmentform.attribute.timeStart", getLocale() );
    	   strInfos[5] = I18nService.getLocalizedString( "appointment.model.entity.appointmentform.attribute.timeEnd", getLocale() );
    	   strInfos[6] = I18nService.getLocalizedString( "appointment.manage_appointments.columnAdminUser", getLocale() );
    	   strInfos[7] = I18nService.getLocalizedString( "appointment.manage_appointments.columnStatus", getLocale() );
    	   tmpObj.add( strInfos );
       }
       if ( listIdAppointments.size() > 0 )
       {
	       	List<Appointment> listAppointments = AppointmentHome.getAppointmentListById( listIdAppointments,
	       			filter.getOrderBy(  ), filter.getOrderAsc(  ) );
	      	for (Appointment tmpApp: listAppointments)
	       	{
	      		Object[] strWriter = new String[8];
	       		strWriter[0]= tmpApp.getLastName();
	       		strWriter[1]= tmpApp.getFirstName();
	       		strWriter[2]= tmpApp.getEmail();
	       		strWriter[3]= DateUtil.getDateString(tmpApp.getDateAppointment(), getLocale( ) );
	       		Calendar tmpDate = GregorianCalendar.getInstance( Locale.FRENCH );
	       		tmpDate.setTimeInMillis(tmpApp.getStartAppointment().getTime());
	       		strWriter[4]= new SimpleDateFormat("HH:mm").format(tmpDate.getTime()) ;
	       		Calendar tmpDateEnd = GregorianCalendar.getInstance( Locale.FRENCH );
	       		tmpDateEnd.setTimeInMillis(tmpApp.getEndAppointment().getTime());
	       		strWriter[5]=new SimpleDateFormat("HH:mm").format(tmpDateEnd.getTime());
	       		strWriter[6]= getAdmins ( ).get(tmpApp.getIdAdminUser()) == null ?  StringUtils.EMPTY : getAdmins ( ).get(tmpApp.getIdAdminUser());
	       		strWriter[7]= getStatus( getLocale() ).get(tmpApp.getStatus()) == null ? StringUtils.EMPTY :  getStatus( getLocale() ).get(tmpApp.getStatus());
	       		tmpObj.add(strWriter);
	       	}
       }
       int nRownum = 0;
       for (Object[] myObj : tmpObj)
       {
    	   Row row = sheet.createRow(nRownum++);
    	   int nCellnum = 0;
    	   for ( Object strLine : myObj)
    	   {
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

           try
           {
        	   String now = new SimpleDateFormat("yyyyMMdd-hhmm").format(GregorianCalendar.getInstance(getLocale()).getTime())+"_"+I18nService.getLocalizedString("appointment.permission.label.resourceType", getLocale());
        	   response.setContentType("application/vnd.ms-excel");
               response.setHeader( "Content-Disposition", "attachment; filename=\""+now+"\";" );
               response.setHeader( "Pragma", "public" );
               response.setHeader( "Expires", "0" );
               response.setHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );

               OutputStream os = response.getOutputStream(  );
               workbook.write( os );
               os.close(  );
               workbook.close();
            }
           catch ( IOException e )
           {
               AppLogService.error( e );
           }

       return null;
   }
   
  /** 
   *    Get Limited Date
   * 	@param nBWeeks
   * 	@return
 */
   private String[] getLimitedDate( int nBWeeks )
   {
	   Calendar startCal = GregorianCalendar.getInstance( Locale.FRENCH );	
	   Calendar endCal   = GregorianCalendar.getInstance( Locale.FRENCH );	
	   startCal.set(Calendar.WEEK_OF_YEAR, startCal.get(Calendar.WEEK_OF_YEAR)-nBWeeks);
	   startCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	   endCal.set(Calendar.WEEK_OF_YEAR, endCal.get(Calendar.WEEK_OF_YEAR)+nBWeeks);
	   endCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	   endCal.add(Calendar.DATE, -1);
	   String[] retour = {DateUtil.getDateString(startCal.getTime(), getLocale() ),DateUtil.getDateString(endCal.getTime(), getLocale() )};
	   return retour;
	   
   }
   
   /**
     * Get the page to manage appointments. Appointments are displayed in a
     * calendar.
     * @param request The request
     * @return The HTML code to display
     */
    @View( value = VIEW_CALENDAR_MANAGE_APPOINTMENTS, defaultView = true )
    public String getCalendarManageAppointments( HttpServletRequest request )
    {
        AppointmentAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );

        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strTimeMilli   = request.getParameter( PARAMETER_ID_TIME );
        
        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            _appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );
            _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );
            
            int nIdForm = Integer.parseInt( strIdForm );
            String strTime =  request.getParameter( PARAMETER_ID_TIME ) ;
          
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 ) + form.getNbWeeksToDisplay();
            String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );
            int nNbWeek = 0;
            if ( !StringUtils.isEmpty(strTimeMilli) || StringUtils.isNumeric( strTimeMilli ))
            {
            	 Date objMyTime = new Date ( Long.valueOf( strTimeMilli) );
           		 // Compute difference in week beetween now and date picked for the calendar button
           		 nNbWeek = computeWeek(objMyTime);
            }

            if ( StringUtils.isNotEmpty( strNbWeek ) )
            {
                nNbWeek = AppointmentService.getService(  ).parseInt( strNbWeek );
                if (Math.abs(nNbWeek) > nNbWeeksToCreate)
                	return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
             }

            List<AppointmentDay> listDays = AppointmentService.getService(  ).findAndComputeDayList( form, nNbWeek,
                    false );

            for ( AppointmentDay day : listDays )
            {
                if ( nNbWeek < 0 )
                {
                    if ( day.getIdDay(  ) > 0 )
                    {
                        List<AppointmentSlot> listSlots = AppointmentSlotHome.findByIdDayWithFreePlaces( day.getIdDay(  ) );

                        for ( AppointmentSlot slotFromDb : listSlots )
                        {
                            for ( AppointmentSlot slotComputed : day.getListSlots(  ) )
                            {
                                if ( ( slotFromDb.getStartingHour(  ) == slotComputed.getStartingHour(  ) ) &&
                                        ( slotFromDb.getStartingMinute(  ) == slotComputed.getStartingMinute(  ) ) )
                                {
                                    slotComputed.setNbFreePlaces( slotFromDb.getNbFreePlaces(  ) );
                                    slotComputed.setNbPlaces( slotFromDb.getNbPlaces(  ) );
                                    slotComputed.setIdSlot( slotFromDb.getIdSlot(  ) );
                                }
                            }
                        }

                        for ( AppointmentSlot slotComputed : day.getListSlots(  ) )
                        {
                            if ( slotComputed.getIdSlot(  ) == 0 )
                            {
                                slotComputed.setIsEnabled( false );
                            }
                        }
                    }
                    else
                    {
                        day.setIsOpen( false );
                    }
                }
                else
                {
                    // If the day has not been loaded from the database, we load its slots
                    // Otherwise, we use default computed slots
                    if ( day.getIdDay(  ) > 0 )
                    {
                        day.setListSlots( AppointmentSlotHome.findByIdDayWithFreePlaces( day.getIdDay(  ) ) );
                    }
                }
            }

            
          
            
            listDays = computeUnavailableDays(form.getIdForm(), listDays, false);
            
            
            List<String> listTimeBegin = new ArrayList<String>(  );
            int nMinAppointmentDuration = AppointmentService.getService(  )
                                                            .getListTimeBegin( listDays, form, listTimeBegin );
/*
            Calendar calendarEnd = getCalendarTime (form.getDateEndValidity(), form.getClosingHour(), form.getClosingMinutes() );
            Calendar calendarStart = getCalendarTime (form.getDateStartValidity(), form.getOpeningHour(), form.getOpeningMinutes() );

            listDays = unvalidAppointmentsbeforeNow(form.getMinDaysBeforeAppointment() , listDays, calendarStart,calendarEnd);
 */           Map<String, Object> model = getModel(  );

            model.put( MARK_FORM, form );
            model.put( MARK_LIST_DAYS, listDays );
            model.put( PARAMETER_NB_WEEK, nNbWeek );
            model.put( PARAMETER_MAX_WEEK, nNbWeeksToCreate-1 );
            model.put( PARAMETER_LIM_DATES, getLimitedDate( nNbWeeksToCreate) );
            model.put( MARK_LIST_TIME_BEGIN, listTimeBegin );
            model.put( MARK_MIN_DURATION_APPOINTMENT, nMinAppointmentDuration );
            model.put( MARK_LIST_DAYS_OF_WEEK, MESSAGE_LIST_DAYS_OF_WEEK );
            model.put( MARK_LANGUAGE, getLocale() );
            
            return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS_CALENDAR, TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR,
                model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

	/**
	 * Erase unavailable slots
	 * @param mySlots
	 * @return
	 */
	private static List<AppointmentSlot> setSlotToErase ( Calendar precisedDateFromNow, List<AppointmentSlot> mySlots , boolean bCheck)
	{
		if ( mySlots != null )
		{
			for  (int ni = 0; ni < mySlots.size();  ni++)
			{
				if (precisedDateFromNow == null)
					mySlots.get( ni ).setIsEnabled( bCheck );
				else
				{
					Calendar now = new GregorianCalendar( Locale.FRENCH );
					precisedDateFromNow = getCalendarTime(new Date ( precisedDateFromNow.getTimeInMillis() ) , Integer.valueOf(mySlots.get( ni ).getStartingHour()), Integer.valueOf( mySlots.get( ni ).getStartingMinute()) );
					if (precisedDateFromNow.before( now)  )
						mySlots.get( ni ).setIsEnabled( bCheck );
				}
			}
		}
		return mySlots;
	}
    
    /**
	 * Compute unavailable Days
	 * @param form
	 * @param listDays
	 */
	private static List<AppointmentDay> computeUnavailableDays( int nIdform, List<AppointmentDay> listDays, boolean bCheck) {
		if (listDays!= null)
		{
			Calendar nMaxSlots = null;
			for (int i = 0; i < listDays.size(); i++)
			{
				if (nMaxSlots == null || nMaxSlots.before( getCalendarTime(null, listDays.get(i).getClosingHour(), listDays.get(i).getClosingMinutes() ) ))
				{
					nMaxSlots = getCalendarTime(null, listDays.get(i).getClosingHour(), listDays.get(i).getClosingMinutes() );
					nMaxSlots.setTimeInMillis(nMaxSlots.getTimeInMillis()+TimeUnit.MINUTES.toMillis( listDays.get( i ).getAppointmentDuration() ));
				}
				if (getNumbersDay(new Date( GregorianCalendar.getInstance().getTimeInMillis() ),  listDays.get ( i ).getDate() ) < 0 )
				{	
					listDays.get(i).setListSlots ( setSlotToErase( null, listDays.get(i).getListSlots(), bCheck ) );
				}
				if (getNumbersDay(new Date( GregorianCalendar.getInstance().getTimeInMillis() ),  listDays.get ( i ).getDate() ) == 0 )
				{
							Calendar tmpCalendar = new GregorianCalendar ();
							tmpCalendar.setTime( listDays.get(i).getDate() );
							listDays.get(i).setListSlots ( setSlotToErase ( tmpCalendar, listDays.get(i).getListSlots(),bCheck));
				}
			}
			
			for (int i = 0; i < listDays.size(); i++)
			{
				if (listDays.get(i).getIsOpen() && listDays.get(i).getListSlots ( )!=null && listDays.get(i).getListSlots ( ).size()>0)
				{
					AppointmentSlot tmpSlot = listDays.get(i).getListSlots ( ).get( listDays.get(i).getListSlots ( ).size() - 1 ).clone();
					Calendar tmpMich = getCalendarTime(null, tmpSlot.getEndingHour(), tmpSlot.getEndingMinute() );
					tmpMich.setTimeInMillis(tmpMich.getTimeInMillis()+TimeUnit.MINUTES.toMillis( listDays.get( i ).getAppointmentDuration() ));
					if (tmpMich.before(nMaxSlots))
					{
						tmpSlot.setStartingHour(tmpMich.get(Calendar.HOUR_OF_DAY));
						tmpSlot.setStartingMinute(tmpMich.get(Calendar.MINUTE));
						tmpSlot.setEndingHour(nMaxSlots.get(Calendar.HOUR_OF_DAY));
						tmpSlot.setEndingMinute(nMaxSlots.get(Calendar.MINUTE));
						tmpSlot.setIdSlot(-1);
						tmpSlot.setIsEnabled(false);
						listDays.get(i).getListSlots ( ).add( listDays.get(i).getListSlots ( ).size(), tmpSlot );
					}
				}
			}
		}
		return listDays;
	}

	/**
	 * ComputeWeek in time
	 * @param objMyTime
	 * @return
	 */
	private static int computeWeek(Date objMyTime) {
		int nNbWeek;
		Calendar objNow = new GregorianCalendar();
		Calendar objAfter = new GregorianCalendar();
		objAfter.setTime(objMyTime);
		int startWeek = objNow.get(Calendar.WEEK_OF_YEAR);
		int endWeek = objAfter.get(Calendar.WEEK_OF_YEAR); 
		int idiff = objNow.get(Calendar.YEAR) - objAfter.get(Calendar.YEAR);
		int ideltaYears = 0;
		Calendar objTmp = objNow.after(objAfter) ? objAfter : objNow;
		for(int i = 0;i < idiff;i++)
		{
			ideltaYears += objTmp.getWeeksInWeekYear( ) ;
			objTmp.add(Calendar.YEAR, 1); 
		}
		 nNbWeek = (endWeek + ideltaYears) - startWeek;
		return nNbWeek;
	}

    /**
     * Transform Date to Calendar
     * @param objTime
     * @param iHour
     * @param iMinute
     * @return
     */
    private static Calendar getCalendarTime ( Date objTime, int iHour, int iMinute) 
    {
    	Calendar calendar = GregorianCalendar.getInstance( Locale.FRENCH );
        if (objTime != null)
        	calendar.setTime( objTime );
    	calendar.set(Calendar.HOUR_OF_DAY, iHour);
    	calendar.set(Calendar.MINUTE, iMinute);
    	calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    
    /**
     * Get the page to manage appointments
     * @param request The request
     * @return The HTML code to display
     */
    @View( value = VIEW_MANAGE_APPOINTMENTS )
    public String getManageAppointments( HttpServletRequest request )
    {
        AppointmentAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );
        boolean bTriForce = false;
        String strFil = null;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if (Boolean.valueOf(request.getParameter(PARAMETER_MARK_FORCE)))
        {
        	bTriForce = true;
        }
        if (request.getParameter(PARAMETER_SEEK)!=null)
        {
        	request.getSession(  ).removeAttribute( PARAMETER_ID_APPOINTMENT_DELETE);
        	request.getSession(  ).removeAttribute( SESSION_CURRENT_PAGE_INDEX);
        	request.getSession(  ).removeAttribute( SESSION_ITEMS_PER_PAGE );
            if ((AppointmentFilter ) request.getSession().getAttribute(MARK_FILTER )!=null)
             	strFil = ((AppointmentFilter ) request.getSession().getAttribute(MARK_FILTER )).getStatusFilter();
         }
        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            _appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );
            _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );

            int nIdForm = Integer.parseInt( strIdForm );
            if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_DELETE_AND_BACK ) ) )
            {
            	String [] strTaberased = request.getParameterValues( PARAMETER_ID_APPOINTMENT_DELETE);
            	if (strTaberased!= null)
            	{
             		   request.getSession(  ).setAttribute( PARAMETER_ID_APPOINTMENT_DELETE, strTaberased );
            		   return getConfirmRemoveMassAppointment ( request ) ;
            	}
            	
            }
            String strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                    (String) request.getSession(  ).getAttribute( SESSION_CURRENT_PAGE_INDEX ) );

            if ( strCurrentPageIndex == null )
            {
                strCurrentPageIndex = DEFAULT_CURRENT_PAGE;
            }

            request.getSession(  ).setAttribute( SESSION_CURRENT_PAGE_INDEX, strCurrentPageIndex );

           
            int nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                    getIntSessionAttribute( request.getSession(  ), SESSION_ITEMS_PER_PAGE ), _nDefaultItemsPerPage );
            request.getSession(  ).setAttribute( SESSION_ITEMS_PER_PAGE, nItemsPerPage );

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );
            UrlItem url = new UrlItem( JSP_MANAGE_APPOINTMENTS );
            url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTS );
            url.addParameter( PARAMETER_ID_FORM, strIdForm );
            url.addParameter( MARK_FILTER_FROM_SESSION, Boolean.TRUE.toString(  ) );
             

            String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
            String strCheckDate = request.getParameter( PARAMETER_DATE_MIN );
            
            AppointmentDay day = null;
            AppointmentSlot slot = null;
            AppointmentFilter filter;
            _filter  = (AppointmentFilter) request.getSession().getAttribute(MARK_FILTER);
            if ( ( _filter != null ) && Boolean.parseBoolean( request.getParameter( MARK_FILTER_FROM_SESSION ) ) )
            {
            	filter = _filter;
            	 if (bTriForce )
                 {	
               	   	filter.setStatusFilter(strCheckDate);
               		filter = dateFiltered(filter);
                 }
                String strOrderBy = request.getParameter( PARAMETER_ORDER_BY );

                if ( StringUtils.isNotEmpty( strOrderBy ) )
                {
                    filter.setOrderBy( strOrderBy );
                    filter.setOrderAsc( Boolean.parseBoolean( request.getParameter( PARAMETER_ORDER_ASC ) ) );
                }
                
            }
            else
            {
                filter = new AppointmentFilter(  );
               
                if (!Boolean.parseBoolean( request.getParameter( MARK_FILTER_FROM_SESSION ) ))
                	populate( filter, request );

                // We manually set the id of the admin user to -1 if no parameter is specified to avoid a bug of population that set it to 0
                String strIdAdminUser = request.getParameter( PARAMETER_ID_ADMIN_USER );

                if ( StringUtils.isBlank( strIdAdminUser ) )
                {
                    filter.setIdAdminUser( -1 );
                }

                if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
                {
                    int nIdSlot = Integer.parseInt( strIdSlot );
                    slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );
                    day = AppointmentDayHome.findByPrimaryKey( slot.getIdDay(  ) );
                    filter.setIdSlot( nIdSlot );
                    url.addParameter( PARAMETER_ID_SLOT, strIdSlot );
                }
                if (strCheckDate == null)
                {
                	strCheckDate = request.getParameter( PARAMETER_DATE_MIN ) == null ?  String.valueOf(STATUS_CODE_ZERO) : request.getParameter( PARAMETER_DATE_MIN )  ;
                	if (request.getParameter(PARAMETER_SEEK)!=null && filter.getStatusFilter()==null)
                		strCheckDate = String.valueOf(STATUS_CODE_TWO);
                }
                if (request.getParameter(PARAMETER_SEEK)!=null)
                {
                	strCheckDate = strFil;
                	bTriForce = true;
                }
               if (bTriForce || filter.getStatusFilter(  ) == null || strCheckDate.equalsIgnoreCase(filter.getStatusFilter(  )) )
              {	
            	   	filter.setStatusFilter(strCheckDate);
            		filter = dateFiltered(filter);
              }


            }
             String strUrl = url.getUrl(  );
             request.getSession().setAttribute(MARK_FILTER, filter );

            List<Integer> listIdAppointments = AppointmentHome.getAppointmentIdByFilter( filter );

            LocalizedPaginator<Integer> paginator = new LocalizedPaginator<Integer>( listIdAppointments, nItemsPerPage,
                    strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex, getLocale(  ) );

            List<Appointment> listAppointments = AppointmentHome.getAppointmentListById( paginator.getPageItems(  ),
                    filter.getOrderBy(  ), filter.getOrderAsc(  ) );

            LocalizedDelegatePaginator<Appointment> delegatePaginator = new LocalizedDelegatePaginator<Appointment>( listAppointments,
                    nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex, listIdAppointments.size(  ),
                    getLocale(  ) );

            // PAGINATOR
/*WORKFLOW FUTURE    
			ReferenceList refListStatus = new ReferenceList();
            refListStatus.addItem( AppointmentFilter.NO_STATUS_FILTER, StringUtils.EMPTY );
 
            StateFilter stateFilter = new StateFilter(  );
    	    stateFilter.setIdWorkflow( form.getIdWorkflow() );	    

            List<State> listStats = _stateService.getListStateByFilter( stateFilter );
            Map <String, String>lsSta = new HashMap<String, String>();
            for (State tmpStat : listStats )
            {
            	refListStatus.addItem( tmpStat.getId(), tmpStat.getName() );
            	lsSta.put(String.valueOf(tmpStat.getId()), tmpStat.getName());
            }
*/
            ReferenceList refListStatus = new ReferenceList( 4 );
            refListStatus.addItem( AppointmentFilter.NO_STATUS_FILTER, StringUtils.EMPTY );
            refListStatus.addItem( Appointment.Status.STATUS_VALIDATED.getValeur(),
                I18nService.getLocalizedString( Appointment.Status.STATUS_VALIDATED.getLibelle(), getLocale(  ) ) );
            refListStatus.addItem( Appointment.Status.STATUS_NOT_VALIDATED.getValeur(),
                I18nService.getLocalizedString( Appointment.Status.STATUS_NOT_VALIDATED.getLibelle(), getLocale(  ) ) );
            refListStatus.addItem( Appointment.Status.STATUS_REJECTED.getValeur(),
                I18nService.getLocalizedString( Appointment.Status.STATUS_REJECTED.getLibelle(), getLocale(  ) ) );
            ReferenceList refListExports = new ReferenceList( );
            for (ExportFilter tmpFilter : ExportFilter.values())
            	refListExports.addItem( tmpFilter.getValeur(), I18nService.getLocalizedString( tmpFilter.getLibelle(), getLocale(  ) ) );

            	Map<String, Object> model = getModel(  );
            /*WORKFLOW FUTURE           model.put( MARK_STATUS, lsSta); */
            model.put( MARK_FORM, form );
            model.put( MARK_FORM_MESSAGES, AppointmentFormMessagesHome.findByPrimaryKey( nIdForm ) );
            model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
            model.put( MARK_PAGINATOR, delegatePaginator );
            model.put( MARK_STATUS_VALIDATED, Appointment.Status.STATUS_VALIDATED.getValeur() );
            model.put( MARK_STATUS_REJECTED, Appointment.Status.STATUS_REJECTED.getValeur() );
            model.put( MARK_LANGUAGE, getLocale() );
            model.put( MARK_ALLDATES, strCheckDate);
            
            if ( ( form.getIdWorkflow(  ) > 0 ) && WorkflowService.getInstance(  ).isAvailable(  ) )
            {
            	 /*WORKFLOW FUTURE                for ( Appointment appointment : delegatePaginator.getPageItems(  ) )
                {
                	Collection<fr.paris.lutece.plugins.workflowcore.business.action.Action> resultActions = new ArrayList<fr.paris.lutece.plugins.workflowcore.business.action.Action>();
                	Collection<fr.paris.lutece.plugins.workflowcore.business.action.Action> tmpActions = WorkflowService.getInstance(  )
                            .getActions( appointment.getIdAppointment(  ),
                            		Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow(  ), getUser(  ) );
                    for (fr.paris.lutece.plugins.workflowcore.business.action.Action mcAction : tmpActions)
                    {
                    	State tmpSt = mcAction.getStateBefore();
                     	if (tmpSt.getId() == appointment.getStatus())
                    	{
                    		resultActions.add(mcAction);
                    	}
                    }
                    appointment.setListWorkflowActions( resultActions );
                }*/
                for ( Appointment appointment : delegatePaginator.getPageItems(  ) )
                {
                    appointment.setListWorkflowActions( WorkflowService.getInstance(  )
                                                                       .getActions( appointment.getIdAppointment(  ),
                            Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow(  ), getUser(  ) ) );
                }
            }
            // We add the list of admin users to filter appointments by admin users.
            Collection<AdminUser> listAdminUser = AdminUserHome.findUserList(  );
            ReferenceList refListAdmins = new ReferenceList(  );
            Map<Integer, String> mapAdminUsers = new HashMap<Integer, String>(  );
            refListAdmins.addItem( StringUtils.EMPTY, StringUtils.EMPTY );
            refListAdmins.addItem( CONSTANT_ZERO,
                I18nService.getLocalizedString( MESSAGE_APPOINTMENT_WITH_NO_ADMIN_USER, getLocale(  ) ) );

            for ( AdminUser adminUser : listAdminUser )
            {
                refListAdmins.addItem( adminUser.getUserId(  ),
                    adminUser.getFirstName(  ) + CONSTANT_SPACE + adminUser.getLastName(  ) );
                mapAdminUsers.put( adminUser.getUserId(  ),
                    adminUser.getFirstName(  ) + CONSTANT_SPACE + adminUser.getLastName(  ) );
            }

            model.put( MARK_LIST_ADMIN_USERS, refListAdmins );

            AdminUser user = getUser(  );

            model.put( MARK_APPOINTMENT_LIST, delegatePaginator.getPageItems(  ) );
            model.put( MARK_SLOT, slot );
            model.put( MARK_DAY, day );
            model.put( MARK_FILTER, filter );
            model.put( MARK_REF_LIST_STATUS, refListStatus );
            model.put( MARK_REF_LIST_EXPORT, refListExports );
            model.put( MARK_RIGHT_CREATE,
                RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, user ) );
            model.put( MARK_RIGHT_MODIFY,
                RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT, user ) );
            model.put( MARK_RIGHT_DELETE,
                RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, user ) );
            model.put( MARK_RIGHT_VIEW,
                RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, user ) );
            model.put( MARK_RIGHT_CHANGE_STATUS,
                RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_STATUS, user ) );

            return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS, TEMPLATE_MANAGE_APPOINTMENTS, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

	/**
	 * @param strCheckDate
	 * @param filter
	 */
	private  static AppointmentFilter dateFiltered(AppointmentFilter filter) {
		//Check filter from Date. Be careful if a slot is enabled
			if (filter.getIdSlot()<= 0)
			{
			    filter.setDateAppointmentMin( new Date ( System.currentTimeMillis() ) );
			    filter.setDateAppointmentMax( new Date ( System.currentTimeMillis() ) );
			    if ( String.valueOf(STATUS_CODE_ONE).equals(filter.getStatusFilter()) )
			    	filter.setDateAppointmentMax( null );
			    	if ( String.valueOf(STATUS_CODE_TWO).equals(filter.getStatusFilter()) )
			    {
			    	filter.setDateAppointmentMin( null );
			    	filter.setDateAppointmentMax( null );
			    }
			}
		    return filter;
	}

    /**
     * Write Title Day MM/DD/YYY locale and Hours
     * @param nIdSlot
     * @return
     */
    private String getTitleComment ( int nIdSlot )
    {
    	String strComment = null;
    	AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );
    	if (slot != null )
    	{
			AppointmentDay objDay  = AppointmentDayHome.findByPrimaryKey( slot.getIdDay(  ) );
			strComment = DateUtil.getDateString( objDay.getDate() , getLocale() );
			strComment += " " + String.format("%02d",slot.getStartingHour( )) +":"  + String.format("%-2d",slot.getStartingMinute( )).replace(' ', '0');
			strComment += " ~ " +  String.format("%02d",slot.getEndingHour( ))   +":"  + String.format("%-2d",slot.getEndingMinute( )).replace(' ', '0');
    	}
    	return strComment;
    }
	
	/**
     * Returns the form to create an appointment
     * @param request The HTTP request
     * @return the HTML code of the appointment form
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    @View( VIEW_CREATE_APPOINTMENT )
    public String getCreateAppointment( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
        
        int nIdForm;
        clearUploadFilesIfNeeded( request.getSession(  ) );

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                        AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, getUser(  ) ) )
            {
                throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT );
            }

            nIdForm = Integer.parseInt( strIdForm );
        }
        else
        {
            String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

            if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
            {
                int nIdAppointment = Integer.parseInt( strIdAppointment );
                Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
                AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
                nIdForm = slot.getIdForm(  );

                if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( nIdForm ),
                            AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT, getUser(  ) ) )
                {
                    throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT );
                }
            }
            else
            {
                return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
            }
        }

        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );
        String strSlot = null;
        String strDayComment = null;
        int nMyWeek = 0;
        
        if ( ( form == null ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
        	int nIdSlot = Integer.parseInt( strIdSlot );
        	AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );
        	if ( slot != null )
        	{
        		strSlot = String.valueOf( slot.getIdSlot() );
        		strDayComment = getTitleComment ( slot.getIdSlot( ) );
        		AppointmentDay day =  AppointmentDayHome.findByPrimaryKey( slot.getIdDay(  ) );
        		nMyWeek = computeWeek(day.getDate());
        	}
        }

        Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );

        if ( appointment != null )
        {
            AppointmentDTO appointmentDTO = new AppointmentDTO(  );
            appointmentDTO.setEmail( appointment.getEmail(  ) );
            appointmentDTO.setFirstName( appointment.getFirstName(  ) );
            appointmentDTO.setLastName( appointment.getLastName(  ) );
            appointmentDTO.setIdAppointment( appointment.getIdAppointment(  ) );

            Map<Integer, List<Response>> mapResponsesByIdEntry = appointmentDTO.getMapResponsesByIdEntry(  );

            for ( Response response : appointment.getListResponse(  ) )
            {
                List<Response> listResponse = mapResponsesByIdEntry.get( response.getEntry(  ).getIdEntry(  ) );

                if ( listResponse == null )
                {
                    listResponse = new ArrayList<Response>(  );
                    mapResponsesByIdEntry.put( response.getEntry(  ).getIdEntry(  ), listResponse );
                }

                listResponse.add( response );
            }

            _appointmentFormService.saveAppointmentInSession( request.getSession(  ), appointmentDTO );
        }

        AppointmentFormMessages formMessages = AppointmentFormMessagesHome.findByPrimaryKey( nIdForm );
        model.put( MARK_FORM_HTML,
            _appointmentFormService.getHtmlForm( nMyWeek, strDayComment, strSlot, form, formMessages, getLocale(  ), false, request ) );

        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession(  )
                                                                                      .getAttribute( SESSION_APPOINTMENT_FORM_ERRORS );

        if ( listErrors != null )
        {
            model.put( MARK_FORM_ERRORS, listErrors );
            request.getSession(  ).removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        }

        _appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );
        _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT, TEMPLATE_CREATE_APPOINTMENT, model );
    }

    /**
     * Get the page to modify an appointment
     * @param request The request
     * @return The HTML content to display or the next URL to redirect to
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    @View( VIEW_MODIFY_APPOINTMENT )
    public String getModifyAppointment( HttpServletRequest request )
        throws AccessDeniedException
    {
        clearUploadFilesIfNeeded( request.getSession(  ) );

        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAppointment = Integer.parseInt( strIdAppointment );

            Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
            List<Integer> listIdResponse = AppointmentHome.findListIdResponse( appointment.getIdAppointment(  ) );
            List<Response> listResponses = new ArrayList<Response>( listIdResponse.size(  ) );

            for ( int nIdResponse : listIdResponse )
            {
                Response response = ResponseHome.findByPrimaryKey( nIdResponse );

                if ( response.getField(  ) != null )
                {
                    response.setField( FieldHome.findByPrimaryKey( response.getField(  ).getIdField(  ) ) );
                }

                if ( response.getFile(  ) != null )
                {
                    fr.paris.lutece.portal.business.file.File file = FileHome.findByPrimaryKey( response.getFile(  )
                                                                                                        .getIdFile(  ) );
                    PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile(  )
                                                                                       .getIdPhysicalFile(  ) );
                    file.setPhysicalFile( physicalFile );
                    response.setFile( file );

                    String strIdEntry = Integer.toString( response.getEntry(  ).getIdEntry(  ) );

                    FileItem fileItem = new GenAttFileItem( physicalFile.getValue(  ), file.getTitle(  ),
                            IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry, response.getIdResponse(  ) );
                    AppointmentAsynchronousUploadHandler.getHandler(  )
                                                        .addFileItemToUploadedFilesList( fileItem,
                        IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry, request );
                }

                listResponses.add( response );
            }

            appointment.setListResponse( listResponses );

            _appointmentFormService.saveValidatedAppointmentForm( request.getSession(  ), appointment );

            return getCreateAppointment( request );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }
    
    /**
     * Come from calendar pick date ?
     * @param strIdSlot
     * @return
     */
    private static boolean comeFromCalendarAppointment( String strIdSlot )
    {
    	 boolean bReturn = false;
    	 if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
         {
            int nIdSlot = Integer.parseInt( strIdSlot );
         	AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );
         	bReturn = slot != null;
         }
    	 return bReturn;
    }

    /*WORKFLOW_FUTURE 
    private State getStatus( int nIdForm)
    {
    	State retour = null;
    	AppointmentForm tmpForm = AppointmentFormHome.findByPrimaryKey( nIdForm );
    	if ( tmpForm != null )
    	{
    		Workflow wFlow = _stateServiceWorkFlow.findByPrimaryKey( tmpForm.getIdWorkflow() );
    		if (wFlow != null)
    		{
    			retour = _stateService.getInitialState( wFlow.getId());
    		}
    	}
    	return retour;
    }
 */   
    /**
     * Do validate data entered by a user to fill a form
     * @param request The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    @Action( ACTION_DO_VALIDATE_FORM )
    public String doValidateForm( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
        
        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
/*WORKFLOW_FUTURE            State myState = getStatus( nIdForm );*/
            EntryFilter filter = new EntryFilter(  );
            filter.setIdResource( nIdForm );
            filter.setResourceType( AppointmentForm.RESOURCE_TYPE );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
            filter.setIdIsComment( EntryFilter.FILTER_FALSE );

            List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

            _appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>(  );
            Locale locale = request.getLocale(  );

            AppointmentDTO appointment;
            String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

            if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
            {
                appointment = new AppointmentDTO( AppointmentHome.findByPrimaryKey( Integer.parseInt( strIdAppointment ) ) );
            }
            else
            {
                appointment = new AppointmentDTO(  );
/*WORKFLOW_FUTURE                 if ( myState != null )
                {
                	appointment.setStatus( myState.getId() );
                }
*/
                if ( SecurityService.isAuthenticationEnable(  ) )
                {
                    LuteceUser luteceUser = SecurityService.getInstance(  ).getRegisteredUser( request );

                    if ( luteceUser != null )
                    {
                        appointment.setIdUser( luteceUser.getName(  ) );
                    }
                }
            }

            appointment.setEmail( request.getParameter( PARAMETER_EMAIL ) );
            appointment.setFirstName( request.getParameter( PARAMETER_FIRST_NAME ) );
            appointment.setLastName( request.getParameter( PARAMETER_LAST_NAME ) );
            appointment.setAppointmentForm(form);

            // We save the appointment in session. The appointment object will contain responses of the user to the form
            _appointmentFormService.saveAppointmentInSession( request.getSession(  ), appointment );

            Set<ConstraintViolation<AppointmentDTO>> listErrors = BeanValidationUtil.validate( appointment );
/*WORKFLOW_FUTURE             if ( myState == null )
            {
            	GenericAttributeError genAttError = new GenericAttributeError(  );
            	genAttError.setErrorMessage( I18nService.getLocalizedString( INFO_APPOINTMENT_STATE_ERROR,
	                        request.getLocale(  ) ));
                listFormErrors.add( genAttError );
            }
            */
            if ( !listErrors.isEmpty(  ) )
            {
                for ( ConstraintViolation<AppointmentDTO> constraintViolation : listErrors )
                {
                    GenericAttributeError genAttError = new GenericAttributeError(  );
                    genAttError.setErrorMessage( constraintViolation.getMessage(  ) );
                    listFormErrors.add( genAttError );
                }
            }
            else
            {
            	if (!StringUtils.isEmpty( appointment.getEmail ( ) ))
                {
            		if (StringUtils.isNumeric( strIdSlot ) )
            		{
            			AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( Integer.valueOf( strIdSlot ) );
            			AppointmentDay day =  AppointmentDayHome.findByPrimaryKey( slot.getIdDay() );
            			
            			List<Date> unvailableSlots = AppointmentFormHome.getLimitedByMail(day.getDate(), null, nIdForm, appointment.getEmail() );
            			boolean bErr = false;
            			for (Date myDate: unvailableSlots)
            			{
            				if ( getNumbersDay( day.getDate(), myDate)  == 0)
            				{
             					bErr = true;
            				}
            			}
            			if ( bErr )
            			{
            				 GenericAttributeError genAttError = new GenericAttributeError(  );
            				 genAttError.setErrorMessage( I18nService.getLocalizedString( INFO_APPOINTMENT_EMAIL_ERROR,
            	                        request.getLocale(  ) ));
            				 listFormErrors.add( genAttError );
            			}
            		}
                }
            }

            for ( Entry entry : listEntryFirstLevel )
            {
                listFormErrors.addAll( _appointmentFormService.getResponseEntry( request, entry.getIdEntry(  ), locale,
                        appointment ) );
            }

            // If there is some errors, we redirect the user to the form page
            if ( listFormErrors.size(  ) > 0 )
            {
                request.getSession(  ).setAttribute( SESSION_APPOINTMENT_FORM_ERRORS, listFormErrors );
                if ( comeFromCalendarAppointment( strIdSlot ) )
                {
	               return redirect( request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_SLOT, Integer.parseInt( ( strIdSlot ) ));
                }
                else
                {
                	return redirect( request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, nIdForm );
                }
            }

            _appointmentFormService.convertMapResponseToList( appointment );
            _appointmentFormService.saveValidatedAppointmentForm( request.getSession(  ), appointment );

            if ( appointment.getIdAppointment(  ) > 0 )
            {
                AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );

                if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( slot.getIdForm(  ) ),
                            AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT, getUser(  ) ) )
                {
                    throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT );
                }

                AppointmentHome.update( appointment );

                List<Integer> listIdResponse = AppointmentHome.findListIdResponse( appointment.getIdAppointment(  ) );

                for ( int nIdResponse : listIdResponse )
                {
                    ResponseHome.remove( nIdResponse );
                }

                AppointmentHome.removeAppointmentResponse( appointment.getIdAppointment(  ) );

                for ( Response response : appointment.getListResponse(  ) )
                {
                    ResponseHome.create( response );
                    AppointmentHome.insertAppointmentResponse( appointment.getIdAppointment(  ),
                        response.getIdResponse(  ) );
                }

                if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_SAVE_AND_BACK ) ) )
                {
                    addInfo( INFO_APPOINTMENT_UPDATED, getLocale(  ) );

                    return redirect( request, getUrlManageAppointment( request, nIdForm ) );
                }
            }
            if ( comeFromCalendarAppointment( strIdSlot ) )
            {
            	return redirect( request, VIEW_DISPLAY_RECAP_APPOINTMENT, PARAMETER_ID_SLOT, Integer.parseInt( strIdSlot ) );
            }
            else
           {
            	return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, nIdForm );
            }
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the page with the calendar with opened and closed days for an
     * appointment form
     * @param request The request
     * @return The XPage to display
     */
    @View( VIEW_GET_APPOINTMENT_CALENDAR )
    public String getAppointmentCalendar( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            if ( _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) ) == null )
            {
                return redirect( request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, nIdForm );
            }

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );
            AppointmentFormMessages formMessages = AppointmentFormMessagesHome.findByPrimaryKey( nIdForm );

            if ( StringUtils.isNotBlank( formMessages.getCalendarDescription(  ) ) )
            {
                addInfo( formMessages.getCalendarDescription(  ) );
            }

            Map<String, Object> model = getModel(  );
            int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 ) + form.getNbWeeksToDisplay();
            String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );
            int nNbWeek = 0;

            if ( StringUtils.isNotEmpty( strNbWeek ) && StringUtils.isNumeric( strNbWeek ) )
            {
                nNbWeek = Integer.parseInt( strNbWeek );

                if ( nNbWeek > ( form.getNbWeeksToDisplay(  ) - 1 ) )
                {
                    nNbWeek = form.getNbWeeksToDisplay(  ) - 1;
                }
            }

            List<AppointmentDay> listDays = AppointmentService.getService(  )
                                                              .getDayListForCalendar( form, new MutableInt( nNbWeek ),
                    false, false );

            if ( listDays != null )
            {
                List<String> listTimeBegin = new ArrayList<String>(  );
                int nMinAppointmentDuration = AppointmentService.getService(  )
                                                                .getListTimeBegin( listDays, form, listTimeBegin );
                

                Calendar calendarEnd = getCalendarTime (form.getDateEndValidity(), form.getClosingHour(), form.getClosingMinutes() );
                Calendar calendarStart = getCalendarTime (form.getDateStartValidity(), form.getOpeningHour(), form.getOpeningMinutes() );

                listDays = computeUnavailableDays(form.getIdForm(), listDays, false);
/*                listDays = unvalidAppointmentsbeforeNow(form.getMinDaysBeforeAppointment() , listDays, calendarStart,calendarEnd);*/
                Appointment myApmt =  _appointmentFormService.getValidatedAppointmentFromSession( request.getSession ( ) );
                if ( myApmt!= null && !StringUtils.isEmpty( myApmt.getEmail ( ) ))
                {
                	listDays = computeIntervalsDays(form.getIdForm(), listDays, myApmt.getEmail());
                }
                model.put( MARK_LIST_DAYS, listDays );
                model.put( MARK_LIST_TIME_BEGIN, listTimeBegin );
                model.put( MARK_MIN_DURATION_APPOINTMENT, nMinAppointmentDuration );
            }

            model.put( MARK_FORM, form );
            model.put( MARK_FORM_MESSAGES, formMessages );
            model.put( PARAMETER_MAX_WEEK, nNbWeeksToCreate-1 );
            model.put( PARAMETER_NB_WEEK, nNbWeek );
            model.put( PARAMETER_LIM_DATES, getLimitedDate( nNbWeeksToCreate) );
            model.put( MARK_LIST_DAYS_OF_WEEK, MESSAGE_LIST_DAYS_OF_WEEK );

            return getPage( PROPERTY_PAGE_TITLE_APPOINTMENT_CALENDAR, TEMPLATE_APPOINTMENT_FORM_CALENDAR, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

	/**
	 * @param form
	 * @param listDays
	 * @param myApmt
	 */
	private List<AppointmentDay> computeIntervalsDays(int nIdForm,
			List<AppointmentDay> listDays, String strMail) {
		Date[] tmpLimit =  {listDays.get(0).getDate(),listDays.get(listDays.size ()-1).getDate()};
		List<Date> unvailableSlots = AppointmentFormHome.getLimitedByMail(null, tmpLimit, nIdForm, strMail );
		for (int i = 0; i < listDays.size (); i++)
		{
			for (Date tmpDate: unvailableSlots)
			{
				if (getNumbersDay(listDays.get(i).getDate(), tmpDate) == 0)
				{
					listDays.get(i).setListSlots(eraseSlots(listDays.get(i).getListSlots ( )));
				}
			}
			
		}
		return listDays;
	}

    /**
  	 * Compute Days beetween date
  	 * @param nStart
  	 * @param nEnd
  	 * @return
  	 */
  	private static int getNumbersDay(Date nStart, Date nEnd)
  	{
  		long timeDiff =  nEnd.getTime() - nStart.getTime();
  		timeDiff = timeDiff / 1000 /(24 * 60 * 60);
  		return Integer.valueOf( String.valueOf(timeDiff)  ) ;
  	}	
	/**
	 * Erase slots
	 * @param objSlots
	 * @return
	 */
	private static List<AppointmentSlot> eraseSlots ( List<AppointmentSlot> objSlots)
	{
		List<AppointmentSlot> returnSlots = objSlots;
		if (objSlots!=null)
		{
			returnSlots = new ArrayList<AppointmentSlot>(); 
			for (AppointmentSlot mySlot: objSlots)
			{
				mySlot.setIsEnabled ( false );
				returnSlots.add( mySlot );
			}
		}
		return returnSlots;
	}
	
	
	/**
	 * Unvalid appointsments before Now
	 * Is is not necessary to check appointment slots if date is before now
	 * Add the time necessary beetwen an appointment
	 * @param form
	 * @param listDays
	 */
	private static List<AppointmentDay> unvalidAppointmentsbeforeNow(int iDaysBeforeAppointment,
			List<AppointmentDay> listDays, Calendar objStart, Calendar objEnd) {

		Calendar objNow = new GregorianCalendar( Locale.FRENCH );
		
		objNow.add(Calendar.HOUR_OF_DAY, iDaysBeforeAppointment);
		
			
		for (int i = 0; i < listDays.size() ; i ++)
		{
			if (listDays.get( i ).getIsOpen())
			{
				if (listDays.get( i ).getListSlots() != null )
				{
					for ( int index = 0; index < listDays.get( i ).getListSlots().size() ; index++)
					{
						Calendar tmpCal = new GregorianCalendar( Locale.FRENCH );
						Calendar objNowClose = new GregorianCalendar( Locale.FRENCH );
						
						tmpCal.setTime( listDays.get( i ).getDate() );					
						tmpCal.set(Calendar.HOUR_OF_DAY, listDays.get( i ).getListSlots().get( index ).getStartingHour() );
						tmpCal.set(Calendar.MINUTE, listDays.get( i ).getListSlots().get( index ).getStartingMinute() );
						
						objNowClose.setTime( listDays.get( i ).getDate() );
						objNowClose.set(Calendar.HOUR_OF_DAY, objEnd.get(Calendar.HOUR_OF_DAY));
						objNowClose.set(Calendar.MINUTE, objEnd.get(Calendar.MINUTE));

						if ( ( objNow.after( tmpCal ) || tmpCal.after( objNowClose ) ) && listDays.get( i ).getListSlots().get( index ).getNbFreePlaces() > 0 ) //Already an appointments
						{
							listDays.get( i ).getListSlots().get( index ).setIsEnabled( false );
						}
					}                		
				}
			}
		}
		return listDays;
	}

	
	 /**
     * Manages the removal form of a appointment whose identifier is in the HTTP
     * request
     * @param request The HTTP request
     * @return the HTML code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_MASS_APPOINTMENT )
    public String getConfirmRemoveMassAppointment( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_MASSAPPOINTMENT ) );
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_MASSAPPOINTMENT,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }
	
    /**
     * Manages the removal form of a appointment whose identifier is in the HTTP
     * request
     * @param request The HTTP request
     * @return the HTML code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_APPOINTMENT )
    public String getConfirmRemoveAppointment( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPOINTMENT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_APPOINTMENT ) );
        url.addParameter( PARAMETER_ID_APPOINTMENT, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_APPOINTMENT,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }
    /**
     * Handles the removal form of a appointment
     * @param request The HTTP request
     * @return the JSP URL to display the form to manage appointments
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    @Action( ACTION_REMOVE_MASSAPPOINTMENT )
    public String doRemoveMassAppointment( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strTableaudelete[] = (String []) request.getSession().getAttribute(PARAMETER_ID_APPOINTMENT_DELETE);
        request.getSession().removeAttribute(PARAMETER_ID_APPOINTMENT_DELETE);
        if (strTableaudelete != null)
        {
        	boolean bIsError = false;
        	for (String strTmp : strTableaudelete)
        	{
        		 if ( StringUtils.isEmpty( strTmp ) || !StringUtils.isNumeric( strTmp ) )
        			 bIsError = true;
        	}
        	if (!bIsError)
        	{
        		Integer idForm = null;
        		for (String strTmp : strTableaudelete)
            	{
        			idForm = doRemoveSingleAppointment ( strTmp, getUser ( ));
            	}
        		addInfo( INFO_APPOINTMENT_MASSREMOVED, getLocale(  ) );
        		
        		return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, idForm.intValue() );
        	}
        }
        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }
    
    /**
     * Handles the removal form of a appointment
     * @param request The HTTP request
     * @return the JSP URL to display the form to manage appointments
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    private static Integer doRemoveSingleAppointment ( String strIdAppointment, AdminUser getUser )
    throws AccessDeniedException
    {
    	Integer iReturn = null;
    	if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
    		 int nId = Integer.parseInt( strIdAppointment );
             Appointment appointment = AppointmentHome.findByPrimaryKey( nId );
             if ( appointment != null )
             {
                 AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
                 if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( slot.getIdForm(  ) ),
                         AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, getUser ) )
             {
                 throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT );
             }

             if ( WorkflowService.getInstance(  ).isAvailable(  ) )
             {
                 WorkflowService.getInstance(  ).doRemoveWorkFlowResource( nId, Appointment.APPOINTMENT_RESOURCE_TYPE );
             }
             iReturn = slot.getIdForm();
             AppointmentHome.remove( nId );
             }
        }
    	return iReturn;
    }
    /**
     * Handles the removal form of a appointment
     * @param request The HTTP request
     * @return the JSP URL to display the form to manage appointments
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    @Action( ACTION_REMOVE_APPOINTMENT )
    public String doRemoveAppointment( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );
        Integer idForm = doRemoveSingleAppointment ( strIdAppointment, getUser ( ));
        if ( idForm != null )
        {
         	addInfo( INFO_APPOINTMENT_REMOVED, getLocale(  ) );
        	return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, idForm.intValue() );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Display the recap before validating an appointment
     * @param request The request
     * @return The HTML content to display or the next URL to redirect to
     */
    @View( VIEW_DISPLAY_RECAP_APPOINTMENT )
    public String displayRecapAppointment( HttpServletRequest request )
    {
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
            Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );

            int nIdSlot = Integer.parseInt( strIdSlot );
            AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );

            if ( appointment != null )
            {
                appointment.setIdSlot( nIdSlot );

                Map<String, Object> model = new HashMap<String, Object>(  );
                model.put( MARK_APPOINTMENT, appointment );
                model.put( MARK_SLOT, appointmentSlot );

                AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) );
                AppointmentDay day = AppointmentDayHome.findByPrimaryKey( appointmentSlot.getIdDay(  ) );
                appointment.setDateAppointment( (Date) day.getDate(  ).clone(  ) );
                model.put( MARK_DAY, day );
                model.put( MARK_FORM, form );
                model.put( MARK_FORM_MESSAGES,
                    AppointmentFormMessagesHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) ) );
                fillCommons( model );

                Locale locale = getLocale(  );

                List<ResponseRecapDTO> listResponseRecapDTO = new ArrayList<ResponseRecapDTO>( appointment.getListResponse(  )
                                                                                                          .size(  ) );

                for ( Response response : appointment.getListResponse(  ) )
                {
                    IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry(  ) );
                    listResponseRecapDTO.add( new ResponseRecapDTO( response,
                            entryTypeService.getResponseValueForRecap( response.getEntry(  ), request, response, locale ) ) );
                }

                model.put( MARK_LIST_RESPONSE_RECAP_DTO, listResponseRecapDTO );

                return getPage( PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT, TEMPLATE_APPOINTMENT_FORM_RECAP, model );
            }

            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointmentSlot.getIdForm(  ) );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }
    /**
     * Do save an appointment into the database if it is valid
     * @param request The request
     * @return The XPage to display
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    @Action( ACTION_DO_MAKE_APPOINTMENT )
    public String doMakeAppointment( HttpServletRequest request )
        throws AccessDeniedException
    {
        Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );
        AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_BACK ) ) )
        {
            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointmentSlot.getIdForm(  ) );
        }

        boolean bCreation = appointment.getIdAppointment(  ) == 0;

        if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE,
                    Integer.toString( appointmentSlot.getIdForm(  ) ),
                    bCreation ? AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT
                                  : AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT, getUser(  ) ) )
        {
            throw new AccessDeniedException( bCreation ? AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT
                                                       : AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT );
        }
        //Careful saving parameters from request to get URL Base for task.
        HttpServletRequest tmpRequest =  LocalVariables.getRequest();
        LocalVariables.setLocal( LocalVariables.getConfig(), request, LocalVariables.getResponse() );
        boolean bResponse = _appointmentFormService.doMakeAppointment( appointment, form, true );
        LocalVariables.setLocal( LocalVariables.getConfig(), tmpRequest,  LocalVariables.getResponse() );
        if ( bResponse )
        {
            addInfo( bCreation ? INFO_APPOINTMENT_CREATED : INFO_APPOINTMENT_UPDATED, getLocale(  ) );

            if ( !bCreation )
            {
                List<String> listMessages = AppointmentListenerManager.notifyListenersAppointmentDateChanged( appointment.getIdAppointment(  ),
                        appointment.getIdSlot(  ), getLocale(  ) );

                for ( String strMessage : listMessages )
                {
                    addInfo( strMessage );
                }
            }
        }
        else
        {
            addError( ERROR_MESSAGE_SLOT_FULL, getLocale(  ) );

            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointmentSlot.getIdForm(  ) );
        }

        _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );
        AppointmentAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );
        return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, appointmentSlot.getIdForm(  ) , PARAMETER_NB_WEEK, computeWeek( appointment.getDateAppointment() )  );

//        return redirect( request, getUrlManageAppointment( request, form.getIdForm(  ) ) );
    }

    /**
     * View details of an appointment
     * @param request The request
     * @return The HTML content to display
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    @View( VIEW_VIEW_APPOINTMENT )
    public String getViewAppointment( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        if ( StringUtils.isEmpty( strIdAppointment ) || !StringUtils.isNumeric( strIdAppointment ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nId = Integer.parseInt( strIdAppointment );
        Appointment appointment = AppointmentHome.findByPrimaryKey( nId );
        AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( slot.getIdForm(  ) );

        if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( slot.getIdForm(  ) ),
                    AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser(  ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT );
        }

        List<Response> listResponse = AppointmentHome.findListResponse( nId );

        for ( Response response : listResponse )
        {
            if ( response.getFile(  ) != null )
            {
                response.setFile( FileHome.findByPrimaryKey( response.getFile(  ).getIdFile(  ) ) );
            }
        }

        appointment.setListResponse( listResponse );

        Map<String, Object> model = getModel(  );
        model.put( MARK_APPOINTMENT, appointment );

        model.put( MARK_SLOT, slot );
        model.put( MARK_FORM, form );
        model.put( MARK_FORM_MESSAGES, AppointmentFormMessagesHome.findByPrimaryKey( slot.getIdForm(  ) ) );

        if ( ( form.getIdWorkflow(  ) > 0 ) && WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            model.put( MARK_RESOURCE_HISTORY,
                WorkflowService.getInstance(  )
                               .getDisplayDocumentHistory( nId, Appointment.APPOINTMENT_RESOURCE_TYPE,
                    form.getIdWorkflow(  ), request, getLocale(  ) ) );
        }

        if ( appointment.getIdAdminUser(  ) > 0 )
        {
            model.put( MARK_ADMIN_USER, AdminUserHome.findByPrimaryKey( appointment.getIdAdminUser(  ) ) );
        }

        Locale locale = getLocale(  );

        List<ResponseRecapDTO> listResponseRecapDTO = new ArrayList<ResponseRecapDTO>( appointment.getListResponse(  )
                                                                                                  .size(  ) );

        for ( Response response : appointment.getListResponse(  ) )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry(  ) );
            listResponseRecapDTO.add( new ResponseRecapDTO( response,
                    entryTypeService.getResponseValueForRecap( response.getEntry(  ), request, response, locale ) ) );
        }

        model.put( MARK_LIST_RESPONSE_RECAP_DTO, listResponseRecapDTO );

        model.put( MARK_ADDON,
            AppointmentAddOnManager.getAppointmentAddOn( appointment.getIdAppointment(  ), getLocale(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT, TEMPLATE_VIEW_APPOINTMENT, model );
    }

    /**
     * Do download a file from an appointment response
     * @param request The request
     * @param httpResponse The response
     * @return nothing.
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    public String getDownloadFile( HttpServletRequest request, HttpServletResponse httpResponse )
        throws AccessDeniedException
    {
        String strIdResponse = request.getParameter( PARAMETER_ID_RESPONSE );

        if ( StringUtils.isEmpty( strIdResponse ) || !StringUtils.isNumeric( strIdResponse ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdResponse = Integer.parseInt( strIdResponse );

        int nIdAppointment = AppointmentHome.findIdAppointmentByIdResponse( nIdResponse );
        Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
        AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );

        if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( slot.getIdForm(  ) ),
                    AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser(  ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT );
        }

        Response response = ResponseHome.findByPrimaryKey( nIdResponse );
        File file = FileHome.findByPrimaryKey( response.getFile(  ).getIdFile(  ) );
        PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile(  ).getIdPhysicalFile(  ) );

        httpResponse.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getTitle(  ) + "\";" );
        httpResponse.setHeader( "Content-type", file.getMimeType(  ) );
        httpResponse.addHeader( "Content-Encoding", "UTF-8" );
        httpResponse.addHeader( "Pragma", "public" );
        httpResponse.addHeader( "Expires", "0" );
        httpResponse.addHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );

        try
        {
            OutputStream os = httpResponse.getOutputStream(  );
            os.write( physicalFile.getValue(  ) );
            // We do not close the output stream in finnaly clause because it is the response stream,
            // and an error message needs to be displayed if an exception occurs
            os.close(  );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getStackTrace(  ), e );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Do change the status of an appointment
     * @param request The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
     */
    @Action( ACTION_DO_CHANGE_APPOINTMENT_STATUS )
    public String doChangeAppointmentStatus( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAppointment = Integer.parseInt( strIdAppointment );
            String strNewStatus = request.getParameter( PARAMETER_NEW_STATUS );
            int nNewStatus = AppointmentService.getService(  ).parseInt( strNewStatus );
            Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );

            if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( slot.getIdForm(  ) ),
                        AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_STATUS, getUser(  ) ) )
            {
                throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_STATUS );
            }

            // We check that the status has changed to avoid doing unnecessary updates.
            // Also, it is not permitted to set the status of an appointment to not validated.
            if ( ( appointment.getStatus(  ) != nNewStatus ) && ( nNewStatus != Appointment.Status.STATUS_NOT_VALIDATED.getValeur() ) )
            {
                appointment.setStatus( nNewStatus );
                AppointmentHome.update( appointment );
            }

            return redirect( request, getUrlManageAppointment( request, slot.getIdForm(  ) ) );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the workflow action form before processing the action. If the action
     * does not need to display any form, then redirect the user to the workflow
     * action processing page.
     * @param request The request
     * @return The HTML content to display, or the next URL to redirect the user
     *         to
     */
    @View( VIEW_WORKFLOW_ACTION_FORM )
    public String getWorkflowActionForm( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdAppointment = Integer.parseInt( strIdAppointment );

            if ( WorkflowService.getInstance(  ).isDisplayTasksForm( nIdAction, getLocale(  ) ) )
            {
                String strHtmlTasksForm = WorkflowService.getInstance(  )
                                                         .getDisplayTasksForm( nIdAppointment,
                        Appointment.APPOINTMENT_RESOURCE_TYPE, nIdAction, request, getLocale(  ) );

                Map<String, Object> model = new HashMap<String, Object>(  );

                model.put( MARK_TASKS_FORM, strHtmlTasksForm );
                model.put( PARAMETER_ID_ACTION, nIdAction );
                model.put( PARAMETER_ID_APPOINTMENT, nIdAppointment );

                return getPage( PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW, TEMPLATE_TASKS_FORM_WORKFLOW, model );
            }

            return doProcessWorkflowAction( request );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Do process a workflow action over an appointment
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_PROCESS_WORKFLOW_ACTION )
    public String doProcessWorkflowAction( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdAppointment = Integer.parseInt( strIdAppointment );

            Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );

            if ( request.getParameter( PARAMETER_BACK ) == null )
            {
                AppointmentForm form = AppointmentFormHome.findByPrimaryKey( slot.getIdForm(  ) );

                if ( WorkflowService.getInstance(  ).isDisplayTasksForm( nIdAction, getLocale(  ) ) )
                {
                    String strError = WorkflowService.getInstance(  )
                                                     .doSaveTasksForm( nIdAppointment,
                            Appointment.APPOINTMENT_RESOURCE_TYPE, nIdAction, form.getIdForm(  ), request, getLocale(  ) );

                    if ( strError != null )
                    {
                        return redirect( request, strError );
                    }
                }
                else
                {
/*WORKFLOW_FUTURE
                	
                	State tmpState = null;
                	Collection<fr.paris.lutece.plugins.workflowcore.business.action.Action>tmpActions = _stateServiceWorkFlow.getActions(nIdAppointment, Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow(  ));
                	for (fr.paris.lutece.plugins.workflowcore.business.action.Action myAction : tmpActions)
                	{
                		if ( myAction.getId() == nIdAction )
                		{
                			if ( myAction.getStateBefore().getId() == appointment.getStatus() )
                			{
                				tmpState = myAction.getStateAfter();
                			}
                			
                		}
                		
                	}
 */
                     WorkflowService.getInstance(  )
                                   .doProcessAction( nIdAppointment, Appointment.APPOINTMENT_RESOURCE_TYPE, nIdAction,
                        form.getIdForm(  ), request, getLocale(  ), false );
/*WORKFLOW_FUTURE                  if ( tmpState != null && tmpState.getId()!= appointment.getStatus() )
                    {
                    	appointment.setStatus( tmpState.getId() );
                    	AppointmentHome.update( appointment );
                    }*/
                }

                Map<String, String> mapParams = new HashMap<String, String>(  );
                mapParams.put( PARAMETER_ID_FORM, Integer.toString( form.getIdForm(  ) ) );
                mapParams.put( MARK_FILTER_FROM_SESSION, Boolean.TRUE.toString(  ) );

                return redirect( request, VIEW_MANAGE_APPOINTMENTS, mapParams );
            }

            return redirect( request, getUrlManageAppointment( request, slot.getIdForm(  ) ) );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get an integer attribute from the session
     * @param session The session
     * @param strSessionKey The session key of the item
     * @return The value of the attribute, or 0 if the key is not associated
     *         with any value
     */
    private int getIntSessionAttribute( HttpSession session, String strSessionKey )
    {
        Integer nAttr = (Integer) session.getAttribute( strSessionKey );

        if ( nAttr != null )
        {
            return nAttr;
        }

        return 0;
    }

    /**
     * Get the URL to manage appointments of a given form
     * @param request The request
     * @param nIdForm The id of the form
     * @return The URL
     */
    public static String getUrlManageAppointment( HttpServletRequest request, int nIdForm )
    {
        return getUrlManageAppointment( request, Integer.toString( nIdForm ) );
    }

    /**
     * Get the URL to manage appointments of a given form
     * @param request The request
     * @param strIdForm The id of the form
     * @return The URL
     */
    public static String getUrlManageAppointment( HttpServletRequest request, String strIdForm )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTS );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTS );
        url.addParameter( PARAMETER_ID_FORM, strIdForm );
        url.addParameter( MARK_FILTER_FROM_SESSION, Boolean.TRUE.toString(  ) );

        return url.getUrl(  );
    }

    /**
     * Get the URL to display the form of a workflow action. If the action has
     * no form, then the user is redirected to the page to execute the workflow
     * action
     * @param request The request
     * @param nIdAppointment The id of the appointment
     * @param nIdAction The id of the workflow action
     * @return The URL
     */
    public static String getUrlExecuteWorkflowAction( HttpServletRequest request, int nIdAppointment, int nIdAction )
    {
        return getUrlExecuteWorkflowAction( request, Integer.toString( nIdAppointment ), Integer.toString( nIdAction ) );
    }

    /**
     * Get the URL to display the form of a workflow action. If the action has
     * no form, then the user is redirected to the page to execute the workflow
     * action
     * @param request The request
     * @param strIdAppointment The id of the appointment
     * @param strIdAction The id of the workflow action
     * @return The URL
     */
    public static String getUrlExecuteWorkflowAction( HttpServletRequest request, String strIdAppointment,
        String strIdAction )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTS );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_WORKFLOW_ACTION_FORM );
        url.addParameter( PARAMETER_ID_APPOINTMENT, strIdAppointment );
        url.addParameter( PARAMETER_ID_ACTION, strIdAction );

        return url.getUrl(  );
    }

    /**
     * Clear uploaded files if needed.
     * @param session The session of the current user
     */
    private void clearUploadFilesIfNeeded( HttpSession session )
    {
        // If we do not reload an appointment, we clear uploaded files.
        if ( ( _appointmentFormService.getAppointmentFromSession( session ) == null ) &&
                ( _appointmentFormService.getValidatedAppointmentFromSession( session ) == null ) )
        {
            AppointmentAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( session.getId(  ) );
        }
    }
}
