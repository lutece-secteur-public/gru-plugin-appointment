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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.category.Category;
import fr.paris.lutece.plugins.appointment.business.category.CategoryHome;
import fr.paris.lutece.util.ReferenceList;

/**
 * Service class for the category
 * 
 * @author Laurent Payen
 *
 */
public final class CategoryService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private CategoryService( )
    {
    }

    /**
     * Find all the categories
     * 
     * @return a list of all the categories
     */
    public static List<Category> findAllCategories( )
    {
        return CategoryHome.findAllCategories( );
    }

    /**
     * Remove a category
     * 
     * @param nIdCategory
     *            the id of the category to remove
     */
    public static void removeCategory( int nIdCategory )
    {
        CategoryHome.delete( nIdCategory );
    }

    /**
     * Create a category
     * 
     * @param category
     *            the category to store
     */
    public static Category saveCategory( Category category )
    {
        Category categoryInDb = CategoryHome.findByLabel( category.getLabel( ) );
        if ( categoryInDb == null )
        {
            categoryInDb = CategoryHome.create( category );
        }
        return categoryInDb;
    }

    /**
     * Find a category by its primary key
     * 
     * @param nIdCategory
     *            the id of the category
     * @return the category
     */
    public static Category findCategoryById( int nIdCategory )
    {
        return CategoryHome.findByPrimaryKey( nIdCategory );
    }

    /**
     * Update a category
     * 
     * @param category
     *            the category to update
     */
    public static void updateCategory( Category category )
    {
        CategoryHome.update( category );
    }

    /**
     * Build a list of all the category (id, label) + an empty line for the form creation
     * 
     * @return the reference list
     */
    public static ReferenceList findAllInReferenceList( )
    {
        List<Category> listCategory = findAllCategories( );
        ReferenceList refListTemplates = new ReferenceList( listCategory.size( ) + 1 );
        refListTemplates.addItem( -1, StringUtils.EMPTY );
        for ( Category category : listCategory )
        {
            refListTemplates.addItem( category.getIdCategory( ), category.getLabel( ) );
        }
        return refListTemplates;
    }

}
