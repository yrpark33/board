<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/includes/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-6 text-center mt-5">
        <h3>오류가 발생했습니다</h3>
        <p class="text-muted">${errorMsg}</p>
        <a href="/board/list" class="btn btn-primary">목록으로</a>
    </div>
</div>

<%@include file="/WEB-INF/views/includes/footer.jsp" %>