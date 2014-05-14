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
package fr.paris.lutece.plugins.appointment.service.upload;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.genericattributes.service.upload.IGAAsyncUploadHandler;
import fr.paris.lutece.plugins.genericattributes.util.JSONUtils;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.filesystem.UploadUtil;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * AppointmentAsynchronousUploadHandler.
 * @see #getFileItems(String, String)
 * @see #removeFileItem(String, String, int)
 *
 */
public class AppointmentAsynchronousUploadHandler implements IGAAsyncUploadHandler
{
    private static final String UPLOAD_SUBMIT_PREFIX = "_appointment_upload_submit_form_";
    private static final String UPLOAD_DELETE_PREFIX = "_appointment_upload_delete_form_";
    private static final String UPLOAD_CHECKBOX_PREFIX = "_appointment_upload_checkbox_form_";
    private static final String PREFIX_ENTRY_ID = IEntryTypeService.PREFIX_ATTRIBUTE + "_";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_FIELD_NAME = "fieldname";
    private static final String PARAMETER_JSESSION_ID = "jsessionid";
    private static final String BEAN_APPOINTMENT_ASYNCHRONOUS_UPLOAD_HANDLER = "appointment.appointmentAsynchronousUploadHandler";

    // PROPERTIES
    private static final String PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_SESSION_LOST = "appointment.message.error.uploading_file.session_lost";

    /** <sessionId,<fieldName,fileItems>> */
    /** contains uploaded file items */
    private static Map<String, Map<String, List<FileItem>>> _mapAsynchronousUpload = new ConcurrentHashMap<String, Map<String, List<FileItem>>>(  );

    /**
     * Get the handler
     * @return the handler
     */
    public static AppointmentAsynchronousUploadHandler getHandler(  )
    {
        return SpringContextService.getBean( BEAN_APPOINTMENT_ASYNCHRONOUS_UPLOAD_HANDLER );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( HttpServletRequest request )
    {
        return AppointmentPlugin.PLUGIN_NAME.equals( request.getParameter( PARAMETER_PAGE ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process( HttpServletRequest request, HttpServletResponse response, JSONObject mainObject,
        List<FileItem> listFileItemsToUpload )
    {
        // prevent 0 or multiple uploads for the same field
        if ( ( listFileItemsToUpload == null ) || listFileItemsToUpload.isEmpty(  ) )
        {
            throw new AppException( "No file uploaded" );
        }

        String strSessionId = request.getParameter( PARAMETER_JSESSION_ID );
        String strIdSession = request.getParameter( PARAMETER_JSESSION_ID );

        if ( StringUtils.isNotBlank( strIdSession ) )
        {
            String strFieldName = request.getParameter( PARAMETER_FIELD_NAME );

            if ( StringUtils.isBlank( strFieldName ) )
            {
                throw new AppException( "id entry is not provided for the current file upload" );
            }

            initMap( strSessionId, strFieldName );

            // find session-related files in the map
            Map<String, List<FileItem>> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );

            List<FileItem> fileItemsSession = mapFileItemsSession.get( strFieldName );

            if ( canUploadFiles( strFieldName, fileItemsSession, listFileItemsToUpload, mainObject,
                        request.getLocale(  ) ) )
            {
                fileItemsSession.addAll( listFileItemsToUpload );

                JSONObject jsonListFileItems = JSONUtils.getUploadedFileJSON( fileItemsSession );
                mainObject.accumulateAll( jsonListFileItems );
                // add entry id to json
                mainObject.element( JSONUtils.JSON_KEY_FIELD_NAME, strFieldName );
            }
        }
        else
        {
            AppLogService.error( AppointmentAsynchronousUploadHandler.class.getName(  ) + " : Session does not exists" );

            String strMessage = I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_SESSION_LOST,
                    request.getLocale(  ) );
            JSONUtils.buildJsonError( mainObject, strMessage );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileItem> getFileItems( String strIdEntry, String strSessionId )
    {
        initMap( strSessionId, buildFieldName( strIdEntry ) );

        if ( StringUtils.isBlank( strIdEntry ) )
        {
            throw new AppException( "id entry is not provided for the current file upload" );
        }

        // find session-related files in the map
        Map<String, List<FileItem>> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );

        return mapFileItemsSession.get( buildFieldName( strIdEntry ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeFileItem( String strIdEntry, String strSessionId, int nIndex )
    {
        // Remove the file (this will also delete the file physically)
        List<FileItem> uploadedFiles = getFileItems( strIdEntry, strSessionId );

        if ( ( uploadedFiles != null ) && !uploadedFiles.isEmpty(  ) && ( uploadedFiles.size(  ) > nIndex ) )
        {
            // Remove the object from the Hashmap
            FileItem fileItem = uploadedFiles.remove( nIndex );
            fileItem.delete(  );
        }
    }

    /**
     * Removes all files associated to the session
     * @param strSessionId the session id
     */
    public synchronized void removeSessionFiles( String strSessionId )
    {
        _mapAsynchronousUpload.remove( strSessionId );
    }

    /**
     * Checks the request parameters to see if an upload submit has been
     * called.
     *
     * @param request the HTTP request
     * @return the name of the upload action, if any. Null otherwise.
     */
    public String getUploadAction( HttpServletRequest request )
    {
        Enumeration<String> enumParamNames = request.getParameterNames(  );

        while ( enumParamNames.hasMoreElements(  ) )
        {
            String paramName = enumParamNames.nextElement(  );

            if ( paramName.startsWith( UPLOAD_SUBMIT_PREFIX ) || paramName.startsWith( UPLOAD_DELETE_PREFIX ) )
            {
                return paramName;
            }
        }

        return null;
    }

    /**
     * Performs an upload action.
     *
     * @param request the HTTP request
     * @param strUploadAction the name of the upload action
     */
    public void doUploadAction( HttpServletRequest request, String strUploadAction )
    {
        // Get the name of the upload field
        String strIdEntry = ( strUploadAction.startsWith( UPLOAD_SUBMIT_PREFIX )
            ? strUploadAction.substring( UPLOAD_SUBMIT_PREFIX.length(  ) )
            : strUploadAction.substring( UPLOAD_DELETE_PREFIX.length(  ) ) );

        String strFieldName = buildFieldName( strIdEntry );

        if ( strUploadAction.startsWith( UPLOAD_SUBMIT_PREFIX ) && request instanceof MultipartHttpServletRequest )
        {
            // A file was submitted
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

            FileItem fileItem = multipartRequest.getFile( strFieldName );

            if ( ( fileItem != null ) && ( fileItem.getSize(  ) > 0 ) )
            {
                addFileItemToUploadedFile( fileItem, strIdEntry, request.getSession(  ) );
            }
        }
        else if ( strUploadAction.startsWith( UPLOAD_DELETE_PREFIX ) )
        {
            HttpSession session = request.getSession( false );

            if ( session != null )
            {
                // Some previously uploaded files were deleted
                // Build the prefix of the associated checkboxes
                String strPrefix = UPLOAD_CHECKBOX_PREFIX + strIdEntry;

                // Look for the checkboxes in the request
                Enumeration<String> enumParamNames = request.getParameterNames(  );
                List<Integer> listIndexes = new ArrayList<Integer>(  );

                while ( enumParamNames.hasMoreElements(  ) )
                {
                    String strParamName = enumParamNames.nextElement(  );

                    if ( strParamName.startsWith( strPrefix ) )
                    {
                        // Get the index from the name of the checkbox
                        listIndexes.add( Integer.parseInt( strParamName.substring( strPrefix.length(  ) ) ) );
                    }
                }

                Collections.sort( listIndexes );
                Collections.reverse( listIndexes );

                for ( int nIndex : listIndexes )
                {
                    removeFileItem( strIdEntry, session.getId(  ), nIndex );
                }
            }
        }
    }

    /**
     * Add file item to the list of uploaded files
     * @param fileItem the file item
     * @param strIdEntry the id entry
     * @param session the session
     */
    public void addFileItemToUploadedFile( FileItem fileItem, String strIdEntry, HttpSession session )
    {
        // This is the name that will be displayed in the form. We keep
        // the original name, but clean it to make it cross-platform.
        String strFileName = UploadUtil.cleanFileName( FileUploadService.getFileNameOnly( fileItem ) );

        // Check if this file has not already been uploaded
        List<FileItem> uploadedFiles = getFileItems( strIdEntry, session.getId(  ) );

        if ( uploadedFiles != null )
        {
            if ( !uploadedFiles.isEmpty(  ) )
            {
                Iterator<FileItem> iterUploadedFiles = uploadedFiles.iterator(  );
                boolean bNew = true;

                while ( bNew && iterUploadedFiles.hasNext(  ) )
                {
                    FileItem uploadedFile = iterUploadedFiles.next(  );
                    String strUploadedFileName = UploadUtil.cleanFileName( FileUploadService.getFileNameOnly( 
                                uploadedFile ) );
                    // If we find a file with the same name and the same
                    // length, we consider that the current file has
                    // already been uploaded
                    bNew = !( strUploadedFileName.equals( strFileName ) &&
                        ( uploadedFile.getSize(  ) == fileItem.getSize(  ) ) );
                }
            }

            uploadedFiles.add( fileItem );
        }
    }

    /**
     * Build the field name from a given id entry
     * i.e. : form_1
     * @param strIdEntry the id entry
     * @return the field name
     */
    public String buildFieldName( String strIdEntry )
    {
        return PREFIX_ENTRY_ID + strIdEntry;
    }

    /**
     * Check if the file can be uploaded or not.
     * This method will check the size of each file and the number max of files
     * that can be uploaded.
     * @param strFieldName the field name
     * @param listUploadedFileItems the list of uploaded files
     * @param listFileItemsToUpload the list of files to upload
     * @param mainObject the JSON object to complete if there is an error
     * @param locale the locale
     * @return true if the list of files can be uploaded, false otherwise
     * @category CALLED_BY_JS (directoryupload.js)
     */
    private boolean canUploadFiles( String strFieldName, List<FileItem> listUploadedFileItems,
        List<FileItem> listFileItemsToUpload, JSONObject mainObject, Locale locale )
    {
        if ( StringUtils.isNotBlank( strFieldName ) && ( strFieldName.length(  ) > PREFIX_ENTRY_ID.length(  ) ) )
        {
            String strIdEntry = strFieldName.substring( PREFIX_ENTRY_ID.length(  ) );

            if ( StringUtils.isEmpty( strIdEntry ) || !StringUtils.isNumeric( strIdEntry ) )
            {
                return false;
            }

            int nIdEntry = Integer.parseInt( strIdEntry );
            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            if ( entry != null )
            {
                GenericAttributeError error = EntryTypeServiceManager.getEntryTypeService( entry )
                                                                     .canUploadFiles( entry, listUploadedFileItems,
                        listFileItemsToUpload, locale );

                if ( error != null )
                {
                    JSONUtils.buildJsonError( mainObject, error.getErrorMessage(  ) );

                    return false;
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Init the map
     * @param strSessionId the session id
     * @param strFieldName the field name
     */
    private void initMap( String strSessionId, String strFieldName )
    {
        // find session-related files in the map
        Map<String, List<FileItem>> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );

        // create map if not exists
        if ( mapFileItemsSession == null )
        {
            synchronized ( strSessionId )
            {
                mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );

                if ( mapFileItemsSession == null )
                {
                    mapFileItemsSession = new ConcurrentHashMap<String, List<FileItem>>(  );
                    _mapAsynchronousUpload.put( strSessionId, mapFileItemsSession );
                }
            }
        }

        List<FileItem> listFileItems = mapFileItemsSession.get( strFieldName );

        if ( listFileItems == null )
        {
            listFileItems = new ArrayList<FileItem>(  );
            mapFileItemsSession.put( strFieldName, listFileItems );
        }
    }
}
