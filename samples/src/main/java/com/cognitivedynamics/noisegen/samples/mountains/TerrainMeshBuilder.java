package com.cognitivedynamics.noisegen.samples.mountains;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

/**
 * Builds JavaFX 3D meshes from heightmap data.
 *
 * <p>Creates triangle meshes suitable for terrain rendering with:
 * <ul>
 *   <li>Proper vertex normals for lighting</li>
 *   <li>Texture coordinates for material mapping</li>
 *   <li>Configurable mesh resolution and scale</li>
 * </ul>
 */
public class TerrainMeshBuilder {

    /**
     * Build a MeshView from a heightmap.
     *
     * @param heightmap  2D array of height values [y][x]
     * @param meshWidth  Width of the mesh in world units
     * @param meshDepth  Depth of the mesh in world units
     * @param heightMult Height multiplier (vertical scale)
     * @return A MeshView ready to add to a 3D scene
     */
    public static MeshView buildTerrainMesh(float[][] heightmap,
                                            float meshWidth, float meshDepth,
                                            float heightMult) {
        int rows = heightmap.length;
        int cols = heightmap[0].length;

        TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);

        // Calculate step sizes
        float xStep = meshWidth / (cols - 1);
        float zStep = meshDepth / (rows - 1);

        // Generate vertices and normals
        // Points: x, y, z for each vertex
        // Normals: nx, ny, nz for each vertex
        // TexCoords: u, v for each vertex
        float[] points = new float[rows * cols * 3];
        float[] normals = new float[rows * cols * 3];
        float[] texCoords = new float[rows * cols * 2];

        int pointIdx = 0;
        int normalIdx = 0;
        int texIdx = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Position (centered on origin)
                float x = col * xStep - meshWidth / 2;
                float y = heightmap[row][col] * heightMult;
                float z = row * zStep - meshDepth / 2;

                points[pointIdx++] = x;
                points[pointIdx++] = y;
                points[pointIdx++] = z;

                // Calculate normal from neighboring heights
                float[] normal = calculateNormal(heightmap, row, col, xStep, zStep, heightMult);
                normals[normalIdx++] = normal[0];
                normals[normalIdx++] = normal[1];
                normals[normalIdx++] = normal[2];

                // Texture coordinates
                texCoords[texIdx++] = (float) col / (cols - 1);
                texCoords[texIdx++] = (float) row / (rows - 1);
            }
        }

        mesh.getPoints().addAll(points);
        mesh.getNormals().addAll(normals);
        mesh.getTexCoords().addAll(texCoords);

        // Generate faces (two triangles per grid cell)
        // Each face: p0, n0, t0, p1, n1, t1, p2, n2, t2
        int[] faces = new int[(rows - 1) * (cols - 1) * 2 * 9];
        int faceIdx = 0;

        for (int row = 0; row < rows - 1; row++) {
            for (int col = 0; col < cols - 1; col++) {
                int topLeft = row * cols + col;
                int topRight = topLeft + 1;
                int bottomLeft = (row + 1) * cols + col;
                int bottomRight = bottomLeft + 1;

                // Triangle 1: topLeft, topRight, bottomLeft (counter-clockwise from above)
                faces[faceIdx++] = topLeft;      // point
                faces[faceIdx++] = topLeft;      // normal
                faces[faceIdx++] = topLeft;      // texcoord
                faces[faceIdx++] = topRight;
                faces[faceIdx++] = topRight;
                faces[faceIdx++] = topRight;
                faces[faceIdx++] = bottomLeft;
                faces[faceIdx++] = bottomLeft;
                faces[faceIdx++] = bottomLeft;

                // Triangle 2: topRight, bottomRight, bottomLeft (counter-clockwise from above)
                faces[faceIdx++] = topRight;
                faces[faceIdx++] = topRight;
                faces[faceIdx++] = topRight;
                faces[faceIdx++] = bottomRight;
                faces[faceIdx++] = bottomRight;
                faces[faceIdx++] = bottomRight;
                faces[faceIdx++] = bottomLeft;
                faces[faceIdx++] = bottomLeft;
                faces[faceIdx++] = bottomLeft;
            }
        }

        mesh.getFaces().addAll(faces);

        // Create MeshView
        MeshView meshView = new MeshView(mesh);
        meshView.setCullFace(CullFace.NONE);  // Disable culling for terrain
        meshView.setDrawMode(DrawMode.FILL);

        return meshView;
    }

    /**
     * Calculate the surface normal at a heightmap point using central differences.
     */
    private static float[] calculateNormal(float[][] heightmap, int row, int col,
                                           float xStep, float zStep, float heightMult) {
        int rows = heightmap.length;
        int cols = heightmap[0].length;

        // Get neighboring heights (clamped to edges)
        float hL = heightmap[row][Math.max(0, col - 1)] * heightMult;
        float hR = heightmap[row][Math.min(cols - 1, col + 1)] * heightMult;
        float hD = heightmap[Math.max(0, row - 1)][col] * heightMult;
        float hU = heightmap[Math.min(rows - 1, row + 1)][col] * heightMult;

        // Calculate gradients
        float dx = (hR - hL) / (2 * xStep);
        float dz = (hU - hD) / (2 * zStep);

        // Normal is (-dx, 1, -dz) normalized
        float nx = -dx;
        float ny = 1.0f;
        float nz = -dz;

        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        return new float[] { nx / len, ny / len, nz / len };
    }

    /**
     * Create a rock/mountain material with slope-based coloring.
     */
    public static PhongMaterial createMountainMaterial() {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.rgb(140, 135, 130));  // Gray rock
        material.setSpecularColor(Color.rgb(60, 60, 60));
        material.setSpecularPower(10);
        return material;
    }

    /**
     * Create a snow-capped mountain material.
     */
    public static PhongMaterial createSnowMaterial() {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.rgb(245, 245, 250));  // Snow white
        material.setSpecularColor(Color.rgb(200, 200, 210));
        material.setSpecularPower(30);
        return material;
    }

    /**
     * Create a material with custom color.
     */
    public static PhongMaterial createMaterial(Color diffuse, Color specular, double specularPower) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(diffuse);
        material.setSpecularColor(specular);
        material.setSpecularPower(specularPower);
        return material;
    }
}
