package fr.paris.lutece.plugins.appointment.service;

import java.sql.Date;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.comment.Comment;
import fr.paris.lutece.plugins.appointment.business.comment.CommentHome;

public class CommentService {
	
	private CommentService () {
		
	}
	
	public static List<Comment> finListComments( Date startingDate, Date endingDate, int nIdForm){
		
		
		return CommentHome.selectCommentsList(  startingDate,  endingDate,  nIdForm);
	}

}
