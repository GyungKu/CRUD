package gk.crud.repository;

import gk.crud.dto.PostCondition;
import gk.crud.dto.PostListDto;
import gk.crud.entity.board.Post;
import gk.crud.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQuerydslRepository {
    Page<PostListDto> findSearchByAll(PostCondition condition, Pageable pageable, String query);

    Page<PostListDto> findSearchMyPostByAll(PostCondition condition, Pageable pageable, Member member, String query);

    Page<PostListDto> findSearchMyLikesByPost(PostCondition condition, Pageable pageable, Member memberQ, String query);

    Boolean findHasLike(Member member, Post post);
}
