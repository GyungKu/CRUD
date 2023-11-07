package gk.crud.dto;

import gk.crud.entity.board.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostListDto {

    private String userId;
    private Long postId;
    private String title;
    private String content;
    private int views;
    private int commentCount;
    private int likeCount;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;


    public void convert(Post post) {
        userId = post.getMember().getUserId();
        postId = post.getId();
        title = post.getTitle();
        content = post.getContent();
        views = post.getViews();
        commentCount = post.getCommentCount();
        creationDate = post.getCreationDate();
        lastModifiedDate = post.getLastModifiedDate();
        likeCount = post.getLikeCount();
    }

}
