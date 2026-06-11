<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
						<input type="text" class="form-control" value="<c:out value='${board.createdTime}'/>" readonly>
					</div>
					
					<c:if test="${not empty board.updatedAt}">
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">수정 시간</span></div>
							<input type="text" class="form-control" value="<c:out value='${board.updatedTime}'/>" readonly>
						</div>
					</c:if>
					
					<c:if test="${not empty board.files}">
						<div class="mb-3">
							<label class="form-label font-weight-bold text-primary">첨부파일</label>
							<div class="row">
								<c:forEach var="file" items="${board.files}">
									<div class="col-md-3 mb-3">
										<div class="card">
											<a href="/images/board/original/${file.uuid}_${file.fileName}" target="_blank">
												<img src="/images/board/original/${file.uuid}_${file.fileName}" class="card-img-top img-fluid" alt="첨부파일"/>
											</a>
										</div>
									</div>
								</c:forEach>
							</div>
						</div>
					</c:if>
					
					
					<div class="float-right">
						<a class="btn btn-info" href="/board/list?${dto.toQueryString()}">목록</a>
						<sec:authentication property="principal" var="secInfo"/>
						<sec:authentication property="authorities" var="roles"/>
						<c:if test="${!board.deleted && (secInfo.username == board.writer || fn:contains(roles, 'ROLE_ADMIN'))}">
							<a class="btn btn-warning" href="/board/modify/${board.boardId}?${dto.toQueryString()}">수정</a>
						</c:if>
					</div>
				</div>
			</div>
		</div>
		
		<div class="col-lg-12">
			<label class="form-label font-weight-bold text-primary">댓글 작성</label>
			<div class="card shadow mb-4">
				<div class="m-4">
					<form id="commentForm">
						<input type="hidden" name="boardId" value="${board.boardId}"/>
						<div class="mb-3 input-group">
							<div class="input-group-prepend"><span class="input-group-text">댓글</span></div>
							<textarea name="content" class="form-control" rows="3" required></textarea>
						</div>
						<div class="text-right">
							<button type="submit" class="btn btn-primary writeCommentBtn">등록</button>
						</div>
					</form>
				</div>
			</div>
		</div>
		
		<div class="col-lg-12">
			<label class="form-label font-weight-bold text-primary">댓글 목록</label>
			<div class="card shadow mb-4">
				<div class="m-4">
					<ul class="list-group commentList">
						<li class="list-group-item">
							<div class="d-flex justify-content-between">
								<div>
									<strong>번호</strong> - 댓글 내용
								</div>
								<div class="text-muted small">
									작성일
								</div>
							</div>
							<div class="mt-1 text-secondary small">
								작성자
							</div>
						</li>
					</ul>
					
					<div class="mt-4">
						<ul class="pagination justify-content-center">
							<li class="page-item disabled">
								<a class="page-link" href="#" tabindex="-1">이전</a>
							</li>
							<li class="page-item active">
								<a class="page-link" href="#">1</a>
							</li>
							<li class="page-item">
								<a class="page-link" href="#">2</a>
							</li>
							<li class="page-item">
								<a class="page-link" href="#">3</a>
							</li>
							<li class="page-item">
								<a class="page-link" href="#">다음</a>
							</li>
						</ul>
					</div>
					
				</div>
			</div>
		</div>
		
	</div>
	
	<div class="modal fade" id="commentModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="staticBackdropLabel">댓글 수정</h5>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body">
	        <form id="commentModifyForm">
	        	<input type="hidden" name="commentId" value="33">
	        	<div class="mb-3">
	        		<label for="contentInput" class="form-label">댓글 내용</label>
	        		<input type="text" id="contentInput" name="content" class="form-control" value="Comment Text" required>
	        	</div>
	        </form>
	      </div>
	      <div class="modal-footer">
	        <button type="submit" form="commentModifyForm" class="btn btn-primary">수정</button>
	        <button type="button" class="btn btn-secondary" data-dismiss="modal">닫기</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
	<script>
		
		let currentPage = 1;
		let currentSize = 10;
	
		const commentForm = document.querySelector('#commentForm')
	
		commentForm.addEventListener('submit', (e) => {
			
			e.preventDefault()
			
			
			
			const formData = new FormData(commentForm)
			
			axios.post('/comments', formData).then(res => {
				commentForm.reset()
				
				getComments(1, true)
				
				
			}).catch(error => {
				alert(error.response.data)
			})
			
		})
		
		
		
		
		const commentList = document.querySelector('.commentList')
		const pagination = document.querySelector('.pagination')
		
		
		function printComments(data) {
			const {commentDTOList, page, size, prev, next, start, end, pageNums, username, admin} = data
			
			let liStr = ''
			
			
			if(commentDTOList.length === 0) {
				commentList.innerHTML = '<li class="list-group-item text-center text-muted">아직 작성된 댓글이 없습니다.</li>'
				pagination.innerHTML = ''
				return
			}
			
			for(commentDTO of commentDTOList) {
				
				const canModify = !commentDTO.deleted && (username === commentDTO.writer || admin);
			
				liStr += `<li class="list-group-item" commentId="\${commentDTO.commentId}">
							<div>\${commentDTO.deleted ? '삭제된 댓글입니다.' : commentDTO.content}</div>
						    <div class="d-flex justify-content-between align-items-center">
						        <span class="text-muted small">\${commentDTO.deleted ? '알 수 없음' : commentDTO.writer}</span>
						        <div>
						            <span class="text-muted small">\${commentDTO.createdAt}</span>
						            \${canModify ? '<button class="btn btn-sm btn-primary commentModifyBtn">수정</button>' : ''}
						            \${canModify ? '<button class="btn btn-sm btn-danger commentRemoveBtn">삭제</button>' : ''}
						        </div>
						    </div>
						</li>`
			
			}
			

			commentList.innerHTML = liStr
			
			
			let pagingStr = ''
			
			
			if(prev) {
				pagingStr += `<li class="page-item"><a class="page-link" href="\${start - 1}" tabindex="-1">이전</a></li>`
			}
			
			for(let i of pageNums) {
				pagingStr += `<li class="page-item \${i == page ? 'active' : ''}")><a class="page-link" href="\${i}">\${i}</a></li>`
			}
			
			if(next) {
				
				pagingStr += `<li class="page-item"><a class="page-link" href="\${end + 1}">다음</a></li>`
			}
			
			pagination.innerHTML = pagingStr
		}
		
		
		const boardId = ${board.boardId}
		
		function getComments(pageNum, goLast) {
			
			axios.get(`/comments/\${boardId}/list`, {
				params : {
					page: pageNum || currentPage,
					size: currentSize
				}
			}).then(res => {
				const data = res.data
				console.log(data)
				const {totalCount, page, size} = data
				
				if(goLast && (totalCount > (page * size))) {
					const lastPage = Math.ceil(totalCount / size)
					getComments(lastPage)
				} else {
					currentPage = page
					currentSize = size
					
					printComments(data)
				}
				
			}).catch(error => {
				console.error('댓글 목록 로딩 실패:', error)
				alert('댓글을 불러오는 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.')
			})
			
		}
		
		getComments(1, true)
		
		
		pagination.addEventListener('click', (e) => {
			
			e.preventDefault()
			const target = e.target
			
			const href = target.getAttribute('href')
			
			if(!href) {
				return
			}
			
			getComments(href, false)
			
		})
		
		const commentModal = new bootstrap.Modal(document.querySelector('#commentModal'), {
		    backdrop: 'static',
		    keyboard: false
		})
		
		
		const commentModifyForm = document.querySelector("#commentModifyForm")

		
		commentList.addEventListener('click', function(e) {
		    if(e.target.classList.contains('commentModifyBtn')) {
		    	
		    		const commentId = e.target.closest('li').getAttribute('commentId');
		    		
		    		
					if(!commentId) return
					
					axios.get(`/comments/\${commentId}`).then(res => {
						
						const targetComment = res.data
						
						
						commentModifyForm.querySelector('input[name = "commentId"]').value = targetComment.commentId
						commentModifyForm.querySelector('input[name = "content"]').value = targetComment.content
						commentModal.show()
						
						
					}).catch(error => {
						alert(error.response.data)
					})
					
			}
		    
		    
		    if(e.target.classList.contains('commentRemoveBtn')) {
			    if(!confirm('정말 삭제하시겠습니까?')) return;
			}
		    
		    
		    if(e.target.classList.contains('commentRemoveBtn')) {
						
				e.preventDefault()
				
				
				const commentId = e.target.closest('li').getAttribute('commentId');
		    		
		    		
				if(!commentId) return
				
				
				
				
				
				axios.delete(`/comments/\${commentId}`).then(res => {
					const data = res.data
					
					commentModal.hide()
					
					getComments(currentPage)
					
				}).catch(error => {
					alert(error.response.data)
				})
				
			}
				 
			 
		    
		    
		})
		
		
	
		
		
		
		
		
		commentModifyForm.addEventListener('submit', (e) => {
			e.preventDefault()
			
			
			const formData = new FormData(commentModifyForm)
			const commentId = formData.get('commentId') 
			
			axios.put(`/comments/\${commentId}`, formData).then(res => {
				const data = res.data
				console.log(data)
				
				commentModal.hide()
				
				
				getComments(currentPage)
			}).catch(error => {
				alert(error.response.data)
			})
		
		})
		
		
	</script>

<%@include file="/WEB-INF/views/includes/footer.jsp" %>