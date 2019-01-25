import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Frame;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

public class UI extends WindowAdapter implements ActionListener {
	
	private FileManager fm;
	private Frame f;
	private Component[] focusList;
	private MyFocusTraversalPolicy ftp;
	private TextArea ta;
	private TextField dirField;
	private TextField extField;
	private TextField searchField;
	private Scrollbar sb;
	private Button refreshButton;
	private Button clearButton;
	private Button submitButton;
	private Checkbox startscb;
	private String screen = "";
	
	private int x = 900;
	private int y = 700;
	private int maxy = y;
	
	public UI() throws IOException {
		// Create window frame
		f = new Frame();
		f.addWindowListener(this);
		
		// These objects allow for tab ordering
		ftp = new MyFocusTraversalPolicy();
		f.setFocusTraversalPolicy(ftp);
		
		// Create a vertical scroll bar
		sb = new Scrollbar(Scrollbar.VERTICAL, 0, 0, 0, 0);
		sb.setBounds(x-30, 30, 30, y-30);
		f.add(sb);
		
		// Adjust the scroll bar with the resizing of the window
		f.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Dimension size = f.getSize();
				x = size.width;
				y = size.height;
				sb.setBounds(x-30, 30, 30, y-30);
				if(y<maxy) {
					sb.setMaximum(maxy-y);
				}
			}
		});
		
		// Creates the button to refresh the file system
		refreshButton = new Button("Refresh Files");
		refreshButton.setBounds(380, 50, 140, 30);
		f.add(refreshButton);
		refreshButton.addActionListener(this);
		
		startscb = new Checkbox("Starts");
		startscb.setBounds(550, 50, 50, 30);
		f.add(startscb);
		
		dirField = new TextField("Directory");
		dirField.setBounds(250, 100, 400, 30);
		f.add(dirField);
		
		extField = new TextField("Extension");
		extField.setBounds(250, 140, 400, 30);
		f.add(extField);
		
		searchField = new TextField("Search");
		searchField.setBounds(250, 180, 400, 30);
		f.add(searchField);
		
		submitButton = new Button("Search");
		submitButton.setBounds(380, 220, 140, 30);
		f.add(submitButton);
		submitButton.addActionListener(this);
		
		ta = new TextArea();
		ta.setEditable(false);
		ta.setBounds(250, 270, 400, 350);
		f.add(ta);
		
		fm = new FileManager();
		
		clearButton = new Button("Clear");
		clearButton.setBounds(675, 590, 80, 30);
		f.add(clearButton);
		clearButton.addActionListener(this);
		
		// Overwrites tab function in text areas allow for tabbing over
		ta.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    if (e.getModifiers() > 0) {
                        ta.transferFocusBackward();
                    } else {
                        ta.transferFocus();
                    }
                    e.consume();
                }
            }
        });
		
		// Sets the tab order
		focusList = new Component[10];
		setFocusList();
		ftp.setList(focusList);
		
		// Allow for screen adjustment with the scroll bar
		sb.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int spot = sb.getValue();
				refreshButton.setBounds(380, 50-spot, 140, 30);
				ta.setBounds(250, 150-spot, 400, 350);
				clearButton.setBounds(675, 470-spot, 80, 30);
			}
		});
		
		f.setSize(x, maxy);
		f.setLayout(null);
		f.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String params = e.paramString();
			// Refreshes the file system
			if (params.contains("Refresh Files")) {
				refresh();
			}
			else if (params.contains("Clear")) {
				screen = "";
				ta.setText(screen);
			}
			else {
				ArrayList<String> validFiles = fm.searchFiles(dirField.getText(), extField.getText(), searchField.getText(), startscb.getState());
				if(validFiles.isEmpty()) {
					setScreen("No files contain the criteria.");
				}
				else {
					for(String fileName : validFiles) {
						setScreen(fileName);
					}
				}
			}
		}
		catch (Exception c) {
			try {
				setScreen("ERROR: ");
				setScreen(c.toString());
				StackTraceElement[] st = c.getStackTrace();
				for(int i=0;i<st.length;i++) {
					setScreen(st[i].toString());
				}
			}
			catch(IOException f) {
				System.out.println(f.toString());
			}
		}
	}

	// Sets the tab order
	public void setFocusList() {
		for(int i = 0; i < focusList.length; i++) {
			if(i==0) {
				focusList[i] = refreshButton;
			}
			else if(i==1) {
				//focusList[i] = addCPEsButton;
			}
			else if(i==2) {
				focusList[i] = ta;
			}
			else if(i==3) {
				focusList[i] = clearButton;
			}
		}
	}
	
	public void refresh() throws Exception {
		fm = null;
		fm = new FileManager();
		setScreen("File system has been refreshed.");
	}
	
	//Prints to the output area
	public void setScreen(String message) throws IOException {
		screen = screen + message + "\n";
		ta.setText(screen);
	}

	// Runs when the window is closed
	public void windowClosing(WindowEvent e) {
		f.dispose();
		System.exit(0);
	}
}

//Focuses on the correct item based on user selection/tab
class MyFocusTraversalPolicy extends FocusTraversalPolicy
{
	private Component[] focusList;
	private int focusNumber = 0;
	
	public void setList(Component[] focusList) {
		this.focusList = focusList;
	}
	
	public Component getComponentAfter(Container focusCycleRoot,Component aComponent)
	{
		for(int i = 0; i < focusList.length; i++) {
			if(focusList[i] == aComponent) {
				focusNumber = (i+1) % focusList.length;
			}
		}
		return focusList[focusNumber];
	}
	
	public Component getComponentBefore(Container focusCycleRoot,Component aComponent)
	{
		for(int i = 0; i < focusList.length; i++) {
			if(focusList[i] == aComponent) {
				focusNumber = (focusList.length+i-1) % focusList.length;
			}
		}
		return focusList[focusNumber];
	}
	
	public Component getInitialComponent(Container focusCycleRoot){return focusList[0];}

	public Component getDefaultComponent(Container focusCycleRoot){return focusList[0];}

	public Component getLastComponent(Container focusCycleRoot){return focusList[focusList.length-1];}

	public Component getFirstComponent(Container focusCycleRoot){return focusList[0];}
}