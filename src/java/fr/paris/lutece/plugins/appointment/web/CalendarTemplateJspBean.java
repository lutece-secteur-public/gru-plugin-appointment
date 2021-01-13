/*
 * Copyright (c) 2002-2021, City of Paris
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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplateHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.datatable.DataTableManager;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage calendar templates
 * 
 * @author Laurent Payen
 *
 */
@Controller( controllerJsp = CalendarTemplateJspBean.CONTROLLER_JSP, controllerPath = CalendarTemplateJspBean.CONTROLLER_PATH, right = CalendarTemplateJspBean.RIGHT_MANAGE_CALENDAR_TEMPLATES )
public class CalendarTemplateJspBean extends MVCAdminJspBean
{
    /**
     * Right to manage appointment calendar templates
     */
    public static final String RIGHT_MANAGE_CALENDAR_TEMPLATES = "APPOINTMENT_CALENDAR_TEMPLATE";

    /**
     * Folder of the JSP of this controller
     */
    public static final String CONTROLLER_PATH = "jsp/admin/plugins/appointment/";

    /**
     * Name of the JSP of this controller
     */
    public static final String CONTROLLER_JSP = "ManageCalendarTemplates.jsp";

    /**
     * URL of the JSP of this controller
     */
    public static final String JSP_URL_MANAGE_CALENDAR_TEMPLATE = CONTROLLER_PATH + CONTROLLER_JSP;

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3406345413046194795L;

    // templates
    private static final String TEMPLATE_MANAGE_CALENDAR_TEMPLATES = "/admin/plugins/appointment/templates/manage_calendar_templates.html";
    private static final String TEMPLATE_CREATE_MODIFY_CALENDAR_TEMPLATE = "/admin/plugins/appointment/templates/create_modify_calendar_template.html";

    // Marks
    private static final String MARK_DATA_TABLE_MANAGER = "dataTableManager";
    private static final String MARK_TEMPLATE = "template";
    private static final String MARK_REF_LIST_TEMPLATES = "refListTemplates";

    // Parameters
    private static final String PARAMETER_ID_TEMPLATE = "idTemplate";
    private static final String PARAMETER_TEMPLACE_PATH = "templatePath";

    // Messages
    private static final String MESSAGE_COLUMN_TITLE_TITLE = "appointment.labelTitle";
    private static final String MESSAGE_COLUMN_TITLE_DESCRIPTION = "appointment.labelDescription";
    private static final String MESSAGE_COLUMN_TITLE_TEMPLATE_PATH = "appointment.manageCalendarTemplates.labelTemplatePath";
    private static final String MESSAGE_COLUMN_TITLE_ACTIONS = "portal.util.labelActions";
    private static final String MESSAGE_DEFAULT_PAGE_TITLE = "appointment.adminFeature.manageCalendarTemplates.name";
    private static final String MESSAGE_CREATE_TEMPLATE_PAGE_TITLE = "appointment.labelAddTemplate";
    private static final String MESSAGE_MODIFY_TEMPLATE_PAGE_TITLE = "appointment.createModifyCalendarTemplate.pageTitleModify";
    private static final String MESSAGE_INFO_TEMPLATE_CREATED = "appointment.createModifyCalendarTemplate.infoTemplateCreated";
    private static final String MESSAGE_INFO_TEMPLATE_UPDATED = "appointment.createModifyCalendarTemplate.infoTemplateUpdated";
    private static final String MESSAGE_INFO_TEMPLATE_REMOVED = "appointment.removeCalendarTemplate.infoTemplateRemoved";
    private static final String MESSAGE_CONFIRM_REMOVE_TEMPLATE = "appointment.removeCalendarTemplate.confirmRemoveTemplate";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE = "appointment.listAppointments.itemsPerPage";
    private static final String PROPERTY_FOLDER_CALENDAR_TEMPLATES = "appointment.calendarTemplates.calendarTemplatesFolder";
    private static final String PROPERTY_TITLE = "title";
    private static final String PROPERTY_DESCRIPTION = "description";
    private static final String PROPERTY_TEMPLATE_PATH = "templatePath";

    // Views
    private static final String VIEW_MANAGE_CALENDAR_TEMPLATES = "viewManageCalendarTemplates";
    private static final String VIEW_CREATE_MODIFY_TEMPLATE = "getCreateModifyTemplate";
    private static final String VIEW_CONFIRM_REMOVE_TEMPLATE = "getConfirmRemoveTemplate";

    // Actions
    private static final String ACTION_CREATE_MODIFY_TEMPLATE = "doCreateModifyTemplate";
    private static final String ACTION_REMOVE_TEMPLATE = "doRemoveTemplate";
    private static final String ACTION_DOWNLOAD_TEMPLATE = "doDownloadTemplate";

    // Constants
    private static final String CONSTANT_TEMPLATE_FOLDER = "/WEB-INF/templates/";
    private static final String CONSTANT_FOLDER_UP = "..";

    // Session variables
    private DataTableManager<CalendarTemplate> _dataTableManager;
    private CalendarTemplate _template;

    /**
     * Get the page to manage calendar templates.
     * 
     * @param request
     *            The request
     * @return The HTML code to display
     */
    @View( value = VIEW_MANAGE_CALENDAR_TEMPLATES, defaultView = true )
    public String getManageAppointmentCalendarTemplates( HttpServletRequest request )
    {
        _template = null;

        if ( _dataTableManager == null )
        {
            _dataTableManager = new DataTableManager<>( getViewFullUrl( VIEW_MANAGE_CALENDAR_TEMPLATES ), null,
                    AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE, 50 ), true );
            _dataTableManager.addColumn( MESSAGE_COLUMN_TITLE_TITLE, PROPERTY_TITLE, true );
            _dataTableManager.addColumn( MESSAGE_COLUMN_TITLE_DESCRIPTION, PROPERTY_DESCRIPTION, true );
            _dataTableManager.addColumn( MESSAGE_COLUMN_TITLE_TEMPLATE_PATH, PROPERTY_TEMPLATE_PATH, true );
            _dataTableManager.addActionColumn( MESSAGE_COLUMN_TITLE_ACTIONS );
        }

        _dataTableManager.filterSortAndPaginate( request, CalendarTemplateHome.findAll( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_DATA_TABLE_MANAGER, _dataTableManager );

        String strContent = getPage( MESSAGE_DEFAULT_PAGE_TITLE, TEMPLATE_MANAGE_CALENDAR_TEMPLATES, model );

        _dataTableManager.clearItems( );

        return strContent;
    }

    /**
     * Get the page to create or modify an existing template
     * 
     * @param request
     *            The request
     * @return The page to create or modify an existing template
     */
    @View( VIEW_CREATE_MODIFY_TEMPLATE )
    public String getCreateModifyTemplate( HttpServletRequest request )
    {
        String strIdTemplate = request.getParameter( PARAMETER_ID_TEMPLATE );

        if ( StringUtils.isNotEmpty( strIdTemplate ) && StringUtils.isNumeric( strIdTemplate ) )
        {
            // If the id template is valid, then we load the template from the
            // database
            int nIdTemplate = Integer.parseInt( strIdTemplate );
            _template = CalendarTemplateHome.findByPrimaryKey( nIdTemplate );
        }
        else
        {
            if ( _template == null )
            {
                // Otherwise, we create a new template
                _template = new CalendarTemplate( );
            }
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_TEMPLATE, _template );

        String strCalendarTemplatesFolder = AppPropertiesService.getProperty( PROPERTY_FOLDER_CALENDAR_TEMPLATES, StringUtils.EMPTY );

        File calendarTemplatesFolder = new File( AppPathService.getWebAppPath( ) + CONSTANT_TEMPLATE_FOLDER + strCalendarTemplatesFolder );

        ReferenceList refListTemplates = new ReferenceList( );
        refListTemplates.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

        if ( calendarTemplatesFolder.exists( ) )
        {
            if ( calendarTemplatesFolder.isDirectory( ) )
            {
                File [ ] listFiles = calendarTemplatesFolder.listFiles( );
                if ( !ArrayUtils.isEmpty( listFiles ) )
                {
                    for ( File file : listFiles )
                    {
                        if ( file != null )
                        {
                            refListTemplates.addItem( strCalendarTemplatesFolder + file.getName( ), file.getName( ) );
                        }
                    }
                }
            }
            else
            {
                // If the specified folder is a file, we add that file to the
                // reference list
                refListTemplates.addItem( strCalendarTemplatesFolder + calendarTemplatesFolder.getName( ), calendarTemplatesFolder.getName( ) );
            }
        }

        model.put( MARK_REF_LIST_TEMPLATES, refListTemplates );

        return getPage( ( _template.getIdCalendarTemplate( ) > 0 ) ? MESSAGE_MODIFY_TEMPLATE_PAGE_TITLE : MESSAGE_CREATE_TEMPLATE_PAGE_TITLE,
                TEMPLATE_CREATE_MODIFY_CALENDAR_TEMPLATE, model );
    }

    /**
     * Do the creation or the modification of a calendar template
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_CREATE_MODIFY_TEMPLATE )
    public String doCreateModifyTemplate( HttpServletRequest request )
    {
        // We reset the session template to prevent collisions with any other
        // tab or page
        _template = new CalendarTemplate( );
        populate( _template, request );

        Set<ConstraintViolation<CalendarTemplate>> listErrors = validate( _template );

        if ( CollectionUtils.isNotEmpty( listErrors ) )
        {
            for ( ConstraintViolation<CalendarTemplate> error : listErrors )
            {
                addError( error.getMessage( ) );
            }

            return redirectView( request, VIEW_CREATE_MODIFY_TEMPLATE );
        }

        if ( _template.getIdCalendarTemplate( ) > 0 )
        {
            CalendarTemplateHome.update( _template );
            addInfo( MESSAGE_INFO_TEMPLATE_UPDATED, getLocale( ) );
        }
        else
        {
            CalendarTemplateHome.create( _template );
            addInfo( MESSAGE_INFO_TEMPLATE_CREATED, getLocale( ) );
        }

        return redirectView( request, VIEW_MANAGE_CALENDAR_TEMPLATES );
    }

    /**
     * Get the confirmation page before removing a template
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @View( VIEW_CONFIRM_REMOVE_TEMPLATE )
    public String getConfirmRemoveTemplate( HttpServletRequest request )
    {
        String strIdTemplate = request.getParameter( PARAMETER_ID_TEMPLATE );

        if ( StringUtils.isEmpty( strIdTemplate ) || !StringUtils.isNumeric( strIdTemplate ) )
        {
            return redirectView( request, VIEW_MANAGE_CALENDAR_TEMPLATES );
        }

        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + getActionUrl( ACTION_REMOVE_TEMPLATE ) );
        urlItem.addParameter( PARAMETER_ID_TEMPLATE, strIdTemplate );

        return redirect( request,
                AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TEMPLATE, urlItem.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Do remove a template
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_REMOVE_TEMPLATE )
    public String doRemoveTemplate( HttpServletRequest request )
    {
        String strIdTemplate = request.getParameter( PARAMETER_ID_TEMPLATE );

        if ( StringUtils.isEmpty( strIdTemplate ) || !StringUtils.isNumeric( strIdTemplate ) )
        {
            return redirectView( request, VIEW_MANAGE_CALENDAR_TEMPLATES );
        }

        CalendarTemplateHome.delete( Integer.parseInt( strIdTemplate ) );

        addInfo( MESSAGE_INFO_TEMPLATE_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_CALENDAR_TEMPLATES );
    }

    /**
     * Do download a template
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DOWNLOAD_TEMPLATE )
    public String doDownloadTemplate( HttpServletRequest request )
    {
        String strTemplate = request.getParameter( PARAMETER_TEMPLACE_PATH );

        String strCalendarTemplatesFolder = AppPropertiesService.getProperty( PROPERTY_FOLDER_CALENDAR_TEMPLATES, StringUtils.EMPTY );

        if ( StringUtils.isNotEmpty( strTemplate ) && strTemplate.startsWith( strCalendarTemplatesFolder ) && !strTemplate.contains( CONSTANT_FOLDER_UP ) )
        {
            File file = new File( AppPathService.getWebAppPath( ) + CONSTANT_TEMPLATE_FOLDER + strTemplate );
            byte [ ] fileContent;

            try
            {
                fileContent = FileUtils.readFileToByteArray( file );
            }
            catch( IOException e )
            {
                AppLogService.error( e.getMessage( ), e );

                return redirectView( request, VIEW_MANAGE_CALENDAR_TEMPLATES );
            }

            download( fileContent, file.getName( ), "text/plain" );

            return null;
        }

        return redirectView( request, VIEW_MANAGE_CALENDAR_TEMPLATES );
    }
}
