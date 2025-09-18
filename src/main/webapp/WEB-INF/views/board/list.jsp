<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/views/includes/header.jsp"%>


	<div class="row justify-content-center">
		<div class="col-lg-12">
			<div class="card shadow mt-3 mb-4">
				<div class="card-header py-3">
					<h6 class="m-0 font-weight-bold text-secondary">Board List</h6>
				</div>
				<div class="card-body">
					<table class="table table-bordered table-hover" id="dataTable">
						<thead class="table-secondary">
							<tr>
								<th>Bno</th>
								<th>Title</th>
								<th>Writer</th>
								<th>RegDate</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="board" items="${list}">
								<tr>
									<td>${board.bno}</td>
									<td><a href='/board/read/${board.bno}'>${board.title}</a></td>
									<td>${board.writer}</td>
									<td>${board.createdDate}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="myModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="exampleModalLabel">등록 성공</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
	        
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary">Save changes</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<script type="text/javascript" defer="defer">
		const register = '${register}'
		const remove = '${remove}'
		
		const myModal = new bootstrap.Modal(document.getElementById('myModal'))
		
		if(register) {
			document.querySelector('.modal-body').textContent = `${register}번 게시물이 등록되었습니다.`
			myModal.show()	
		}
		
		if(remove) {
			document.querySelector('.modal-body').textContent = `${remove}번 게시물이 삭제되었습니다.`
			myModal.show()
		}
		
		
	</script>
<%@include file="/WEB-INF/views/includes/footer.jsp"%>