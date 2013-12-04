<jsp:useBean id="manageappointmentformAppointmentForm" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentFormJspBean" />
<% String strContent = manageappointmentformAppointmentForm.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
