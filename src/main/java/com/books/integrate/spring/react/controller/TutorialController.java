package com.books.integrate.spring.react.controller;

import java.util.*;

import com.books.integrate.spring.react.model.Tutorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books.integrate.spring.react.repository.TutorialRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class TutorialController {

	@Autowired
	TutorialRepository tutorialRepository;

	@GetMapping("/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
		try {
			List<Tutorial> tutorials = new ArrayList<Tutorial>();

			if (title == null)
				tutorialRepository.findAll().forEach(tutorials::add);
			else
				tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}


	@PostMapping("/tutorials")
	public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
		try {
			Tutorial _tutorial = tutorialRepository
					.save(new Tutorial(tutorial.getTitle(),tutorial.getDescription(),false,tutorial.getPrice()));
			return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PutMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			Tutorial _tutorial = tutorialData.get();
			_tutorial.setTitle(tutorial.getTitle());
			_tutorial.setDescription(tutorial.getDescription());
			_tutorial.setPublished(tutorial.isPublished());
			_tutorial.setPrice(tutorial.getPrice());
			return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

//HttpStatus
	@DeleteMapping("/tutorials/{id}")
	public ResponseEntity<String> deleteTutorial(@PathVariable("id") long id) {
		try {
			tutorialRepository.deleteById(id);
				return new ResponseEntity<>("Tutorials DELETE!! ",HttpStatus.NO_CONTENT);
			} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@DeleteMapping("/tutorials/delete/{title}")
	public ResponseEntity<String> deleteTutorialByTitle(@PathVariable("title") String title) {
		try{
			//Obtengo una lista de los tutoriales con el Título especificado
			List<Tutorial> tutorial = new ArrayList<>();
			tutorial = tutorialRepository.findByTitleContaining(title);
			//Itero entre los tutoriales, verifico que el Título sea el especificado, obtengo su ID y lo elimino.
			for(int i=0;i<tutorial.size();i++){
				if(tutorial.get(i).getTitle().equalsIgnoreCase(title)){
					//Obtengo el ID del tutorial a eliminar, y es eliminado.
					tutorialRepository.deleteById(tutorial.get(i).getId());
					return new ResponseEntity<>("El tutotiral con titulo "+title+" ha sido eliminado.",HttpStatus.OK);
				}
			}
			return new ResponseEntity<>("El tutotiral con titulo "+title+" no se ha podido eliminar.",HttpStatus.CONFLICT);
		}catch(Exception e){
			return new ResponseEntity<>("El tutotiral con titulo "+title+" no se ha podido eliminar.",HttpStatus.CONFLICT);
		}
	}
	@DeleteMapping("/tutorials")
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		try {
			tutorialRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}

	}

	@GetMapping("/tutorials/published")
	public ResponseEntity<List<Tutorial>> findByPublished() {
		try {
			List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PutMapping("/tutorials/update/{title}")
	public ResponseEntity<String> updateByTitle(@PathVariable("title") String title, @RequestBody Tutorial tutorial){
		try{
			//Obtengo una lista de los tutoriales con el Título especificado
			List<Tutorial> tutorialList = new ArrayList<>();
			tutorialList = tutorialRepository.findByTitleContaining(title);
			//Itero entre los tutoriales, verifico que el Título sea el especificado, y es actualizado.
			for(int i=0;i<tutorialList.size();i++){
				if(tutorialList.get(i).getTitle().equalsIgnoreCase(title)){
					tutorialList.get(i).setTitle(tutorial.getTitle());
					tutorialList.get(i).setDescription(tutorial.getDescription());
					tutorialList.get(i).setPublished(tutorial.isPublished());
					tutorialList.get(i).setPrice(tutorial.getPrice());
					tutorialRepository.save(tutorialList.get(i));
					return new ResponseEntity<>("El tutotiral con titulo "+title+" ha sido actualizado.",HttpStatus.OK);
				}
			}
			return new ResponseEntity<>("El tutotiral con titulo "+title+" no se ha podido actualizar.",HttpStatus.CONFLICT);
		}catch(Exception e){
			return new ResponseEntity<>("El tutotiral con titulo "+title+" no se ha podido actualizar.",HttpStatus.CONFLICT);
		}
	}

	@GetMapping("/tutorials/precio/{price}")
	public ResponseEntity<List<Tutorial>> getTutorialByPrice(@PathVariable("price") double price) {
		try{
			List<Tutorial> tutorialList = new ArrayList<>();
			tutorialList = tutorialRepository.findByPrice(price);
			return new ResponseEntity<>(tutorialList, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

}
