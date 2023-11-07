package gk.crud.service;

import gk.crud.dto.CommentListDto;
import gk.crud.dto.PostCondition;
import gk.crud.dto.PostListDto;
import gk.crud.entity.board.Comment;
import gk.crud.entity.board.Post;
import gk.crud.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    Long createPost(Post post);
    Long createComment(Comment comment);
    void deletePost(Long postId, Long memberId);
    Long deleteComment(Long commentId, Long memberId);
    Long updatePost(Post postUpdateParam, Long postId);
    Long updateComment(Comment commentUpdateParam);

    Post postView(Long id);

    Post postOne(Long id);
    Page<PostListDto> postList(Pageable pageable, PostCondition condition);

    Page<PostListDto> myPostList(Pageable pageable, PostCondition condition, Member member);

    Page<CommentListDto> commentsByPost(Long postId, Pageable pageable);

    Page<CommentListDto> myComments(Long memberId, Pageable pageable);

    Boolean hasLike(Member member, Post post);

    void like(Member member, Post post);

    public void unLike(Member member, Post post);

    Page<PostListDto> myLikeList(Pageable pageable, PostCondition condition, Member member);
}
