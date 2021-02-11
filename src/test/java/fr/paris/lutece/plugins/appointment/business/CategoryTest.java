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
package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.plugins.appointment.business.category.Category;
import fr.paris.lutece.plugins.appointment.business.category.CategoryHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Display
 *
 * @author Laurent Payen
 *
 */
public final class CategoryTest extends LuteceTestCase
{

    public static final String LABEL_1 = "Catégorie 1";
    public static final String LABEL_2 = "Catégorie 2";

    /**
     * Test method for the Category (CRUD)
     */
    public void testCategory( )
    {
        // Initialize a Category
        Category category = buildCategory( LABEL_1 );
        // Create the Display in database
        CategoryHome.create( category );
        // Find the Category created in database
        Category categoryStored = CategoryHome.findByPrimaryKey( category.getIdCategory( ) );
        // Check Asserts
        checkAsserts( categoryStored, category );

        // Update the Display
        category.setLabel( LABEL_2 );
        // Update the Display in database
        CategoryHome.update( category );
        // Find the category updated in database
        categoryStored = CategoryHome.findByPrimaryKey( category.getIdCategory( ) );
        // Check Asserts
        checkAsserts( categoryStored, category );

        // Delete the Category
        CategoryHome.delete( category.getIdCategory( ) );
        categoryStored = CategoryHome.findByPrimaryKey( category.getIdCategory( ) );
        // Check the Category has been removed from database
        assertNull( categoryStored );

    }

    /**
     * Build a Category Business Object
     *
     * @return the category
     */
    public static Category buildCategory( String strLabel )
    {
        Category category = new Category( );
        category.setLabel( strLabel );
        return category;
    }

    /**
     * Check that all the asserts are true
     *
     * @param categoryStored
     *            the Category stored
     * @param category
     *            the Category created
     */
    public void checkAsserts( Category categoryStored, Category category )
    {
        assertEquals( categoryStored.getLabel( ), category.getLabel( ) );
    }
}