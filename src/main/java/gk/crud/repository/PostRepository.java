package gk.crud.repository;

import gk.crud.entity.board.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostQuerydslRepository {
}
