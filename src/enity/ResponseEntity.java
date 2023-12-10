package enity;

public class ResponseEntity {
    private String answer;
    private int code;

    public ResponseEntity(String answer, int code) {
        this.answer = answer;
        this.code = code;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
