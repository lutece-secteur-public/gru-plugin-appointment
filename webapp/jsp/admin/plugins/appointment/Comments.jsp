<jsp:useBean id="comment" scope="session" class="fr.paris.lutece.plugins.appointment.web.CommentJspBean" />
<% String strContent = comment.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>