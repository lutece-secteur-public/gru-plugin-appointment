<%@ page errorPage="../../ErrorPage.jsp" %>

${ pageContext.setAttribute( 'strContent', appointmentJspBean.processController( pageContext.request , pageContext.response ) ) }

<jsp:include page="../../AdminHeader.jsp" />

${ appointmentCommentJspBean.getCommentInfos( ) }
${ pageContext.getAttribute( 'strContent' ) }

<%@ include file="../../AdminFooter.jsp" %>
