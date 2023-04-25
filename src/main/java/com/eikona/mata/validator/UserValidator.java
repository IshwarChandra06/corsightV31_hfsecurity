package com.eikona.mata.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.constants.UserConstants;
import com.eikona.mata.entity.User;





@Component
public class UserValidator implements Validator {
//    @Autowired
//    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserConstants.EMAIL, UserConstants.NOT_EMPTY);
        if (user.getUserName().length() < NumberConstants.SIX || user.getUserName().length() > NumberConstants.THIRTY_TWO) {
            errors.rejectValue(UserConstants.EMAIL, UserConstants.SIZE_USER_EMAIL);
        }
//        if (userService.findByEmail(user.getEmail()) != null) {
//            errors.rejectValue("email", "Duplicate.userForm.email");
//        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserConstants.PASSWORD, UserConstants.NOT_EMPTY);
        if (user.getPassword().length() < NumberConstants.EIGHT || user.getPassword().length() > NumberConstants.THIRTY_TWO) {
            errors.rejectValue(UserConstants.PASSWORD, UserConstants.SIZE_USER_PASSWORD);
        }

        
    }
}