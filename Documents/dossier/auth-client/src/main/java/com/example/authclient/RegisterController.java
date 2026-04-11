package com.example.authclient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RegisterController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordConfirmField;
    @FXML private Label strengthLabel;
    @FXML private Label messageLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    private void handlePasswordStrength() {
        String password = passwordField.getText();
        int score = calculateStrength(password);

        if (score < 3) {
            strengthLabel.setText("Force : Faible");
            strengthLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if (score < 5) {
            strengthLabel.setText("Force : Moyenne");
            strengthLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
        } else {
            strengthLabel.setText("Force : Forte");
            strengthLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }

    private int calculateStrength(String password) {
        int score = 0;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[^a-zA-Z0-9].*")) score++;
        return score;
    }

    @FXML
    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String passwordConfirm = passwordConfirmField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }
        if (!password.equals(passwordConfirm)) {
            showError("Les mots de passe ne correspondent pas");
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/register" +
                            "?email=" + email + "&password=" + password))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Inscription réussie !");
            } else {
                showError("Erreur : " + response.body());
            }
        } catch (Exception e) {
            showError("Erreur de connexion au serveur");
        }
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    @FXML
    private void handleGoToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AuthApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            AuthApplication.primaryStage.setTitle("Connexion");
            AuthApplication.primaryStage.setScene(scene);
        } catch (IOException e) {
            messageLabel.setText("Erreur chargement écran connexion");
        }
    }
}