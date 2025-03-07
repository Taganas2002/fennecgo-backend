package com.fennec.fennecgo.services.Implementation;


import com.fennec.fennecgo.dto.mapper.UserMapper;
import com.fennec.fennecgo.dto.request.UserSearchRequest;
import com.fennec.fennecgo.dto.response.UserSearchResponse;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.services.Interface.UserSearchService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSearchServiceImpl.class);

    @PersistenceContext
    private final EntityManager entityManager;

    private final UserMapper userMapper;

    @Override
    public List<UserSearchResponse> searchUsers(UserSearchRequest request) {
        String normalizedQuery = request.getQuery() == null ? "" : request.getQuery().trim().toLowerCase();
        LOGGER.info("Starting search for users with query: {}", normalizedQuery);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        // Build predicates for phoneNumber and emailAddress (case-insensitive)
        Predicate phonePredicate = cb.like(cb.lower(root.get("phone")), "%" + normalizedQuery + "%");
        Predicate emailPredicate = cb.like(cb.lower(root.get("email")), "%" + normalizedQuery + "%");

        // Combine predicates using OR
        Predicate finalPredicate = cb.or(phonePredicate, emailPredicate);
        cq.select(root).where(finalPredicate);

        List<User> users = entityManager.createQuery(cq).getResultList();
        LOGGER.info("Found {} users matching query '{}'", users.size(), normalizedQuery);

        return users.stream()
                .map(userMapper::toUserSearchResponse)
                .collect(Collectors.toList());
    }
}
