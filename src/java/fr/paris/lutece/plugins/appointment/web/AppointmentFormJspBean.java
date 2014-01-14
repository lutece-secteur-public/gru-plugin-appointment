/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormMessages;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormMessagesHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormService;
import fr.paris.lutece.plugins.appointment.service.AppointmentSlotService;
import fr.paris.lutece.plugins.appointment.service.CalendarService;
import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.service.EntryTypeService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class provides the user interface to manage AppointmentForm features (
 * manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageAppointmentForms.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentFormJspBean extends MVCAdminJspBean
{
    /**
     * Right to manage appointment forms
     */
    public static final String RIGHT_MANAGEAPPOINTMENTFORM = "APPOINTMENT_FORM_MANAGEMENT";
    private static final long serialVersionUID = -615061018633136997L;

    // templates
    private static final String TEMPLATE_MANAGE_APPOINTMENTFORMS = "/admin/plugins/appointment/appointmentform/manage_appointmentforms.html";
    private static final String TEMPLATE_CREATE_APPOINTMENTFORM = "/admin/plugins/appointment/appointmentform/create_appointmentform.html";
    private static final String TEMPLATE_MODIFY_APPOINTMENTFORM = "/admin/plugins/appointment/appointmentform/modify_appointmentform.html";
    private static final String TEMPLATE_MODIFY_APPOINTMENTFORM_MESSAGES = "/admin/plugins/appointment/appointmentform/modify_appointmentform_messages.html";

    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_BACK = "back";
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTFORMS = "appointment.manage_appointmentforms.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM = "appointment.modify_appointmentform.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_APPOINTMENTFORM = "appointment.create_appointmentform.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_MESSAGES = "appointment.modify_appointmentformMessages.pageTitle";

    // Markers
    private static final String MARK_APPOINTMENTFORM_LIST = "appointmentform_list";
    private static final String MARK_APPOINTMENTFORM = "appointmentform";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";
    private static final String MARK_GROUP_ENTRY_LIST = "entry_group_list";
    private static final String MARK_LIST_ORDER_FIRST_LEVEL = "listOrderFirstLevel";
    private static final String MARK_LIST_WORKFLOWS = "listWorkflows";
    private static final String MARK_IS_CAPTCHA_ENABLED = "isCaptchaEnabled";
    private static final String MARK_FORM_MESSAGE = "formMessage";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String JSP_MANAGE_APPOINTMENTFORMS = "jsp/admin/plugins/appointment/ManageAppointmentForms.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENTFORM = "appointment.message.confirmRemoveAppointmentForm";
    private static final String PROPERTY_DEFAULT_LIST_APPOINTMENTFORM_PER_PAGE = "appointment.listAppointmentForms.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointmentform.attribute.";

    // Views
    private static final String VIEW_MANAGE_APPOINTMENTFORMS = "manageAppointmentForms";
    private static final String VIEW_CREATE_APPOINTMENTFORM = "createAppointmentForm";
    private static final String VIEW_MODIFY_APPOINTMENTFORM = "modifyAppointmentForm";
    private static final String VIEW_MODIFY_FORM_MESSAGES = "modifyAppointmentFormMessages";

    // Actions
    private static final String ACTION_CREATE_APPOINTMENTFORM = "createAppointmentForm";
    private static final String ACTION_MODIFY_APPOINTMENTFORM = "modifyAppointmentForm";
    private static final String ACTION_REMOVE_APPOINTMENTFORM = "removeAppointmentForm";
    private static final String ACTION_CONFIRM_REMOVE_APPOINTMENTFORM = "confirmRemoveAppointmentForm";
    private static final String ACTION_DO_CHANGE_FORM_ACTIVATION = "doChangeFormActivation";
    private static final String ACTION_DO_MODIFY_FORM_MESSAGES = "doModifyAppointmentFormMessages";

    // Infos
    private static final String INFO_APPOINTMENTFORM_CREATED = "appointment.info.appointmentform.created";
    private static final String INFO_APPOINTMENTFORM_UPDATED = "appointment.info.appointmentform.updated";
    private static final String INFO_APPOINTMENTFORM_REMOVED = "appointment.info.appointmentform.removed";
    private static final String INFO_MODIFY_APPOINTMENTFORM_SLOTS_UPDATED = "appointment.info.appointmentform.updated.slotsUpdated";

    // Session variable to store working values
    private static final String SESSION_ATTRIBUTE_APPOINTMENT_FORM = "appointment.session.appointmentForm";
    private static final String SESSION_CURRENT_PAGE_INDEX = "appointment.session.appointmentForm.currentPageIndex";
    private static final String SESSION_ITEMS_PER_PAGE = "appointment.session.appointmentForm.itemsPerPage";
    private static final String DEFAULT_CURRENT_PAGE = "1";

    // Local variables
    private static final CaptchaSecurityService _captchaSecurityService = new CaptchaSecurityService(  );
    private final EntryService _entryService = EntryService.getService(  );
    private final AppointmentFormService _appointmentFormService = SpringContextService.getBean( AppointmentFormService.BEAN_NAME );
    private int _nDefaultItemsPerPage;

    /**
     * Default constructor
     */
    public AppointmentFormJspBean(  )
    {
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_APPOINTMENTFORM_PER_PAGE, 50 );
    }

    /**
     * Get the page to manage appointment forms
     * @param request the request
     * @return The HTML content to display
     */
    @View( value = VIEW_MANAGE_APPOINTMENTFORMS, defaultView = true )
    public String getManageAppointmentForms( HttpServletRequest request )
    {
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

        request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );

        UrlItem url = new UrlItem( JSP_MANAGE_APPOINTMENTFORMS );
        String strUrl = url.getUrl(  );
        List<AppointmentForm> listAppointmentForms = (List<AppointmentForm>) AppointmentFormHome.getAppointmentFormsList(  );

        // PAGINATOR
        LocalizedPaginator<AppointmentForm> paginator = new LocalizedPaginator<AppointmentForm>( listAppointmentForms,
                nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex, getLocale(  ) );

        Map<String, Object> model = getModel(  );

        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_APPOINTMENTFORM_LIST, paginator.getPageItems(  ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTFORMS, TEMPLATE_MANAGE_APPOINTMENTFORMS, model );
    }

    /**
     * Returns the form to create an appointment form
     *
     * @param request The HTTP request
     * @return the HTML code of the appointment form
     */
    @View( VIEW_CREATE_APPOINTMENTFORM )
    public String getCreateAppointmentForm( HttpServletRequest request )
    {
        AppointmentForm appointmentForm = (AppointmentForm) request.getSession(  )
                                                                   .getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );

        if ( appointmentForm == null )
        {
            appointmentForm = new AppointmentForm(  );
            request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, appointmentForm );
        }

        Map<String, Object> model = getModel(  );
        addElementsToModelForLeftColumn( request, appointmentForm, getUser(  ), getLocale(  ), model );

        //        model.put( MARK_LOCALE, AppointmentPlugin.getPluginLocale( getLocale( ) ) );
        return getPage( PROPERTY_PAGE_TITLE_CREATE_APPOINTMENTFORM, TEMPLATE_CREATE_APPOINTMENTFORM, model );
    }

    /**
     * Process the data capture form of a new appointment form
     * @param request The HTTP Request
     * @return The JSP URL of the process result
     */
    @Action( ACTION_CREATE_APPOINTMENTFORM )
    public String doCreateAppointmentForm( HttpServletRequest request )
    {
        AppointmentForm appointmentForm = (AppointmentForm) request.getSession(  )
                                                                   .getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );

        if ( appointmentForm == null )
        {
            appointmentForm = new AppointmentForm(  );
        }

        prepareFormForPopulate( appointmentForm );
        populate( appointmentForm, request );

        // Check constraints
        if ( !validateBean( appointmentForm, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_APPOINTMENTFORM );
        }

        AppointmentFormHome.create( appointmentForm, _appointmentFormService.getDefaultAppointmentFormMessage(  ) );

        AppointmentSlotService.getInstance(  ).computeAndCreateSlotsForForm( appointmentForm );

        request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        addInfo( INFO_APPOINTMENTFORM_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
    }

    /**
     * Manages the removal form of a appointment form whose identifier is in the
     * HTTP request
     * @param request The HTTP request
     * @return the HTML code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_APPOINTMENTFORM )
    public String getConfirmRemoveAppointmentForm( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_APPOINTMENTFORM ) );
        url.addParameter( PARAMETER_ID_FORM, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_APPOINTMENTFORM,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of an appointment form
     * @param request The HTTP request
     * @return the JSP URL to display the form to manage appointment forms
     */
    @Action( ACTION_REMOVE_APPOINTMENTFORM )
    public String doRemoveAppointmentForm( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );

        _entryService.removeEntriesByIdAppointmentForm( nId );

        AppointmentFormHome.remove( nId );
        addInfo( INFO_APPOINTMENTFORM_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
    }

    /**
     * Returns the form to update info about a appointment form
     * @param request The HTTP request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_APPOINTMENTFORM )
    public String getModifyAppointmentForm( HttpServletRequest request )
    {
        AppointmentForm appointmentForm = (AppointmentForm) request.getSession(  )
                                                                   .getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );

        if ( appointmentForm == null )
        {
            int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
            appointmentForm = AppointmentFormHome.findByPrimaryKey( nIdForm );
            request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, appointmentForm );
        }

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdResource( appointmentForm.getIdForm(  ) );
        entryFilter.setResourceType( AppointmentForm.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( entryFilter );
        List<Entry> listEntry = new ArrayList<Entry>( listEntryFirstLevel.size(  ) );

        //        Map<Integer, Integer> mapGroupItemsNumber = new HashMap<Integer, Integer>( );
        List<Integer> listOrderFirstLevel = new ArrayList<Integer>( listEntryFirstLevel.size(  ) );

        for ( Entry entry : listEntryFirstLevel )
        {
            listEntry.add( entry );
            // If the entry is a group, we add entries associated with this group
            listOrderFirstLevel.add( listEntry.size(  ) );

            if ( entry.getEntryType(  ).getGroup(  ) )
            {
                entryFilter = new EntryFilter(  );
                entryFilter.setIdResource( appointmentForm.getIdForm(  ) );
                entryFilter.setResourceType( AppointmentForm.RESOURCE_TYPE );
                entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
                entryFilter.setIdEntryParent( entry.getIdEntry(  ) );

                List<Entry> listEntryGroup = EntryHome.getEntryList( entryFilter );
                entry.setChildren( listEntryGroup );
                //                mapGroupItemsNumber.put( entry.getIdEntry( ), listEntryGroup.size( ) );
                listEntry.addAll( listEntryGroup );
            }
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_GROUP_ENTRY_LIST, getRefListGroups( appointmentForm.getIdForm(  ) ) );
        model.put( MARK_ENTRY_TYPE_LIST, EntryTypeService.getInstance(  ).getEntryTypeReferenceList(  ) );
        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_LIST_ORDER_FIRST_LEVEL, listOrderFirstLevel );
        addElementsToModelForLeftColumn( request, appointmentForm, getUser(  ), getLocale(  ), model );

        //        model.put( MARK_MAP_CHILD, mapGroupItemsNumber );
        return getPage( PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM, TEMPLATE_MODIFY_APPOINTMENTFORM, model );
    }

    /**
     * Process the change form of a appointment form
     * @param request The HTTP request
     * @return The JSP URL of the process result
     */
    @Action( ACTION_MODIFY_APPOINTMENTFORM )
    public String doModifyAppointmentForm( HttpServletRequest request )
    {
        AppointmentForm appointmentForm = (AppointmentForm) request.getSession(  )
                                                                   .getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        prepareFormForPopulate( appointmentForm );
        populate( appointmentForm, request );

        // Check constraints
        if ( !validateBean( appointmentForm, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_APPOINTMENTFORM, PARAMETER_ID_FORM, appointmentForm.getIdForm(  ) );
        }

        AppointmentForm formFromDb = AppointmentFormHome.findByPrimaryKey( appointmentForm.getIdForm(  ) );

        AppointmentFormHome.update( appointmentForm );

        if ( AppointmentSlotService.getInstance(  ).checkForFormModification( appointmentForm, formFromDb ) )
        {
            addInfo( INFO_MODIFY_APPOINTMENTFORM_SLOTS_UPDATED, getLocale(  ) );
        }

        request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        addInfo( INFO_APPOINTMENTFORM_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
    }

    /**
     * Change the enabling of an appointment form
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_CHANGE_FORM_ACTIVATION )
    public String doChangeFormActivation( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            // If we enable the form, we check that its days has already been created
            if ( !form.getIsActive(  ) )
            {
                CalendarService.getService(  ).checkFormDays( form );

                // If we enable the form, and it has a passed date of end of validity, we remove it to prevent to form from being disabled by the publication daemon
                if ( ( form.getDateEndValidity(  ) != null ) &&
                        ( form.getDateEndValidity(  ).getTime(  ) < System.currentTimeMillis(  ) ) )
                {
                    form.setDateEndValidity( null );
                }
            }
            else
            {
                // If we disable the form, and it has a passed date of start of validity, we remove it to prevent to form from being enabled by the publication daemon
                if ( ( form.getDateStartValidity(  ) != null ) &&
                        ( form.getDateStartValidity(  ).getTime(  ) < System.currentTimeMillis(  ) ) )
                {
                    form.setDateStartValidity( null );
                }
            }

            form.setIsActive( !form.getIsActive(  ) );
            AppointmentFormHome.update( form );
        }

        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
    }

    /**
     * Get the page to modify an appointment form
     * @param request The request
     * @return The HTML content to display
     */
    @View( VIEW_MODIFY_FORM_MESSAGES )
    public String getModifyAppointmentFormMessages( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
            AppointmentFormMessages formMessages = AppointmentFormMessagesHome.findByPrimaryKey( nIdForm );
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_FORM_MESSAGE, formMessages );
            model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
            model.put( MARK_LOCALE, getLocale(  ) );

            return getPage( PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_MESSAGES,
                TEMPLATE_MODIFY_APPOINTMENTFORM_MESSAGES, model );
        }

        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
    }

    /**
     * Do modify an appointment form messages
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MODIFY_FORM_MESSAGES )
    public String doModifyAppointmentFormMessages( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) &&
                ( request.getParameter( PARAMETER_BACK ) == null ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            AppointmentFormMessages formMessages = AppointmentFormMessagesHome.findByPrimaryKey( nIdForm );

            populate( formMessages, request );

            AppointmentFormMessagesHome.update( formMessages );

            return redirect( request, VIEW_MODIFY_FORM_MESSAGES, PARAMETER_ID_FORM, nIdForm );
        }

        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
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
     * Prepare an appointment form for population. Boolean fields of the form
     * will be set to false, as check boxes are not committed in the request.
     * @param appointmentForm The appointment form
     */
    private void prepareFormForPopulate( AppointmentForm appointmentForm )
    {
        appointmentForm.setIsOpenMonday( false );
        appointmentForm.setIsOpenTuesday( false );
        appointmentForm.setIsOpenWednesday( false );
        appointmentForm.setIsOpenThursday( false );
        appointmentForm.setIsOpenFriday( false );
        appointmentForm.setIsOpenSaturday( false );
        appointmentForm.setIsOpenSunday( false );
        appointmentForm.setEnableCaptcha( false );
    }

    /**
     * Get the reference list of groups
     * @param nIdForm the id of the appointment form
     * @return The reference list of groups of the given form
     */
    private static ReferenceList getRefListGroups( int nIdForm )
    {
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdResource( nIdForm );
        entryFilter.setResourceType( AppointmentForm.RESOURCE_TYPE );
        entryFilter.setIdIsGroup( 1 );

        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

        ReferenceList refListGroups = new ReferenceList(  );

        for ( Entry entry : listEntry )
        {
            refListGroups.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
        }

        return refListGroups;
    }

    /**
     * Get the URL to modify an appointment form
     * @param request The request
     * @param nIdForm The id of the form to modify
     * @return The URL to modify the given Appointment form
     */
    public static String getURLModifyAppointmentForm( HttpServletRequest request, int nIdForm )
    {
        return getURLModifyAppointmentForm( request, Integer.toString( nIdForm ) );
    }

    /**
     * Get the URL to modify an appointment form
     * @param request The request
     * @param strIdForm The id of the form to modify
     * @return The URL to modify the given Appointment form
     */
    public static String getURLModifyAppointmentForm( HttpServletRequest request, String strIdForm )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTFORMS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MODIFY_APPOINTMENTFORM );
        urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );

        return urlItem.getUrl(  );
    }

    /**
     * Get the URL to manage appointment forms
     * @param request The request
     * @return The URL to manage appointment forms
     */
    public static String getURLManageAppointmentForms( HttpServletRequest request )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTFORMS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTFORMS );

        return urlItem.getUrl(  );
    }

    /**
     * Add elements to the model to display the left column to modify an
     * appointment form
     * @param request The request to store the appointment form in session
     * @param appointmentForm The appointment form
     * @param user The user
     * @param locale The locale
     * @param model the model to add elements in
     */
    public static void addElementsToModelForLeftColumn( HttpServletRequest request, AppointmentForm appointmentForm,
        AdminUser user, Locale locale, Map<String, Object> model )
    {
        model.put( MARK_APPOINTMENTFORM, appointmentForm );
        model.put( MARK_LIST_WORKFLOWS, WorkflowService.getInstance(  ).getWorkflowsEnabled( user, locale ) );
        model.put( MARK_IS_CAPTCHA_ENABLED, _captchaSecurityService.isAvailable(  ) );
        request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, appointmentForm );
    }
}
