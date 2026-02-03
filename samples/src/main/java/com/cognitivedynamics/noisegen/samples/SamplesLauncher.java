package com.cognitivedynamics.noisegen.samples;

import com.cognitivedynamics.noisegen.samples.multibiome.MultiBiomeApp;
import com.cognitivedynamics.noisegen.samples.mountains.MountainViewApp;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main launcher for the FastNoiseLite Nouveau sample applications.
 *
 * <p>Provides a simple menu to select and launch different demo applications
 * that showcase the noise generation library's capabilities.
 */
public class SamplesLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #2d2d2d;");

        // Title
        Label title = new Label("FastNoiseLite Nouveau Samples");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Select a sample to launch:");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaaaaa;");

        // Sample buttons
        Button multiBiomeBtn = createSampleButton(
            "Multi-Biome Terrain Generator",
            "Demonstrates layered terrain composition using the Node Graph System.\n" +
            "Features continental shapes, mountains, hills, and domain warping.",
            () -> launchSample(MultiBiomeApp.class)
        );

        Button mountainsBtn = createSampleButton(
            "3D Mountain Terrain",
            "Realistic 3D mountain visualization with mesh rendering.\n" +
            "Features ridged noise, erosion detail, and orbital camera controls.",
            () -> launchSample(MountainViewApp.class)
        );

        // Placeholder for future samples
        Button comingSoon1 = createSampleButton(
            "Nebula Generator (Coming Soon)",
            "Volumetric nebula generation with curl noise and 4D animation.",
            null
        );
        comingSoon1.setDisable(true);

        Button comingSoon2 = createSampleButton(
            "Cave System Generator (Coming Soon)",
            "3D cave network generation using cellular and fractal noise.",
            null
        );
        comingSoon2.setDisable(true);

        root.getChildren().addAll(
            title,
            subtitle,
            multiBiomeBtn,
            mountainsBtn,
            comingSoon1,
            comingSoon2
        );

        Scene scene = new Scene(root, 500, 480);
        primaryStage.setTitle("FastNoiseLite Nouveau - Samples");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createSampleButton(String name, String description, Runnable action) {
        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(10));

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(380);

        content.getChildren().addAll(nameLabel, descLabel);

        Button button = new Button();
        button.setGraphic(content);
        button.setPrefWidth(420);
        button.setStyle(
            "-fx-background-color: #f0f0f0; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-cursor: hand;"
        );

        if (action != null) {
            button.setOnAction(e -> action.run());
            button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #e0e0e0; " +
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand;"
            ));
            button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #f0f0f0; " +
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand;"
            ));
        }

        return button;
    }

    private void launchSample(Class<? extends Application> appClass) {
        try {
            Stage stage = new Stage();
            Application app = appClass.getDeclaredConstructor().newInstance();
            app.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
