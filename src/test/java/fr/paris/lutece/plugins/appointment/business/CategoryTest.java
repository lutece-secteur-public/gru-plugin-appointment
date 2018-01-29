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
