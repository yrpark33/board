<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/views/includes/header.jsp" %>
	<div class="row justify-content-center">
		<div class="col-lg-12">
			<div class="card shadow mb-4">
				<div class="card-header py-3">
					<h5 class="m-0 font-weight-bold text-primary">게시물 목록</h5>
				</div>
				<div class="card-body">
					<table class="table table-bordered" id="dataTable">
						<thead>
							<tr>
								<th>번호</th>
								<th>제목</th>
								<th>작성자</th>
								<th>작성일</th>
							</tr>
						</thead>
						
						<tbody class="tbody">
							<c:forEach var="board" items="${list}">
								<tr data-boardId="${board.boardId}">
									<td><c:out value="${board.boardId}"/></td>
									<td><a href="/board/${board.boardId}"><c:out value="${board.title}"/></a></td>
									<td><c:out value="${board.writer}"/></td>
									<td><c:out value="${board.createdDate}"/></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="removeModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h3 class="modal-title fs-5" id="exampleModalLabel">알림</h3>
	        <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
	      </div>
	      <div class="modal-body">
	        
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" data-dismiss="modal">닫기</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<script type="text/javascript" defer="defer">
		const removed = '${removed}'
		
		const modal = new bootstrap.Modal(document.getElementById("removeModal"))
		
		if(removed) {
			document.querySelector('.modal-body').textContent = removed + '번 게시물이 삭제되었습니다'
			modal.show()
		}
		
		
		
	</script>
	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>