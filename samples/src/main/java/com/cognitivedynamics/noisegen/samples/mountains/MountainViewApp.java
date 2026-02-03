package com.cognitivedynamics.noisegen.samples.mountains;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.Random;

/**
 * 3D Mountain Terrain Viewer using JavaFX 3D.
 *
 * <p>Demonstrates realistic mountain terrain generation with:
 * <ul>
 *   <li>3D mesh rendering with lighting</li>
 *   <li>Orbital camera controls (drag to rotate, scroll to zoom)</li>
 *   <li>Adjustable terrain parameters</li>
 *   <li>Real-time regeneration</li>
 * </ul>
 */
public class MountainViewApp extends Application {

    private static final int SCENE_WIDTH = 900;
    private static final int SCENE_HEIGHT = 700;
    private static final int MESH_RESOLUTION = 256;  // Grid size for terrain mesh

    // 3D scene components
    private SubScene subScene;
    private Group terrainGroup;
    private PerspectiveCamera camera;
    private MeshView currentMesh;

    // Camera state
    private double cameraDistance = 500;
    private double cameraAngleX = -25;  // Pitch (look down slightly)
    private double cameraAngleY = 30;   // Yaw (rotation around terrain)

    // Mouse drag tracking
    private double mouseOldX, mouseOldY;

    // Terrain state
    private MountainTerrain terrain;

    // UI Controls
    private TextField seedField;
    private Slider baseFreqSlider;
    private Slider ridgeFreqSlider;
    private Slider detailFreqSlider;
    private Slider warpSlider;
    private Slider heightSlider;
    private Slider meshSizeSlider;
    private Label statusLabel;
    private CheckBox wireframeCheck;

    @Override
    public void start(Stage primaryStage) {
        // Initialize terrain
        terrain = new MountainTerrain(1337);

        // Build UI
        BorderPane root = new BorderPane();
        root.setCenter(create3DView());
        root.setRight(createControlPanel());
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, SCENE_WIDTH + 280, SCENE_HEIGHT + 30);
        scene.setFill(Color.rgb(30, 30, 35));

        primaryStage.setTitle("3D Mountain Terrain - Node Graph Demo");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial terrain generation
        regenerateTerrain();
    }

    private Pane create3DView() {
        // Create 3D scene root
        terrainGroup = new Group();

        // Create camera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);   // Very small to avoid clipping when zoomed in
        camera.setFarClip(10000);
        camera.setFieldOfView(45);
        updateCameraPosition();

        // Add lighting
        AmbientLight ambient = new AmbientLight(Color.rgb(60, 60, 70));

        // Main directional light (sun)
        PointLight sunLight = new PointLight(Color.rgb(255, 250, 240));
        sunLight.setTranslateX(300);
        sunLight.setTranslateY(500);
        sunLight.setTranslateZ(-200);

        // Fill light from opposite side
        PointLight fillLight = new PointLight(Color.rgb(100, 110, 130));
        fillLight.setTranslateX(-200);
        fillLight.setTranslateY(200);
        fillLight.setTranslateZ(300);

        // Scene root
        Group root3D = new Group(terrainGroup, ambient, sunLight, fillLight);

        // Create SubScene for 3D content
        subScene = new SubScene(root3D, SCENE_WIDTH, SCENE_HEIGHT, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.rgb(135, 170, 200));  // Sky blue background
        subScene.setCamera(camera);

        // Mouse controls
        subScene.setOnMousePressed(this::handleMousePressed);
        subScene.setOnMouseDragged(this::handleMouseDragged);
        subScene.setOnScroll(this::handleScroll);

        StackPane pane = new StackPane(subScene);
        pane.setStyle("-fx-background-color: #1e1e23;");
        return pane;
    }

    private void handleMousePressed(MouseEvent event) {
        mouseOldX = event.getSceneX();
        mouseOldY = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        double deltaX = event.getSceneX() - mouseOldX;
        double deltaY = event.getSceneY() - mouseOldY;

        mouseOldX = event.getSceneX();
        mouseOldY = event.getSceneY();

        // Rotate camera around terrain
        cameraAngleY += deltaX * 0.5;
        cameraAngleX -= deltaY * 0.3;

        // Clamp pitch
        cameraAngleX = Math.max(-89, Math.min(-5, cameraAngleX));

        updateCameraPosition();
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        cameraDistance -= delta * 0.5;
        cameraDistance = Math.max(50, Math.min(1500, cameraDistance));
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        // Convert spherical coordinates to Cartesian
        double angleXRad = Math.toRadians(cameraAngleX);
        double angleYRad = Math.toRadians(cameraAngleY);

        double cosX = Math.cos(angleXRad);
        double sinX = Math.sin(angleXRad);
        double cosY = Math.cos(angleYRad);
        double sinY = Math.sin(angleYRad);

        double x = cameraDistance * cosX * sinY;
        double y = -cameraDistance * sinX;
        double z = cameraDistance * cosX * cosY;

        camera.getTransforms().clear();
        camera.getTransforms().addAll(
            new Translate(x, y, z),
            new Rotate(cameraAngleY, new Point3D(0, 1, 0)),
            new Rotate(-cameraAngleX, new Point3D(1, 0, 0))
        );

        // Look at origin
        camera.setTranslateX(x);
        camera.setTranslateY(y);
        camera.setTranslateZ(z);

        // Calculate look-at rotation
        double lookAtX = -x;
        double lookAtY = -y;
        double lookAtZ = -z;

        double length = Math.sqrt(lookAtX * lookAtX + lookAtY * lookAtY + lookAtZ * lookAtZ);
        lookAtX /= length;
        lookAtY /= length;
        lookAtZ /= length;

        double pitch = Math.toDegrees(Math.asin(lookAtY));
        double yaw = Math.toDegrees(Math.atan2(lookAtX, lookAtZ));

        camera.getTransforms().clear();
        camera.getTransforms().addAll(
            new Translate(x, y, z),
            new Rotate(-yaw, Rotate.Y_AXIS),
            new Rotate(pitch, Rotate.X_AXIS)
        );
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(12);
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
        seedField.setPrefWidth(80);
        Button randomBtn = new Button("Random");
        randomBtn.setOnAction(e -> {
            seedField.setText(String.valueOf(new Random().nextInt(100000)));
            regenerateTerrain();
        });
        seedBox.getChildren().addAll(seedLabel, seedField, randomBtn);

        // Sliders
        VBox baseFreqBox = createSliderBox("Base Frequency", 0.001, 0.01, terrain.getBaseFrequency(), v -> baseFreqSlider = v);
        VBox ridgeFreqBox = createSliderBox("Ridge Frequency", 0.002, 0.02, terrain.getRidgeFrequency(), v -> ridgeFreqSlider = v);
        VBox detailFreqBox = createSliderBox("Detail Frequency", 0.005, 0.1, terrain.getDetailFrequency(), v -> detailFreqSlider = v);
        VBox warpBox = createSliderBox("Warp Amplitude", 0, 200, terrain.getWarpAmplitude(), v -> warpSlider = v);
        VBox heightBox = createSliderBox("Height Scale", 0.3, 3.0, 1.0, v -> heightSlider = v);
        VBox meshSizeBox = createSliderBox("Terrain Size", 200, 600, 400, v -> meshSizeSlider = v);

        // Wireframe toggle
        wireframeCheck = new CheckBox("Wireframe Mode");
        wireframeCheck.setStyle("-fx-text-fill: white;");
        wireframeCheck.setOnAction(e -> updateWireframeMode());

        // Regenerate button
        Button regenerateBtn = new Button("Regenerate Terrain");
        regenerateBtn.setMaxWidth(Double.MAX_VALUE);
        regenerateBtn.setStyle("-fx-font-size: 14px;");
        regenerateBtn.setOnAction(e -> regenerateTerrain());

        // Camera reset
        Button resetCameraBtn = new Button("Reset Camera");
        resetCameraBtn.setMaxWidth(Double.MAX_VALUE);
        resetCameraBtn.setOnAction(e -> {
            cameraDistance = 500;
            cameraAngleX = -25;
            cameraAngleY = 30;
            updateCameraPosition();
        });

        // Instructions
        Label instructions = new Label(
            "Controls:\n" +
            "• Drag to rotate view\n" +
            "• Scroll to zoom\n" +
            "• Adjust sliders and Regenerate"
        );
        instructions.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11px;");

        panel.getChildren().addAll(
            title,
            new Separator(),
            seedBox,
            baseFreqBox,
            ridgeFreqBox,
            detailFreqBox,
            warpBox,
            heightBox,
            meshSizeBox,
            wireframeCheck,
            new Separator(),
            regenerateBtn,
            resetCameraBtn,
            new Separator(),
            instructions
        );

        return panel;
    }

    private VBox createSliderBox(String name, double min, double max, double value,
                                  java.util.function.Consumer<Slider> sliderConsumer) {
        VBox box = new VBox(2);

        Label label = new Label(String.format("%s: %.4f", name, value));
        label.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");

        Slider slider = new Slider(min, max, value);
        slider.setShowTickMarks(true);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            label.setText(String.format("%s: %.4f", name, newVal.doubleValue()));
        });

        sliderConsumer.accept(slider);

        box.getChildren().addAll(label, slider);
        return box;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(15);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: #1a1a1a;");

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #888888;");

        statusBar.getChildren().add(statusLabel);
        return statusBar;
    }

    private void regenerateTerrain() {
        long startTime = System.currentTimeMillis();

        try {
            int seed = Integer.parseInt(seedField.getText());
            double baseFreq = baseFreqSlider.getValue();
            double ridgeFreq = ridgeFreqSlider.getValue();
            double detailFreq = detailFreqSlider.getValue();
            double warp = warpSlider.getValue();
            double heightScale = heightSlider.getValue();
            float meshSize = (float) meshSizeSlider.getValue();

            // Create new terrain
            terrain = new MountainTerrain(seed, baseFreq, ridgeFreq, detailFreq, warp, heightScale);

            // Generate heightmap
            float[][] heightmap = terrain.generateHeightmapFloat(
                MESH_RESOLUTION, MESH_RESOLUTION,
                0, 0, 1.0
            );

            // Build mesh
            MeshView newMesh = TerrainMeshBuilder.buildTerrainMesh(
                heightmap,
                meshSize, meshSize,
                100  // Height multiplier for visual scale
            );

            // Apply material
            newMesh.setMaterial(TerrainMeshBuilder.createMountainMaterial());

            // Update wireframe mode
            if (wireframeCheck.isSelected()) {
                newMesh.setDrawMode(javafx.scene.shape.DrawMode.LINE);
            }

            // Replace old mesh
            if (currentMesh != null) {
                terrainGroup.getChildren().remove(currentMesh);
            }
            terrainGroup.getChildren().add(newMesh);
            currentMesh = newMesh;

            long elapsed = System.currentTimeMillis() - startTime;
            int triangles = (MESH_RESOLUTION - 1) * (MESH_RESOLUTION - 1) * 2;
            statusLabel.setText(String.format("Generated %d×%d mesh (%,d triangles) in %d ms",
                MESH_RESOLUTION, MESH_RESOLUTION, triangles, elapsed));

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid seed value");
        }
    }

    private void updateWireframeMode() {
        if (currentMesh != null) {
            currentMesh.setDrawMode(wireframeCheck.isSelected() ?
                javafx.scene.shape.DrawMode.LINE :
                javafx.scene.shape.DrawMode.FILL);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
