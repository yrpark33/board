<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/includes/header.jsp"%>
	<div class="row justify-content-center">
		<div class="col-lg-12">
			<div class="card shadow mt-3 mb-4">
				<div class="card-header py-3">
					<h6 class="m-0 font-weight-bold text-secondary">Board Register</h6>
				</div>
				<div class="card-body">
					<form action="/board/register" method="post">
						
						<div class="mb-3">
							<label class="form-label">Title</label>
							<input type="text" name="title" class="form-control"/>
						</div>
						
						<div class="mb-3">
							<label class="form-label">Content</label>
							<textarea name="content" class="form-control" rows="3"></textarea>
						</div>
						
						<div class="mb-3">
							<label class="form-label">Writer</label>
							<input type="text" name="writer" class="form-control"/>
						</div>
						
						
						<div class="d-flex justify-content-end">
							<button type="submit" class="btn btn-secondary btn-lg">Submit</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
<%@include file="/WEB-INF/views/includes/footer.jsp"%>