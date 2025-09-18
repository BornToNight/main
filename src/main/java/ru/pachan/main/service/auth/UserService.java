package ru.pachan.main.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.model.auth.RefreshToken;
import ru.pachan.main.model.auth.User;
import ru.pachan.main.repository.auth.RefreshTokenRepository;
import ru.pachan.main.repository.auth.UserRepository;
import ru.pachan.main.util.auth.TokenSearcher;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;
import static ru.pachan.main.util.enums.ExceptionEnum.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenSearcher tokenSearcher;

    public User createOne(User user) throws RequestException {
        if (user.getPassword().isBlank()) {
            throw new RequestException(REQUIRED_FIELDS_EMPTY.getMessage(), BAD_REQUEST);
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        User savedUser = repository.save(user);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(savedUser);
        refreshToken.setRefreshToken("emptyToken");
        refreshTokenRepository.save(refreshToken);
        return savedUser;
    }

    public PaginatedResponse<User> getAll(Pageable pageable) {
        Page<User> result = repository.findAll(pageable);
        return new PaginatedResponse<>(result.getTotalPages(), result.getContent());
    }

    public User getOne(long id, String token) throws RequestException {
        if (tokenSearcher.isAdmin(token) || tokenSearcher.isOriginalUser(token, id)) {
            User result = repository.findById(id).orElseThrow(() -> new RequestException(OBJECT_NOT_FOUND.getMessage(), GONE));
            result.setPassword("");
            return result;
        } else {
            throw new RequestException(ATTEMPT_TO_BYPASS_ACCESS.getMessage(), FORBIDDEN);
        }
    }

    public User updateOne(long id, String token, User user) throws RequestException {
        user.setId(id);
        User old = repository.findById(id).orElseThrow(() -> new RequestException(OBJECT_NOT_FOUND.getMessage(), GONE));
        if (Objects.equals(user.getLogin(), "admin")) {
            throw new RequestException(PERMISSION_DENIED.getMessage(), FORBIDDEN);
        }
        if (tokenSearcher.isAdmin(token)) {
            if (!user.getPassword().isBlank()) {
                user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            } else {
                user.setPassword(old.getPassword());
            }
            repository.save(user);
            return user;
        } else if (tokenSearcher.isOriginalUser(token, id)) {
            return old;
        } else {
            throw new RequestException(ATTEMPT_TO_BYPASS_ACCESS.getMessage(), FORBIDDEN);
        }
    }

    public void deleteOne(long id) throws RequestException {
        Optional<User> user = repository.findById(id);
        if (user.isPresent()) {
            if (Objects.equals(user.get().getLogin(), "admin")) {
                throw new RequestException(PERMISSION_DENIED.getMessage(), FORBIDDEN);
            }
        }
    }

}
