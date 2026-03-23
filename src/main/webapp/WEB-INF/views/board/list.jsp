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
					
					<div class="d-flex justify-content-end" style="margin-bottom: 1em">
						<div style="width: 50%" class="d-flex">
							<select name="typeSelect" class="form-control mr-2">
								<option value="">--</option>
								<option value="T" ${dto.types == 'T' ? 'selected' : ''}>제목</option>
								<option value="C" ${dto.types == 'C' ? 'selected' : ''}>내용</option>
								<option value="W" ${dto.types == 'W' ? 'selected' : ''}>작성자</option>
								<option value="TC" ${dto.types == 'TC' ? 'selected' : ''}>제목+내용</option>
								<option value="TW" ${dto.types == 'TW' ? 'selected' : ''}>제목+작성자</option>
								<option value="TCW" ${dto.types == 'TCW' ? 'selected' : ''}>제목+내용+작성자</option>
							</select>
							<input type="text" name="keywordInput" class="form-control mr-2" value="<c:out value='${dto.keyword}'/>">
							<button class="btn btn-primary searchBtn" style="white-space: nowrap">검색</button>
						</div>
					</div>
					
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
							<c:forEach var="board" items="${dto.boardDTOList}">
								<tr data-boardId="${board.boardId}">
									<td><c:out value="${board.boardId}"/></td>
									<td><a class="boardTitle" href="${board.boardId}"><c:out value="${board.title}"/></a></td>
									<td><c:out value="${board.writer}"/></td>
									<td><c:out value="${board.createdDate}"/></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					
					<div class="d-flex justify-content-center">
						<ul class="pagination">
							<c:if test="${dto.prev}">
								<li class="page-item">
									<a href="${dto.start - 1}" tabindex="-1" class="page-link">이전</a>
								</li>
							</c:if>
							<c:forEach var="num" items="${dto.pageNums}">
								<li class="page-item ${dto.page == num ? 'active' : ''}">
									<a href="${num}" class="page-link">${num}</a>
								</li>
							</c:forEach>
							<c:if test="${dto.next}">
								<li class="page-item">
									<a href="${dto.end + 1}" class="page-link">다음</a>
								</li>
							</c:if>
						</ul>
					</div>
					
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
		
		const modal = new bootstrap.Modal(document.getElementById('removeModal'))
		
		if(removed) {
			document.querySelector('.modal-body').textContent = removed + '번 게시물이 삭제되었습니다'
			modal.show()
		}
		
		
		const pagination = document.querySelector('.pagination');
		
		pagination.addEventListener('click', (e) => {
			e.preventDefault()
			
			const target = e.target
			
			const targetPage = target.getAttribute('href')
			
			
			const size = ${dto.size} || 10
			
			const params = new URLSearchParams({
				page: targetPage,
				size: size,
			})
			
			const types = '${dto.types}'
			const keyword = '${dto.keyword}'
			
			if(types && keyword) {
				params.set('types', types)
				params.set('keyword', keyword)
			}
			
			
			self.location = `/board/list?\${params.toString()}`
			
		})
		
		document.querySelector(".searchBtn").addEventListener('click', (e) => {
			
			const keyword = document.querySelector('input[name="keywordInput"]').value
			const selectObj = document.querySelector('select[name="typeSelect"]')
			
			const types = selectObj.options[selectObj.selectedIndex].value
			
			const params = new URLSearchParams({
				types: types,
				keyword: keyword
			})
			
			self.location = `/board/list?\${params.toString()}`
			
			
		});
		
		
		document.querySelector('tbody').addEventListener('click', (e) => {
			
			e.preventDefault()
			
			const target = e.target 
		
			if(target.tagName !== 'A') {
				return
			}
			
			
			const boardId = target.getAttribute('href')
			const page = '${dto.page}'
			const size = '${dto.size}'
			
			
			const params = new URLSearchParams({
				page: page,
				size: size
			})
			
			const types = '${dto.types}'
			const keyword = '${dto.keyword}'
			
			if(types && keyword) {
				params.set('types', types)
				params.set('keyword', keyword)
			}
			
			console.log(params.toString())
			
 			self.location = `/board/\${boardId}?\${params.toString()}`
			
			
			
		})
		
	</script>
	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>