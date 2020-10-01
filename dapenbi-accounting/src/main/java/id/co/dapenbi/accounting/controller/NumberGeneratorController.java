package id.co.dapenbi.accounting.controller;

import id.co.dapenbi.accounting.entity.NumberGenerator;
import id.co.dapenbi.accounting.service.impl.NumberGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/akunting/number-generator")
public class NumberGeneratorController {
	@Autowired
	private NumberGeneratorService service;

	@PostMapping("/")
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> save(@RequestBody @Valid NumberGenerator request) {
		service.save(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> delete(@RequestBody @Valid NumberGenerator value) {
		service.delete(value.getId());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/findByName/{name}")
	@ResponseBody
	public ResponseEntity<NumberGenerator> findByName(@PathVariable String name) {
		return new ResponseEntity<>(service.findByName(name), HttpStatus.OK);
	}

	@GetMapping("/incrementByName/{name}")
	@ResponseBody
	public ResponseEntity<?> incrementByName(@PathVariable String name) {
		return new ResponseEntity<>(service.incrementByName(name), HttpStatus.OK);
	}
}
