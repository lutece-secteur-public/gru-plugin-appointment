<jsp:useBean id="appointmentFormDay" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentFormDayJspBean" />
<% String strContent = appointmentFormDay.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
