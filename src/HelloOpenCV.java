import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

//
// Detects faces in an image, draws boxes around them, and writes the results
// to "faceDetection.png".
//
class DetectFaceDemo {
    public void run() {
        System.out.println("\nRunning DetectFaceDemo");

        // Create a face detector from the cascade file in the resources
        // directory.
        CascadeClassifier faceDetector = new CascadeClassifier("resources/lbpcascade_frontalface.xml");
        Mat image = Highgui.imread("resources/faces.jpg");

        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }

        // Save the visualized detection.
        String filename = "faceDetection.png";
        System.out.println(String.format("Writing %s", filename));
        Highgui.imwrite(filename, image);
    }
}

class DetectVideoDemo {
    public void run() {
        System.out.println("\nRunning DetectVideoDemo");
        Mat frame = new Mat();

        CascadeClassifier faceDetector = new CascadeClassifier("resources/lbpcascade_frontalface.xml");
        VideoCapture camera = new VideoCapture("resources/face_vid.mp4");

        JFrame jframe = new JFrame("Video Face Detector");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setVisible(true);

        while (true) {
            if (camera.read(frame)) {
                MatOfRect faceDetections = new MatOfRect();
                faceDetector.detectMultiScale(frame, faceDetections);
                for (Rect rect : faceDetections.toArray()) {
                    Core.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                }

                ImageIcon image = new ImageIcon(Mat2BufferedImage(frame));
                vidpanel.setIcon(image);
                vidpanel.repaint();

            }
        }

    }

    private BufferedImage Mat2BufferedImage(Mat m){
        //source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
        //Fastest code
        //The output can be assigned either to a BufferedImage or to an Image

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

}



public class HelloOpenCV {
    public static void main(String[] args) {
        System.out.println("Hello, OpenCV");

        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        new DetectFaceDemo().run();
        new DetectVideoDemo().run();
    }
}