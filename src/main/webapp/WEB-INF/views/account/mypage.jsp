<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/includes/header.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

	<div class="row justify-content-center">
		<div class="col-lg-6">
			<div class="card shadow mb-4">
				<div class="card-header py-3">
					<h5 class="m-0 font-weight-bold text-primary">회원정보</h5>
				</div>
				
				<div class="card-body">
					<div class="input-group input-group-lg mb-3">
						<div class="input-group-prepend"><span class="input-group-text">아이디</span></div>
						<input class="form-control" type="text" name="username" value="<c:out value='${account.username}'/>" readonly>
					</div>
					<div class="input-group input-group-lg mb-3">
						<div class="input-group-prepend"><span class="input-group-text">이름</span></div>
						<input class="form-control" type="text" value="<c:out value='${account.name}'/>" readonly>
					</div>
					<div class="input-group input-group-lg mb-3">
						<div class="input-group-prepend"><span class="input-group-text">이메일</span></div>
						<input type="text" class="form-control" value="<c:out value='${account.email}'/>" readonly/>
					</div>
					<div class="input-group input-group-lg mb-3">
						<div class="input-group-prepend"><span class="input-group-text">가입일</span></div>
						<input type="text" class="form-control" value="<c:out value='${account.createdTime}'/>" readonly>
					</div>
					
					
					<c:if test="${not empty account.profileImg}">
						<div class="mb-3">
							<label class="form-label font-weight-bold text-primary">프로필 이미지</label>
							<div class="row">
								<div class="col-md-3 mb-3">
									<div class="card">
											<a href="/images/profile/original/${account.profileImg}" target="_blank">
												<img src="/images/profile/thumbnail/s_${account.profileImg}" class="card-img-top img-fluid" alt="첨부파일"/>
											</a>
										</div>
								</div>
							</div>
						</div>
					</c:if>
					
					
					<div class="d-flex justify-content-between">
					    <form action="/account/remove" method="post">
					    	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
					    	<button type="submit" class="btn btn-danger" onclick="return confirm('정말 탈퇴하시겠습니까?')">회원탈퇴</button>
					    </form>
					    <div>
					        <button type="button" class="btn btn-secondary" data-toggle="modal" data-target="#passwordModal">비밀번호 변경</button>
					        <a class="btn btn-warning" href="/account/modify">회원정보 수정</a>
					    </div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
	<div class="modal fade" id="passwordModal" tabindex="-1">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h5 class="modal-title">비밀번호 변경</h5>
	                <button type="button" class="close" data-dismiss="modal">
	                    <span>&times;</span>
	                </button>
	            </div>
	            <div class="modal-body">
	                <div class="form-group">
	                    <label>현재 비밀번호</label>
	                    <div class="input-group">
	                        <input type="password" id="currentPassword" class="form-control"/>
	                        <div class="input-group-append">
	                            <button type="button" class="btn btn-outline-secondary" id="btnCheckPassword">확인</button>
	                        </div>
	                    </div>
	                    <small id="currentPasswordMsg" class="form-text"></small>
	                </div>
	                <div class="form-group">
	                    <label>새 비밀번호</label>
	                    <input type="password" id="newPassword" class="form-control"/>
	                </div>
	                <div class="form-group">
	                    <label>새 비밀번호 확인</label>
	                    <input type="password" id="newPasswordConfirm" class="form-control"/>
	                    <small id="passwordMsg" class="form-text"></small>
	                </div>
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-secondary" data-dismiss="modal">취소</button>
	                <button type="button" class="btn btn-primary" id="btnChangePassword">변경</button>
	            </div>
	        </div>
	    </div>
	</div>
	
	<script>
		
		let currentPasswordChecked = false	
		
		
		
		$('#passwordModal').on('hidden.bs.modal', function() {
		    document.getElementById('currentPassword').value = ''
		    document.getElementById('newPassword').value = ''
		    document.getElementById('newPasswordConfirm').value = ''
		    document.getElementById('currentPasswordMsg').textContent = ''
		    document.getElementById('passwordMsg').textContent = ''
		    currentPasswordChecked = false
		});
		
		
		
		document.getElementById('currentPassword').addEventListener('input', function() {
		    currentPasswordChecked = false;
		    document.getElementById('currentPasswordMsg').textContent = '';
		});
		
		document.getElementById('btnCheckPassword').addEventListener('click', function() {
			
			
			const password = document.getElementById('currentPassword').value
			const msg = document.getElementById('currentPasswordMsg');
			
			if(!password) {
				
				msg.textContent = '비밀번호를 입력해주세요.'
				msg.style.color = 'red'
				return
			}
			
			
			
			axios.post('/account/checkPassword', null, { params: { password } })
	        .then(res => {
	            
	            if(res.data.matches) {
	                msg.textContent = '비밀번호가 확인되었습니다.'
	                msg.style.color = 'green'
	                currentPasswordChecked = true
	            } else {
	                msg.textContent = '비밀번호가 일치하지 않습니다.'
	                msg.style.color = 'red'
	                currentPasswordChecked = false
	            }
	        });
			
			
			
		})
		
		document.getElementById('btnChangePassword').addEventListener('click', function() {
			
			const newPassword = document.getElementById('newPassword').value
			const newPasswordConfirm = document.getElementById('newPasswordConfirm').value
			
			if(!newPassword) {
				alert('새 비밀번호를 입력해주세요.')
				return
			}
			
			if(newPassword.length < 8) {
				alert('비밀번호는 8자 이상이어야합니다.')
				return
			}
			
			if(!currentPasswordChecked) {
				alert('현재 비밀번호를 확인해주세요.')
				return
			}
			
			if(newPassword !== newPasswordConfirm) {
		        
		        alert('비밀번호가 일치하지 않습니다.')
		        return
		    }
			
			axios.post('/account/changePassword', null, { params: { newPassword } })
		        .then(res => {
		            alert('비밀번호가 변경되었습니다. 다시 로그인해주세요.')
		            window.location.href = '/account/login'
		            
		        })
			
			
			
			
		})
		
		
		
		
		
		
		
	
	</script>
	
	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>