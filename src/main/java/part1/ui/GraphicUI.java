package part1.ui;

import akka.actor.typed.ActorRef;
import part1.message.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;

public class GraphicUI extends JFrame implements ActionListener {

	@Serial
	private static final long serialVersionUID = 1L;
	private final ActorRef<Message> uiActor;

	public JButton startButton, stopButton, chooseDirectoryButton,chooseFileButton;
	private JTextField directoryPath, filePath, wordNumber;
	public JTextArea resultConsole;

	//for test purpose
	private final String defaultFileChooserText = "assets/stopwords.pdf";
	private final String defaultFolderChooserText = "assets/pdf";

	public GraphicUI(ActorRef<Message> uiActor) {
		this.uiActor = uiActor;

		setView();

		this.uiActor.tell(new UIInitializedMessage(this));

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				uiActor.tell(new TerminateSystemMessage());
				System.exit(0);
			}
		});
	}

	public void actionPerformed(ActionEvent e) {

		JFileChooser fileChooser;
		if(e.getSource() == startButton) {

			resultConsole.setText("Starting...");

			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			this.uiActor.tell(new StartTaskReqMessage(directoryPath.getText(), filePath.getText(), Integer.parseInt(wordNumber.getText()), this.uiActor));

		} else if(e.getSource() == stopButton) {

			stopButton.setEnabled(false);
			startButton.setEnabled(true);
			this.uiActor.tell(new StopTaskMessage());
			this.resultConsole.append("\nStopped!");

		} else if(e.getSource() == chooseDirectoryButton) {

			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);

			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				directoryPath.setText(fileChooser.getSelectedFile().toString());
			} else {
				directoryPath.setText(defaultFolderChooserText);
			}

		} else if(e.getSource() == chooseFileButton) {

			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);

			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				filePath.setText(fileChooser.getSelectedFile().toString());
			} else {
				filePath.setText(defaultFileChooserText);
			}

		}
	}

	private void setView() {
		setTitle("PDF Words counter");
		setSize(700,500);
		setResizable(false);

		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		JLabel directoryPathLabel = new JLabel("Folder path:");
		directoryPathLabel.setSize(100,30);
		directoryPathLabel.setLocation(10, 10);
		contentPane.add(directoryPathLabel);

//		directoryPath = new JTextField("Choose a directory -->");
		directoryPath = new JTextField(defaultFolderChooserText);
		directoryPath.setSize(400,30);
		directoryPath.setLocation(110,10);
		contentPane.add(directoryPath);

		chooseDirectoryButton = new JButton("CHOOSE");
		chooseDirectoryButton.setSize(100,30);
		chooseDirectoryButton.setLocation(510, 10);
		chooseDirectoryButton.addActionListener(this);
		contentPane.add(chooseDirectoryButton);

		JLabel wordNumberLabel = new JLabel("Word number: ");
		wordNumberLabel.setSize(100,30);
		wordNumberLabel.setLocation(10, 50);
		contentPane.add(wordNumberLabel);

		wordNumber = new JTextField("10");
		wordNumber.setSize(200,30);
		wordNumber.setLocation(110,50);
		contentPane.add(wordNumber);

		JLabel filePathLabel = new JLabel("File path:");
		filePathLabel.setSize(100,30);
		filePathLabel.setLocation(10,90);
		contentPane.add(filePathLabel);

//		filePath = new JTextField("Choose a file -->");
		filePath = new JTextField(defaultFileChooserText);
		filePath.setSize(400,30);
		filePath.setLocation(110,90);
		contentPane.add(filePath);

		chooseFileButton = new JButton("CHOOSE");
		chooseFileButton.setSize(100,30);
		chooseFileButton.setLocation(510, 90);
		chooseFileButton.addActionListener(this);
		contentPane.add(chooseFileButton);

		startButton = new JButton("START");
		startButton.setSize(100,30);
		startButton.setLocation(110, 130);
		startButton.addActionListener(this);
		contentPane.add(startButton);

		stopButton = new JButton("STOP");
		stopButton.setEnabled(false);
		stopButton.setSize(100, 30);
		stopButton.setLocation(210, 130);
		stopButton.addActionListener(this);
		contentPane.add(stopButton);

		resultConsole = new JTextArea();
		JScrollPane resultScrollPane = new JScrollPane(resultConsole,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		contentPane.add(resultScrollPane);
		resultScrollPane.setSize(650, 250);
		resultScrollPane.setLocation(10, 170);

		setVisible(true);

	}

}
