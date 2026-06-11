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
					<h5 class="m-0 font-weight-bold text-primary">게시물 수정</h5>
				</div>
				<div class="card-body">
					<form id="modifyForm" action="/board/modify" method="post" enctype="multipart/form-data">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<c:if test="${not empty errorMsg}">
    						<div class="alert alert-danger">${errorMsg}</div>
						</c:if>
						<input type="hidden" name="boardId" value="${board.boardId}">
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">제목</span></div>
							<input type="text" name="title" class="form-control" value="<c:out value='${board.title}'/>" required>
						</div>
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">내용</span></div>
							<textarea name="content" class="form-control" rows="3" required><c:out value='${board.content}'/></textarea>
						</div>
						<div class="mb-3">
						   <div class="input-group">
						       <div class="input-group-prepend">
						           <span class="input-group-text" style="min-width: 100px; justify-content: center;">
						                첨부파일
						            </span>
						        </div>
						        <input type="file" name="addedFiles" class="form-control" multiple/>
						    </div>
						</div>
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">작성자</span></div>
							<input type="text" class="form-control" value="<c:out value='${board.writer}'/>" readonly/>
						</div>
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">작성 시간</span></div>	
							<input type="text" class="form-control" value="<c:out value='${board.createdTime}'/>" readonly>
						</div>
						
					<c:if test="${not empty board.files}">
						<div class="mb-3 boardFiles">
							<label class="form-label font-weight-bold text-primary">첨부파일</label>
							<div class="row">
								<c:forEach var="file" items="${board.files}">
									<div class="col-md-3 mb-3">
										<div class="card position-relative">
											<a href="/images/board/original/${file.uuid}_${file.fileName}" target="_blank">
												<img src="/images/board/original/${file.uuid}_${file.fileName}" class="card-img-top img-fluid" alt="첨부파일"/>
											</a>
											<button type="button" class="btn btn-danger btn-sm position-absolute top-0 m-2 delete-file-btn" data-uuid="${file.uuid}" data-filename="${file.fileName}" data-image="${file.image}">삭제</button>
										</div>
									</div>
								</c:forEach>
							</div>
						</div>
					</c:if>
				
						
					</form>
					<div class="float-right">
						<a class="btn btn-info" href="/board/list?${dto.toQueryString()}">목록</a>
						
						<sec:authentication property="principal" var="secInfo"/>
						<sec:authentication property="authorities" var="roles"/>
						
						<c:if test="${!board.deleted && (secInfo.username == board.writer || fn:contains(roles, 'ROLE_ADMIN'))}">
							<button type="submit" form="modifyForm" class="btn btn-warning">수정</button>
							<button type="button" form="modifyForm" class="btn btn-danger btnRemove">삭제</button>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
	

		document.addEventListener('DOMContentLoaded', () => {
			
			
			const boardFiles = document.querySelector('.boardFiles')
			
			const formObj = document.querySelector('#modifyForm')
			
			const btnRemove = document.querySelector('.btnRemove')
		
			
			
			let fileState = [];
			
			
			btnRemove.addEventListener('click', () => {
				
				if(!confirm('정말 삭제하시겠습니까?')) return;
				
                formObj.action = '/board/remove'
                formObj.method = 'post'
                formObj.submit()
            }) 
			
			
			function initFileState() {
				const fileButtons = document.querySelectorAll('.boardFiles button')
				
				fileButtons.forEach(btn => {
					fileState.push({
						uuid: btn.getAttribute('data-uuid'),
						fileName: btn.getAttribute('data-filename'),
						image: btn.getAttribute('data-image') === 'true',
						deleted: false //삭제 여부 플래그
					})
				})
				
			}
			
			initFileState()
			
			boardFiles.addEventListener('click', (e) => {
				const target = e.target
				const uuid = target.getAttribute('data-uuid')
				
				if(!uuid) return
				
				e.preventDefault()

				const file = fileState.find(f => f.uuid === uuid)
				file.deleted = !file.deleted
				
				const divObj = target.closest('.col-md-3')
				divObj.style.opacity = file.deleted ? '0.3' : '1'
				divObj.style.filter = file.deleted ? 'grayscale(100%)' : 'none'
				target.innerText = file.deleted ? '삭제 취소' : '삭제'
				
			})
			
			
			formObj.addEventListener('submit', (e) => {
				
				e.preventDefault()
				
				const oldFiles = fileState.filter(f => !f.deleted)
				const deletedFiles = fileState.filter(f => f.deleted)
				
				const addJsonInput = (name, dataArray) => {
					const input = document.createElement('input')
					input.type = 'hidden'
					input.name = name
					input.value = JSON.stringify(dataArray)
					formObj.appendChild(input)
				}
				
				addJsonInput('oldFileInfosJson', oldFiles)
				addJsonInput('deletedFileInfosJson', deletedFiles)
				
				formObj.action = '/board/modify'
				formObj.method = 'post'
				formObj.submit()
				
				
			})
			
			
			
			
			
			
})
		
		
		
		
		
	</script>
	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>