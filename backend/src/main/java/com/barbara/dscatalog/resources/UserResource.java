package com.barbara.dscatalog.resources;

import com.barbara.dscatalog.dto.ProductDTO;
import com.barbara.dscatalog.dto.UserDTO;
import com.barbara.dscatalog.dto.UserInsertDTO;
import com.barbara.dscatalog.dto.UserUpdateDTO;
import com.barbara.dscatalog.services.ProductService;
import com.barbara.dscatalog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "users")
public class UserResource {

    @Autowired
    private UserService service;
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        Page<UserDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserInsertDTO dto) {
       UserDTO newDto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDto.getId()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto) {
        UserDTO newDto = service.update(id, dto);
        return ResponseEntity.ok().body(newDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
