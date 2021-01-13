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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.RemovalListenerService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Service to manage entries
 * 
 * @author Laurent Payen
 *
 */
public final class EntryService extends RemovalListenerService implements Serializable
{
    /**
     * Name of the bean of this service
     */
    public static final String BEAN_NAME = "appointment.entryService";
    private static final long serialVersionUID = -5378918040356139703L;

    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";
    private static final String MARK_GROUP_ENTRY_LIST = "entry_group_list";
    private static final String MARK_LIST_ORDER_FIRST_LEVEL = "listOrderFirstLevel";
    private static final String MARK_STR_LIST_CHILDREN = "str_list_entry_children";
    private static final String MARK_FIELD = "field";
    private static final String MARK_LIST_RESPONSES = "list_responses";
    private static final String MARK_UPLOAD_HANDLER = "uploadHandler";

    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PREFIX_ATTRIBUTE = "attribute";

    // Templates
    private static final String TEMPLATE_DIV_CONDITIONAL_ENTRY = "skin/plugins/appointment/html_code_div_conditional_entry.html";

    /**
     * Get an instance of the service
     * 
     * @return An instance of the service
     */
    public static EntryService getService( )
    {
        return SpringContextService.getBean( BEAN_NAME );
    }

    /**
     * Build an entry filter with static parameter
     * 
     * @param nIdForm
     *            the Form Id
     * @return the entry filter
     */
    public static EntryFilter buildEntryFilter( int nIdForm )
    {
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( nIdForm );
        filter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );
        filter.setIsOnlyDisplayInBack( EntryFilter.FILTER_FALSE );
        return filter;
    }

    /**
     * Change the attribute's order to a greater one (move down in the list)
     * 
     * @param nOrderToSet
     *            the new order for the attribute
     * @param entryToChangeOrder
     *            the attribute which will change
     */
    public void moveDownEntryOrder( int nOrderToSet, Entry entryToChangeOrder )
    {
        if ( entryToChangeOrder.getParent( ) == null )
        {
            int nNbChild = 0;
            int nNewOrder = 0;

            EntryFilter filter = new EntryFilter( );
            filter.setIdResource( entryToChangeOrder.getIdResource( ) );
            filter.setResourceType( Form.RESOURCE_TYPE );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<Entry> listEntryFirstLevel = EntryHome.findEntriesWithoutParent( entryToChangeOrder.getIdResource( ), entryToChangeOrder.getResourceType( ) );

            List<Integer> orderFirstLevel = new ArrayList<>( );
            initOrderFirstLevel( listEntryFirstLevel, orderFirstLevel );

            Integer nbChildEntryToChangeOrder = 0;

            if ( entryToChangeOrder.getChildren( ) != null )
            {
                nbChildEntryToChangeOrder = entryToChangeOrder.getChildren( ).size( );
            }

            for ( Entry entry : listEntryFirstLevel )
            {
                for ( int i = 0; i < orderFirstLevel.size( ); i++ )
                {
                    if ( ( orderFirstLevel.get( i ).equals( Integer.valueOf( entry.getPosition( ) ) ) )
                            && ( entry.getPosition( ) > entryToChangeOrder.getPosition( ) ) && ( entry.getPosition( ) <= nOrderToSet ) )
                    {
                        if ( nNbChild == 0 )
                        {
                            nNewOrder = orderFirstLevel.get( i - 1 );

                            if ( !orderFirstLevel.get( i - 1 ).equals( Integer.valueOf( entryToChangeOrder.getPosition( ) ) ) )
                            {
                                nNewOrder -= nbChildEntryToChangeOrder;
                            }
                        }
                        else
                        {
                            nNewOrder += ( nNbChild + 1 );
                        }

                        entry.setPosition( nNewOrder );
                        EntryHome.update( entry );
                        nNbChild = 0;

                        if ( entry.getChildren( ) != null )
                        {
                            for ( Entry child : entry.getChildren( ) )
                            {
                                nNbChild++;
                                child.setPosition( nNewOrder + nNbChild );
                                EntryHome.update( child );
                            }
                        }
                    }
                }
            }

            entryToChangeOrder.setPosition( nNewOrder + nNbChild + 1 );
            EntryHome.update( entryToChangeOrder );
            nNbChild = 0;

            for ( Entry child : entryToChangeOrder.getChildren( ) )
            {
                nNbChild++;
                child.setPosition( entryToChangeOrder.getPosition( ) + nNbChild );
                EntryHome.update( child );
            }
        }
        else
        {
            EntryFilter filter = new EntryFilter( );
            filter.setIdResource( entryToChangeOrder.getIdResource( ) );
            filter.setResourceType( Form.RESOURCE_TYPE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<Entry> listAllEntry = EntryHome.getEntryList( filter );

            for ( Entry entry : listAllEntry )
            {
                if ( ( entry.getPosition( ) > entryToChangeOrder.getPosition( ) ) && ( entry.getPosition( ) <= nOrderToSet ) )
                {
                    entry.setPosition( entry.getPosition( ) - 1 );
                    EntryHome.update( entry );
                }
            }

            entryToChangeOrder.setPosition( nOrderToSet );
            EntryHome.update( entryToChangeOrder );
        }
    }

    /**
     * Change the attribute's order to a lower one (move up in the list)
     * 
     * @param nOrderToSet
     *            the new order for the attribute
     * @param entryToChangeOrder
     *            the attribute which will change
     */
    public void moveUpEntryOrder( int nOrderToSet, Entry entryToChangeOrder )
    {
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( entryToChangeOrder.getIdResource( ) );
        filter.setResourceType( Form.RESOURCE_TYPE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        if ( entryToChangeOrder.getParent( ) == null )
        {
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );

            List<Integer> orderFirstLevel = new ArrayList<>( );

            int nNbChild = 0;
            int nNewOrder = nOrderToSet;
            int nEntryToMoveOrder = entryToChangeOrder.getPosition( );

            List<Entry> listEntryFirstLevel = EntryHome.findEntriesWithoutParent( entryToChangeOrder.getIdResource( ), entryToChangeOrder.getResourceType( ) );
            // the list of all the orders in the first level
            initOrderFirstLevel( listEntryFirstLevel, orderFirstLevel );

            for ( Entry entry : listEntryFirstLevel )
            {
                Integer entryInitialPosition = entry.getPosition( );

                for ( int i = 0; i < orderFirstLevel.size( ); i++ )
                {
                    if ( ( orderFirstLevel.get( i ).equals( entryInitialPosition ) ) && ( entryInitialPosition < nEntryToMoveOrder )
                            && ( entryInitialPosition >= nOrderToSet ) )
                    {
                        if ( entryToChangeOrder.getPosition( ) == nEntryToMoveOrder )
                        {
                            entryToChangeOrder.setPosition( nNewOrder );
                            EntryHome.update( entryToChangeOrder );

                            for ( Entry child : entryToChangeOrder.getChildren( ) )
                            {
                                nNbChild++;
                                child.setPosition( entryToChangeOrder.getPosition( ) + nNbChild );
                                EntryHome.update( child );
                            }
                        }

                        nNewOrder = nNewOrder + nNbChild + 1;
                        entry.setPosition( nNewOrder );
                        EntryHome.update( entry );
                        nNbChild = 0;

                        for ( Entry child : entry.getChildren( ) )
                        {
                            nNbChild++;
                            child.setPosition( nNewOrder + nNbChild );
                            EntryHome.update( child );
                        }
                    }
                }
            }
        }
        else
        {
            List<Entry> listAllEntry = EntryHome.getEntryList( filter );

            for ( Entry entry : listAllEntry )
            {
                if ( ( entry.getPosition( ) < entryToChangeOrder.getPosition( ) ) && ( entry.getPosition( ) >= nOrderToSet ) )
                {
                    entry.setPosition( entry.getPosition( ) + 1 );
                    EntryHome.update( entry );
                }
            }

            entryToChangeOrder.setPosition( nOrderToSet );
            EntryHome.update( entryToChangeOrder );
        }
    }

    /**
     * Move EntryToMove into entryGroup
     * 
     * @param entryToMove
     *            the entry which will be moved
     * @param entryGroup
     *            the entry group
     */
    public void moveEntryIntoGroup( Entry entryToMove, Entry entryGroup )
    {
        if ( ( entryToMove != null ) && ( entryGroup != null ) )
        {
            // If the entry already has a parent, we must remove it before
            // adding it to a new one
            if ( entryToMove.getParent( ) != null )
            {
                moveOutEntryFromGroup( entryToMove );
            }

            int nPosition;

            if ( entryToMove.getPosition( ) < entryGroup.getPosition( ) )
            {
                nPosition = entryGroup.getPosition( );
                moveDownEntryOrder( nPosition, entryToMove );
            }
            else
            {
                nPosition = entryGroup.getPosition( ) + entryGroup.getChildren( ).size( ) + 1;
                moveUpEntryOrder( nPosition, entryToMove );
            }

            entryToMove.setParent( entryGroup );
            EntryHome.update( entryToMove );
        }
    }

    /**
     * Remove an entry from a group
     * 
     * @param entryToMove
     *            the entry to remove from a group
     */
    public void moveOutEntryFromGroup( Entry entryToMove )
    {
        Entry parent = EntryHome.findByPrimaryKey( entryToMove.getParent( ).getIdEntry( ) );

        // The new position of the entry is the position of the group plus the
        // number of entries in the group (including this entry)
        moveDownEntryOrder( parent.getPosition( ) + parent.getChildren( ).size( ), entryToMove );
        entryToMove.setParent( null );
        EntryHome.update( entryToMove );
    }

    /**
     * Init the list of the attribute's orders (first level only)
     * 
     * @param listEntryFirstLevel
     *            the list of all the attributes of the first level
     * @param orderFirstLevel
     *            the list to set
     */
    private void initOrderFirstLevel( List<Entry> listEntryFirstLevel, List<Integer> orderFirstLevel )
    {
        for ( Entry entry : listEntryFirstLevel )
        {
            orderFirstLevel.add( entry.getPosition( ) );
        }
    }

    /**
     * Remove every entries associated with a given appointment form
     * 
     * @param nIdForm
     *            The id of the appointment to remove entries of
     */
    public void removeEntriesByIdAppointmentForm( int nIdForm )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( nIdForm );
        entryFilter.setResourceType( Form.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

        for ( Entry entry : listEntry )
        {
            EntryHome.remove( entry.getIdEntry( ) );
        }
    }

    /**
     * Add the entries to the model
     * 
     * @param nIdForm
     *            The form Id
     * @param model
     *            the model
     */
    public static void addListEntryToModel( int nIdForm, Map<String, Object> model )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( nIdForm );
        entryFilter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( entryFilter );
        List<Entry> listEntry = new ArrayList<>( listEntryFirstLevel.size( ) );
        List<Integer> listOrderFirstLevel = new ArrayList<>( listEntryFirstLevel.size( ) );
        for ( Entry entry : listEntryFirstLevel )
        {
            listEntry.add( entry );
            listOrderFirstLevel.add( listEntry.size( ) );
            if ( Boolean.TRUE.equals( entry.getEntryType( ).getGroup( ) ) )
            {
                entryFilter = new EntryFilter( );
                entryFilter.setIdResource( nIdForm );
                entryFilter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
                entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
                entryFilter.setIdEntryParent( entry.getIdEntry( ) );
                List<Entry> listEntryGroup = EntryHome.getEntryList( entryFilter );
                entry.setChildren( listEntryGroup );
                listEntry.addAll( listEntryGroup );
            }
        }
        model.put( MARK_GROUP_ENTRY_LIST, getRefListGroups( nIdForm ) );
        model.put( MARK_ENTRY_TYPE_LIST, EntryTypeService.getInstance( ).getEntryTypeReferenceList( ) );
        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_LIST_ORDER_FIRST_LEVEL, listOrderFirstLevel );
    }

    /**
     * Find all the entries (with its fields) of a form
     * 
     * @param nIdForm
     *            the form Id
     * @return a list of all the entries
     */
    public static List<Entry> findListEntry( int nIdForm )
    {
        List<Entry> listEntries = new ArrayList<>( );
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( nIdForm );
        entryFilter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        List<Entry> listEntriesLight = EntryHome.getEntryList( entryFilter );
        if ( CollectionUtils.isNotEmpty( listEntriesLight ) )
        {
            for ( Entry entryLight : listEntriesLight )
            {
                listEntries.add( EntryHome.findByPrimaryKey( entryLight.getIdEntry( ) ) );
            }
        }
        return listEntries;
    }

    /**
     * Get the reference list of groups
     * 
     * @param nIdForm
     *            the id of the appointment form
     * @return The reference list of groups of the given form
     */
    private static ReferenceList getRefListGroups( int nIdForm )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( nIdForm );
        entryFilter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entryFilter.setIdIsGroup( 1 );
        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );
        ReferenceList refListGroups = new ReferenceList( );
        for ( Entry entry : listEntry )
        {
            refListGroups.addItem( entry.getIdEntry( ), entry.getTitle( ) );
        }
        return refListGroups;
    }

    /**
     * Get the html part of the additional entry of the form
     * 
     * @param nIdEntry
     *            the entry id
     * @param stringBuffer
     *            the string buffer
     * @param locale
     * @param bDisplayFront
     * @param request
     */
    public static void getHtmlEntry( Map<String, Object> model, int nIdEntry, StringBuilder stringBuffer, Locale locale, boolean bDisplayFront,
            AppointmentDTO appointmentDTO )
    {
        StringBuilder strConditionalQuestionStringBuffer = null;
        HtmlTemplate template;
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );
        if ( Boolean.TRUE.equals( entry.getEntryType( ).getGroup( ) ) )
        {
            StringBuilder strGroupStringBuffer = new StringBuilder( );
            for ( Entry entryChild : entry.getChildren( ) )
            {
                getHtmlEntry( model, entryChild.getIdEntry( ), strGroupStringBuffer, locale, bDisplayFront, appointmentDTO );
            }
            model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString( ) );
        }
        else
        {
            if ( entry.getNumberConditionalQuestion( ) != 0 )
            {
                for ( Field field : entry.getFields( ) )
                {
                    field.setConditionalQuestions( FieldHome.findByPrimaryKey( field.getIdField( ) ).getConditionalQuestions( ) );
                }
            }
        }
        if ( entry.getNumberConditionalQuestion( ) != 0 )
        {
            strConditionalQuestionStringBuffer = new StringBuilder( );
            for ( Field field : entry.getFields( ) )
            {
                if ( CollectionUtils.isNotEmpty( field.getConditionalQuestions( ) ) )
                {
                    StringBuilder strGroupStringBuffer = new StringBuilder( );
                    for ( Entry entryConditional : field.getConditionalQuestions( ) )
                    {
                        getHtmlEntry( model, entryConditional.getIdEntry( ), strGroupStringBuffer, locale, bDisplayFront, appointmentDTO );
                    }
                    model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString( ) );
                    model.put( MARK_FIELD, field );
                    template = AppTemplateService.getTemplate( TEMPLATE_DIV_CONDITIONAL_ENTRY, locale, model );
                    strConditionalQuestionStringBuffer.append( template.getHtml( ) );
                }
            }
            model.put( MARK_STR_LIST_CHILDREN, strConditionalQuestionStringBuffer.toString( ) );
        }
        model.put( MARK_ENTRY, entry );
        model.put( MARK_LOCALE, locale );
        if ( ( appointmentDTO != null ) && ( appointmentDTO.getMapResponsesByIdEntry( ) != null ) )
        {
            List<Response> listResponses = appointmentDTO.getMapResponsesByIdEntry( ).get( entry.getIdEntry( ) );
            model.put( MARK_LIST_RESPONSES, listResponses );

        }
        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );
        // If the entry type is a file, we add the
        if ( entryTypeService instanceof AbstractEntryTypeUpload )
        {
            model.put( MARK_UPLOAD_HANDLER, ( (AbstractEntryTypeUpload) entryTypeService ).getAsynchronousUploadHandler( ) );
        }
        template = AppTemplateService.getTemplate( entryTypeService.getTemplateHtmlForm( entry, bDisplayFront ), locale, model );
        stringBuffer.append( template.getHtml( ) );
    }

    /**
     * Add to the map of the appointment the response of the additional entry of the form
     * 
     * @param request
     *            the Request
     * @param nIdEntry
     *            the Entry Id
     * @param locale
     *            the Locale
     * @param appointment
     *            the Appointment
     * @return the list of possible errors
     */
    public static List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry, Locale locale, AppointmentDTO appointment )
    {
        List<Response> listResponse = new ArrayList<>( );
        appointment.getMapResponsesByIdEntry( ).put( nIdEntry, listResponse );

        return getResponseEntry( request, nIdEntry, listResponse, false, locale, appointment );
    }

    /**
     * Add to the map of the appointment the response of the additional entry of the form
     * 
     * @param request
     *            the request
     * @param nIdEntry
     *            the entry id
     * @param listResponse
     *            the list of the responses
     * @param bResponseNull
     *            true if the response can be null
     * @param locale
     *            the local
     * @param appointment
     *            the appointment
     * @return a list of possible errors
     */
    private static List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry, List<Response> listResponse, boolean bResponseNull,
            Locale locale, AppointmentDTO appointment )
    {
        List<GenericAttributeError> listFormErrors = new ArrayList<>( );
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        List<Field> listField = new ArrayList<>( );

        for ( Field field : entry.getFields( ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField( ) );
            listField.add( field );
        }

        entry.setFields( listField );

        if ( Boolean.TRUE.equals( entry.getEntryType( ).getGroup( ) ) )
        {
            for ( Entry entryChild : entry.getChildren( ) )
            {
                List<Response> listResponseChild = new ArrayList<>( );
                appointment.getMapResponsesByIdEntry( ).put( entryChild.getIdEntry( ), listResponseChild );

                listFormErrors.addAll( getResponseEntry( request, entryChild.getIdEntry( ), listResponseChild, false, locale, appointment ) );
            }
        }
        else
            if ( !Boolean.TRUE.equals( entry.getEntryType( ).getComment( ) ) )
            {
                GenericAttributeError formError = null;

                if ( !bResponseNull )
                {
                    formError = EntryTypeServiceManager.getEntryTypeService( entry ).getResponseData( entry, request, listResponse, locale );

                    if ( formError != null )
                    {
                        formError.setUrl( getEntryUrl( entry ) );
                    }
                }
                else
                {
                    Response response = new Response( );
                    response.setEntry( entry );
                    listResponse.add( response );
                }

                if ( formError != null )
                {
                    entry.setError( formError );
                    listFormErrors.add( formError );
                }

                if ( entry.getNumberConditionalQuestion( ) != 0 )
                {
                    for ( Field field : entry.getFields( ) )
                    {
                        boolean bIsFieldInResponseList = isFieldInTheResponseList( field.getIdField( ), listResponse );

                        for ( Entry conditionalEntry : field.getConditionalQuestions( ) )
                        {
                            List<Response> listResponseChild = new ArrayList<>( );
                            appointment.getMapResponsesByIdEntry( ).put( conditionalEntry.getIdEntry( ), listResponseChild );

                            listFormErrors.addAll( getResponseEntry( request, conditionalEntry.getIdEntry( ), listResponseChild, !bIsFieldInResponseList,
                                    locale, appointment ) );
                        }
                    }
                }
            }

        return listFormErrors;
    }

    /**
     * Tell if the id of the field given is in the response list
     * 
     * @param nIdField
     *            the id of the field
     * @param listResponse
     *            the list to search in
     * @return true if the id is in the list
     */
    public static Boolean isFieldInTheResponseList( int nIdField, List<Response> listResponse )
    {
        for ( Response response : listResponse )
        {
            if ( ( response.getField( ) != null ) && ( response.getField( ).getIdField( ) == nIdField ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Add parameters to the url with the entry given
     * 
     * @param entry
     *            the entry
     * @return the url
     */
    public static String getEntryUrl( Entry entry)
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl( ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, AppointmentPlugin.PLUGIN_NAME );
        url.addParameter( MVCUtils.PARAMETER_VIEW, AppointmentApp.VIEW_APPOINTMENT_FORM );

        if ( ( entry != null ) && ( entry.getIdResource( ) > 0 ) )
        {
            url.addParameter( PARAMETER_ID_FORM, entry.getIdResource( ) );
            url.setAnchor( PREFIX_ATTRIBUTE + entry.getIdEntry( ) );
        }

        return url.getUrl( );
    }

    /**
     * Get the list of entries filtered
     * 
     * @param iform
     *            the form id
     * @param bDisplayFront
     *            true if it is displayed on FO
     * @return the list of entries
     */
    public static List<Entry> getFilter( int iform, boolean bDisplayFront )
    {
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( iform );
        filter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        if ( bDisplayFront )
        {
            filter.setIsOnlyDisplayInBack( EntryFilter.FILTER_FALSE );
        }
        return EntryHome.getEntryList( filter );
    }

}
