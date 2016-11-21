package confucian.shutterbug;

import confucian.exception.FrameworkException;
import confucian.shutterbug.utils.Coordinates;
import confucian.shutterbug.utils.ImageProcessor;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.awt.image.RasterFormatException;

/**
 * 页面截图
 */
public class PageSnapshot extends Snapshot {

    /**
     * 页面截图
     */
    PageSnapshot() {
    }

    /**
     * Highlights WebElement within the page with Color.red
     * and line width 3.
     *
     * @param element WebElement to be highlighted
     * @return instance of type PageSnapshot
     */
    public PageSnapshot highlight(WebElement element) {
        try {
            highlight(element, Color.red, 3);
        } catch (RasterFormatException rfe) {
            throw new FrameworkException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Highlights WebElement within the page with provided color
     * and line width.
     *
     * @param element   WebElement to be highlighted
     * @param color     color of the line
     * @param lineWidth width of the line
     * @return instance of type PageSnapshot
     */
    public PageSnapshot highlight(WebElement element, Color color, int lineWidth) {
        try {
            image = ImageProcessor.highlight(image, new Coordinates(element), color, lineWidth);
        } catch (RasterFormatException rfe) {
            throw new FrameworkException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Highlight WebElement within the page (same as in {@link #highlight(WebElement)}}
     * and adding provided text above highlighted element.
     *
     * @param element WebElement to be highlighted with Color.red                and line width 3
     * @param text    test to be places above highlighted element with             Color.red, font "Serif", BOLD, size 20
     * @return instance of type PageSnapshot
     */
    public PageSnapshot highlightWithText(WebElement element, String text) {
        try {
            highlightWithText(element, Color.red, 3, text, Color.red, new Font("Serif", Font.BOLD, 20));
        } catch (RasterFormatException rfe) {
            throw new FrameworkException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Highlight WebElement within the page, same as in {@link #highlight(WebElement)}
     * but providing ability to override default color, font values.
     *
     * @param element      WebElement to be highlighted
     * @param elementColor element highlight color
     * @param lineWidth    line width around the element
     * @param text         text to be placed above the highlighted element
     * @param textColor    color of the text
     * @param textFont     text font
     * @return instance of type PageSnapshot
     */
    public PageSnapshot highlightWithText(WebElement element, Color elementColor, int lineWidth, String text,
                                          Color textColor, Font textFont) {
        try {
            highlight(element, elementColor, 0);
            Coordinates coords = new Coordinates(element);
            image = ImageProcessor
                    .addText(image, coords.getX(), coords.getY() - textFont.getSize() / 2, text, textColor, textFont);
        } catch (RasterFormatException rfe) {
            throw new FrameworkException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Blur the entire page.
     *
     * @return instance of type PageSnapshot
     */
    public PageSnapshot blur() {
        image = ImageProcessor.blur(image);
        return this;
    }

    /**
     * Blur provided element within the page only.
     *
     * @param element WebElement to be blurred
     * @return instance of type PageSnapshot
     */
    public PageSnapshot blur(WebElement element) {
        try {
            image = ImageProcessor.blurArea(image, new Coordinates(element));
        } catch (RasterFormatException rfe) {
            throw new FrameworkException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Makes an element withing a page 'monochrome' - applies gray-and-white filter.
     * Original colors remain on the rest of the page.
     *
     * @param element WebElement within the page to be made 'monochrome'
     * @return instance of type PageSnapshot
     */
    public PageSnapshot monochrome(WebElement element) {
        try {
            image = ImageProcessor.monochromeArea(image, new Coordinates(element));
        } catch (RasterFormatException rfe) {
            throw new FrameworkException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Blurs all the page except the element provided.
     *
     * @param element WebElement to stay not blurred
     * @return instance of type PageSnapshot
     */
    public PageSnapshot blurExcept(WebElement element) {
        try {
            image = ImageProcessor.blurExceptArea(image, new Coordinates(element));
        } catch (RasterFormatException rfe) {
            throw new FrameworkException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    @Override
    protected PageSnapshot self() {
        return this;
    }

}
