<%@ page errorPage="../../ErrorPage.jsp" trimDirectiveWhitespaces="true" %>

<%@page import="fr.paris.lutece.plugins.appointment.web.AppointmentFormJspBean"%>

${ appointmentJspBean.init( pageContext.request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM ) }
${ appointmentJspBean.getDownloadFileFromSession( pageContext.request, pageContext.response ) }
