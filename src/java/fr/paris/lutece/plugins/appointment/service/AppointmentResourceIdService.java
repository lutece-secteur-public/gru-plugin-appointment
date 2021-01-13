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
package fr.paris.lutece.plugins.appointment.service;

import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

/**
 *
 * class FormResourceIdService
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentResourceIdService extends ResourceIdService
{
    /** Permission to create appointments */
    public static final String PERMISSION_CREATE_FORM = "CREATE_FORM";

    /** Permission to create appointments of the form */
    public static final String PERMISSION_CREATE_APPOINTMENT = "CREATE_APPOINTMENT";

    /** Permission for deleting a form */
    public static final String PERMISSION_DELETE_FORM = "DELETE_FORM";

    /** Permission for deleting appointments of the form */
    public static final String PERMISSION_DELETE_APPOINTMENT = "DELETE_APPOINTMENT";

    /** Permission for modifying a form */
    public static final String PERMISSION_MODIFY_FORM = "MODIFY_FORM";

    /** Permission for modifying an advanced setting form */
    public static final String PERMISSION_MODIFY_ADVANCED_SETTING_FORM = "MODIFY_ADVANCED_SETTING_FORM";

    /** Permission for modifying appointments of the form */
    public static final String PERMISSION_MODIFY_APPOINTMENT = "MODIFY_APPOINTMENT";

    /** Permission for viewing appointment forms */
    public static final String PERMISSION_VIEW_FORM = "VIEW_FORM";

    /** Permission for viewing appointments */
    public static final String PERMISSION_VIEW_APPOINTMENT = "VIEW_APPOINTMENT";

    /** Permission for changing the state of a form */
    public static final String PERMISSION_CHANGE_STATE = "CHANGE_STATE";

    /** Permission for changing appointment status */
    public static final String PERMISSION_CHANGE_APPOINTMENT_STATUS = "CHANGE_APPOINTMENT_STATUS";

    /** Permission for changing the date of the appointment */
    public static final String PERMISSION_CHANGE_APPOINTMENT_DATE = "CHANGE_APPOINTMENT_DATE";
    /** Permission for taking appointment */
    public static final String PERMISSION_OVERBOOKING_FORM = "OVERBOOKING_FORM";

    // Permission labels
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "appointment.appointment.name";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE_CREATE = "appointment.appointment.name.create";
    private static final String PROPERTY_LABEL_CREATE_FORM = "appointment.permission.label.createForm";
    private static final String PROPERTY_LABEL_CREATE_APPOINTMENT = "appointment.permission.label.createAppointment";
    private static final String PROPERTY_LABEL_DELETE_FORM = "appointment.permission.label.deleteForm";
    private static final String PROPERTY_LABEL_DELETE_APPOINTMENT = "appointment.permission.label.deleteAppointment";
    private static final String PROPERTY_LABEL_MODIFY_FORM = "appointment.permission.label.modifyForm";
    private static final String PROPERTY_LABEL_MODIFY_ADVANCED_SETTINGS_FORM = "appointment.permission.label.modifyAdvancedSettingForm";
    private static final String PROPERTY_LABEL_MODIFY_APPOINTMENT = "appointment.permission.label.modifyAppointment";
    private static final String PROPERTY_LABEL_VIEW_FORM = "appointment.permission.label.viewForm";
    private static final String PROPERTY_LABEL_VIEW_APPOINTMENT = "appointment.permission.label.viewAppointment";
    private static final String PROPERTY_LABEL_CHANGE_STATE = "appointment.permission.label.changeState";
    private static final String PROPERTY_LABEL_CHANGE_APPOINTMENT_STATUS = "appointment.permission.label.changeAppointmentStatus";
    private static final String PROPERTY_LABEL_CHANGE_APPOINTMENT_DATE = "appointment.permission.label.changeAppointmentDate";
    public static final String PROPERTY_LABEL_OVERBOOKING_FORM = "appointment.permission.label.overbooking";

    /** Creates a new instance of DocumentTypeResourceIdService */
    public AppointmentResourceIdService( )
    {
        setPluginName( AppointmentPlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register( )
    {
        ResourceType resourceType = new ResourceType( );
        resourceType.setResourceIdServiceClass( AppointmentResourceIdService.class.getName( ) );
        resourceType.setPluginName( AppointmentPlugin.PLUGIN_NAME );
        resourceType.setResourceTypeKey( AppointmentFormDTO.RESOURCE_TYPE );
        resourceType.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission permission = new Permission( );
        permission.setPermissionKey( PERMISSION_VIEW_FORM );
        permission.setPermissionTitleKey( PROPERTY_LABEL_VIEW_FORM );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_MODIFY_FORM );
        permission.setPermissionTitleKey( PROPERTY_LABEL_MODIFY_FORM );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
        permission.setPermissionTitleKey( PROPERTY_LABEL_MODIFY_ADVANCED_SETTINGS_FORM );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_DELETE_FORM );
        permission.setPermissionTitleKey( PROPERTY_LABEL_DELETE_FORM );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_CHANGE_STATE );
        permission.setPermissionTitleKey( PROPERTY_LABEL_CHANGE_STATE );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_VIEW_APPOINTMENT );
        permission.setPermissionTitleKey( PROPERTY_LABEL_VIEW_APPOINTMENT );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_CREATE_APPOINTMENT );
        permission.setPermissionTitleKey( PROPERTY_LABEL_CREATE_APPOINTMENT );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_MODIFY_APPOINTMENT );
        permission.setPermissionTitleKey( PROPERTY_LABEL_MODIFY_APPOINTMENT );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_DELETE_APPOINTMENT );
        permission.setPermissionTitleKey( PROPERTY_LABEL_DELETE_APPOINTMENT );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_CHANGE_APPOINTMENT_STATUS );
        permission.setPermissionTitleKey( PROPERTY_LABEL_CHANGE_APPOINTMENT_STATUS );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_CHANGE_APPOINTMENT_DATE );
        permission.setPermissionTitleKey( PROPERTY_LABEL_CHANGE_APPOINTMENT_DATE );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_OVERBOOKING_FORM );
        permission.setPermissionTitleKey( PROPERTY_LABEL_OVERBOOKING_FORM );
        resourceType.registerPermission( permission );

        ResourceTypeManager.registerResourceType( resourceType );

        ResourceType resourceTypeCreate = new ResourceType( );
        resourceTypeCreate.setResourceIdServiceClass( AppointmentResourceIdService.class.getName( ) );
        resourceTypeCreate.setPluginName( AppointmentPlugin.PLUGIN_NAME );
        resourceTypeCreate.setResourceTypeKey( AppointmentFormDTO.RESOURCE_TYPE_CREATE );
        resourceTypeCreate.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE_CREATE );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_CREATE_FORM );
        permission.setPermissionTitleKey( PROPERTY_LABEL_CREATE_FORM );
        resourceTypeCreate.registerPermission( permission );

        ResourceTypeManager.registerResourceType( resourceTypeCreate );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        Collection<Form> listForms = FormService.findAllForms( );
        ReferenceList refListForms = new ReferenceList( );
        for ( Form form : listForms )
        {
            refListForms.addItem( form.getIdForm( ), form.getTitle( ) );
        }
        return refListForms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        int nIdForm = -1;
        try
        {
            nIdForm = Integer.parseInt( strId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }
        Form form = FormService.findFormLightByPrimaryKey( nIdForm );
        return ( form == null ) ? StringUtils.EMPTY : form.getTitle( );
    }
}
