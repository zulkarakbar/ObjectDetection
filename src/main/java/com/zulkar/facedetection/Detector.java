package com.zulkar.facedetection;

import static org.bytedeco.javacpp.opencv_calib3d.Rodrigues;
import static org.bytedeco.javacpp.opencv_core.CV_64FC1;
import static org.bytedeco.javacpp.opencv_core.CV_8UC1;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.approxPolyDP;
import static org.bytedeco.javacpp.opencv_imgproc.arcLength;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.drawContours;
import static org.bytedeco.javacpp.opencv_imgproc.findContours;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.threshold;
import static org.bytedeco.javacpp.opencv_imgproc.warpPerspective;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;

import java.io.File;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import com.zulkar.facedetection.enums.CascadeMapping;

public class Detector {

	OpenCVFrameGrabber grabber;

	public void startDetect(String objectType, String fileName, String detectionMode) throws Exception {

		String classifierName = null;
		CascadeMapping mapping = CascadeMapping.valueOf(objectType);
		String classifierFile = mapping.getObjectType();
		if (classifierFile == null)
			throw new Exception("No Classifier Provided");

		File file = new File(classifierFile);

		classifierName = file.getAbsolutePath();
		if (detectionMode.equals("Video")) {
			grabber = new OpenCVFrameGrabber(fileName);
		} else {
			grabber = new OpenCVFrameGrabber(0);
		}
		
		Loader.load(opencv_objdetect.class);

		CascadeClassifier classifier = new CascadeClassifier(classifierName);

		grabber.start();

		
		OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
		Mat grabbedImage = converter.convert(grabber.grab());
		int height = grabbedImage.rows();
		int width = grabbedImage.cols();

		Mat grayImage = new Mat(height, width, CV_8UC1);
		Mat rotatedImage = grabbedImage.clone();

		
		FrameRecorder recorder = FrameRecorder.createDefault("D:/output.mp4", width, height);
		recorder.start();

		CanvasFrame frame = new CanvasFrame("Detector", CanvasFrame.getDefaultGamma() / grabber.getGamma());

		// Let's create some random 3D rotation...
		Mat randomR = new Mat(3, 3, CV_64FC1), randomAxis = new Mat(3, 1, CV_64FC1);
		
		DoubleIndexer Ridx = randomR.createIndexer(), axisIdx = randomAxis.createIndexer();
		axisIdx.put(0, (Math.random() - 0.5) / 4, (Math.random() - 0.5) / 4, (Math.random() - 0.5) / 4);
		Rodrigues(randomAxis, randomR);
		double f = (width + height) / 2.0;
		Ridx.put(0, 2, Ridx.get(0, 2) * f);
		Ridx.put(1, 2, Ridx.get(1, 2) * f);
		Ridx.put(2, 0, Ridx.get(2, 0) / f);
		Ridx.put(2, 1, Ridx.get(2, 1) / f);
		System.out.println(Ridx);


		while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
			// Let's try to detect some faces! but we need a grayscale image...
			cvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
			RectVector faces = new RectVector();
			classifier.detectMultiScale(grayImage, faces, 1.1, 3, CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH,
					null, null);
			long total = faces.size();
			for (long i = 0; i < total; i++) {
				Rect r = faces.get(i);
				int x = r.x(), y = r.y(), w = r.width(), h = r.height();
				rectangle(grabbedImage, new Point(x, y), new Point(x + w, y + h), Scalar.RED, 1, CV_AA, 0);

				// To access or pass as argument the elements of a native array, call position()
				// before.

			}

			// Let's find some contours! but first some thresholding...
			threshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

			// To check if an output argument is null we may call either isNull() or
			// equals(null).
			MatVector contours = new MatVector();
			findContours(grayImage, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
			long n = contours.size();
			for (long i = 0; i < n; i++) {
				Mat contour = contours.get(i);
				Mat points = new Mat();
				approxPolyDP(contour, points, arcLength(contour, true) * 0.02, true);
				drawContours(grabbedImage, new MatVector(points), -1, Scalar.BLUE);
			}

			warpPerspective(grabbedImage, rotatedImage, randomR, rotatedImage.size());

			Frame rotatedFrame = converter.convert(rotatedImage);
			frame.showImage(rotatedFrame);
			recorder.record(rotatedFrame);
		}
		frame.dispose();
		recorder.stop();
		grabber.stop();
		classifier.close();
	}
}