package org.t34.service;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.t34.dao.UserDAO;
import org.t34.dto.LoginDTO;
import org.t34.dto.UserContextDTO;
import org.t34.entity.User;
import org.t34.exception.InvalidPasswordException;
import org.t34.exception.NotFoundException;
import org.t34.util.GeneralHelper;

import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public UserService(SessionFactory sessionFactory) {
        this.userDAO = new UserDAO(sessionFactory);
    }

    public UserContextDTO login(LoginDTO loginDTO) throws NotFoundException, InvalidPasswordException {
        logger.info("Entered login");
        Optional<User> optionalUser = userDAO.findByEmail(loginDTO.getEmail());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = optionalUser.get();

        if (!GeneralHelper.isPasswordMatch(loginDTO.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }
        logger.info("Exit login with valid user");
        return user.toUserContextDTO();

    }

    public String create(User user) {
        logger.info("Entered create");
        user.setPassword(GeneralHelper.hashPassword(user.getPassword()));
        userDAO.save(user);
        logger.info("Exit create success");
        return "OK";
    }

    public String update(User updatedUser, int userId) throws NotFoundException {
        logger.info("Entered update");
        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = optionalUser.get();
        user.setPassword(GeneralHelper.hashPassword(updatedUser.getPassword()));
        user.setAddress(updatedUser.getAddress());
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setContactNo(updatedUser.getContactNo());
        userDAO.update(user);
        logger.info("Exit update success");
        return "OK";
    }

    public User view(int userId) throws NotFoundException {
        logger.info("Entered view");
        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        logger.info("Exit view success");
        return optionalUser.get();
    }
}
