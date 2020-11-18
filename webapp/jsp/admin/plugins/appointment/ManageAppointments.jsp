<jsp:useBean id="comment" scope="session" class="fr.paris.lutece.plugins.appointment.web.CommentJspBean" />
<jsp:useBean id="manageappointmentAppointment" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentJspBean" />
<% String strContent = manageappointmentAppointment.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<%= comment.getCommentInfos( ) %>
<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
