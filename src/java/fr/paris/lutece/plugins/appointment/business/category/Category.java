package fr.paris.lutece.plugins.appointment.business.category;

import java.io.Serializable;

import fr.paris.lutece.portal.service.rbac.RBACResource;

/**
 * Business class of the Category
 * 
 * @author Laurent Payen
 *
 */
public class Category implements Serializable, RBACResource {

	/**
	 * Name of the resource type of Appointment Forms
	 */
	public static final String RESOURCE_TYPE = "APPOINTMENT_CATEGORY";
	
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
	public int getIdCategory() {
		return _nIdCategory;
	}

	/**
	 * Set the id of the category
	 * 
	 * @param nIdCategory
	 *            the id to set
	 */
	public void setIdCategory(int nIdCategory) {
		this._nIdCategory = nIdCategory;
	}

	/**
	 * Get the label of the category
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return _strLabel;
	}

	/**
	 * Set the label of the category
	 * 
	 * @param strLabel
	 *            the label to set
	 */
	public void setLabel(String strLabel) {
		this._strLabel = strLabel;
	}

	@Override
	public String getResourceTypeCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourceId() {
		// TODO Auto-generated method stub
		return null;
	}

}
