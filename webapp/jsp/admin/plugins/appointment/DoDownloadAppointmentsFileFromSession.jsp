<%@ page errorPage="../../ErrorPage.jsp" trimDirectiveWhitespaces="true" %>
<%@page import="fr.paris.lutece.plugins.appointment.web.AppointmentFormJspBean"%>
<jsp:useBean id="manageappointmentAppointment" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentJspBean" />
<% 
	manageappointmentAppointment.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM ) ;
	manageappointmentAppointment.getDownloadFileFromSession ( request , response );
%>