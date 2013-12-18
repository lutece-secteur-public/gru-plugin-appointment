<jsp:useBean id="calendarSlot" scope="session" class="fr.paris.lutece.plugins.appointment.web.CalendarSlotJspBean" />
<% String strContent = calendarSlot.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
