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

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.category.Category;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.service.CategoryService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage AppointmentForm features ( manage, create, modify, copy, remove )
 * 
 * @author L.Payen
 * 
 */
@Controller( controllerJsp = "ManageAppointmentCategory.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentCategoryJspBean.RIGHT_MANAGECATEGORY )
public class AppointmentCategoryJspBean extends AbstractAppointmentFormAndSlotJspBean
{
    private static final long serialVersionUID = 5438468406405679511L;

    /**
     * Right to manage appointment category
     */
    public static final String RIGHT_MANAGECATEGORY = "APPOINTMENT_CATEGORY_MANAGEMENT";
    private static final String JSP_MANAGE_CATEGORY = "jsp/admin/plugins/appointment/ManageAppointmentCategory.jsp";

    // templates
    private static final String TEMPLATE_MANAGE_CATEGORY = "/admin/plugins/appointment/category/manage_category.html";
    private static final String TEMPLATE_CREATE_CATEGORY = "/admin/plugins/appointment/category/create_category.html";
    private static final String TEMPLATE_MODIFY_CATEGORY = "/admin/plugins/appointment/category/modify_category.html";

    // Parameters
    private static final String PARAMETER_ID_CATEGORY = "id_category";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CATEGORY = "appointment.adminFeature.manageCategories.name";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CATEGORY = "appointment.create.category.title";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CATEGORY = "appointment.modify.category.title";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.category.attribute.";

    // Markers
    private static final String MARK_CATEGORY = "category";
    private static final String MARK_LIST_CATEGORY = "category_list";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORY = "appointment.message.confirmRemoveCategory";
    private static final String MESSAGE_ERROR_REMOVE_CATEGORY = "appointment.message.categoryIsAffected.errorRemoveCategory";

    // Views
    private static final String VIEW_MANAGE_CATEGORY = "manageCategory";
    private static final String VIEW_CREATE_CATEGORY = "createCategory";
    private static final String VIEW_MODIFY_CATEGORY = "modifyCategory";

    // Actions
    private static final String ACTION_CONFIRM_REMOVE_CATEGORY = "confirmRemoveCategory";
    private static final String ACTION_CREATE_CATEGORY = "createCategory";
    private static final String ACTION_MODIFY_CATEGORY = "modifyCategory";
    private static final String ACTION_REMOVE_CATEGORY = "removeCategory";

    // Infos
    private static final String INFO_CATEGORY_CREATED = "appointment.info.category.created";
    private static final String INFO_CATEGORY_UPDATED = "appointment.info.category.updated";
    private static final String INFO_CATEGORY_REMOVED = "appointment.info.category.removed";

    // Session variables
    private Category _category;

    /**
     * Get the page to manage appointment categories
     * 
     * @param request
     *            the request
     * @return The HTML content to display
     */
    @View( value = VIEW_MANAGE_CATEGORY, defaultView = true )
    public String getManageCategory( HttpServletRequest request )
    {
        _category = null;
        List<Category> listCategory = CategoryService.findAllCategories( );
        Map<String, Object> model = getPaginatedListModel( request, MARK_LIST_CATEGORY, listCategory, JSP_MANAGE_CATEGORY );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_CATEGORY, TEMPLATE_MANAGE_CATEGORY, model );
    }

    /**
     * Display a popup to ask the user if he really wants to delete the category he selected
     * 
     * @param request
     *            the request
     * @return the HTML code to confirm
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @Action( ACTION_CONFIRM_REMOVE_CATEGORY )
    public String getConfirmRemoveCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        if ( StringUtils.isEmpty( strIdCategory ) )
        {
            return redirectView( request, VIEW_MANAGE_CATEGORY );
        }
        int nIdCategory = Integer.parseInt( strIdCategory );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CATEGORY ) );
        url.addParameter( PARAMETER_ID_CATEGORY, nIdCategory );
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CATEGORY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
        return redirect( request, strMessageUrl );
    }

    /**
     * Remove the category selected
     * 
     * @param request
     *            the request
     * @return The JSP URL of the process result
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @Action( ACTION_REMOVE_CATEGORY )
    public String doRemoveCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        if ( CollectionUtils.isNotEmpty( FormHome.findByCategory( Integer.parseInt( strIdCategory ) ) ) )
        {
            addError( MESSAGE_ERROR_REMOVE_CATEGORY, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_CATEGORY );
        }
        int nIdCategory = Integer.parseInt( strIdCategory );
        CategoryService.removeCategory( nIdCategory );
        addInfo( INFO_CATEGORY_REMOVED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_CATEGORY );
    }

    /**
     * Display the screen to create a new category
     * 
     * @param request
     *            the request
     * @return The HTML content to display
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @View( VIEW_CREATE_CATEGORY )
    public String getCreateCategory( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );
        _category = ( _category == null ) ? new Category( ) : _category;
        model.put( MARK_CATEGORY, _category );
        return getPage( PROPERTY_PAGE_TITLE_CREATE_CATEGORY, TEMPLATE_CREATE_CATEGORY, model );
    }

    /**
     * Create a new category with the fields completed
     * 
     * @param request
     *            the request
     * @return The JSP URL of the process result
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @Action( ACTION_CREATE_CATEGORY )
    public String doCreateCategory( HttpServletRequest request )
    {
        populate( _category, request );
        // Check constraints
        if ( !validateBean( _category, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CATEGORY );
        }

        CategoryService.saveCategory( _category );
        addInfo( INFO_CATEGORY_CREATED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_CATEGORY );
    }

    /**
     * Get the view to modify an existed category
     * 
     * @param request
     *            the request
     * @return The HTML content to display
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @View( VIEW_MODIFY_CATEGORY )
    public String getModifyCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = Integer.parseInt( strIdCategory );
        _category = CategoryService.findCategoryById( nIdCategory );
        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORY, _category );
        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CATEGORY, TEMPLATE_MODIFY_CATEGORY, model );
    }

    /**
     * Modify a category
     * 
     * @param request
     *            the request
     * @return The JSP URL of the process result
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @Action( ACTION_MODIFY_CATEGORY )
    public String doModifyCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = Integer.parseInt( strIdCategory );
        _category = ( _category == null || _category.getIdCategory( ) != nIdCategory ) ? new Category( ) : _category;
        _category.setIdCategory( nIdCategory );
        populate( _category, request );
        // Check constraints
        if ( !validateBean( _category, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_MODIFY_CATEGORY );
        }
        CategoryService.updateCategory( _category );
        addInfo( INFO_CATEGORY_UPDATED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_CATEGORY );
    }
}
