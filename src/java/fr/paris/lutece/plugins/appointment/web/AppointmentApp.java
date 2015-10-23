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

import fr.paris.lutece.plugins.appointment.business.Appointment;
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
import fr.paris.lutece.plugins.appointment.business.template.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.template.CalendarTemplateHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormService;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.sql.TransactionManager;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.lang.time.DateUtils;

import org.bouncycastle.util.Strings;

import org.dozer.converters.DateConverter;

import java.sql.Date;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import javax.validation.ConstraintViolation;


/**
 * This class provides a simple implementation of an XPage
 */
@Controller( xpageName = AppointmentApp.XPAGE_NAME, pageTitleI18nKey = AppointmentApp.MESSAGE_DEFAULT_PAGE_TITLE, pagePathI18nKey = AppointmentApp.MESSAGE_DEFAULT_PATH )
public class AppointmentApp extends MVCApplication
{
    /**
     * Name of the view of the first step of the form
     */
    public static final String VIEW_APPOINTMENT_FORM_FIRST_STEP = "getAppointmentFormFirstStep";

    /**
     * Name of the view of the second step of the form
     */
    public static final String VIEW_APPOINTMENT_FORM_SECOND_STEP = "getAppointmentFormSecondStep";

    /**
     * Default page of XPages of this app
     */
    public static final String MESSAGE_DEFAULT_PATH = "appointment.appointmentApp.defaultPath";

    /**
     * Default page title of XPages of this app
     */
    public static final String MESSAGE_DEFAULT_PAGE_TITLE = "appointment.appointmentApp.defaultTitle";

    /** Infos error WorkFlow */
    private static final String INFO_APPOINTMENT_STATE_ERROR = "appointment.info.appointment.etatinitial";

    /**
     * The name of the XPage
     */
    protected static final String XPAGE_NAME = "appointment";

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 5741361182728887387L;

    // Templates
    private static final String TEMPLATE_APPOINTMENT_FORM_LIST = "/skin/plugins/appointment/appointment_form_list.html";
    private static final String TEMPLATE_APPOINTMENT_FORM = "/skin/plugins/appointment/appointment_form.html";
    private static final String TEMPLATE_APPOINTMENT_FORM_RECAP = "/skin/plugins/appointment/appointment_form_recap.html";
    private static final String TEMPLATE_APPOINTMENT_CREATED = "skin/plugins/appointment/appointment_created.html";
    private static final String TEMPLATE_CANCEL_APPOINTMENT = "skin/plugins/appointment/cancel_appointment.html";
    private static final String TEMPLATE_APPOINTMENT_CANCELED = "skin/plugins/appointment/appointment_canceled.html";
    private static final String TEMPLATE_MY_APPOINTMENTS = "skin/plugins/appointment/my_appointments.html";

    // Views
    private static final String VIEW_APPOINTMENT_FORM_LIST = "getViewFormList";
    private static final String VIEW_DISPLAY_RECAP_APPOINTMENT = "displayRecapAppointment";
    private static final String VIEW_GET_APPOINTMENT_CREATED = "getAppointmentCreated";
    private static final String VIEW_GET_CANCEL_APPOINTMENT = "getCancelAppointment";
    private static final String VIEW_APPOINTMENT_CANCELED = "getAppointmentCanceled";
    private static final String VIEW_GET_MY_APPOINTMENTS = "getMyAppointments";
    private static final String VIEW_GET_VIEW_CANCEL_APPOINTMENT = "getViewCancelAppointment";

    // Actions
    private static final String ACTION_DO_VALIDATE_FORM = "doValidateForm";
    private static final String ACTION_DO_MAKE_APPOINTMENT = "doMakeAppointment";
    private static final String ACTION_DO_CANCEL_APPOINTMENT = "doCancelAppointment";
    private static final String ACTION_DO_SELECT_SLOT = "doSelectSlot";

    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_NB_WEEK = "nb_week";
    private static final String PARAMETER_DIRECTION = "dir";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_EMAIL_CONFIRMATION = "emailConfirm";
    private static final String PARAMETER_FIRST_NAME = "firstname";
    private static final String PARAMETER_LAST_NAME = "lastname";
    private static final String PARAMETER_ID_SLOT = "idSlot";
    private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";
    private static final String PARAMETER_BACK = "back";
    private static final String PARAMETER_REF_APPOINTMENT = "refAppointment";
    private static final String PARAMETER_DATE_APPOINTMENT = "dateAppointment";
    private static final String PARAMETER_FROM_MY_APPOINTMENTS = "fromMyappointments";
    private static final String PARAMETER_REFERER = "referer";

    // Marks
    private static final String MARK_REF_APPOINTMENT = "refAppointment";
    private static final String MARK_DATE_APPOINTMENT = "dateAppointment";
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_FORM_ERRORS = "form_errors";
    private static final String MARK_LIST_DAYS = "listDays";
    private static final String MARK_LIST_AVAILABLE_DAYS = "listAvailableDays";
    private static final String MARK_FORM = "form";
    private static final String MARK_DAY = "day";
    private static final String MARK_FORM_MESSAGES = "formMessages";
    private static final String MARK_LIST_TIME_BEGIN = "list_time_begin";
    private static final String MARK_MIN_DURATION_APPOINTMENT = "min_duration_appointments";
    private static final String MARK_APPOINTMENT = "appointment";
    private static final String MARK_CAPTCHA = "captcha";
    private static final String MARK_SLOT = "slot";
    private static final String MARK_LIST_DAYS_OF_WEEK = "list_days_of_week";
    private static final String MARK_REF = "%%REF%%";
    private static final String MARK_DATE_APP = "%%DATE%%";
    private static final String MARK_TIME_BEGIN = "%%HEURE_DEBUT%%";
    private static final String MARK_TIME_END = "%%HEURE_FIN%%";
    private static final String MARK_LIST_APPOINTMENTS = "list_appointments";
    private static final String MARK_BACK_URL = "backUrl";
    private static final String MARK_STATUS_RESERVED = "status_reserved";
    private static final String MARK_STATUS_UNRESERVED = "status_unreserved";
    private static final String MARK_FROM_URL = "fromUrl";
    private static final String MARK_LIST_RESPONSE_RECAP_DTO = "listResponseRecapDTO";
    private static final String MARK_IS_FORM_FIRST_STEP = "isFormFirstStep";
    private static final String MARK_TITLE = "title";
    private static final String MARK_INFOS = "infos";
    private static final String MARK_ERRORS = "errors";
    private static final String MARK_DATE_LAST_MONDAY = "dateLastMonday";
    private static final String MARK_STATUS = "libelled_status";
    private static final String MARK_CONSTANT_STR_NULL = "";
    private static final String MARK_DATA = "data";
    private static final String MARK_BASE_64 = "base64";
    private static final String MARK_NULL = "NULL";
    private static final String MARK_SEMI_COLON = ";";
    private static final String MARK_COMMA = ",";
    private static final String MARK_COLON = ":";
    private static final String MARK_ICONS = "icons";
    private static final String MARK_ICON_NULL = "NULL";

    // Errors
    private static final String ERROR_MESSAGE_SLOT_FULL = "appointment.message.error.slotFull";
    private static final String ERROR_MESSAGE_CAPTCHA = "portal.admin.message.wrongCaptcha";
    private static final String ERROR_MESSAGE_UNKNOWN_REF = "appointment.message.error.unknownRef";
    private static final String ERROR_MESSAGE_CAN_NOT_CANCEL_APPOINTMENT = "appointment.message.error.canNotCancelAppointment";
    private static final String ERROR_MESSAGE_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
    private static final String ERROR_MESSAGE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
    private static final String ERROR_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";

    // Session keys
    private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";

    // Messages
    private static final String[] MESSAGE_LIST_DAYS_OF_WEEK = AppointmentService.getListDaysOfWeek(  );
    private static final String MESSAGE_CANCEL_APPOINTMENT_PAGE_TITLE = "appointment.cancel_appointment.pageTitle";
    private static final String MESSAGE_MY_APPOINTMENTS_PAGE_TITLE = "appointment.my_appointments.pageTitle";
    private final static StateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );

    // Local variables
    private final AppointmentFormService _appointmentFormService = SpringContextService.getBean( AppointmentFormService.BEAN_NAME );
    private final fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService _stateServiceWorkFlow = SpringContextService.getBean( fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService.BEAN_SERVICE );
    private transient CaptchaSecurityService _captchaSecurityService;
    private transient DateConverter _dateConverter;

    //    private transient DateConverter _dateConverter;

    /**
     * Get the list of appointment form list
     * @param request The request
     * @return The XPage to display
     */
    @View( value = VIEW_APPOINTMENT_FORM_LIST, defaultView = true )
    public XPage getFormList( HttpServletRequest request )
    {
        Locale locale = getLocale( request );
        String strHtmlContent = getFormListHtml( request, _appointmentFormService, null, locale );
        XPage xpage = new XPage(  );
        xpage.setContent( strHtmlContent );
        xpage.setPathLabel( getDefaultPagePath( locale ) );
        xpage.setTitle( getDefaultPageTitle( locale ) );

        return xpage;
    }

    /**
     * Get the page to complete a form
     * @param request The request
     * @return The XPage to display
     */

    //    @View( VIEW_GET_FORM )
    public XPage getViewForm( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            if ( !_appointmentFormService.isFormFirstStep( nIdForm ) &&
                    ( ( _appointmentFormService.getAppointmentFromSession( request.getSession(  ) ) == null ) ||
                    ( _appointmentFormService.getAppointmentFromSession( request.getSession(  ) ).getIdSlot(  ) == 0 ) ) )
            {
                return redirect( request, VIEW_APPOINTMENT_FORM_FIRST_STEP, PARAMETER_ID_FORM, nIdForm );
            }

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            if ( ( form == null ) || !form.getIsActive(  ) )
            {
                return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
            }

            String strHtmlContent = getAppointmentFormHtml( request, form, _appointmentFormService, getModel(  ),
                    getLocale( request ) );

            XPage page = new XPage(  );
            page.setContent( strHtmlContent );
            page.setPathLabel( getDefaultPagePath( getLocale( request ) ) );

            if ( form.getDisplayTitleFo(  ) )
            {
                page.setTitle( form.getTitle(  ) );
            }

            return page;
        }

        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /*WORKFLOW FUTURE
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
     * @throws SiteMessageException
     */
    @Action( ACTION_DO_VALIDATE_FORM )
    public XPage doValidateForm( HttpServletRequest request )
        throws SiteMessageException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            EntryFilter filter = new EntryFilter(  );
            filter.setIdResource( nIdForm );
            filter.setResourceType( AppointmentForm.RESOURCE_TYPE );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
            filter.setIdIsComment( EntryFilter.FILTER_FALSE );

            List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );
            AppointmentDTO appointmentFromSession = _appointmentFormService.getAppointmentFromSession( request.getSession(  ) );
            _appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );

            List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>(  );
            Locale locale = request.getLocale(  );

            //Email confirmation
            String strEmail = request.getParameter( PARAMETER_EMAIL );
            String emailConfirm = ( request.getParameter( PARAMETER_EMAIL_CONFIRMATION ) == null )
                ? String.valueOf( MARK_CONSTANT_STR_NULL ) : request.getParameter( PARAMETER_EMAIL_CONFIRMATION );

            AppointmentDTO appointment = new AppointmentDTO(  );

            if ( form.getEnableMandatoryEmail(  ) )
            {
                if ( StringUtils.isEmpty( strEmail ) )
                {
                    GenericAttributeError genAttError = new GenericAttributeError(  );
                    genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_EMPTY_EMAIL,
                            request.getLocale(  ) ) );
                    listFormErrors.add( genAttError );
                }
            }

            if ( form.getEnableConfirmEmail(  ) )
            {
                if ( StringUtils.isEmpty( emailConfirm ) )
                {
                    GenericAttributeError genAttError = new GenericAttributeError(  );
                    genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_EMPTY_CONFIRM_EMAIL,
                            request.getLocale(  ) ) );
                    listFormErrors.add( genAttError );
                }
            }

            if ( !emailConfirm.equals( strEmail ) && !StringUtils.isEmpty( emailConfirm ) )
            {
                GenericAttributeError genAttError = new GenericAttributeError(  );
                genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_CONFIRM_EMAIL,
                        request.getLocale(  ) ) );
                listFormErrors.add( genAttError );
            }

            appointment.setEmail( strEmail );
            appointment.setFirstName( request.getParameter( PARAMETER_FIRST_NAME ) );
            appointment.setLastName( request.getParameter( PARAMETER_LAST_NAME ) );
            appointment.setStatus( Appointment.Status.STATUS_RESERVED.getValeur(  ) );
            appointment.setAppointmentForm( form );

            if ( appointmentFromSession != null )
            {
                appointment.setIdSlot( appointmentFromSession.getIdSlot(  ) );
            }

            if ( SecurityService.isAuthenticationEnable(  ) )
            {
                LuteceUser luteceUser = SecurityService.getInstance(  ).getRegisteredUser( request );

                if ( luteceUser != null )
                {
                    appointment.setIdUser( luteceUser.getName(  ) );
                    appointment.setAuthenticationService( luteceUser.getAuthenticationService(  ) );
                }
            }

            /*WORKFLOW FUTURE
                       State valid = getStatus ( nIdForm );

                      if ( valid != null )
                      {
                              appointment.setStatus( valid.getId() );
                      }
            */

            // We save the appointment in session. The appointment object will contain responses of the user to the form
            _appointmentFormService.saveAppointmentInSession( request.getSession(  ), appointment );

            Set<ConstraintViolation<AppointmentDTO>> listErrors = BeanValidationUtil.validate( appointment );

            /*WORKFLOW FUTURE            if ( valid == null )
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

            for ( Entry entry : listEntryFirstLevel )
            {
                listFormErrors.addAll( _appointmentFormService.getResponseEntry( request, entry.getIdEntry(  ), locale,
                        appointment ) );
            }

            // If there is some errors, we redirect the user to the form page
            if ( listFormErrors.size(  ) > 0 )
            {
                request.getSession(  ).setAttribute( SESSION_APPOINTMENT_FORM_ERRORS, listFormErrors );

                return redirect( request, getFormStepName( nIdForm ), PARAMETER_ID_FORM, nIdForm );
            }

            _appointmentFormService.convertMapResponseToList( appointment );
            _appointmentFormService.saveValidatedAppointmentForm( request.getSession(  ), appointment );

            if ( _appointmentFormService.isFormFirstStep( nIdForm ) )
            {
                return getAppointmentCalendar( request );
            }

            return redirectView( request, VIEW_DISPLAY_RECAP_APPOINTMENT );
        }

        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Get the page with the calendar with opened and closed days for an
     * appointment form
     * @param request The request
     * @return The XPage to display
     */
    public XPage getAppointmentCalendar( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            if ( _appointmentFormService.isFormFirstStep( nIdForm ) &&
                    ( _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) ) == null ) )
            {
                return redirect( request, getFormStepName( nIdForm ), PARAMETER_ID_FORM, nIdForm );
            }

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            if ( !form.getIsActive(  ) )
            {
                return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
            }

            Locale locale = getLocale( request );

            String strHtmlContent = getAppointmentCalendarHtml( request, form, _appointmentFormService, getModel(  ),
                    locale );

            XPage xpage = new XPage(  );
            xpage.setContent( strHtmlContent );
            xpage.setPathLabel( getDefaultPagePath( locale ) );
            xpage.setTitle( getDefaultPageTitle( locale ) );

            return xpage;
        }

        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Do select a slot
     * @param request the request
     * @return The XPage to display
     */
    @Action( ACTION_DO_SELECT_SLOT )
    public XPage doSelectSlot( HttpServletRequest request )
    {
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
            int nIdSlot = Integer.parseInt( strIdSlot );

            // We check that the selected slot exists and is valid
            AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );

            if ( ( appointmentSlot == null ) || !appointmentSlot.getIsEnabled(  ) )
            {
                return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
            }

            // we check that the form of the selected slot is active
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) );

            if ( !form.getIsActive(  ) )
            {
                return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
            }

            Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );

            if ( appointment != null )
            {
                appointment.setIdSlot( nIdSlot );
            }

            // If the calendar is the first step, then we must create the appointment object and save it into the session
            // Then we redirect the user to the second step
            if ( !_appointmentFormService.isFormFirstStep( form.getIdForm(  ) ) )
            {
                appointment = _appointmentFormService.getAppointmentFromSession( request.getSession(  ) );

                if ( appointment == null )
                {
                    AppointmentDTO appointmentDTO = new AppointmentDTO(  );
                    _appointmentFormService.setUserInfo( request, appointmentDTO );
                    _appointmentFormService.saveAppointmentInSession( request.getSession(  ), appointmentDTO );
                    appointment = appointmentDTO;
                }

                appointment.setIdSlot( nIdSlot );

                return redirect( request, VIEW_APPOINTMENT_FORM_SECOND_STEP, PARAMETER_ID_FORM,
                    appointmentSlot.getIdForm(  ) );
            }

            return redirectView( request, VIEW_DISPLAY_RECAP_APPOINTMENT );
        }

        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Display the recap before validating an appointment
     * @param request The request
     * @return The HTML content to display or the next URL to redirect to
     */
    @View( VIEW_DISPLAY_RECAP_APPOINTMENT )
    public XPage displayRecapAppointment( HttpServletRequest request )
    {
        //        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );

        //        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        //        {
        Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );

        if ( appointment == null )
        {
            return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
        }

        //            int nIdSlot = Integer.parseInt( strIdSlot );
        AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );

        //         We check that the appointment is not null and that the form associated with the validated appointment in session
        //         is the form associated with the selected slot
        //        if ( ( ( appointment.getListResponse( ) == null ) || ( appointment.getListResponse( ).size( ) == 0 ) || ( appointment
        //                .getListResponse( ).get( 0 ).getEntry( ).getIdResource( ) == appointmentSlot.getIdForm( ) ) ) )
        //        {
        //                appointment.setIdSlot( nIdSlot );
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_APPOINTMENT, appointment );
        model.put( MARK_SLOT, appointmentSlot );

        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) );

        if ( form.getEnableCaptcha(  ) && getCaptchaService(  ).isAvailable(  ) )
        {
            model.put( MARK_CAPTCHA, getCaptchaService(  ).getHtmlCode(  ) );
        }

        AppointmentDay day = AppointmentDayHome.findByPrimaryKey( appointmentSlot.getIdDay(  ) );
        appointment.setDateAppointment( (Date) day.getDate(  ).clone(  ) );
        model.put( MARK_FORM_MESSAGES, AppointmentFormMessagesHome.findByPrimaryKey( form.getIdForm(  ) ) );
        model.put( MARK_DAY, day );
        model.put( MARK_FORM, form );
        fillCommons( model );

        Locale locale = getLocale( request );

        List<ResponseRecapDTO> listResponseRecapDTO = new ArrayList<ResponseRecapDTO>( appointment.getListResponse(  )
                                                                                                  .size(  ) );

        for ( Response response : appointment.getListResponse(  ) )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry(  ) );
            listResponseRecapDTO.add( new ResponseRecapDTO( response,
                    entryTypeService.getResponseValueForRecap( response.getEntry(  ), request, response, locale ) ) );
        }

        model.put( MARK_LIST_RESPONSE_RECAP_DTO, listResponseRecapDTO );

        return getXPage( TEMPLATE_APPOINTMENT_FORM_RECAP, getLocale( request ), model );

        //        }

        //        return redirect( request, VIEW_APPOINTMENT_FORM_SECOND_STEP, PARAMETER_ID_FORM, appointmentSlot.getIdForm( ) );
        //        }

        //        return redirectView( request, VIEW_GET_FORM );
    }

    /**
     * Do save an appointment into the database if it is valid
     * @param request The request
     * @return The XPage to display
     */
    @Action( ACTION_DO_MAKE_APPOINTMENT )
    public XPage doMakeAppointment( HttpServletRequest request )
    {
        Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );

        if ( appointment != null )
        {
            AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKeyWithFreePlaces( appointment.getIdSlot(  ),
                    appointment.getDateAppointment(  ) );
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) );

            if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_BACK ) ) )
            {
                return redirect( request, VIEW_APPOINTMENT_FORM_SECOND_STEP, PARAMETER_ID_FORM,
                    appointmentSlot.getIdForm(  ) );
            }

            if ( form.getEnableCaptcha(  ) && getCaptchaService(  ).isAvailable(  ) )
            {
                if ( !getCaptchaService(  ).validate( request ) )
                {
                    addError( ERROR_MESSAGE_CAPTCHA, getLocale( request ) );

                    return redirect( request, VIEW_DISPLAY_RECAP_APPOINTMENT, PARAMETER_ID_SLOT,
                        appointmentSlot.getIdSlot(  ) );
                }
            }

            if ( !_appointmentFormService.doMakeAppointment( appointment, form, false ) )
            {
                addError( ERROR_MESSAGE_SLOT_FULL, getLocale( request ) );

                return redirect( request, getCalendarStepName( appointmentSlot.getIdForm(  ) ), PARAMETER_ID_FORM,
                    appointmentSlot.getIdForm(  ) );
            }

            _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );

            AppointmentAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );

            return redirect( request, VIEW_GET_APPOINTMENT_CREATED, PARAMETER_ID_FORM, appointmentSlot.getIdForm(  ),
                PARAMETER_ID_APPOINTMENT, appointment.getIdAppointment(  ) );
        }

        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Get the page to notify the user that the appointment has been created
     * @param request The request
     * @return The XPage to display
     */
    @View( VIEW_GET_APPOINTMENT_CREATED )
    public XPage getAppointmentCreated( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) &&
                StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
            int nIdAppointment = Integer.parseInt( strIdAppointment );
            Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );
            AppointmentFormMessages formMessages = AppointmentFormMessagesHome.findByPrimaryKey( nIdForm );
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );

            String strTimeBegin = _appointmentFormService.convertTimeIntoString( slot.getStartingHour(  ),
                    slot.getStartingMinute(  ) );
            String strTimeEnd = _appointmentFormService.convertTimeIntoString( slot.getEndingHour(  ),
                    slot.getEndingMinute(  ) );
            String strReference = StringUtils.isEmpty( form.getReference(  ) ) ? ""
                                                                               : ( Strings.toUpperCase( form.getReference(  )
                                                                                                            .trim(  ) ) +
                " - " );
            strReference += AppointmentService.getService(  ).computeRefAppointment( appointment );
            formMessages.setTextAppointmentCreated( formMessages.getTextAppointmentCreated(  )
                                                                .replaceAll( MARK_REF, strReference )
                                                                .replaceAll( MARK_DATE_APP,
                    getDateFormat(  ).format( appointment.getDateAppointment(  ) ) )
                                                                .replaceAll( MARK_TIME_BEGIN, strTimeBegin )
                                                                .replaceAll( MARK_TIME_END, strTimeEnd ) );

            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_FORM, form );
            model.put( MARK_FORM_MESSAGES, formMessages );

            return getXPage( TEMPLATE_APPOINTMENT_CREATED, getLocale( request ), model );
        }

        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Get the page to cancel an appointment
     * @param request The request
     * @return The XPage to display
     */
    @View( VIEW_GET_CANCEL_APPOINTMENT )
    public XPage getCancelAppointment( HttpServletRequest request )
    {
        //        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        //        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        //        {
        //            int nIdForm = Integer.parseInt( strIdForm );
        //            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

        //            if ( form.getAllowUsersToCancelAppointments(  ) )
        //            {
        Map<String, Object> model = new HashMap<String, Object>(  );

        //                model.put( MARK_FORM, form );
        Locale locale = getLocale( request );
        XPage xpage = getXPage( TEMPLATE_CANCEL_APPOINTMENT, locale, model );
        xpage.setTitle( I18nService.getLocalizedString( MESSAGE_CANCEL_APPOINTMENT_PAGE_TITLE, locale ) );

        return xpage;

        //            }
        //        }

        //        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Get the page to cancel an appointment
     * @param request The request
     * @return The XPage to display
     */
    @View( VIEW_GET_VIEW_CANCEL_APPOINTMENT )
    public XPage getViewCancelAppointment( HttpServletRequest request )
    {
        String refAppointment = request.getParameter( PARAMETER_REF_APPOINTMENT );
        String dateAppointment = request.getParameter( PARAMETER_DATE_APPOINTMENT );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( PARAMETER_REF_APPOINTMENT, refAppointment );
        model.put( MARK_DATE_APPOINTMENT, dateAppointment );

        Locale locale = getLocale( request );
        XPage xpage = getXPage( TEMPLATE_CANCEL_APPOINTMENT, locale, model );
        xpage.setTitle( I18nService.getLocalizedString( MESSAGE_CANCEL_APPOINTMENT_PAGE_TITLE, locale ) );

        return xpage;
    }

    /**
     * Do cancel an appointment
     * @param request The request
     * @return The XPage to display
     * @throws SiteMessageException If a site message needs to be displayed
     */
    @Action( ACTION_DO_CANCEL_APPOINTMENT )
    public XPage doCancelAppointment( HttpServletRequest request )
        throws SiteMessageException
    {
        String strRef = request.getParameter( PARAMETER_REF_APPOINTMENT );

        String strIdAppointment = strRef.substring( 0,
                strRef.length(  ) - AppointmentService.getService(  ).getRefSizeRandomPart(  ) );
        String strDate = request.getParameter( PARAMETER_DATE_APPOINTMENT );

        if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAppointment = Integer.parseInt( strIdAppointment );
            Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );

            Date date = (Date) getDateConverter(  ).convert( Date.class, strDate );

            if ( StringUtils.equals( strRef, AppointmentService.getService(  ).computeRefAppointment( appointment ) ) &&
                    DateUtils.isSameDay( date, appointment.getDateAppointment(  ) ) )
            {
                AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
                AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) );

                if ( !form.getAllowUsersToCancelAppointments(  ) )
                {
                    SiteMessageService.setMessage( request, ERROR_MESSAGE_CAN_NOT_CANCEL_APPOINTMENT,
                        SiteMessage.TYPE_STOP );
                }

                Plugin appointmentPlugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

                TransactionManager.beginTransaction( appointmentPlugin );

                try
                {
                    if ( appointment.getIdActionCancel(  ) > 0 )
                    {
                        boolean automaticUpdate = ( AdminUserService.getAdminUser( request ) == null ) ? true : false;
                        WorkflowService.getInstance(  )
                                       .doProcessAction( appointment.getIdAppointment(  ),
                            Appointment.APPOINTMENT_RESOURCE_TYPE, appointment.getIdActionCancel(  ),
                            form.getIdForm(  ), request, request.getLocale(  ), automaticUpdate );
                    }
                    else
                    {
                        appointment.setStatus( Appointment.Status.STATUS_UNRESERVED.getValeur(  ) );
                        AppointmentHome.update( appointment );
                    }

                    TransactionManager.commitTransaction( appointmentPlugin );
                }
                catch ( Exception e )
                {
                    TransactionManager.rollBack( appointmentPlugin );
                    throw new AppException( e.getMessage(  ), e );
                }

                if ( form.getAllowUsersToCancelAppointments(  ) && StringUtils.isNotEmpty( strRef ) )
                {
                    Map<String, String> mapParameters = new HashMap<String, String>(  );

                    if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FROM_MY_APPOINTMENTS ) ) )
                    {
                        String strReferer = request.getHeader( PARAMETER_REFERER );

                        if ( StringUtils.isNotEmpty( strReferer ) )
                        {
                            mapParameters.put( MARK_FROM_URL, strReferer );
                        }

                        mapParameters.put( PARAMETER_FROM_MY_APPOINTMENTS,
                            request.getParameter( PARAMETER_FROM_MY_APPOINTMENTS ) );
                    }

                    mapParameters.put( PARAMETER_ID_FORM, Integer.toString( appointmentSlot.getIdForm(  ) ) );

                    return redirect( request, VIEW_APPOINTMENT_CANCELED, mapParameters );
                }

                SiteMessageService.setMessage( request, ERROR_MESSAGE_UNKNOWN_REF, SiteMessage.TYPE_STOP );
            }
        }

        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Get the page to confirm that the appointment has been canceled
     * @param request The request
     * @return The XPage to display
     */
    @View( VIEW_APPOINTMENT_CANCELED )
    public XPage getAppointmentCanceled( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_FORM_MESSAGES, AppointmentFormMessagesHome.findByPrimaryKey( nIdForm ) );

            if ( Boolean.parseBoolean( request.getParameter( PARAMETER_FROM_MY_APPOINTMENTS ) ) )
            {
                String strFromUrl = request.getParameter( MARK_FROM_URL );
                model.put( MARK_BACK_URL,
                    StringUtils.isNotEmpty( strFromUrl ) ? strFromUrl : getViewUrl( VIEW_GET_MY_APPOINTMENTS ) );
            }

            return getXPage( TEMPLATE_APPOINTMENT_CANCELED, getLocale( request ), model );
        }

        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Get the page to view the appointments of a user
     * @param request The request
     * @return The XPage to display
     * @throws UserNotSignedException If the authentication is enabled and the
     *             user has not signed in
     */
    @View( VIEW_GET_MY_APPOINTMENTS )
    public XPage getMyAppointments( HttpServletRequest request )
        throws UserNotSignedException
    {
        if ( !SecurityService.isAuthenticationEnable(  ) )
        {
            return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
        }

        XPage xpage = new XPage(  );
        Locale locale = getLocale( request );
        xpage.setContent( getMyAppointmentsXPage( request, locale ) );
        xpage.setTitle( I18nService.getLocalizedString( MESSAGE_MY_APPOINTMENTS_PAGE_TITLE, locale ) );

        return xpage;
    }

    /**
     * Get the first step of the form
     * @param request The request
     * @return The first step of the form
     */
    @View( VIEW_APPOINTMENT_FORM_FIRST_STEP )
    public XPage getAppointmentFormFirstStep( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            if ( _appointmentFormService.isFormFirstStep( nIdForm ) )
            {
                return getViewForm( request );
            }
        }

        return getAppointmentCalendar( request );
    }

    /**
     * Get the second step of the form
     * @param request The request
     * @return The second step of the form
     */
    @View( VIEW_APPOINTMENT_FORM_SECOND_STEP )
    public XPage getAppointmentFormSecondStep( HttpServletRequest request )
    {
        _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );

        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            if ( _appointmentFormService.isFormFirstStep( nIdForm ) )
            {
                return getAppointmentCalendar( request );
            }
        }

        return getViewForm( request );
    }

    /**
     * Get the HTML content of the my appointment page of a user
     * @param request The request
     * @param locale The locale
     * @return The HTML content, or null if the
     * @throws UserNotSignedException If the user has not signed in
     */
    public static String getMyAppointmentsXPage( HttpServletRequest request, Locale locale )
        throws UserNotSignedException
    {
        if ( !SecurityService.isAuthenticationEnable(  ) )
        {
            return null;
        }

        LuteceUser luteceUser = SecurityService.getInstance(  ).getRegisteredUser( request );

        if ( luteceUser == null )
        {
            throw new UserNotSignedException(  );
        }

        AppointmentFilter appointmentFilter = new AppointmentFilter(  );
        appointmentFilter.setIdUser( luteceUser.getName(  ) );
        appointmentFilter.setAuthenticationService( luteceUser.getAuthenticationService(  ) );
        appointmentFilter.setDateAppointmentMin( new Date( System.currentTimeMillis(  ) ) );

        List<Appointment> listAppointments = AppointmentHome.getAppointmentListByFilter( appointmentFilter );

        List<AppointmentDTO> listAppointmentDTO = new ArrayList<AppointmentDTO>( listAppointments.size(  ) );

        Map<String, String> lsSta = new HashMap<String, String>(  );
        List<Integer> nidForm = new ArrayList<Integer>(  );

        for ( Appointment appointment : listAppointments )
        {
            AppointmentDTO appointmantDTO = new AppointmentDTO( appointment );
            appointmantDTO.setAppointmentSlot( AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) ) );
            /*WORKFLOW FUTURE            if (!nidForm.contains( Integer.valueOf( appointmantDTO.getAppointmentSlot(  ).getIdForm(  ) ) ));
                        {
                                nidForm.add(Integer.valueOf( appointmantDTO.getAppointmentSlot(  ).getIdForm(  ) ) );
                        }
            */
            appointmantDTO.setAppointmentForm( AppointmentFormHome.findByPrimaryKey( 
                    appointmantDTO.getAppointmentSlot(  ).getIdForm(  ) ) );
            listAppointmentDTO.add( appointmantDTO );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_LIST_APPOINTMENTS, listAppointmentDTO );
        /*WORKFLOW FUTURE
                model.put( MARK_STATUS, getAllStatus (nidForm));
        */
        model.put( MARK_STATUS_RESERVED, Appointment.Status.STATUS_RESERVED.getValeur(  ) );
        model.put( MARK_STATUS_UNRESERVED, Appointment.Status.STATUS_UNRESERVED.getValeur(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MY_APPOINTMENTS, locale, model );

        return template.getHtml(  );
    }

    /**WORKFLOW FUTURE
     * Get Status
     * @param nIdForm
     * @return

    private static Map <String, String> getAllStatus( List<Integer> nIdForm )
    {
        Map <String, String>lsSta = new HashMap<String, String>();
        for (int i=0; i < nIdForm.size(); i++)
        {
                AppointmentForm tmpForm = AppointmentFormHome.findByPrimaryKey( nIdForm.get(i) );
                StateFilter stateFilter = new StateFilter(  );
                stateFilter.setIdWorkflow( tmpForm.getIdWorkflow() );
                List<State> listStats = _stateService.getListStateByFilter( stateFilter );
                for (State tmpStat : listStats )
                {
                        lsSta.put(String.valueOf(tmpStat.getId()), tmpStat.getName());
                }
        }

        return lsSta;
    }*/

    /**
     * Get the HTML content of the first step of the form
     * @param request The request
     * @param form The form to display the first step of
     * @param appointmentFormService The instance of the appointment form
     *            service to use
     * @param model The model to use
     * @param locale The locale
     * @return The HTML content of the first step of the form
     */
    public static String getHtmlFormFirstStep( HttpServletRequest request, AppointmentForm form,
        AppointmentFormService appointmentFormService, Map<String, Object> model, Locale locale )
    {
        if ( appointmentFormService.isFormFirstStep( form.getIdForm(  ) ) )
        {
            return getAppointmentFormHtml( request, form, appointmentFormService, model, locale );
        }

        return getAppointmentCalendarHtml( request, form, appointmentFormService, model, locale );
    }

    /**
     * Get the HTML code of an appointment form
     * @param request The request
     * @param form The form to display. The form must not be null and must be
     *            active
     * @param appointmentFormService The appointment form service to use
     * @param model The model to use
     * @param locale the locale
     * @return The HTML code to display, or an empty string if the form is null
     *         or not active
     */
    private static String getAppointmentFormHtml( HttpServletRequest request, AppointmentForm form,
        AppointmentFormService appointmentFormService, Map<String, Object> model, Locale locale )
    {
        if ( ( form == null ) || !form.getIsActive(  ) )
        {
            return StringUtils.EMPTY;
        }

        AppointmentFormMessages formMessages = AppointmentFormMessagesHome.findByPrimaryKey( form.getIdForm(  ) );

        Appointment appointment = appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );

        if ( appointment != null )
        {
            AppointmentDTO appointmentDTO = new AppointmentDTO(  );
            appointmentDTO.setEmail( appointment.getEmail(  ) );
            appointmentDTO.setFirstName( appointment.getFirstName(  ) );
            appointmentDTO.setLastName( appointment.getLastName(  ) );
            appointmentDTO.setIdSlot( appointment.getIdSlot(  ) );

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

            appointmentFormService.saveAppointmentInSession( request.getSession(  ), appointmentDTO );
        }

        model.put( MARK_FORM_HTML, appointmentFormService.getHtmlForm( form, formMessages, locale, true, request ) );

        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession(  )
                                                                                      .getAttribute( SESSION_APPOINTMENT_FORM_ERRORS );

        if ( listErrors != null )
        {
            model.put( MARK_FORM_ERRORS, listErrors );
            request.getSession(  ).removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        }

        //        appointmentFormService.removeAppointmentFromSession( request.getSession( ) );
        appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_APPOINTMENT_FORM, locale, model );

        return template.getHtml(  );
    }

    /**
     * Get the HTML code to display the calendar of an appointment form
     * @param request The request
     * @param form The appointment form
     * @param appointmentFormService The instance of the appointment form
     *            service to use
     * @param model The model to use
     * @param locale the locale
     * @return The HTML content to display
     */
    private static String getAppointmentCalendarHtml( HttpServletRequest request, AppointmentForm form,
        AppointmentFormService appointmentFormService, Map<String, Object> model, Locale locale )
    {
        AppointmentFormMessages formMessages = AppointmentFormMessagesHome.findByPrimaryKey( form.getIdForm(  ) );

        String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );
        boolean bBack = StringUtils.isBlank( request.getParameter( PARAMETER_DIRECTION ) ) ? false
                                                                                           : Boolean.valueOf( request.getParameter( 
                    PARAMETER_DIRECTION ).trim(  ) );
        int nNbWeek = 0;

        if ( StringUtils.isNotEmpty( strNbWeek ) && StringUtils.isNumeric( strNbWeek ) )
        {
            nNbWeek = Integer.parseInt( strNbWeek );

            if ( nNbWeek > ( form.getNbWeeksToDisplay(  ) - 1 ) )
            {
                nNbWeek = form.getNbWeeksToDisplay(  ) - 1;
            }
        }

        MutableInt nMutableNbWeek = new MutableInt( nNbWeek );

        List<AppointmentDay> listDays = AppointmentService.getService(  )
                                                          .getDayListForCalendar( form, nMutableNbWeek, true, bBack );

        nNbWeek = nMutableNbWeek.intValue(  );

        if ( StringUtils.isNotBlank( formMessages.getCalendarDescription(  ) ) )
        {
            addInfo( model, formMessages.getCalendarDescription(  ) );
        }

        if ( listDays != null )
        {
            Appointment myApmt = appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );

            if ( ( myApmt != null ) && !StringUtils.isEmpty( myApmt.getEmail(  ) ) )
            {
                Date[] tmpLimit = { listDays.get( 0 ).getDate(  ), listDays.get( listDays.size(  ) - 1 ).getDate(  ) };
                List<Date> unvailableSlots = AppointmentFormHome.getLimitedByMail( null, tmpLimit, form.getIdForm(  ),
                        myApmt.getEmail(  ) );

                for ( int i = 0; i < listDays.size(  ); i++ )
                {
                    for ( Date tmpDate : unvailableSlots )
                    {
                        if ( getNumbersDay( listDays.get( i ).getDate(  ), tmpDate ) == 0 )
                        {
                            listDays.get( i ).setListSlots( eraseSlots( listDays.get( i ).getListSlots(  ) ) );
                        }
                    }
                }
            }

            List<String> listTimeBegin = new ArrayList<String>(  );
            int nMinAppointmentDuration = AppointmentService.getService(  )
                                                            .getListTimeBegin( listDays, form, listTimeBegin );
            List<AppointmentDay> listAvailableDays = AppointmentService.getService(  ).getAllAvailableDays( form );
            model.put( MARK_LIST_DAYS, listDays );
            model.put( MARK_LIST_AVAILABLE_DAYS, listAvailableDays );
            model.put( MARK_LIST_TIME_BEGIN, listTimeBegin );
            model.put( MARK_MIN_DURATION_APPOINTMENT, nMinAppointmentDuration );
            model.put( MARK_DATE_LAST_MONDAY,
                DateUtils.truncate( AppointmentService.getService(  ).getDateLastMonday(  ), Calendar.DATE ).getTime(  ) );
        }
        else
        {
            addInfo( model, formMessages.getNoAvailableSlot(  ) );
        }

        model.put( MARK_FORM, form );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( PARAMETER_NB_WEEK, nNbWeek );
        model.put( MARK_LIST_DAYS_OF_WEEK, MESSAGE_LIST_DAYS_OF_WEEK );
        model.put( MARK_IS_FORM_FIRST_STEP, appointmentFormService.isFormFirstStep( form.getIdForm(  ) ) );

        CalendarTemplate calendarTemplate = CalendarTemplateHome.findByPrimaryKey( form.getCalendarTemplateId(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( calendarTemplate.getTemplatePath(  ), locale, model );

        return template.getHtml(  );
    }

    /**
     * Get the HTML content to display the list of forms
     * @param request The r         * Compute unavailable Days
         * @param form
         * @param listDays
         */
    private static List<AppointmentDay> computeUnavailableDays( int nIdform, List<AppointmentDay> listDays )
    {
        if ( listDays != null )
        {
            for ( int i = 0; i < listDays.size(  ); i++ )
            {
                List<AppointmentSlot> dayApp = AppointmentSlotHome.getSlotsUnavailable( listDays.get( i ).getIdDay(  ),
                        nIdform );

                if ( ( dayApp.size(  ) > 0 ) && listDays.get( i ).getIsOpen(  ) )
                {
                    List<AppointmentSlot> tmpSlots = new ArrayList<AppointmentSlot>(  );

                    for ( AppointmentSlot mySlot : listDays.get( i ).getListSlots(  ) )
                    {
                        for ( AppointmentSlot myDayApp : dayApp )
                        {
                            if ( myDayApp.getIdSlot(  ) == mySlot.getIdSlot(  ) )
                            {
                                mySlot.setIsEnabled( false );
                            }
                        }

                        tmpSlots.add( mySlot );
                    }

                    listDays.get( i ).setListSlots( tmpSlots );
                }
            }
        }

        return listDays;
    }

    /**
     * Erase slots
     * @param objSlots
     * @return
     */
    private static List<AppointmentSlot> eraseSlots( List<AppointmentSlot> objSlots )
    {
        List<AppointmentSlot> returnSlots = objSlots;

        if ( objSlots != null )
        {
            returnSlots = new ArrayList<AppointmentSlot>(  );

            for ( AppointmentSlot mySlot : objSlots )
            {
                mySlot.setIsEnabled( false );
                returnSlots.add( mySlot );
            }
        }

        return returnSlots;
    }

    /**
    * @param appointmentFormService The service to use
    * @param strTitle The title to display, or null to display the default
    *            title.
    * @param locale The locale
    * @return The HTML content to display
    */
    public static String getFormListHtml( HttpServletRequest request, AppointmentFormService appointmentFormService,
        String strTitle, Locale locale )
    {
        appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );
        appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );
        AppointmentAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        Collection<AppointmentForm> listAppointmentForm = AppointmentFormHome.getActiveAppointmentFormsList(  );

        List<String> icons = new ArrayList<String>(  );

        for ( AppointmentForm form : listAppointmentForm )
        {
            ImageResource img = form.getIcon(  );

            if ( ( img.getImage(  ) == null ) || StringUtils.isBlank( img.getMimeType(  ) ) )
            {
                icons.add( MARK_ICON_NULL );
            }
            else
            {
                byte[] imgBytesAsBase64 = Base64.encodeBase64( img.getImage(  ) );

                String imgDataAsBase64 = new String( imgBytesAsBase64 );
                String strMimeType = img.getMimeType(  );
                String imgAsBase64 = MARK_DATA + MARK_COLON + strMimeType + MARK_SEMI_COLON + MARK_BASE_64 +
                    MARK_COMMA + imgDataAsBase64;
                icons.add( imgAsBase64 );
            }
        }

        model.put( MARK_ICONS, icons );
        model.put( MARK_FORM_LIST, listAppointmentForm );
        model.put( MARK_TITLE, strTitle );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_APPOINTMENT_FORM_LIST, locale, model );

        return template.getHtml(  );
    }

    /**
     * Get the captcha security service
     * @return The captcha security service
     */
    private CaptchaSecurityService getCaptchaService(  )
    {
        if ( _captchaSecurityService == null )
        {
            _captchaSecurityService = new CaptchaSecurityService(  );
        }

        return _captchaSecurityService;
    }

    /**
     * Get the converter to convert string to java.sql.Date.
     * @return The converter to convert String to java.sql.Date.
     */
    private DateConverter getDateConverter(  )
    {
        if ( _dateConverter == null )
        {
            _dateConverter = new DateConverter( getDateFormat(  ) );
        }

        return _dateConverter;
    }

    /**
     * Get the calendar step name
     * @return the calendar step name
     */
    private String getCalendarStepName( int nIdForm )
    {
        return _appointmentFormService.isFormFirstStep( nIdForm ) ? VIEW_APPOINTMENT_FORM_SECOND_STEP
                                                                  : VIEW_APPOINTMENT_FORM_FIRST_STEP;
    }

    /**
     * Get the form step name
     * @return The form step name
     */
    private String getFormStepName( int nIdForm )
    {
        return _appointmentFormService.isFormFirstStep( nIdForm ) ? VIEW_APPOINTMENT_FORM_FIRST_STEP
                                                                  : VIEW_APPOINTMENT_FORM_SECOND_STEP;
    }

    /**
     * Get the URL
     * @param request Get the URL to cancel an appointment in FO
     * @param appointment The appointment
     * @return The URL to cancel the appointment
     */
    public static String getCancelAppointmentUrl( HttpServletRequest request, Appointment appointment )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getProdUrl( request ) + AppPathService.getPortalUrl(  ) );
        urlItem.addParameter( MVCUtils.PARAMETER_PAGE, XPAGE_NAME );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_GET_VIEW_CANCEL_APPOINTMENT );
        urlItem.addParameter( PARAMETER_DATE_APPOINTMENT, getDateFormat(  ).format( appointment.getDateAppointment(  ) ) );
        urlItem.addParameter( PARAMETER_REF_APPOINTMENT,
            AppointmentService.getService(  ).computeRefAppointment( appointment ) );

        return urlItem.getUrl(  );
    }

    /**
     * Get the URL
     * @param request Get the URL to cancel an appointment in FO
     * @param appointment The appointment
     * @return The URL to cancel the appointment
     */
    public static String getCancelAppointmentUrl( Appointment appointment )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getProdUrl(  ) + AppPathService.getPortalUrl(  ) );
        urlItem.addParameter( MVCUtils.PARAMETER_PAGE, XPAGE_NAME );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_GET_VIEW_CANCEL_APPOINTMENT );
        urlItem.addParameter( PARAMETER_DATE_APPOINTMENT, getDateFormat(  ).format( appointment.getDateAppointment(  ) ) );
        urlItem.addParameter( PARAMETER_REF_APPOINTMENT,
            AppointmentService.getService(  ).computeRefAppointment( appointment ) );

        return urlItem.getUrl(  );
    }

    /**
     * Add an info message to a model
     * @param model The model
     * @param strMessage The message to add
     */
    private static void addInfo( Map<String, Object> model, String strMessage )
    {
        List<ErrorMessage> listInfos = (List<ErrorMessage>) model.get( MARK_INFOS );

        if ( listInfos == null )
        {
            listInfos = new ArrayList<ErrorMessage>(  );
            model.put( MARK_INFOS, listInfos );
        }

        List<ErrorMessage> listErrors = (List<ErrorMessage>) model.get( MARK_ERRORS );

        if ( listErrors == null )
        {
            listErrors = new ArrayList<ErrorMessage>(  );
            model.put( MARK_ERRORS, listErrors );
        }

        MVCMessage message = new MVCMessage( strMessage );
        listInfos.add( message );
    }

    /**
     * Get the date format
     * @return The date format
     */
    private static DateFormat getDateFormat(  )
    {
        return DateFormat.getDateInstance( DateFormat.SHORT, Locale.FRANCE );
    }

    /**
         * Compute Days beetween date
         * @param nStart
         * @param nEnd
         * @return
         */
    private static int getNumbersDay( Date nStart, Date nEnd )
    {
        long timeDiff = nEnd.getTime(  ) - nStart.getTime(  );
        timeDiff = timeDiff / 1000 / ( 24 * 60 * 60 );

        return Integer.valueOf( String.valueOf( timeDiff ) );
    }
}
