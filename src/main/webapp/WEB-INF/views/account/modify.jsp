<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/views/includes/header.jsp" %>
	
	<div class="row justify-content-center">
		<div class="col-lg-6">
			<div class="card shadow mb-4">
				<div class="card-header py-3">
					<h5 class="m-0 font-weight-bold text-primary">회원정보 수정</h5>
				</div>
				<div class="card-body">
					<form id="modifyForm" action="/account/modify" method="post" enctype="multipart/form-data">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<c:if test="${not empty errorMsg}">
    						<div class="alert alert-danger">${errorMsg}</div>
						</c:if>
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">아이디</span></div>
							<input type="text" class="form-control" value="<c:out value='${account.username}'/>" readonly>
						</div>
						<div class="input-group input-group-lg mb-3">
							<div class="input-group-prepend"><span class="input-group-text">이름</span></div>
							<input type="text" class="form-control" value="<c:out value='${account.name}'/>" readonly>
						</div>
						
						
						<div class="input-group input-group-lg mb-3">
						    <div class="input-group-prepend"><span class="input-group-text">이메일</span></div>
						    <input type="text" class="form-control" id="email" name="email" value="<c:out value='${account.email}'/>" required>
						    <div class="input-group-append">
						        <button type="button" class="btn btn-outline-secondary" id="btnCheckEmail">중복확인</button>
						    </div>
						</div>
						<small id="emailMsg" class="form-text mb-4"></small>
						
						<c:if test="${not empty account.profileImg}">
							<input type="hidden" name="deleteProfileImg" id="deleteProfileImgFlag" value="false"/>
							<div class="mb-3">
								<label class="form-label font-weight-bold text-primary">프로필 이미지</label>
								<div class="row">
									<div class="col-md-3 mb-3">
										<div class="card">
											<a href="/images/profile/original/${account.profileImg}" target="_blank">
												<img src="/images/profile/thumbnail/s_${account.profileImg}" id="profileImgPreview" class="card-img-top img-fluid" alt="첨부파일"/>
											</a>
										</div>
										<button type="button" class="btn btn-danger btn-sm mt-2" id="deleteProfileImgBtn">
											삭제
										</button>
									</div>
								</div>
							</div>
						</c:if>
						
						<div class="mb-3">
						   <div class="input-group">
						       <div class="input-group-prepend">
						           <span class="input-group-text" style="min-width: 100px; justify-content: center;">
						                새 프로필 이미지 등록
						            </span>
						        </div>
						        <input type="file" name="file" class="form-control"/>
						    </div>
						</div>
				
					</form>
					<div class="float-right">
						<button type="submit" form="modifyForm" class="btn btn-warning">수정</button>
						<a class="btn btn-primary" href="/account/mypage">취소</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
	
		
		let emailChecked = true; // 처음엔 기존 이메일이니까 true
		const originalEmail = document.getElementById('email').value;

		document.getElementById('email').addEventListener('input', function() {
			document.getElementById('emailMsg').textContent = ''
			if(this.value === originalEmail) {
		        emailChecked = true
		    } else {
		        emailChecked = false
		    }
		});
		
		
	
		document.getElementById('btnCheckEmail').addEventListener('click', function() {
			
			const email = document.getElementById('email').value
			const msg = document.getElementById('emailMsg')
			
			if(!email) {
				msg.textContent = '이메일을 입력해주세요.'
				msg.style.color = 'red'
				return
			}
			
			const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		    if(!emailRegex.test(email)) {
		        
		        msg.textContent = '이메일 형식이 올바르지 않습니다.';
		        msg.style.color = 'red';
		        return
		    }
		    
		    
		    if(email == originalEmail) {
		    	msg.textContent = '기존 이메일을 사용합니다.'
		    	msg.style.color = 'green'
		    	emailChecked = true
		    	return
		    }
		    
		    	
		    	
		    axios.get('/account/checkEmail', { params: { email } })
			.then(res => {
					if(res.data.duplicate) {
						msg.textContent = '이미 사용 중인 이메일입니다.'
						msg.style.color = 'red'
						emailChecked = false
					} else {
						
						msg.textContent = '사용 가능한 이메일입니다.'
						msg.style.color = 'green'
						emailChecked = true
					}
			
			})
			
			
		    
		})
	
		document.getElementById('modifyForm').addEventListener('submit', function(e) {
			
			
			if(!emailChecked) {
				e.preventDefault()
				alert('이메일 중복확인을 해주세요.')
				return
			}
			
			
		})
		
		
		const img = document.getElementById('profileImgPreview')
			
		if(img) {
		
			document.getElementById('deleteProfileImgBtn').addEventListener('click', function(){
				
				const flag = document.getElementById('deleteProfileImgFlag').value
				
				
				if(flag == 'false') {
					
					document.getElementById('deleteProfileImgFlag').value = 'true'
					this.textContent = '삭제 취소'
					this.classList.remove('btn-danger')
					this.classList.add('btn-secondary')
					document.getElementById('profileImgPreview').style.opacity = '0.3';
					
				} else {
					
					document.getElementById('deleteProfileImgFlag').value = 'false'
					this.textContent = '삭제'
					this.classList.remove('btn-secondary')
					this.classList.add('btn-danger')
					document.getElementById('profileImgPreview').style.opacity = '1';
					
				}
				
				
			})
		}
			
			
			
		
		
		
		
		
	</script>

	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>