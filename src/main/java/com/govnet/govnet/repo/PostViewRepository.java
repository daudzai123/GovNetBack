package com.govnet.govnet.repo;

import com.govnet.govnet.entity.MyUser;
import com.govnet.govnet.entity.Post;
import com.govnet.govnet.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    boolean existsByPostAndUser(Post post, MyUser user);
    List<PostView> findByPost(Post post);
}

