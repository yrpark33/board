<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/includes/header.jsp"%>
	
<div class="row justify-content-center">
	<div class="col-lg-12">
		<div class="card shadow mt-3 mb-4">
			<div class="card-header py-3">
				<h6 class="m-0 font-weight-bold text-secondary">Product Register</h6>
			</div>
			<div class="card-body">
				<form action="/product/register" method="post" class="p-3" enctype="multipart/form-data">
					<div class="mb-3">
						<label class="form-label">Product Name</label>
						<input type="text" name="pname" class="form-control">
					</div>
					<div class="mb-3">
						<label class="form-label">Product Desc</label>
						<textarea class="form-control" name="pdesc" rows="3"></textarea>
					</div>
					<div class="mb-3">
						<label class="form-label">Price</label>
						<input type="number" class="form-control" name="price">
					</div>
					<div class="mb-3">
						<label class="form-label">Image Files</label>
						<input type="file" name="files" class="form-control" multiple="multiple">
					</div>
					<div class="mb-3">
						<label class="form-label">Writer</label>
						<input type="text" class="form-control" name="writer">
					</div>
					<div class="d-flex justify-content-end">
						<button type="submit" class="btn btn-secondary btn-lg">Submit</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp"%>