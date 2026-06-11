<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/includes/header.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

	<div class="row justify-content-center">
		<div class="col-lg-6">
			<div class="card shadow mb-4">
				<div class="card-header py-3">
					<h5 class="m-0 font-weight-bold text-primary">회원가입</h5>
				</div>
				
				<div class="card-body">
					<form id="registerForm" action="/account/register" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<c:if test="${not empty errorMsg}">
    						<div class="alert alert-danger">${errorMsg}</div>
						</c:if>
						<div class="mb-3">
						    <label class="form-label">아이디</label>
						    <div class="input-group">
						        <input type="text" id="username" name="username" class="form-control" required>
						        <div class="input-group-append">
						            <button type="button" class="btn btn-outline-secondary" id="btnCheckUsername">확인</button>
						        </div>
						    </div>
						    <small id="usernameMsg" class="form-text"></small>
						</div>
						<div class="mb-3">
							<label class="form-label">비밀번호</label>
							<input type="password" id="password" name="password" class="form-control" required> 
						</div>
						<div class="mb-3">
							<label class="form-label">비밀번호 확인</label>
							<input type="password" id="passwordConfirm" name="passwordConfirm" class="form-control" required> 
						</div>
						<div class="mb-3">
							<label class="form-label">이름</label>
							<input type="text" name="name" class="form-control" required>
						</div>
						<div class="mb-3">
						    <label class="form-label">이메일</label>
						    <div class="input-group">
						        <input type="email" id="email" name="email" class="form-control" required>
						        <div class="input-group-append">
						            <button type="button" class="btn btn-outline-secondary" id="btnCheckEmail">중복확인</button>
						        </div>
						    </div>
						    <small id="emailMsg" class="form-text"></small>
						</div>
						
						<div class="d-flex justify-content-end">
							<button type="submit" class="btn btn-primary">등록</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	
	<script>
		
		
		let usernameChecked = false;
		let emailChecked = false;
		
		
		document.getElementById('username').addEventListener('input', function() {
		    usernameChecked = false;
		    document.getElementById('usernameMsg').textContent = '';
		});

		document.getElementById('email').addEventListener('input', function() {
		    emailChecked = false;
		    document.getElementById('emailMsg').textContent = '';
		});

		document.getElementById('btnCheckUsername').addEventListener('click', function() {
		    const username = document.getElementById('username').value
		    if(!username) {
		    	alert('아이디를 입력해주세요.')
		    	return
		    }
		    
		    if(username.length < 4) {
		    	alert('아이디는 4자 이상이어야합니다.')
		    	return
		    }
		    
		    axios.get('/account/checkUsername', { params: { username } })
		        .then(res => {
		            const msg = document.getElementById('usernameMsg')
		            if(res.data.duplicate) {
		                msg.textContent = '이미 사용 중인 아이디입니다.'
		                msg.style.color = 'red'
		                usernameChecked = false
		            } else {
		                msg.textContent = '사용 가능한 아이디입니다.'
		                msg.style.color = 'green'
		                usernameChecked = true
		            }
		        })
		})
		
		
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
		
		
		
		document.getElementById('registerForm').addEventListener('submit', function(e) {
		    if(!usernameChecked) { 
		    	e.preventDefault()
		    	alert('아이디 중복확인을 해주세요.')
		    	return
		    }
		    if(!emailChecked) {
		    	e.preventDefault()
		    	alert('이메일 중복확인을 헤주세요.') 
		    	return
		    }
		    
		    if(document.getElementById('password').value.length < 8) {
		    	e.preventDefault()
		    	alert('비밀번호는 8자 이상이어야합니다.')
		    	return
		    }
		    
		    if(document.getElementById('password').value !== document.getElementById('passwordConfirm').value) {
		        e.preventDefault()
		        alert('비밀번호가 일치하지 않습니다.')
		        return
		    }
		})
		
		
		
	
	</script>
	
	
	<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>