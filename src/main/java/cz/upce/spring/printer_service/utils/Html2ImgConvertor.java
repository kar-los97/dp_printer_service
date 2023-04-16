package cz.upce.spring.printer_service.utils;

import com.openhtmltopdf.extend.FSObjectDrawer;
import com.openhtmltopdf.extend.OutputDevice;
import com.openhtmltopdf.extend.OutputDeviceGraphicsDrawer;
import com.openhtmltopdf.extend.SVGDrawer;
import com.openhtmltopdf.java2d.api.BufferedImagePageProcessor;
import com.openhtmltopdf.java2d.api.DefaultPageProcessor;
import com.openhtmltopdf.java2d.api.Java2DRendererBuilder;
import com.openhtmltopdf.latexsupport.LaTeXDOMMutator;
import com.openhtmltopdf.mathmlsupport.MathMLDrawer;
import com.openhtmltopdf.render.DefaultObjectDrawerFactory;
import com.openhtmltopdf.render.RenderingContext;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import com.openhtmltopdf.objects.StandardObjectDrawerFactory;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;


/**
 * Util to convert html from string to image in PNG
 */
public class Html2ImgConvertor {
    /**
     * Static path to IMG file
     */
    private static final String IMG_PATH = "./print/print.png";
    /***
     * Static path to HTML file
     */
    private static final String HTML_PATH = "./print/print.html";
    /**
     * Instance of this class
     */
    private static Html2ImgConvertor instance = null;

    /**
     * Get instance method of Html2ImgConvertor
     *
     * @return instance of Html2ImgConvertor
     */
    public static Html2ImgConvertor getInstance() {
        if (instance == null) {
            instance = new Html2ImgConvertor();
        }
        return instance;
    }

    private DefaultObjectDrawerFactory buildObjectDrawerFactory() {
        DefaultObjectDrawerFactory objectDrawerFactory = new StandardObjectDrawerFactory();
        objectDrawerFactory.registerDrawer("custom/binary-tree", new SampleObjectDrawerBinaryTree());
        return objectDrawerFactory;
    }
    public class SampleObjectDrawerBinaryTree implements FSObjectDrawer {
        int fanout;
        int angle;

        @Override
        public Map<Shape,String> drawObject(Element e, double x, double y, final double width, final double height,
                                            OutputDevice outputDevice, RenderingContext ctx, final int dotsPerPixel) {
            final int depth = Integer.parseInt(e.getAttribute("data-depth"));
            fanout = Integer.parseInt(e.getAttribute("data-fanout"));
            angle = Integer.parseInt(e.getAttribute("data-angle"));

            outputDevice.drawWithGraphics((float) x, (float) y, (float) width / dotsPerPixel,
                    (float) height / dotsPerPixel, new OutputDeviceGraphicsDrawer() {
                        @Override
                        public void render(Graphics2D graphics2D) {
                            double realWidth = width / dotsPerPixel;
                            double realHeight = height / dotsPerPixel;
                            double titleBottomHeight = 10;

                            renderTree(graphics2D, realWidth / 2f, realHeight - titleBottomHeight, realHeight / depth,
                                    -90, depth);

                            /*
                             * Now draw some text using different fonts to exercise all different font
                             * mappings
                             */
                            Font font = Font.decode("Times New Roman").deriveFont(10f);
                            if (depth == 10)
                                font = Font.decode("Arial"); // Does not get mapped
                            if (angle == 35)
                                font = Font.decode("Courier"); // Would get mapped to Courier
                            if (depth == 6)
                                font = Font.decode("Dialog"); // Gets mapped to Helvetica
                            graphics2D.setFont(font);
                            String txt = "FanOut " + fanout + " Angle " + angle + " Depth " + depth;
                            Rectangle2D textBounds = font.getStringBounds(txt, graphics2D.getFontRenderContext());
                            graphics2D.setPaint(new Color(16, 133, 30));
                            GradientPaint gp = new GradientPaint(10.0f, 25.0f, Color.blue,
                                    (float) textBounds.getWidth(), (float) textBounds.getHeight(), Color.red);
                            if (angle == 35)
                                graphics2D.setPaint(gp);
                            graphics2D.drawString(txt, (int) ((realWidth - textBounds.getWidth()) / 2),
                                    (int) (realHeight - titleBottomHeight));
                        }
                    });
            return null;
        }

        private void renderTree(Graphics2D gfx, double x, double y, double len, double angleDeg, int depth) {
            double rad = angleDeg * Math.PI / 180f;
            double xTarget = x + Math.cos(rad) * len;
            double yTarget = y + Math.sin(rad) * len;
            gfx.setStroke(new BasicStroke(2f));
            gfx.setColor(new Color(255 / depth, 128, 128));
            gfx.draw(new Line2D.Double(x, y, xTarget, yTarget));

            if (depth > 1) {
                double childAngle = angleDeg - (((fanout - 1) * angle) / 2f);
                for (int i = 0; i < fanout; i++) {
                    renderTree(gfx, xTarget, yTarget, len * 0.95, childAngle, depth - 1);
                    childAngle += angle;
                }
            }
        }
    }
    /**
     * Method to convert html data from string to image
     *
     * @param html   html data to convert
     * @param height height of image
     * @param width  width of image
     * @return opened FileInputStream with converted image
     */
    public FileInputStream convert(String html, Long height, Long width) throws IOException {
        saveHtmlToFile(html);

        try (SVGDrawer svg = new BatikSVGDrawer();
             SVGDrawer mathMl = new MathMLDrawer()) {

            Java2DRendererBuilder builder = new Java2DRendererBuilder();
            builder.useSVGDrawer(svg);
            builder.useMathMLDrawer(mathMl);
            builder.addDOMMutator(LaTeXDOMMutator.INSTANCE);
            builder.useObjectDrawerFactory(buildObjectDrawerFactory());
            builder.withHtmlContent(html, IMG_PATH);

            BufferedImagePageProcessor bufferedImagePageProcessor = new BufferedImagePageProcessor(
                    BufferedImage.TYPE_INT_RGB, 3.0);

            builder.useDefaultPageSize(width, height, Java2DRendererBuilder.PageSizeUnits.MM);
            builder.useEnvironmentFonts(true);

            /*
             * Render Single Page Image
             */
            builder.toSinglePage(bufferedImagePageProcessor).runFirstPage();
            BufferedImage image = bufferedImagePageProcessor.getPageImages().get(0);

            ImageIO.write(image, "PNG", new File(IMG_PATH));

            /*
             * Render Multipage Image Files
             */
            builder.toPageProcessor(new DefaultPageProcessor(
                    zeroBasedPageNumber -> new FileOutputStream(IMG_PATH.replace(".png", "_" + zeroBasedPageNumber + ".png")),
                    BufferedImage.TYPE_INT_ARGB, "PNG")).runPaged();
        }

        return new FileInputStream(IMG_PATH);
    }

    /**
     * Save html data from string to file.html
     *
     * @param htmlData html data in String
     */
    private static void saveHtmlToFile(String htmlData) {
        try {
            FileWriter fileWriter = new FileWriter(HTML_PATH);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(htmlData);
            bufferedWriter.close();
        } catch (Exception ex) {
        }
    }

}
