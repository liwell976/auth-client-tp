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
import java.util.UUID;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            String nonce = UUID.randomUUID().toString();
            long timestamp = System.currentTimeMillis() / 1000;
            String message = email + ":" + nonce + ":" + timestamp;
            String hmac = HmacUtil.compute(password, message);

            String body = String.format(
                    "{\"email\":\"%s\",\"nonce\":\"%s\",\"timestamp\":%d,\"hmac\":\"%s\"}",
                    email, nonce, timestamp, hmac);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Connexion réussie ! Token : "
                        + response.body());
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Échec : " + response.body());
            }
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AuthApplication.class.getResource("register-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 450);
            AuthApplication.primaryStage.setTitle("Inscription");
            AuthApplication.primaryStage.setScene(scene);
        } catch (IOException e) {
            messageLabel.setText("Erreur chargement écran inscription");
        }
    }
}