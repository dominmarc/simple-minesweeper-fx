package de.ifdgmbh.mad.minesweeper.util;

import javafx.scene.image.Image;

import java.io.IOException;

public class ImageUtils {

    //Class could be expanded to fit a more generic style, caching all sorts of Images

    private static Image iconImage;
    private static Image flagImage;

    /**
     * @return constant of icon image, must not be null
     * @throws IllegalStateException if icon can not be loaded
     */
    public static Image getIconImage() throws IllegalStateException {
        if (iconImage == null) {
            try (final var resource = ImageUtils.class.getResourceAsStream("/de/ifdgmbh/mad/minesweeper/images/bombeRed.png");) {
                if (resource == null) {
                    throw new IllegalStateException("Unable to load icon!");
                }
                iconImage = new Image(resource);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load icon!", e);
            }
        }
        return iconImage;
    }

    /**
     * @return constant of flag image, must not be null
     * @throws IllegalStateException if icon can not be loaded
     */
    public static Image getFlagImage() throws IllegalStateException {
        if (flagImage == null) {
            try (final var resource = ImageUtils.class.getResourceAsStream("/de/ifdgmbh/mad/minesweeper/images/flag.png");) {
                if (resource == null) {
                    throw new IllegalStateException("Unable to load icon!");
                }
                flagImage = new Image(resource);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load icon!", e);
            }
        }
        return flagImage;
    }

}
