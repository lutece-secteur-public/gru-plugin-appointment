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
