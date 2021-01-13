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
package fr.paris.lutece.plugins.appointment.business.category;

import java.io.Serializable;

/**
 * Business class of the Category
 * 
 * @author Laurent Payen
 *
 */
public final class Category implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 7889020298488911210L;

    /**
     * Category Id
     */
    private int _nIdCategory;

    /**
     * Label of the category
     */
    private String _strLabel;

    /**
     * Get the id of the category
     * 
     * @return the id
     */
    public int getIdCategory( )
    {
        return _nIdCategory;
    }

    /**
     * Set the id of the category
     * 
     * @param nIdCategory
     *            the id to set
     */
    public void setIdCategory( int nIdCategory )
    {
        this._nIdCategory = nIdCategory;
    }

    /**
     * Get the label of the category
     * 
     * @return the label
     */
    public String getLabel( )
    {
        return _strLabel;
    }

    /**
     * Set the label of the category
     * 
     * @param strLabel
     *            the label to set
     */
    public void setLabel( String strLabel )
    {
        this._strLabel = strLabel;
    }

}
