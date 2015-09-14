<%@ page errorPage="../../ErrorPage.jsp"  trimDirectiveWhitespaces="true" %>
<%@ page import="fr.paris.lutece.plugins.appointment.web.AppointmentFormJspBean"%>
<jsp:useBean id="appointmentSlot" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentSlotJspBean" />
<%
appointmentSlot.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM ) ;
appointmentSlot.getExportClosingDays ( request , response );
%>
