/*
 * Copyright (c) 2002-2025, City of Paris
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.CategoryTest;
import fr.paris.lutece.plugins.appointment.business.category.Category;
import fr.paris.lutece.plugins.appointment.business.category.CategoryHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * The labels of the categories are unique: a creation or a modification with the label of another category is refused, without error
 */
public class CategoryServiceLabelTest extends LuteceTestCase
{
    private static final String LABEL_A = "bh category A";
    private static final String LABEL_B = "bh category B";

    private Category _categoryA;
    private Category _categoryB;

    @BeforeEach
    public void createCategories( )
    {
        _categoryA = CategoryHome.create( CategoryTest.buildCategory( LABEL_A ) );
        _categoryB = CategoryTest.buildCategory( LABEL_B );
        _categoryB.setNbMaxAppointmentsPerUser( 2 );
        _categoryB = CategoryHome.create( _categoryB );
    }

    @AfterEach
    public void removeCategories( )
    {
        CategoryHome.delete( _categoryA.getIdCategory( ) );
        CategoryHome.delete( _categoryB.getIdCategory( ) );
    }

    @Test
    public void testCreateWithTheLabelOfAnotherCategory( )
    {
        Category category = CategoryTest.buildCategory( LABEL_B );
        category.setNbMaxAppointmentsPerUser( 9 );

        assertNull( CategoryService.saveCategory( category ) );
        assertEquals( 2, CategoryHome.findByLabel( LABEL_B ).getNbMaxAppointmentsPerUser( ) );
    }

    @Test
    public void testModifyWithTheLabelOfAnotherCategory( )
    {
        Category category = CategoryHome.findByPrimaryKey( _categoryA.getIdCategory( ) );
        category.setLabel( LABEL_B );

        CategoryService.updateCategory( category );

        assertEquals( LABEL_A, CategoryHome.findByPrimaryKey( _categoryA.getIdCategory( ) ).getLabel( ) );
    }

    @Test
    public void testModifyKeepingItsLabel( )
    {
        Category category = CategoryHome.findByPrimaryKey( _categoryA.getIdCategory( ) );
        category.setNbMaxAppointmentsPerUser( 5 );

        CategoryService.updateCategory( category );

        assertEquals( 5, CategoryHome.findByPrimaryKey( _categoryA.getIdCategory( ) ).getNbMaxAppointmentsPerUser( ) );
    }
}
