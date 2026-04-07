<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/views/includes/header.jsp" %>
	<div class="row justify-content-center">
		<div class="col-lg-12">
			<div class="card shadow mb-4">
				<div class="card-header py-3">
					<h5 class="m-0 font-weight-bold text-primary">게시물 작성</h5>
				</div>
				<div class="card-body">
					<form action="/board/write" method="post">
						<c:if test="${not empty errorMsg}">
    						<div class="alert alert-danger">${errorMsg}</div>
						</c:if>
						<div class="mb-3">
							<label class="form-label">제목</label>
							<input type="text" name="title" class="form-control" value="${board.title}" required>
						</div>
						<div class="mb-3">
							<label class="form-label">내용</label>
							<textarea class="form-control" name="content" required>${board.content}</textarea>
						</div>
						<div class="mb-3">
							<label class="form-label">작성자</label>
							<input type="text" name="writer" class="form-control" value="${board.writer}" required>
						</div>
						<div class="d-flex justify-content-end">
							<button type="submit" class="btn btn-primary">등록</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	
	
	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>