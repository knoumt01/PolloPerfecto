import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static javax.swing.GroupLayout.Alignment.CENTER;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class ordertaker extends JFrame {
	/* variables for model & elements */
	private DefaultListModel model;
	private JList list;
	private JButton addbtn;
	private JButton delbtn;
	public String text = " ";
	
	/* default constructor */
	public ordertaker() {
		initGUI();
	}
	
	/* main GUI construction method */
	private void initGUI() {
		createMenuBar();
		createList();
		createButtons();
		JScrollPane scrollpane = new JScrollPane(list);
		
		Container pane = getContentPane();
		GroupLayout gl = new GroupLayout(pane);
		pane.setLayout(gl);
		
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		
		gl.setHorizontalGroup(gl.createSequentialGroup()
			.addComponent(scrollpane)
			.addGroup(gl.createParallelGroup()
				.addComponent(addbtn)
				.addComponent(delbtn))
		);
		
		gl.setVerticalGroup(gl.createParallelGroup(CENTER)
			.addComponent(scrollpane)
			.addGroup(gl.createSequentialGroup()
				.addComponent(addbtn)
				.addComponent(delbtn))
		);

		gl.linkSize(addbtn, delbtn);
		
		pack();
		
		setTitle("Pollo Perfecto Order Taker");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/* Creates the list area where orders are stored */
	private void createList() {
		
		model = new DefaultListModel();
		list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	/* creates buttons and actionevents for mouse / keyboard & dialogs */
	private void createButtons() {
		addbtn = new JButton("Add");
		delbtn = new JButton("Delete");
		
		addbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				/* Formatted string construction that includes order #, who it was taken by & date */
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				Date date = new Date();
				String dateString = dateFormat.format(date);
				String text1 = JOptionPane.showInputDialog("Order Number");
				loggingEnabled(text1);
				String text2 = JOptionPane.showInputDialog("Taken By (name)");
				loggingEnabled(text2);
				String text = null;
				/* verify user identity */
				boolean validUser = verifyName(text2);
				if (validUser) {
					text = text1 + " | " + text2 + " | " + dateString;
				}
				else
				{
					/* how will this be handled? */
				}
				
				String item = null;
				
				if (text != null) {
					item = text.trim();
				}
				else {
					return;
				}
					
				if(!item.isEmpty()) {
					model.addElement(item);
				}
			}
		});

		delbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				ListSelectionModel selmodel = list.getSelectionModel();
				int index = selmodel.getMinSelectionIndex();
				if (index >= 0) {
					String userDel = JOptionPane.showInputDialog("Who is deleting?");
					String userDel2 = JOptionPane.showInputDialog("Verify order #");
					loggingEnabled(userDel + userDel2);
					if (userDel != null && userDel2 != null) {
						model.remove(index);
					}
					else {
						/* error message - non numeric */
					}
				}
			}
		});
	}
	
	
	private void createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		/* Create the default menu bar and mnemonics */
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		
		JMenu edit = new JMenu("Edit");
		edit.setMnemonic(KeyEvent.VK_E);
		
		JMenu help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);
		
		JMenu exit = new JMenu("Exit");
		exit.setMnemonic(KeyEvent.VK_X);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		
		JMenuItem help2email = new JMenuItem("E-Mail");
		help2email.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String user = JOptionPane.showInputDialog("Who is deleting?");
				String recipientEmail = JOptionPane.showInputDialog("Enter your e-mail address:");
				sendEmail(recipientEmail);
			}
		});
		
		JMenuItem file2open = new JMenuItem("Open");
		JMenuItem file2savelog = new JMenuItem("Save logfile");
		file2savelog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
			/*	logSave(); how to properly implement */
			}
		});
		
		JMenuItem file2exit = new JMenuItem("Exit");
		file2exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		
		file.add(file2open);
		file.add(file2savelog);
		file.add(file2exit);
		help.add(help2email);
		exit.add(file2exit);
		
		menubar.add(file);
		menubar.add(edit);
		menubar.add(help);
		menubar.add(exit);
		
		setJMenuBar(menubar);
	}
	/* method for sending email to creator */
	public void sendEmail(String recipientEmail) {
		String to = "matt.knouff@mkdesignsoftware.com";
		String from = recipientEmail;
		String host = "localhost";
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);
		try { 
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			String subject = JOptionPane.showInputDialog("Subject:");
			message.setSubject(subject);
			String comment = JOptionPane.showInputDialog("Comments:");
			message.setText(comment);
			Transport.send(message);
		}
		catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
	/* will verify that name is correctly used & will verify against a password - not implemented yet */
	public boolean verifyName(String name) {
			return true;
	}
	public void logSave() throws IOException {
		try { logged(text); }
		catch (IOException e) { }
	}
	public void loggingEnabled(String logText) {
			text = text + logText;
			text = text + "\n";
	}
	
	public void logged(String textToLog) throws IOException {
		DateFormat dateFormatFilename = new SimpleDateFormat("ddMMyyyy");
		Date dateForFilename = new Date();
		String filename = dateFormatFilename.format(dateForFilename) + ".log";
		try {
			FileWriter writer = new FileWriter(filename);
			DateFormat tsFormat = new SimpleDateFormat("yyyy/MM/dd @ hh:MM:ss");
			String tsDate = tsFormat.format(dateForFilename);
			writer.write(tsDate);
			writer.write("==");
			writer.write(textToLog);
			writer.write("\n");
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String args[]) {
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				ordertaker chickenGUI = new ordertaker();
				chickenGUI.setVisible(true);
			}
		});
	}
}
