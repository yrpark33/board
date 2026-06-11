package org.oolong.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("error")
public class ErrorController {
	
	@GetMapping("403")
	public void error403() {
		
	}
	
	@GetMapping("404")
	public void error404() {
	}

	@GetMapping("500")
	public void error500() {
		
	}
	
}
