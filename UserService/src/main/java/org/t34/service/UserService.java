package org.t34.service;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.t34.dao.UserDAO;
import org.t34.dto.LoginDTO;
import org.t34.dto.Status;
import org.t34.dto.UserContextDTO;
import org.t34.entity.User;
import org.t34.util.GeneralHelper;

import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    public UserService(SessionFactory sessionFactory) {
        this.userDAO = new UserDAO(sessionFactory);
    }

    public UserContextDTO login(LoginDTO loginDTO) {
        logger.info("Entered login");
        Optional<User> optionalUser = userDAO.findByEmail(loginDTO.getEmail());
        if (optionalUser.isEmpty()) {
            logger.info("Exit login with user not found");
            return new UserContextDTO(Status.NOT_FOUND);
        }
        User user = optionalUser.get();

        if (!GeneralHelper.isPasswordMatch(loginDTO.getPassword(), user.getPassword())) {
            logger.info("Exit login with wrong user password");
            return new UserContextDTO(Status.WRONG_PASSWORD);
        }
        logger.info("Exit login with valid user");
        return user.toUserContextDTO();

    }

    public boolean create(User user) {
        user.setPassword(GeneralHelper.hashPassword(user.getPassword()));
        userDAO.save(user);
        return true;
    }
}
