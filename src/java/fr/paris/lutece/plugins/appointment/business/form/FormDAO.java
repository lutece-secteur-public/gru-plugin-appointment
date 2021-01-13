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
package fr.paris.lutece.plugins.appointment.business.form;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form objects
 * 
 * @author Laurent Payen
 *
 */
public final class FormDAO implements IFormDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form ( title, description, reference, id_category, starting_validity_date, ending_validity_date, is_active, id_workflow, workgroup,is_multislot_appointment, role_fo ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form SET title = ?, description = ?, reference = ?, id_category = ?, starting_validity_date = ?, ending_validity_date = ?, is_active = ?, id_workflow = ?, workgroup = ?, is_multislot_appointment = ?, role_fo = ? WHERE id_form = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form WHERE id_form = ? ";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT form.id_form, form.title, form.description, form.reference, form.id_category, form.starting_validity_date, form.ending_validity_date, form.is_active, form.id_workflow, form.workgroup, form.is_multislot_appointment, form.role_fo FROM appointment_form form";
    private static final String SQL_QUERY_SELECT_BY_TITLE = SQL_QUERY_SELECT_COLUMNS + " WHERE title = ?";
    private static final String SQL_QUERY_SELECT_ALL = SQL_QUERY_SELECT_COLUMNS;
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_ACTIVE_FORMS = SQL_QUERY_SELECT_COLUMNS + " WHERE is_active = 1";
    private static final String SQL_QUERY_SELECT_ACTIVE_AND_DISPLAYED_ON_PORTLET_FORMS = SQL_QUERY_SELECT_COLUMNS
            + " INNER JOIN appointment_display display ON form.id_form = display.id_form WHERE form.is_active = 1 AND display.is_displayed_on_portlet = 1";

    @Override
    public void insert( Form form, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, form, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                form.setIdForm( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public void update( Form form, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, form, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdForm, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public Form select( int nIdForm, Plugin plugin )
    {
        Form form = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                form = buildForm( daoUtil );
            }
        }
        return form;
    }

    @Override
    public List<Form> findActiveForms( Plugin plugin )
    {
        List<Form> listForms = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ACTIVE_FORMS, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listForms.add( buildForm( daoUtil ) );
            }
        }
        return listForms;
    }

    @Override
    public List<Form> findActiveAndDisplayedOnPortletForms( Plugin plugin )
    {
        List<Form> listForms = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ACTIVE_AND_DISPLAYED_ON_PORTLET_FORMS, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listForms.add( buildForm( daoUtil ) );
            }
        }
        return listForms;
    }

    @Override
    public List<Form> findByTitle( String strTitle, Plugin plugin )
    {
        List<Form> listForms = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_TITLE, plugin ) )
        {
            daoUtil.setString( 1, strTitle );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listForms.add( buildForm( daoUtil ) );
            }
        }
        return listForms;
    }

    @Override
    public List<Form> findAllForms( Plugin plugin )
    {
        List<Form> listForms = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listForms.add( buildForm( daoUtil ) );
            }
        }
        return listForms;
    }

    /**
     * Build a Form business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new Form with all its attributes assigned
     */
    private Form buildForm( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Form form = new Form( );
        form.setIdForm( daoUtil.getInt( nIndex++ ) );
        form.setTitle( daoUtil.getString( nIndex++ ) );
        form.setDescription( daoUtil.getString( nIndex++ ) );
        form.setReference( daoUtil.getString( nIndex++ ) );
        form.setIdCategory( daoUtil.getInt( nIndex++ ) );
        form.setStartingValiditySqlDate( daoUtil.getDate( nIndex++ ) );
        form.setEndingValiditySqlDate( daoUtil.getDate( nIndex++ ) );
        form.setIsActive( daoUtil.getBoolean( nIndex++ ) );
        form.setIdWorkflow( daoUtil.getInt( nIndex++ ) );
        form.setWorkgroup( daoUtil.getString( nIndex++ ) );
        form.setIsMultislotAppointment( daoUtil.getBoolean( nIndex++ ) );
        form.setRole( daoUtil.getString( nIndex ) );
        return form;
    }

    /**
     * Build a daoUtil object with the form
     * 
     * @param query
     *            the query
     * @param form
     *            the form
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, Form form, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = null;
        if ( isInsert )
        {
            daoUtil = new DAOUtil( query, Statement.RETURN_GENERATED_KEYS, plugin );
        }
        else
        {
            daoUtil = new DAOUtil( query, plugin );
        }
        daoUtil.setString( nIndex++, form.getTitle( ) );
        daoUtil.setString( nIndex++, form.getDescription( ) );
        daoUtil.setString( nIndex++, form.getReference( ) );
        if ( form.getIdCategory( ) == null || form.getIdCategory( ) == 0 )
        {
            daoUtil.setIntNull( nIndex++ );
        }
        else
        {
            daoUtil.setInt( nIndex++, form.getIdCategory( ) );
        }
        daoUtil.setDate( nIndex++, form.getStartingValiditySqlDate( ) );
        daoUtil.setDate( nIndex++, form.getEndingValiditySqlDate( ) );
        daoUtil.setBoolean( nIndex++, form.getIsActive( ) );
        daoUtil.setInt( nIndex++, form.getIdWorkflow( ) );
        daoUtil.setString( nIndex++, form.getWorkgroup( ) );
        daoUtil.setBoolean( nIndex++, form.getIsMultislotAppointment( ) );
        daoUtil.setString( nIndex++, form.getRole( ) );

        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, form.getIdForm( ) );
        }

        return daoUtil;
    }
}
