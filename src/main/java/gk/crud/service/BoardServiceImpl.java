package gk.crud.service;

import gk.crud.dto.CommentListDto;
import gk.crud.dto.PostCondition;
import gk.crud.dto.PostListDto;
import gk.crud.entity.board.Comment;
import gk.crud.entity.board.Likes;
import gk.crud.entity.board.Post;
import gk.crud.entity.member.Member;
import gk.crud.repository.CommentRepository;
import gk.crud.repository.LikesRepository;
import gk.crud.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService{

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;

    @Override
    public Long createPost(Post post) {
        return postRepository.save(post).getId();
    }

    @Override
    public Long createComment(Comment comment) {
        comment.create();
        return commentRepository.save(comment).getId();
    }

    @Override
    public void deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글입니다."));
        if(post.getMember().getId() == memberId) {
            postRepository.delete(post);
        }else {
            throw new RuntimeException("권한이 없습니다.");
        }
    }

    @Override
    public Long deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalStateException("존재하지 않는 댓글입니다."));
        if(comment.getMember().getId() == memberId) {
            Long postId = comment.getPost().getId();
            comment.removeComment();
            commentRepository.delete(comment);
            return postId;
        }else {
            throw new RuntimeException("권한이 없습니다.");
        }
    }

    @Override
    public Long updatePost(Post postUpdateParam, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글 입니다"));
        post.updatePost(postUpdateParam);
        return post.getId();
    }

    @Override
    public Long updateComment(Comment commentUpdateParam) {
        Comment comment = commentRepository.findById(commentUpdateParam.getId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 댓글입니다."));
        comment.updateComment(commentUpdateParam);
        return comment.getPost().getId();
    }

    @Override
    public Post postView(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글입니다."));
        post.plusViews();
        return post;
    }

    @Override
    public Post postOne(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글입니다."));
        return post;
    }

    @Override
    public Page<PostListDto> postList(Pageable pageable, PostCondition condition) {
        return postRepository.findSearchByAll(condition, pageable, null);
    }

    @Override
    public Page<PostListDto> myPostList(Pageable pageable, PostCondition condition, Member member) {

        return postRepository.findSearchMyPostByAll(condition, pageable, member, null);
    }

    @Override
    public Page<CommentListDto> commentsByPost(Long postId, Pageable pageable) {
        return commentRepository.findCommentsByPostId(postId, pageable);
    }

    @Override
    public Page<CommentListDto> myComments(Long memberId, Pageable pageable) {
        return commentRepository.findMyCommentsByMemberId(memberId, pageable);
    }

    @Override
    public Boolean hasLike(Member member, Post post) {
        return postRepository.findHasLike(member, post);
    }

    @Override
    public void like(Member member, Post post) {
        Likes like = new Likes(member, post);
        likesRepository.save(like);
        like.addLike();
    }

    @Override
    public void unLike(Member member, Post post) {
        Likes like = likesRepository.findByMemberAndPost(member, post);
        likesRepository.delete(like);
        like.removeLike();
    }

    @Override
    public Page<PostListDto> myLikeList(Pageable pageable, PostCondition condition, Member member) {
        return postRepository.findSearchMyLikesByPost(condition, pageable, member, null);
    }
}
