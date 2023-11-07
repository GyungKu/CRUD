package gk.crud.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentListDto {

    private Long id;
    private String userId;
    private Long postId;
    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;

    public CommentListDto(Long id, String userId, Long postId, String content, LocalDateTime creationDate, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
    }
}
