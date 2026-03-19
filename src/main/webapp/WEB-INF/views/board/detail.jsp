<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/views/includes/header.jsp" %>
	<div class="row justify-content-center">
		<div class="col-lg-12">
			<div class="card shadow mb-4">
				<div class="card-header py-3">
					<h5 class="m-0 font-weight-bold text-primary">게시물 조회</h5>
				</div>
				<div class="card-body">
					<input type="hidden" name="boardId" value="<c:out value='${board.boardId}'/>">
					<div class="input-group input-group-lg mb-3">
						<div class="input-group-prepend"><span class="input-group-text">제목</span></div>
						<input class="form-control" type="text" value="<c:out value='${board.title}'/>" readonly>
					</div>
					<div class="input-group input-group-lg mb-3">
						<div class="input-group-prepend"><span class="input-group-text">내용</span></div>
						<textarea class="form-control" rows="3" readonly><c:out value="${board.content}"/></textarea>
					</div>
					<div class="input-group input-group-lg mb-3">
						<div class="input-group-prepend"><span class="input-group-text">작성자</span></div>
						<input type="text" class="form-control" value="<c:out value='${board.writer}'/>" readonly>
					</div>
					<div class="input-group input-group-lg mb-3">
						<div class="input-group-prepend"><span class="input-group-text">작성 시간</span></div>
						<input type="text" class="form-control" value="<c:out value='${board.createdDate}'/>" readonly>
					</div>
					<div class="float-right">
						<a class="btn" href="/board/list"><button type="button" class="btn btn-info btnList">목록</button></a>
						<a class="btn" href="/board/modify/${board.boardId}"><button type="button" class="btn btn-warning btnModify">수정</button></a>
					</div>
				</div>
			</div>
		</div>
	</div>
<%@include file="/WEB-INF/views/includes/footer.jsp" %>