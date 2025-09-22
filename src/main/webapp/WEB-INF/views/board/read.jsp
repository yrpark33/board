<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/views/includes/header.jsp"%>
	<div class="row justify-content-center">
		<div class="col-lg-12">
			<div class="card shadow mt-3 mb-4">
				<div class="card-header py-3">
					<h6 class="m-0 font-weight-bold text-secondary">Board Read</h6>
				</div>
				<div class="card-body">
					<div class="mb-3 input-group input-group-lg">
						<span class="input-group-text">Bno</span>
						<input type="text" class="form-control"  value="<c:out value='${board.bno}'/>" readonly />
					</div>
					<div class="mb-3 input-group input-group-lg">
						<span class="input-group-text">Title</span>
						<input type="text" class="form-control" value="<c:out value='${board.title}'/>" readonly/>
					</div>
					<div class="mb-3 input-group input-group-lg">
						<span class="input-group-text">Content</span>
						<textarea class="form-control" rows="3" readonly><c:out value='${board.content}'/></textarea>
					</div>
					<div class="mb-3 input-group input-group-lg">
						<span class="input-group-text">Writer</span>
						<input type="text" class="form-control" value="<c:out value='${board.writer}'/>" readonly />
					</div>
					<div class="mb-3 input-group input-group-lg">
						<span class="input-group-text">RegDate</span>
						<input type="text" class="form-control" value="<c:out value='${board.createdDate}'/>" readonly />
					</div>
					
					
					<div class="float-end">
						<a href="/board/list">
							<button type="button" class="btn btn-info btnList">LIST</button>
						</a>
						
						<c:if test="${!board.delFlag}">
							<a href='/board/modify/${board.bno}'><button type="button" class="btn btn-warning btnModify">MODIFY</button></a> 
						</c:if>
					</div>
					
				</div>
			</div>
		</div>
	</div>
	
	<div class="col-lg-12">
		<div class="card shadow mb-4">
			<div class="m-4">
				<!-- 댓글 작성 폼 -->
				<form id="replyForm" class="mt-4">
					<!-- 게시글 번호 hidden 처리 -->
					<input type="hidden" name="bno" value="<c:out value='${board.bno}'/>" />
					
					<div class="mb-3 input-group input-group-lg">
						<span class="input-group-text">Replier</span>
						<input type="text" class="form-control" name="replier" required/>
					</div>
					
					<div class="mb-3 input-group input-group-lg">
						<span class="input-group-text">ReplyText</span>
						<textarea name="replyText" class="form-control" rows="3" required></textarea>
					</div>
					
					<div class="text-end">
						<button type="submit" class="btn btn-secondary addReplyBtn">Submit Reply</button>	
					</div>
				</form>
				<!-- 댓글 작성 폼 끝 -->
			</div>
		</div>
	</div>
	
	<div class="col-lg-12">
		
		<div class="card shadow mb-4">
			<div class="m-4">
				<ul class="list-group replyList">
					
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
				
				
				<div aria-label="댓글 페이지 네비게이션" class="mt-4">
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
	
	<div class="modal fade" id="replyModal" tabindex="-1" aria-hidden="true" aria-labelledby="replyModalLabel" data-bs-backdrop="static" data-bs-keyboard="false" >
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="replyModalLabel">댓글 수정 / 삭제</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
	        <form id="replyModForm">
	        	<input type="hidden" name="rno" value="33"/>
	        	<div class="mb-3">
	        		<label for="replyText" class="form-label">댓글 내용</label>
	        		<input type="text" name="replyText" id="replyText" class="form-control" value="Reply Text"/>
	        	</div>
	        </form>
	      </div>
	      <div class="modal-footer">
			<button type="button" class="btn btn-primary btnReplyMod">수정</button>
			<button type="button" class="btn btn-danger btnReplyDel">삭제</button> 
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
<script>
	const replyForm = document.querySelector("#replyForm");
	
	document.querySelector(".addReplyBtn").addEventListener("click", (e) => {
		
		e.preventDefault()
		e.stopPropagation()
		
		const formData = new FormData(replyForm)
		
		axios.post("/replies", formData).then(res => {
		
			console.log("-----------server response------------")
			console.log(res)
			replyForm.reset()
			
			getReplies(1, true);
			
			
		})
		
		
	}, false)
	
	
	let currentPage = 1
	let currentSize = 10
	
	const bno = '${board.bno}'
	
	function getReplies(pageNum, goLast) {
		
		axios.get(`/replies/\${bno}/list`, {
			params : {
				page: pageNum || currentPage,
				size: currentSize
			}
		}).then(res => {
			const data = res.data
			
			console.log(data)
			
			const {totalCount, page, size} = data
			
			if(goLast && totalCount > page * size) {
				//마지막 페이지를 계산
				const lastPage = Math.ceil(totalCount / size)
				getReplies(lastPage)
			} else {
				currentPage = page
				currentSize = size
				
				printReplies(data) //출력
				
			}
			
			
		})
		
	}
	
	getReplies(1, true);
	
	
	const replyList = document.querySelector(".replyList")
	
	
	function printReplies(data) {
		
		const {replyDTOList, page, size, prev, next, end, start, pageNums} = data
		
		let listStr = ''
		
		
		for(replyDTO of replyDTOList) {
			
			listStr += `<li class="list-group-item" data-rno="\${replyDTO.rno}">
							<div class="d-flex justify-content-between">
								 <div>
								 	<strong>\${replyDTO.rno}</strong> - \${replyDTO.replyText}
								 </div>
								 <div class="text-muted small">
								 	\${replyDTO.replyDate}
								 </div>
							</div>
							<div class="mt-1 text-secondary small">
								\${replyDTO.replier}
							</div>
						</li>`
		
		}
		
		replyList.innerHTML = listStr
		
		let pagingStr = ''
		
		if(prev) {
			pagingStr += `<li class="page-item">
							<a class="page-link" href="\${start - 1}" tabindex="-1">이전</a>			
						</li>`
			
		}
		
		for(let i of pageNums) {
				
			pagingStr += `<li class="page-item \${page === i ? 'active' : ''}">
							<a class="page-link" href="\${i}">\${i}</a>
						</li>`
			
		}
		
		if(next) {
			
			pagingStr += `<li class="page-item">
							<a class="page-link" href="\${end + 1}">다음</a>
						</li>`
		}
		
		
		document.querySelector(".pagination").innerHTML = pagingStr
		
		
	}
	
	
	document.querySelector(".pagination").addEventListener("click", (e) => {
		
		e.stopPropagation()
		e.preventDefault()
		
		const target = e.target
		
		const href = target.getAttribute("href")
		
		if(!href) {
			return
		}
		
		getReplies(href)
	
	})
	
	
	
	
	const replyModal = new bootstrap.Modal(document.querySelector("#replyModal"))
	
	const replyModForm = document.querySelector("#replyModForm")
	
	
	replyList.addEventListener("click", (e) => {
		
		const targetLi = e.target.closest("li")
		
		const rno = targetLi.getAttribute("data-rno")
		
		axios.get(`/replies/\${rno}`).then(res => {
			
			const targetReply = res.data
			
			if(targetReply.delFlag === false) {
				
				replyModForm.querySelector("input[name='rno']").value = targetReply.rno
				replyModForm.querySelector("input[name='replyText']").value = targetReply.replyText
				
				replyModal.show()
			
			} else {
				
				alert('삭제된 댓글은 조회할 수 없습니다.')
				
			}
			
			
		})
		
		
	})
	
	
	document.querySelector('.btnReplyDel').addEventListener('click', e => {
		
		e.stopPropagation()
		e.preventDefault()
		
		
		const formData = new FormData(replyModForm)
		
		const rno = formData.get("rno")
		
		axios.delete(`/replies/\${rno}`).then(res => {
			
			const data = res.data
			console.log(data)
		
			replyModal.hide()
			
			getReplies(currentPage)
			
			
		})
		
	})
	
	
	document.querySelector('.btnReplyMod').addEventListener('click', e => {
		
		e.stopPropagation()
		e.preventDefault()
		
		const formData = new FormData(replyModForm)
		
		const rno = formData.get("rno")
		
		axios.put(`/replies/\${rno}`, formData).then(res => {
			
			const data = res.data
			console.log(data)
			
			replyModal.hide()
			
			getReplies(currentPage)
			
		})
		
		
		
	})
	
	
	
	
</script>
<%@include file="/WEB-INF/views/includes/footer.jsp"%>