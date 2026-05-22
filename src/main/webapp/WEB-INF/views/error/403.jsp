<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/includes/header.jsp" %>
	<div class="container vh-100 d-flex justify-content-center align-items-center">
		
		<div class="col-lg-6 text-center mt-5">
	        <h1 class="display-1 text-danger fw-bold">403</h1>
			<h2 class="mb-3">접근이 거부되었습니다.</h2>
			<p class="lead mb-4">이 페이지에 접근할 권한이 없습니다.<br>필요한 권한이 있는지 확인하거나 관리자에게 문의하세요.</p>
	        <a href="/board/list" class="btn btn-primary">목록으로</a>
    	</div>
	</div>
	
	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>