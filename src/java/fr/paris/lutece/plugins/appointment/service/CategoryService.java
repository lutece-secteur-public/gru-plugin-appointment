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
public class CategoryService {
	
	public static List<Category> findAllCategories(){
		return CategoryHome.findAllCategories();
	}
	
	public static void removeCategory(int nIdCategory){
		CategoryHome.delete(nIdCategory);
	}
	
	public static void createCategory(Category category){
		Category categoryInDb = CategoryHome.findByLabel(category.getLabel());
		if (categoryInDb == null) {
			CategoryHome.create(category);
		}
	}
	
	public static Category findCategoryById(int nIdCategory){
		return CategoryHome.findByPrimaryKey(nIdCategory);
	}
	
	public static void updateCategory(Category category){
		CategoryHome.update(category);
	}
	
	public static ReferenceList findAllInReferenceList() {
		List<Category> listCategory = findAllCategories();
		ReferenceList refListTemplates = new ReferenceList(listCategory.size()+1);
		refListTemplates.addItem(-1, StringUtils.EMPTY);
		for (Category category : listCategory) {
			refListTemplates.addItem(category.getIdCategory(), category.getLabel());
		}
		return refListTemplates;
	}
	
}
