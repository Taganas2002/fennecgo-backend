package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.UserSearchRequest;
import com.fennec.fennecgo.dto.response.UserSearchResponse;

import java.util.List;

public interface UserSearchService {
    /**
     * Searches for users based on the given query (phone or email).
     *
     * @param request contains the search query.
     * @return a list of matching user responses.
     */
    List<UserSearchResponse> searchUsers(UserSearchRequest request);
}
