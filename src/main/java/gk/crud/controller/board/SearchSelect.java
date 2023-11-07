package gk.crud.controller.board;

public class SearchSelect {

    private String code;
    private String name;

    public SearchSelect(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
