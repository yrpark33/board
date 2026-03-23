<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/views/includes/header.jsp" %>
	<div class="row justify-content-center">
		<div class="col-lg-12">
			<div class="card shadow mb-4">
				<div class="card-header py-3">
					<h5 class="m-0 font-weight-bold text-primary">게시물 수정</h5>
				</div>
				<div class="card-body">
					<form id="modifyForm" action="/board/modify" method="post">
						<input type="hidden" name="boardId" value="${board.boardId}">
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">제목</span></div>
							<input type="text" name="title" class="form-control" value="<c:out value='${board.title}'/>"/>
						</div>
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">내용</span></div>
							<textarea name="content" class="form-control" rows="3"><c:out value='${board.content}'/></textarea>
						</div>
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">작성자</span></div>
							<input type="text" class="form-control" value="<c:out value='${board.writer}'/>" readonly/>
						</div>
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">작성 시간</span></div>	
							<input type="text" class="form-control" value="<c:out value='${board.createdDate}'/>" readonly>
						</div>
						
					</form>
					<div class="float-right">
						<a class="btn btn-info" href="/board/list?${dto.toQueryString()}">목록</a>
						<button type="button" class="btn btn-warning btnModify">수정</button>
						<button type="button" class="btn btn-danger btnRemove">삭제</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
	
		const formObj = document.querySelector('#modifyForm');
		
	
		document.querySelector('.btnModify').addEventListener('click', () => {
			formObj.action = '/board/modify'
			formObj.method = 'post'
			formObj.submit()
		})
		
		
		
		
		document.querySelector('.btnRemove').addEventListener('click', () => {
			formObj.action = '/board/remove'
			formObj.method = 'post'
			formObj.submit()
		})
		
		
		
	</script>
	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>