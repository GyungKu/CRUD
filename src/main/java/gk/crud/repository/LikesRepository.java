package gk.crud.repository;

import gk.crud.entity.board.Likes;
import gk.crud.entity.board.Post;
import gk.crud.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Likes findByMemberAndPost(Member member, Post post);
}
