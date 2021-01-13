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
package fr.paris.lutece.plugins.appointment.web.dto;

import java.util.Objects;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.file.File;

/**
 * DTO that represent a response to display a recap
 * 
 * @author Laurent Payen
 *
 */
public final class ResponseRecapDTO extends Response implements Comparable<ResponseRecapDTO>
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -248405445729375667L;

    /**
     * The recap value
     */
    private String _strRecapValue;

    /**
     * The response
     */
    private Response _response;

    /**
     * Creates a new response DTO for recap from a response
     * 
     * @param response
     *            The response
     */
    public ResponseRecapDTO( Response response )
    {
        this._response = response;
    }

    /**
     * Creates a new response DTO for recap from a response
     * 
     * @param response
     *            The response
     * @param strRecapValue
     *            The recap value
     */
    public ResponseRecapDTO( Response response, String strRecapValue )
    {
        this._response = response;
        this._strRecapValue = strRecapValue;
    }

    /**
     * Get the recap value of this response
     * 
     * @return The recap value of this response
     */
    public String getRecapValue( )
    {
        return this._strRecapValue;
    }

    /**
     * Set the recap value of this response
     * 
     * @param strRecapValue
     *            The recap value of this response
     */
    public void setRecapValue( String strRecapValue )
    {
        this._strRecapValue = strRecapValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry getEntry( )
    {
        return _response.getEntry( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIdResponse( )
    {
        return _response.getIdResponse( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field getField( )
    {
        return _response.getField( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToStringValueResponse( )
    {
        return _response.getToStringValueResponse( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValue( )
    {
        return _response.getResponseValue( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus( )
    {
        return _response.getStatus( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile( )
    {
        return _response.getFile( );
    }

    @Override
    public int compareTo( ResponseRecapDTO o )
    {

        if ( this._response.getEntry( ) != null && o._response.getEntry( ) != null )
        {
            return ( this._response.getEntry( ).getPosition( ) - o._response.getEntry( ).getPosition( ) );
        }
        return 0;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o == this )
        {
            return true;
        }
        if ( !( o instanceof ResponseRecapDTO ) )
        {
            return false;
        }
        ResponseRecapDTO responseToCompare = (ResponseRecapDTO) o;
        return Objects.equals( _strRecapValue, responseToCompare._strRecapValue ) && Objects.equals( _response, responseToCompare._response );
    }

    @Override
    public int hashCode( )
    {
        return Objects.hash( _strRecapValue, _response );
    }

}
