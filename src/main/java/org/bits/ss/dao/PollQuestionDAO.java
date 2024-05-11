package org.bits.ss.dao;

import org.bits.ss.service.PGService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PollQuestionDAO {
    private static final Logger logger = Logger.getLogger(PollQuestionDAO.class.getName());
    private final PGService pgService;

    public PollQuestionDAO(PGService pgService) {
        this.pgService = pgService;
    }

    public String generateCode(String description) {
        String questionId = null;
        try (Connection conn = pgService.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO questions (description, question) VALUES (?, '{\"quest\" : []}') RETURNING id")) {
            stmt.setString(1, description);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                questionId = rs.getString(1);
            }
        } catch (SQLException e) {
            logger.info("Exception " + e);
        }
        return questionId;
    }

    public boolean insertQuestion(String questionId, String question) {
        boolean isInserted = false;
        try (Connection conn = pgService.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE questions SET question = jsonb_insert(question, '{quest,0}', '\"" + question + "\"' ) WHERE id = ?")) {
            stmt.setString(1, questionId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                isInserted = true;
            }
        } catch (SQLException e) {
            logger.info("Exception " + e);
        }
        return isInserted;
    }

    public String getQuestions(String questionId) {
        String questions = null;
        try (Connection conn = pgService.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement("select json_build_object('description', description, 'question', question) from questions where UPPER(id) = UPPER(?)")) {
            stmt.setString(1, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                questions = rs.getString(1);
            }
        } catch (SQLException e) {
            logger.info("Exception " + e);
        }
        return questions;
    }
}
