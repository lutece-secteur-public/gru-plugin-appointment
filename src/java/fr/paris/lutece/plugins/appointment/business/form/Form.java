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

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;

/**
 * Business class of the Form
 * 
 * @author Laurent Payen
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public final class Form implements RBACResource, AdminWorkgroupResource, Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4742702767509625292L;

    /**
     * Name of the resource type of Appointment Forms
     */
    @JsonIgnore
    public static final String RESOURCE_TYPE = "APPOINTMENT_FORM";

    @JsonIgnore
    public static final String ROLE_NONE = "none";

    /**
     * Form Id
     */
    private int _nIdForm;

    /**
     * Title of the form
     */
    @NotBlank( message = "#i18n{appointment.validation.appointmentform.Title.notEmpty}" )
    @Size( max = 255, message = "#i18n{appointment.validation.appointmentform.Title.size}" )
    private String _strTitle;

    /**
     * Description of the form
     */
    @NotBlank( message = "#i18n{appointment.validation.appointmentform.Description.notEmpty}" )
    private String _strDescription;

    /**
     * Reference of the form
     */
    private String _strReference;

    /**
     * Category of the form
     */
    private Integer _nIdCategory;

    /**
     * Starting validity date of the form
     */
    private LocalDate _startingValidityDate;

    /**
     * Ending validity date of the form
     */
    private LocalDate _endingValidityDate;

    /**
     * Indicate whether the form is active or not
     */
    private boolean _bIsActive;

    /**
     * Workflow Id
     */
    private int _nIdWorkflow;

    /**
     * Workgroup
     */
    private String _strWorkgroup;

    /**
     * _bIsMultislotAppointment
     */
    private boolean _bIsMultislotAppointment;

    /**
     * Role FO
     */
    private String _strRole;

    /**
     * Get the form Id
     * 
     * @return the form Id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the form Id
     * 
     * @param nIdForm
     *            the Id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * Get the title of the form
     * 
     * @return the form title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Set the form title
     * 
     * @param title
     *            the Title to set
     */
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    /**
     * Get the description of the form
     * 
     * @return the description of the form
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Set the description of the form
     * 
     * @param description
     *            the description to set
     */
    public void setDescription( String strDescription )
    {
        this._strDescription = strDescription;
    }

    /**
     * Get the reference of the form
     * 
     * @return the reference of the form
     */
    public String getReference( )
    {
        return _strReference;
    }

    /**
     * Set the reference of the form
     * 
     * @param reference
     *            the reference to set
     */
    public void setReference( String strReference )
    {
        this._strReference = strReference;
    }

    /**
     * Get the category id of the form
     * 
     * @return the category id of the form
     */
    public Integer getIdCategory( )
    {
        return _nIdCategory;
    }

    /**
     * Set the category id of the form
     * 
     * @param nIdCategory
     *            the category id to set
     */
    public void setIdCategory( Integer nIdCategory )
    {
        this._nIdCategory = nIdCategory;
    }

    /**
     * Get the starting validity date of the form (in LocalDate format)
     * 
     * @return the starting validity date of the form
     */
    public LocalDate getStartingValidityDate( )
    {
        return _startingValidityDate;
    }

    /**
     * Get the starting validity date of the form (in sql date format)
     * 
     * @return the starting validity date
     */
    public Date getStartingValiditySqlDate( )
    {
        Date date = null;
        if ( _startingValidityDate != null )
        {
            date = Date.valueOf( _startingValidityDate );
        }
        return date;
    }

    /**
     * Set the starting date of the validity of the form
     * 
     * @param startValidity
     *            the starting validity date to set
     */
    public void setStartingValidityDate( LocalDate startingValidityDate )
    {
        this._startingValidityDate = startingValidityDate;
    }

    /**
     * Set the starting validity date of the form
     * 
     * @param startingValidityDate
     *            the starting validity date to set (in sql Date format)
     */
    public void setStartingValiditySqlDate( Date startingValidityDate )
    {
        if ( startingValidityDate != null )
        {
            this._startingValidityDate = startingValidityDate.toLocalDate( );
        }
        else
        {
            this._startingValidityDate = null;
        }
    }

    /**
     * Get the end date of the validity of the form
     * 
     * @return the end validity date of the form
     */
    public LocalDate getEndingValidityDate( )
    {
        return _endingValidityDate;
    }

    /**
     * Get the ending validity date of the form (in sql date format)
     * 
     * @return the ending validity date
     */
    public Date getEndingValiditySqlDate( )
    {
        Date date = null;
        if ( _endingValidityDate != null )
        {
            date = Date.valueOf( _endingValidityDate );
        }
        return date;
    }

    /**
     * Set the end date of the validity of the form
     * 
     * @param endValidity
     *            the end validity date to set
     */
    public void setEndingValidityDate( LocalDate endingValidityDate )
    {
        this._endingValidityDate = endingValidityDate;
    }

    /**
     * Set the ending validity date of the form
     * 
     * @param endingValidityDate
     *            the ending validity date to set (in sql Date format)
     */
    public void setEndingValiditySqlDate( Date endingValidityDate )
    {
        if ( endingValidityDate != null )
        {
            this._endingValidityDate = endingValidityDate.toLocalDate( );
        }
        else
        {
            this._endingValidityDate = null;
        }
    }

    /**
     * Returns the IsActive
     * 
     * @return The IsActive
     */
    public boolean getIsActive( )
    {
        return _bIsActive;
    }

    /**
     * Set the active boolean value of the form
     * 
     * @param isActive
     *            the boolean active value to set
     */
    public void setIsActive( boolean bIsActive )
    {
        this._bIsActive = bIsActive;
    }

    /**
     * Get the workflow id
     * 
     * @return the workflow id
     */
    public int getIdWorkflow( )
    {
        return _nIdWorkflow;
    }

    /**
     * Set the workflow Id
     * 
     * @param nIdWorkflow
     *            the workflow id to set
     */
    public void setIdWorkflow( int nIdWorkflow )
    {
        this._nIdWorkflow = nIdWorkflow;
    }

    @Override
    public String getWorkgroup( )
    {
        return _strWorkgroup;
    }

    /**
     * Set the workgroup
     * 
     * @param strWorkgroup
     *            the workgroup
     */
    public void setWorkgroup( String strWorkgroup )
    {
        this._strWorkgroup = strWorkgroup;
    }

    @Override
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    @Override
    public String getResourceId( )
    {
        return Integer.toString( getIdForm( ) );
    }

    /**
     * Returns the IsMultislotAppointment
     * 
     * @return The IsMultislotAppointment
     */
    public boolean getIsMultislotAppointment( )
    {
        return _bIsMultislotAppointment;
    }

    /**
     * Sets the IsMultislotAppointment
     * 
     * @param bIsMultislotAppointment
     *            The IsMultislotAppointment
     */
    public void setIsMultislotAppointment( boolean bIsMultislotAppointment )
    {
        _bIsMultislotAppointment = bIsMultislotAppointment;
    }

    /**
     * @return the strRole
     */
    public String getRole( )
    {
        return _strRole;
    }

    /**
     * @param strRole
     *            the strRole to set
     */
    public void setRole( String strRole )
    {
        _strRole = StringUtils.isEmpty( strRole ) ? ROLE_NONE : strRole;
    }

}
