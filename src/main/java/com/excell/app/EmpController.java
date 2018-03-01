package com.excell.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;
import com.poiji.option.PoijiOptions.PoijiOptionsBuilder;

@RestController
public class EmpController {

	@GetMapping(path = "/test")
	public ResponseEntity<Object> test() {
		List<Employee> employees = new ArrayList<>();
		employees.add(new Employee(101, "Shahzad Hussain", "husssainshahzad", 26, true, "07/02/1992"));
		return new ResponseEntity<Object>(employees, HttpStatus.OK);
	}

	@PostMapping(path = "/hello")
	public ResponseEntity<Object> getObj(@RequestParam("file") MultipartFile mFile)
			throws IllegalStateException, IOException {

		File file = new File(mFile.getOriginalFilename());
		/**
		 * mFile.transferTo(file);
		 */

		PoijiOptions options = PoijiOptionsBuilder.settings(1).build();
		List<Employee> employees = Poiji.fromExcel(file, Employee.class, options);
		return new ResponseEntity<Object>(employees, HttpStatus.OK);
	}
}
