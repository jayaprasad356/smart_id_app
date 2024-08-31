package com.app.smart_id_maker.model;

public class FAQS {
    String ques_no,question,answer;
    public FAQS(){

    }

    public FAQS(String ques_no, String question, String answer) {
        this.ques_no = ques_no;
        this.question = question;
        this.answer = answer;
    }

    public String getQues_no() {
        return ques_no;
    }

    public void setQues_no(String ques_no) {
        this.ques_no = ques_no;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
