package com.barbara.dscatalog.services.validation;

import com.barbara.dscatalog.dto.UserInsertDTO;
import com.barbara.dscatalog.entities.User;
import com.barbara.dscatalog.repositories.UserRepository;
import com.barbara.dscatalog.resources.exceptions.FieldMessage;

import java.util.ArrayList;
import java.util.List;

import com.barbara.dscatalog.services.validation.UserInsertValid;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {


    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        User user = repository.findByEmail(dto.getEmail());

        // Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

        if(user != null) {
            list.add(new FieldMessage("email", "Este email já existe."));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}