package SessionManagement;

public class Question {

    private String incorrect1;
    private String incorrect2;
    private String incorrect3;
    private String correct;
    private String statement;
    private String category;

    public Question(String incorrect1, String incorrect2, String incorrect3, String correct, String statement, String category) {
        this.incorrect1 = incorrect1;
        this.incorrect2 = incorrect2;
        this.incorrect3 = incorrect3;
        this.correct = correct;
        this.statement = statement;
        this.category = category;
    }


    public String getIncorrect1() {
        return incorrect1;
    }

    public String getIncorrect2() {
        return incorrect2;
    }

    public String getIncorrect3() {
        return incorrect3;
    }

    public String getCorrect() {
        return correct;
    }

    public String getStatement() {
        return statement;
    }

    public String getCategory() {
        return category;
    }

    public void setIncorrect1(String incorrect1) {
        this.incorrect1 = incorrect1;
    }

    public void setIncorrect2(String incorrect2) {
        this.incorrect2 = incorrect2;
    }

    public void setIncorrect3(String incorrect3) {
        this.incorrect3 = incorrect3;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
