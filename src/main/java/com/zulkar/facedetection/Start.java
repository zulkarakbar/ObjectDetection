package com.zulkar.facedetection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Start implements ActionListener {
	JButton cameraButton;
	JButton videoButton;
	JButton faceButton;
	JButton eyesButton;
	JButton licenseButton;
	JButton carButton;
	JTextField textField;
	JButton uploadVideo;
	JFileChooser fileChooser;
	JButton browse;

	private static String detectionMode;
	private static String objectType;
	private static String fileName;

	public void detectionMode() {
		JFrame sourceMode = new JFrame("Object Detection");
		videoButton = new JButton("Video");
		cameraButton = new JButton("Camera");
		videoButton.setBounds(50, 200, 200, 60);
		cameraButton.setBounds(50, 100, 200, 60);
		sourceMode.add(cameraButton);
		sourceMode.add(videoButton);
		sourceMode.setSize(500, 400);
		cameraButton.addActionListener(this);
		videoButton.addActionListener(this);
		sourceMode.setLayout(null);
		sourceMode.setVisible(true);
	}

	public void uploadFrame() {
		JFrame uploadFrame = new JFrame("Upload Video");
		textField = new JTextField();
		textField.setBounds(20, 50, 190, 30);
		uploadFrame.add(textField);
		browse = new JButton("Browse");
		browse.setBounds(250, 50, 80, 30);
		uploadFrame.add(browse);
		browse.addActionListener(this);
		fileChooser = new JFileChooser();
		uploadFrame.setLayout(null);
		uploadFrame.setSize(400, 300);
		uploadFrame.setVisible(true);
	}

	public void objectType() {
		JFrame f = new JFrame("Object Type");
		faceButton = new JButton("Face");
		eyesButton = new JButton("Eyes");
		licenseButton = new JButton("LicensePlate");
		carButton = new JButton("Car");
		eyesButton.setBounds(100, 100, 200, 60);
		faceButton.setBounds(100, 200, 200, 60);
		licenseButton.setBounds(100, 300, 200, 60);
		carButton.setBounds(100, 400, 200, 60);
		f.add(faceButton);
		f.add(eyesButton);
		f.add(carButton);
		f.add(licenseButton);
		f.setSize(700, 700);
		faceButton.addActionListener(this);
		eyesButton.addActionListener(this);
		carButton.addActionListener(this);
		licenseButton.addActionListener(this);
		f.setLayout(null);
		f.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cameraButton) {
			detectionMode = cameraButton.getText();
		} else if (e.getSource() == videoButton) {
			detectionMode = videoButton.getText();

		} else if (e.getSource() == faceButton) {
			objectType = faceButton.getText();
		} else if (e.getSource() == eyesButton) {
			objectType = eyesButton.getText();
		} else if (e.getSource() == carButton) {
			objectType = carButton.getText();
		} else if (e.getSource() == licenseButton) {
			objectType = licenseButton.getText();
		} else if (e.getSource() == textField) {
			fileName = textField.getText();
		} else if (e.getSource() == browse) {
			int x = fileChooser.showOpenDialog(null);
			if (x == JFileChooser.APPROVE_OPTION) {
				File fileToBeSent = fileChooser.getSelectedFile();
				textField.setText(fileToBeSent.getAbsolutePath());
				fileName = textField.getText();

			}
		}

	}

	public static void main(String[] args) throws Exception {
		Start startApp = new Start();
		startApp.detectionMode();
		while (detectionMode == null)
			TimeUnit.SECONDS.sleep(1);
		if (detectionMode.equals("Video")) {
			startApp.uploadFrame();
			while (fileName == null)
				TimeUnit.SECONDS.sleep(1);
		}
		startApp.objectType();
		while (objectType == null)
			TimeUnit.SECONDS.sleep(1);
		Detector demo1 = new Detector();
		demo1.startDetect(objectType, fileName, detectionMode);
		System.exit(0);
	}

}