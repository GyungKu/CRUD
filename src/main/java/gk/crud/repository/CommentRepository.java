package gk.crud.repository;

import gk.crud.dto.CommentListDto;
import gk.crud.entity.board.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //게시글 댓글 목록
    @Query("select new gk.crud.dto.CommentListDto(c.id, m.userId, p.id, c.content, c.creationDate, c.lastModifiedDate) from " +
            "Comment c left join c.member m left join c.post p where p.id = :id")
    public Page<CommentListDto> findCommentsByPostId(@Param("id") Long id, Pageable pageable);

    //내 댓글 목록
    @Query("select new gk.crud.dto.CommentListDto(c.id, m.userId, p.id, c.content, c.creationDate, c.lastModifiedDate) from " +
            "Comment c left join c.member m left join c.post p where m.id = :id")
    public Page<CommentListDto> findMyCommentsByMemberId(@Param("id") Long id, Pageable pageable);

}
