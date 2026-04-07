<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/includes/header.jsp" %>

<div class="container mt-5">
    <div class="jumbotron text-center">
        <h1>Welcome to Oolong Board!</h1>
        <p class="lead">스프링 프레임워크와 Axios로 만든 커뮤니티입니다.</p>
        <hr class="my-4">
        <a class="btn btn-primary btn-lg" href="/board/list" role="button">게시판 구경하기</a>
        <a class="btn btn-primary btn-lg" href="/board/write" role="button">게시물 작성하기</a>
    </div>
</div>



<script>
	
	const errorMsg = `${errorMsg}`
	
	if(errorMsg) {
		alert(errorMsg)
	}
	
</script>

<%@include file="/WEB-INF/views/includes/footer.jsp" %>

