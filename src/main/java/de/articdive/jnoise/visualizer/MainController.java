package de.articdive.jnoise.visualizer;

import de.articdive.jnoise.core.util.vectors.Vector;
import de.articdive.jnoise.generators.noisegen.opensimplex.SuperSimplexNoiseGenerator;
import de.articdive.jnoise.generators.noisegen.worley.WorleyNoiseGenerator;
import de.articdive.jnoise.generators.noisegen.worley.WorleyNoiseResult;
import de.articdive.jnoise.pipeline.JNoise;
import de.articdive.jnoise.pipeline.JNoiseDetailed;
import de.articdive.jnoise.transformers.domain_warp.DomainWarpTransformer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

public class MainController {
    private final MainModel model = new MainModel();
    @FXML
    private AnchorPane innerContainer;
    @FXML
    private ImageView imageView;
    @FXML
    private MenuItem exportMenuItem;

    public void initialize() {
        innerContainer.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));

        imageView.fitWidthProperty().bind(innerContainer.widthProperty().subtract(10));
        imageView.fitHeightProperty().bind(innerContainer.heightProperty().subtract(10));

        imageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() != newValue.intValue()) {
                model.setNoiseImage(createNewImage(imageView.getFitWidth(), newValue.doubleValue()));
            }
        });

        imageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() != newValue.intValue()) {
                model.setNoiseImage(createNewImage(newValue.doubleValue(), imageView.getFitHeight()));
            }
        });

        model.getNoiseImageProperty().addListener((observable, oldBufferedImage, newBufferedImage) -> {
            WritableImage wr;
            wr = new WritableImage(newBufferedImage.getWidth(), newBufferedImage.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < newBufferedImage.getWidth(); x++) {
                for (int y = 0; y < newBufferedImage.getHeight(); y++) {
                    pw.setArgb(x, y, newBufferedImage.getRGB(x, y));
                }
            }
            imageView.setImage(wr);
        });

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an output file.");
        fileChooser.setInitialDirectory(new File("."));

        exportMenuItem.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
            try {
                ImageIO.write(model.getNoiseImageProperty().get(), "png", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private BufferedImage createNewImage(double imageViewWidth, double imageViewHeight) {

        JNoiseDetailed<WorleyNoiseResult<Vector>> noise = JNoise.newBuilder()
                .worley(WorleyNoiseGenerator.newBuilder().setSeed(23).build())
                .addDetailedTransformer(
                        DomainWarpTransformer.newBuilder().setNoiseSource(
                                        SuperSimplexNoiseGenerator.newBuilder().build()
                                )
                                .build()
                )
                .scale(1 / 64.0)
                .buildDetailed();

//        JNoise worleyOctavated = JNoise.newBuilder()
//                .setNoiseSource(
//                                WorleyNoiseGenerator.newBuilder().build()
//                )
//                .octavate(5, 1.0, 0.5, FractalFunction.FBM, true)
//                .clamp(0.0, 1.0)
//                .scale(0.1)
//                .build();

//        JNoise noise = JNoise.newBuilder()
//                .combination(perlinOctavated, worleyOctavated, Combiner.ADD)
//                .clamp(0.0, 1.0)
//                .build();
//        JNoise noise = JNoise.newBuilder()
//                .worley(
//                        WorleyNoiseGenerator.newBuilder().setDistanceFunction(DistanceFunctionType.EUCLIDEAN)
//                                .setSeed(1337)
//                                .setDepth(1)
//                                .setMinFunction(MinimizationFunctionType.POLYNOMIAL_SMOOTH_MIN)
//                )
//                .addModifier(v -> v * 0.5)
//                .scale(0.010)
//                .clamp(0.0, 1.0)
//                .invert()
//                .build();

        int width = (int) imageViewWidth;
        int height = (int) imageViewHeight;

        if (width <= 0 || height <= 0) {
            return null;
        }

        HashMap<Vector, Integer> rgbFromPoint = new HashMap<>();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = -width / 2; x < width / 2; x++) {
            for (int y = -height / 2; y < height / 2; y++) {
//                double noiseValue = noise.evaluateNoise(x, y);
                WorleyNoiseResult<Vector> noiseResult = noise.evaluateNoiseResult(x, y);
                Vector closestPoint = noiseResult.getClosestPoint();
                int colour = rgbFromPoint.computeIfAbsent(closestPoint, point -> getIntFromColor(point.x() - (int) point.x(), point.y() - (int) point.y(), 0.5));

                bufferedImage.setRGB(x + width / 2, y + height / 2, colour);
            }
        }
        bufferedImage.setRGB(width / 2, height / 2, getIntFromColor(0.0, 1, 0.0));
        return bufferedImage;
    }

    public int getIntFromColor(double red, double green, double blue) {
        long R = Math.round(255 * red);
        long G = Math.round(255 * green);
        long B = Math.round(255 * blue);

        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return (int) (0xFF000000 | R | G | B);
    }
}