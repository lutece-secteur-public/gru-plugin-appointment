<jsp:useBean id="appointmentCommentNotification" scope="session" class="fr.paris.lutece.plugins.appointment.web.CommentNotificationJspBean" />
<% String strContent = appointmentCommentNotification.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>