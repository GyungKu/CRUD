package gk.crud.dto;

import lombok.Getter;

@Getter
public class PostCondition {

    private String selected;
    private String query;

    public PostCondition(String selected, String query) {
        this.selected = selected;
        this.query = query;
    }
}
