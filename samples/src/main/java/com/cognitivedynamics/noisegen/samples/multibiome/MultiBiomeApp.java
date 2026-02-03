package com.cognitivedynamics.noisegen.samples.multibiome;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JavaFX application demonstrating multi-biome terrain generation
 * using the Node Graph System.
 *
 * <p>Features:
 * <ul>
 *   <li>Real-time terrain visualization with biome coloring</li>
 *   <li>Adjustable parameters (seed, scales, warp amplitude)</li>
 *   <li>Layer visualization modes (continental, mountains, hills, detail)</li>
 *   <li>Pan and zoom navigation</li>
 * </ul>
 */
public class MultiBiomeApp extends Application {

    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;

    private Canvas canvas;
    private MultiBiomeTerrain terrain;
    private TerrainRenderer renderer;
    private ExecutorService renderExecutor;

    // View state
    private double viewX = 0;
    private double viewY = 0;
    private double viewScale = 1.0;  // World units per pixel

    // Render mode
    private RenderMode renderMode = RenderMode.BIOME;

    // Controls
    private TextField seedField;
    private Slider continentalSlider;
    private Slider mountainSlider;
    private Slider hillSlider;
    private Slider detailSlider;
    private Slider warpSlider;
    private Label statusLabel;
    private ProgressIndicator progressIndicator;

    @Override
    public void start(Stage primaryStage) {
        renderExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "TerrainRenderer");
            t.setDaemon(true);
            return t;
        });

        // Initialize with default terrain
        terrain = new MultiBiomeTerrain(1337);
        renderer = new TerrainRenderer(terrain);

        // Build UI
        BorderPane root = new BorderPane();
        root.setCenter(createCanvasPane());
        root.setRight(createControlPanel());
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root);
        primaryStage.setTitle("Multi-Biome Terrain Generator - Node Graph Demo");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Initial render
        scheduleRender();
    }

    private Pane createCanvasPane() {
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        // Mouse drag for panning
        final double[] dragStart = new double[2];
        final double[] viewStart = new double[2];

        canvas.setOnMousePressed(e -> {
            dragStart[0] = e.getX();
            dragStart[1] = e.getY();
            viewStart[0] = viewX;
            viewStart[1] = viewY;
        });

        canvas.setOnMouseDragged(e -> {
            double dx = e.getX() - dragStart[0];
            double dy = e.getY() - dragStart[1];
            viewX = viewStart[0] - dx * viewScale;
            viewY = viewStart[1] - dy * viewScale;
            scheduleRender();
        });

        // Scroll for zoom
        canvas.setOnScroll(e -> {
            double factor = e.getDeltaY() > 0 ? 0.9 : 1.1;

            // Zoom centered on mouse position
            double mouseX = e.getX();
            double mouseY = e.getY();
            double worldX = viewX + mouseX * viewScale;
            double worldY = viewY + mouseY * viewScale;

            viewScale *= factor;
            viewScale = Math.max(0.1, Math.min(100, viewScale));

            viewX = worldX - mouseX * viewScale;
            viewY = worldY - mouseY * viewScale;

            scheduleRender();
        });

        StackPane pane = new StackPane(canvas);
        pane.setStyle("-fx-background-color: #1a1a1a;");
        return pane;
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(280);
        panel.setStyle("-fx-background-color: #2d2d2d;");

        // Title
        Label title = new Label("Terrain Parameters");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Seed control
        HBox seedBox = new HBox(10);
        seedBox.setAlignment(Pos.CENTER_LEFT);
        Label seedLabel = new Label("Seed:");
        seedLabel.setStyle("-fx-text-fill: white;");
        seedField = new TextField(String.valueOf(terrain.getSeed()));
        seedField.setPrefWidth(100);
        Button randomSeedBtn = new Button("Random");
        randomSeedBtn.setOnAction(e -> {
            seedField.setText(String.valueOf(new Random().nextInt(100000)));
            regenerateTerrain();
        });
        seedBox.getChildren().addAll(seedLabel, seedField, randomSeedBtn);

        // Scale sliders (createSlider sets the field references internally)
        VBox continentalBox = createSlider("Continental Scale", 0.0005, 0.01, terrain.getContinentalScale());
        VBox mountainBox = createSlider("Mountain Scale", 0.002, 0.05, terrain.getMountainScale());
        VBox hillBox = createSlider("Hill Scale", 0.005, 0.1, terrain.getHillScale());
        VBox detailBox = createSlider("Detail Scale", 0.02, 0.5, terrain.getDetailScale());
        VBox warpBox = createSlider("Warp Amplitude", 0, 200, terrain.getWarpAmplitude());

        // Regenerate button
        Button regenerateBtn = new Button("Regenerate Terrain");
        regenerateBtn.setMaxWidth(Double.MAX_VALUE);
        regenerateBtn.setOnAction(e -> regenerateTerrain());

        // View mode
        Label viewLabel = new Label("View Mode:");
        viewLabel.setStyle("-fx-text-fill: white;");

        ToggleGroup viewGroup = new ToggleGroup();
        VBox viewModes = new VBox(5);
        for (RenderMode mode : RenderMode.values()) {
            RadioButton rb = new RadioButton(mode.displayName);
            rb.setToggleGroup(viewGroup);
            rb.setStyle("-fx-text-fill: white;");
            rb.setSelected(mode == renderMode);
            rb.setOnAction(e -> {
                renderMode = mode;
                scheduleRender();
            });
            viewModes.getChildren().add(rb);
        }

        // Reset view button
        Button resetViewBtn = new Button("Reset View");
        resetViewBtn.setMaxWidth(Double.MAX_VALUE);
        resetViewBtn.setOnAction(e -> {
            viewX = 0;
            viewY = 0;
            viewScale = 1.0;
            scheduleRender();
        });

        // Biome legend
        Label legendLabel = new Label("Biome Legend:");
        legendLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        VBox legend = createBiomeLegend();

        // Instructions
        Label instructions = new Label(
            "Controls:\n" +
            "• Drag to pan\n" +
            "• Scroll to zoom\n" +
            "• Adjust sliders and click Regenerate"
        );
        instructions.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11px;");

        panel.getChildren().addAll(
            title,
            new Separator(),
            seedBox,
            continentalBox,
            mountainBox,
            hillBox,
            detailBox,
            warpBox,
            regenerateBtn,
            new Separator(),
            viewLabel,
            viewModes,
            resetViewBtn,
            new Separator(),
            legendLabel,
            legend,
            new Separator(),
            instructions
        );

        return panel;
    }

    private VBox createSlider(String name, double min, double max, double value) {
        VBox box = new VBox(2);

        Label label = new Label(String.format("%s: %.4f", name, value));
        label.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");

        Slider slider = new Slider(min, max, value);
        slider.setShowTickMarks(true);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            label.setText(String.format("%s: %.4f", name, newVal.doubleValue()));
        });

        // Store slider reference based on name
        switch (name) {
            case "Continental Scale" -> continentalSlider = slider;
            case "Mountain Scale" -> mountainSlider = slider;
            case "Hill Scale" -> hillSlider = slider;
            case "Detail Scale" -> detailSlider = slider;
            case "Warp Amplitude" -> warpSlider = slider;
        }

        box.getChildren().addAll(label, slider);
        return box;
    }

    private VBox createBiomeLegend() {
        VBox legend = new VBox(3);
        for (BiomeType biome : BiomeType.values()) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);

            Region colorBox = new Region();
            colorBox.setPrefSize(16, 16);
            colorBox.setStyle(String.format(
                "-fx-background-color: rgb(%d,%d,%d); -fx-border-color: #555;",
                (int)(biome.getBaseColor().getRed() * 255),
                (int)(biome.getBaseColor().getGreen() * 255),
                (int)(biome.getBaseColor().getBlue() * 255)
            ));

            Label nameLabel = new Label(biome.getDisplayName());
            nameLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 10px;");

            row.getChildren().addAll(colorBox, nameLabel);
            legend.getChildren().add(row);
        }
        return legend;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(15);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: #1a1a1a;");

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #888888;");

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(16, 16);
        progressIndicator.setVisible(false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label coordsLabel = new Label();
        coordsLabel.setStyle("-fx-text-fill: #888888;");

        canvas.setOnMouseMoved(e -> {
            double worldX = viewX + e.getX() * viewScale;
            double worldY = viewY + e.getY() * viewScale;
            double elevation = terrain.getHeight(worldX, worldY);
            BiomeType biome = BiomeType.fromElevation(elevation);
            coordsLabel.setText(String.format(
                "World: (%.1f, %.1f) | Elevation: %.3f | Biome: %s | Scale: %.2f",
                worldX, worldY, elevation, biome.getDisplayName(), viewScale
            ));
        });

        statusBar.getChildren().addAll(progressIndicator, statusLabel, spacer, coordsLabel);
        return statusBar;
    }

    private void regenerateTerrain() {
        try {
            int seed = Integer.parseInt(seedField.getText());
            double continental = continentalSlider.getValue();
            double mountain = mountainSlider.getValue();
            double hill = hillSlider.getValue();
            double detail = detailSlider.getValue();
            double warp = warpSlider.getValue();

            terrain = new MultiBiomeTerrain(seed, continental, mountain, hill, detail, warp);
            renderer = new TerrainRenderer(terrain);
            scheduleRender();
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid seed value");
        }
    }

    private void scheduleRender() {
        progressIndicator.setVisible(true);
        statusLabel.setText("Rendering...");

        renderExecutor.submit(() -> {
            long start = System.currentTimeMillis();

            Platform.runLater(() -> {
                switch (renderMode) {
                    case BIOME -> renderer.renderBiome(canvas, viewX, viewY, viewScale);
                    case GRAYSCALE -> renderer.renderGrayscale(canvas, viewX, viewY, viewScale);
                    case CONTINENTAL -> renderer.renderContinental(canvas, viewX, viewY, viewScale);
                    case MOUNTAINS -> renderer.renderMountains(canvas, viewX, viewY, viewScale);
                    case HILLS -> renderer.renderHills(canvas, viewX, viewY, viewScale);
                    case DETAIL -> renderer.renderDetail(canvas, viewX, viewY, viewScale);
                }

                long elapsed = System.currentTimeMillis() - start;
                statusLabel.setText(String.format("Rendered in %d ms", elapsed));
                progressIndicator.setVisible(false);
            });
        });
    }

    @Override
    public void stop() {
        renderExecutor.shutdownNow();
    }

    /**
     * Available render modes for visualization.
     */
    enum RenderMode {
        BIOME("Biome Colors"),
        GRAYSCALE("Grayscale Height"),
        CONTINENTAL("Continental Layer"),
        MOUNTAINS("Mountain Layer"),
        HILLS("Hills Layer"),
        DETAIL("Detail Layer");

        final String displayName;

        RenderMode(String displayName) {
            this.displayName = displayName;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
