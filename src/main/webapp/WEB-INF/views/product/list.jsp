<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<style>
	.deleted-row {
		
		background-color: #f0f0f0;
		color: #888;
		text-decoration: line-through;
		font-style: italic;
	
	}
	
	
	.deleted-row img {
		
		opacity: 0.4;
		
	}
	
</style>


<div class="row justify-content-center">
	<div class="col-lg-12">
		<div class="card shadow mt-3 mb-4">
			<div class="card-header py-3">
				<h6 class="m-0 font-weight-bold text-secondary">Product List</h6>
			</div>
			<div class="card-body">
				<table class="table table-bordered" id="dataTable">
					<thead class="table-secondary">
						<tr>
							<th>No</th>
							<th>Product Name</th>
							<th>Price</th>
							<th>Writer</th>
						</tr>
					</thead>
					<tbody class="tbody">
						<c:forEach var="product" items="${dto.productDTOList}">
							<tr data-pno="${product.pno}" class="${not product.sale ? 'deleted-row' : ''}">
								<td><c:out value='${product.pno}'/></td>
								<td>
									<img src="/images/s_${product.uuid}_${product.fileName}">
									<a href="/product/read/${product.pno}"><c:out value='${product.pname}'/></a>
								</td>
								<td><c:out value='${product.price}'/></td>
								<td><c:out value='${product.writer}'/></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				
				<div class="d-flex justify-content-center">
					<ul class="pagination">
						<c:if test="${dto.prev}">
							<li class="page-item">
								<a class="page-link" href="${dto.start - 1}" tabindex="-1">이전</a>
							</li>
						</c:if>
						<c:forEach var="num" items="${dto.pageNums}">
							<li class="page-item ${dto.page == num ? 'active' : ''}">
								<a class="page-link" href="${num}">${num}</a>
							</li>
						</c:forEach>
						
						<c:if test="${dto.next}">
							<li class="page-item">
								<a class="page-link" href="${dto.end + 1}">다음</a> 
							</li>
						</c:if>
					</ul>
				</div>
				
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="myModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel">Modal title</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        New Product Added
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary">Save changes</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript" defer="defer">
	const registered = '${registered}'
	const deleted = '${deleted}'
	
	
	
	const myModal = new bootstrap.Modal(document.getElementById("myModal"))
	
	if(registered) {
		console.log(registered)
		document.querySelector('.modal-body').innerHTML = `\${registered}번 상품이 등록되었습니다`
	}
	
	if(deleted) {
		console.log(deleted)
		document.querySelector('.modal-body').innerHTML = `\${deleted}번 상품이 삭제되었습니다`
	}
	
	if(registered || deleted) {
		myModal.show()
	}
	
	
	
	const pagination = document.querySelector(".pagination")
	
	pagination.addEventListener("click", e => {
		
		e.stopPropagation()
		e.preventDefault()
		
		const target = e.target
		
		const targetPage = target.getAttribute('href')
		
		const size = '${dto.size}' || 10
		
		const params = new URLSearchParams({
			page: targetPage,
			size: size
		})
		
		self.location = `/product/list?\${params.toString()}`
		
	})
	

	
</script>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>