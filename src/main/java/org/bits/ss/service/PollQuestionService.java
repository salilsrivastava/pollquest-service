package org.bits.ss.service;

import org.bits.ss.dao.PollQuestionDAO;

public class PollQuestionService {

    private final PollQuestionDAO pollQuestionDAO;

    public PollQuestionService(PollQuestionDAO pollQuestionDAO) {
        this.pollQuestionDAO = pollQuestionDAO;
    }

    public String generateCode(String description) {
        return pollQuestionDAO.generateCode(description);
    }

    public boolean addQuestion(String questionId, String question) {
        return pollQuestionDAO.insertQuestion(questionId, question);
    }

    public String getQuestions(String questionId) {
        return pollQuestionDAO.getQuestions(questionId);
    }
}
