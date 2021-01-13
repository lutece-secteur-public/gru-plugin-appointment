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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.service.EntryTypeService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * JspBean to manage appointment form fieldsl
 * 
 * @author Laurent Payen
 *
 */
@Controller( controllerJsp = "ManageAppointmentFormFields.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentFormFieldJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -1505164256619633838L;

    // Properties
    private static final String PROPERTY_CREATE_FIELD_TITLE = "appointment.createField.title";
    private static final String PROPERTY_MODIFY_FIELD_TITLE = "appointment.modifyField.title";

    // Urls
    private static final String JSP_URL_MANAGE_APPOINTMENT_FORM_FIELDS = "jsp/admin/plugins/appointment/ManageAppointmentFormFields.jsp";

    // Marks
    private static final String MARK_FIELD = "field";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_FIELD = "appointment.message.confirmRemoveField";
    private static final String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryField";
    private static final String MESSAGE_FIELD_VALUE_FIELD = "appointment.message.error.fieldValue";

    // Views
    private static final String VIEW_GET_CREATE_FIELD = "getCreateField";
    private static final String VIEW_GET_MODIFY_FIELD = "getModifyField";
    private static final String VIEW_GET_MODIFY_FIELD_WITH_CONDITIONAL_QUESTIONS = "getModifyFieldCC";
    private static final String VIEW_GET_CONFIRM_REMOVE_FIELD = "getConfirmRemoveField";

    // Actions
    private static final String ACTION_DO_CREATE_FIELD = "doCreateField";
    private static final String ACTION_DO_MODIFY_FIELD = "doModifyField";
    private static final String ACTION_DO_MODIFY_FIELD_WITH_CONDITIONAL_QUESTIONS = "doModifyFieldCC";
    private static final String ACTION_DO_MOVE_FIELD_UP = "doMoveFieldUp";
    private static final String ACTION_DO_MOVE_FIELD_DOWN = "doMoveFieldDown";
    private static final String ACTION_DO_REMOVE_FIELD = "doRemoveField";

    // Parameters
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ID_FIELD = "id_field";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";
    private static final String PARAMETER_NO_DISPLAY_TITLE = "no_display_title";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String FIELD_TITLE_FIELD = "appointment.labelTitle";
    private static final String FIELD_VALUE_FIELD = "appointment.value.name";

    // Templates
    private static final String TEMPLATE_CREATE_FIELD = "admin/plugins/appointment/create_field.html";
    private static final String TEMPLATE_MODIFY_FIELD_WITH_CONDITIONAL_QUESTION = "admin/plugins/appointment/modify_field_with_conditional_question.html";
    private static final String TEMPLATE_MODIFY_FIELD = "admin/plugins/appointment/modify_field.html";

    /**
     * Gets the field creation page
     * 
     * @param request
     *            The HTTP request
     * @return the field creation page
     */
    @View( VIEW_GET_CREATE_FIELD )
    public String getCreateField( HttpServletRequest request )
    {
        Entry entry = EntryHome.findByPrimaryKey( Integer.parseInt( request.getParameter( PARAMETER_ID_ENTRY ) ) );
        Field field = new Field( );
        field.setParentEntry( entry );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_FIELD, field );

        return getPage( PROPERTY_CREATE_FIELD_TITLE, TEMPLATE_CREATE_FIELD, model );
    }

    /**
     * Get the page to modify a field without displaying its conditional questions
     * 
     * @param request
     *            The request
     * @return The HTML content to display, or the next URL to redirect to
     */
    @View( VIEW_GET_MODIFY_FIELD )
    public String getModifyField( HttpServletRequest request )
    {
        return getModifyField( request, false );
    }

    /**
     * Get the page to modify a field with its conditional questions
     * 
     * @param request
     *            The request
     * @return The HTML content to display, or the next URL to redirect to
     */
    @View( VIEW_GET_MODIFY_FIELD_WITH_CONDITIONAL_QUESTIONS )
    public String getModifyFieldWithConditionalQuestions( HttpServletRequest request )
    {
        return getModifyField( request, true );
    }

    /**
     * Gets the field modification page
     * 
     * @param request
     *            The HTTP request
     * @param bWithConditionalQuestion
     *            true if the field is associate to conditionals questions
     * @return the field modification page
     */
    private String getModifyField( HttpServletRequest request, boolean bWithConditionalQuestion )
    {
        if ( StringUtils.isEmpty( request.getParameter( PARAMETER_ID_FIELD ) ) || !StringUtils.isNumeric( request.getParameter( PARAMETER_ID_FIELD ) ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdField = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELD ) );
        Field field = FieldHome.findByPrimaryKey( nIdField );
        Entry entry = EntryHome.findByPrimaryKey( field.getParentEntry( ).getIdEntry( ) );

        field.setParentEntry( entry );

        HashMap<String, Object> model = new HashMap<>( );
        model.put( MARK_FIELD, field );

        String strTemplateName;

        if ( bWithConditionalQuestion )
        {
            model.put( MARK_ENTRY_TYPE_LIST, EntryTypeService.getInstance( ).getEntryTypeReferenceList( ) );
            model.put( MARK_ENTRY_LIST, field.getConditionalQuestions( ) );
            strTemplateName = TEMPLATE_MODIFY_FIELD_WITH_CONDITIONAL_QUESTION;
        }
        else
        {
            strTemplateName = TEMPLATE_MODIFY_FIELD;
        }

        return getPage( PROPERTY_MODIFY_FIELD_TITLE, strTemplateName, model );
    }

    /**
     * Perform creation field
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_CREATE_FIELD )
    public String doCreateField( HttpServletRequest request )
    {
        if ( StringUtils.isEmpty( request.getParameter( PARAMETER_ID_ENTRY ) ) || !StringUtils.isNumeric( request.getParameter( PARAMETER_ID_ENTRY ) ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdEntry = Integer.parseInt( request.getParameter( PARAMETER_ID_ENTRY ) );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Entry entry = new Entry( );
            entry.setIdEntry( nIdEntry );

            Field field = new Field( );
            field.setParentEntry( entry );

            String strError = getFieldData( request, field );

            if ( strError != null )
            {
                return redirect( request, strError );
            }

            FieldHome.create( field );
        }

        return redirect( request, AppointmentFormEntryJspBean.getURLModifyEntry( request, nIdEntry ) );
    }

    /**
     * Perform modification field
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_MODIFY_FIELD )
    public String doModifyField( HttpServletRequest request )
    {
        return doModifyField( request, false );
    }

    /**
     * Perform modification field
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_MODIFY_FIELD_WITH_CONDITIONAL_QUESTIONS )
    public String doModifyFieldWithConditionalQuestions( HttpServletRequest request )
    {
        return doModifyField( request, true );
    }

    /**
     * Perform modification field
     * 
     * @param request
     *            The HTTP request
     * @param bWithConditionalQuestion
     *            True if the field to modify accepts conditional questions
     * @return The URL to go after performing the action
     */

    // @Action( ACTION_DO_MODIFY_FIELD )
    private String doModifyField( HttpServletRequest request, boolean bWithConditionalQuestion )
    {
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );

        if ( StringUtils.isEmpty( strIdField ) || !StringUtils.isNumeric( strIdField ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        Field field = null;
        int nIdField = Integer.parseInt( strIdField );

        field = FieldHome.findByPrimaryKey( nIdField );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strError = getFieldData( request, field );

            if ( strError != null )
            {
                return redirect( request, strError );
            }

            FieldHome.update( field );
        }

        if ( request.getParameter( PARAMETER_APPLY ) == null )
        {
            return redirect( request, AppointmentFormEntryJspBean.getURLModifyEntry( request, field.getParentEntry( ).getIdEntry( ) ) );
        }

        return redirect( request, bWithConditionalQuestion ? VIEW_GET_MODIFY_FIELD_WITH_CONDITIONAL_QUESTIONS : VIEW_GET_MODIFY_FIELD, PARAMETER_ID_FIELD,
                nIdField );
    }

    /**
     * Gets the confirmation page before deleting a field
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page before deleting a field
     */
    @View( VIEW_GET_CONFIRM_REMOVE_FIELD )
    public String getConfirmRemoveField( HttpServletRequest request )
    {
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );

        if ( StringUtils.isEmpty( strIdField ) || !StringUtils.isNumeric( strIdField ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        UrlItem url = new UrlItem( JSP_URL_MANAGE_APPOINTMENT_FORM_FIELDS );
        url.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_DO_REMOVE_FIELD );
        url.addParameter( PARAMETER_ID_FIELD, strIdField );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FIELD, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Perform the suppression of a field
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_REMOVE_FIELD )
    public String doRemoveField( HttpServletRequest request )
    {
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );

        if ( StringUtils.isEmpty( strIdField ) || !StringUtils.isNumeric( strIdField ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdField = Integer.parseInt( strIdField );

        if ( nIdField != -1 )
        {
            Field field = FieldHome.findByPrimaryKey( nIdField );

            if ( field != null )
            {
                FieldHome.remove( nIdField );

                return redirect( request, AppointmentFormEntryJspBean.getURLModifyEntry( request, field.getParentEntry( ).getIdEntry( ) ) );
            }
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Move a field up
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_FIELD_UP )
    public String doMoveFieldUp( HttpServletRequest request )
    {
        return doMoveField( request, true );
    }

    /**
     * Move a field up
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_FIELD_DOWN )
    public String doMoveFieldDown( HttpServletRequest request )
    {
        return doMoveField( request, false );
    }

    /**
     * Move a field up or down
     * 
     * @param request
     *            The request
     * @param bMoveUp
     *            True to move the field up, false to move it down
     * @return The next URL to redirect to
     */
    public String doMoveField( HttpServletRequest request, boolean bMoveUp )
    {
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );

        if ( StringUtils.isEmpty( strIdField ) || !StringUtils.isNumeric( strIdField ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdField = Integer.parseInt( strIdField );

        List<Field> listField;
        Field field = FieldHome.findByPrimaryKey( nIdField );

        listField = FieldHome.getFieldListByIdEntry( field.getParentEntry( ).getIdEntry( ) );

        int nIndexField = getIndexFieldInFieldList( nIdField, listField );

        int nNewPosition;
        Field fieldToInversePosition;
        fieldToInversePosition = listField.get( bMoveUp ? ( nIndexField - 1 ) : ( nIndexField + 1 ) );
        nNewPosition = fieldToInversePosition.getPosition( );
        fieldToInversePosition.setPosition( field.getPosition( ) );
        field.setPosition( nNewPosition );
        FieldHome.update( field );
        FieldHome.update( fieldToInversePosition );

        return redirect( request, AppointmentFormEntryJspBean.getURLModifyEntry( request, field.getParentEntry( ).getIdEntry( ) ) );
    }

    /**
     * Get the request data and if there is no error insert the data in the field specified in parameter. return null if there is no error or else return the
     * error page URL
     * 
     * @param request
     *            the request
     * @param field
     *            field
     * @return null if there is no error or else return the error page URL
     */
    private String getFieldData( HttpServletRequest request, Field field )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strDefaultValue = request.getParameter( PARAMETER_DEFAULT_VALUE );
        String strNoDisplayTitle = request.getParameter( PARAMETER_NO_DISPLAY_TITLE );
        String strComment = request.getParameter( PARAMETER_COMMENT );

        String strFieldError = null;

        if ( StringUtils.isEmpty( strTitle ) )
        {
            strFieldError = FIELD_TITLE_FIELD;
        }
        else
            if ( StringUtils.isEmpty( strValue ) )
            {
                strFieldError = FIELD_VALUE_FIELD;
            }
            else
                if ( !StringUtil.checkCodeKey( strValue ) )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_FIELD_VALUE_FIELD, AdminMessage.TYPE_STOP );
                }

        if ( strFieldError != null )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        field.setCode( IEntryTypeService.FIELD_ANSWER_CHOICE );
        field.setTitle( strTitle );
        field.setValue( strValue );
        field.setComment( strComment );

        field.setDefaultValue( strDefaultValue != null );
        field.setNoDisplayTitle( strNoDisplayTitle != null );

        return null; // No error
    }

    /**
     * Return the index in the list of the field whose key is specified in parameter
     * 
     * @param nIdField
     *            the key of the field
     * @param listField
     *            the list of field
     * @return the index in the list of the field whose key is specified in parameter
     */
    private static int getIndexFieldInFieldList( int nIdField, List<Field> listField )
    {
        int nIndex = 0;

        for ( Field field : listField )
        {
            if ( field.getIdField( ) == nIdField )
            {
                return nIndex;
            }

            nIndex++;
        }

        return nIndex;
    }

    /**
     * Get the URL to modify a field. The field is assumed to allow conditional questions.
     * 
     * @param request
     *            The request
     * @param nIdField
     *            The id of the field
     * @return The URL of the page to modify the field
     */
    public static String getUrlModifyField( HttpServletRequest request, int nIdField )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_APPOINTMENT_FORM_FIELDS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_GET_MODIFY_FIELD_WITH_CONDITIONAL_QUESTIONS );
        urlItem.addParameter( PARAMETER_ID_FIELD, nIdField );

        return urlItem.getUrl( );
    }
}
