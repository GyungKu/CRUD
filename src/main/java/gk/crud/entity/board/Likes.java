package gk.crud.entity.board;

import gk.crud.entity.BaseEntity;
import gk.crud.entity.member.Member;
import lombok.Getter;

import javax.persistence.*;

@Entity @Getter
public class Likes extends BaseEntity {

    @Id @GeneratedValue(generator = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    protected Likes() {
    }

    public Likes(Member member, Post post) {
        this.member = member;
        this.post = post;
    }

    public void addLike() {
        post.plusLikeCount();
        post.getLikes().add(this);
        member.getLikes().add(this);
    }

    public void removeLike() {
        post.minusLikeCount();
        post.getLikes().remove(this);
        member.getLikes().remove(this);
    }

}
