/*
 * Created: May 28, 2013
 * 
    The MIT License (MIT)
    
    Copyright (c) 2013 Gamma (Douglas Chidester, James Howard, Steve Corbette)
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
 */

package quasar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.*;

public class MainWindow
{
    private final String author = "Douglas Chidester";
    private final String version = " v0.7.0";
    private final String windowTitle = "Quasar";
    private final int frameWidth = 450;
    private final int frameHeight = 400;
    
    private final String imagePath = "/images/";    // path in jar file
    
    private JFrame mainWindow;
    private JPanel mainPanel;
    private JPanel aboutPane;
    
    // Variables
    private Data entry;
    
    private String quasarLicenseText = "Quasar";
    private String quasarLicenseUrl = "https://github.com/objectDisorientedProgrammer/Quasar2/blob/master/license.txt";
    private String commonsIoLicenseText = "commons-io";
    private String commonsIoLicenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt";
    private String urlErrorWindowTitle = "Error openning URL";
    
    // GUI
    private JTextField searchTF;
    private JLabel filterLbl;
    private JButton searchBtn;
    
    private JList<Data> dataList;
    private JScrollPane scrollPane;
    private int clickcount = 0;
    private Data previouslySelected;
    
    private JComboBox<String> filterComboBox;
    private JButton newNodeBtn;
    private JButton editBtn;
    private String licenseMenuText = "Licenses";
    private String databaseFilePath;

    private ActionListener searchListener;

    public MainWindow(Data model)
    {
        super();
        this.entry = model;
        
        initializeMainWindowAndPanel();
        
        createAboutPanel();
        
        createAndAddMenuBar();
        
        createGUIElements();
        
        updateListDisplay();
        
        addGUIElements();

        mainWindow.setVisible(true);
    }

    private void addGUIElements()
    {
        mainPanel.add(searchTF);
        mainPanel.add(searchBtn);
        mainPanel.add(scrollPane);
        mainPanel.add(filterLbl);
        mainPanel.add(filterComboBox);
        mainPanel.add(newNodeBtn);
        mainPanel.add(editBtn);
    }

    private void createGUIElements()
    {
        searchListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // get the query text
                String searchString = searchTF.getText();
                // get filter
                int filter = filterComboBox.getSelectedIndex();
                // return results based on query text and filter
                Vector<Data> temp = new Vector<Data>(10);

                if(Quasar.search(searchString, filter, temp))
                {
                    // update the display list
                    dataList.setListData(temp);
                    dataList.setSelectedIndex(0);
                }
                else
                    // tell the user there are no matches
                    JOptionPane.showMessageDialog(null, "No results.", "Search",  JOptionPane.INFORMATION_MESSAGE, null);
                
            }
        };
        
        searchTF = new JTextField();
        searchTF.setToolTipText("Search here");
        searchTF.setBounds(10, 11, 315, 20);
        searchTF.setColumns(10);
        searchTF.addActionListener(searchListener);
        
        searchBtn = new JButton("Search");
        searchBtn.setBounds(341, 10, 91, 23);
        searchBtn.addActionListener(searchListener);
        
        filterLbl = new JLabel("Search in:");
        filterLbl.setBounds(10, 42, 80, 14);

        filterComboBox = new JComboBox<String>();
        filterComboBox.setModel(new DefaultComboBoxModel<String>(Quasar.entryTypeStrings));
        filterComboBox.setSelectedIndex(0);
        filterComboBox.setMaximumRowCount(Quasar.entryTypeStrings.length);
        filterComboBox.setBounds(95, 38, 120, 22);
        
        // create the data container for the UI
        dataList = new JList<Data>();
        dataList.setLayoutOrientation(JList.VERTICAL);
        // create a scrollable area to display the data
        scrollPane = new JScrollPane(dataList);
        scrollPane.setBounds(10, 80, 315, 188);
        
        dataList.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {}
            
            @Override
            public void mousePressed(MouseEvent e)
            {}
            
            @Override
            public void mouseExited(MouseEvent e)
            {}
            
            @Override
            public void mouseEntered(MouseEvent e)
            {}
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
                ++clickcount;
                // display list item on double click
                if(clickcount >= 2 && previouslySelected == dataList.getSelectedValue())
                {
                    Quasar.displayEntry(dataList.getSelectedValue());
                    
                    // reset double click logic
                    clickcount = 0;
                    previouslySelected = null;
                }
                else // First click. Save selected list item.
                    previouslySelected = dataList.getSelectedValue();
            }
        });

        newNodeBtn = new JButton("New");
        newNodeBtn.setToolTipText("Create a new entry.");
        newNodeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arrrrg) {
            	// TODO activate the edit window with no Data object
            }
        });
        newNodeBtn.setBounds(341, 77, 91, 23);

        editBtn = new JButton("View");
        editBtn.setToolTipText("View or edit the selected entry.");
        editBtn.setBounds(341, 111, 91, 23);
        editBtn.setEnabled(false);
        editBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                // thread this TODO issue #9
                // Display selected entry in an edit window
                Quasar.displayEntry(dataList.getSelectedValue());
            }
        });
    }
    
    public void requestListDisplayUpdate()
    {
        updateListDisplay();
    }

    private void updateListDisplay()
    {
        if(Quasar.isEmpty())
        {   
            editBtn.setEnabled(false);
        }
        else
        {
            dataList.setListData(Quasar.getAllData());
            editBtn.setEnabled(true);
            // select the first item
            dataList.setSelectedIndex(0);
        }
    }

    private void initializeMainWindowAndPanel()
    {
        mainWindow = new JFrame(windowTitle);
        mainWindow.setSize(frameWidth, frameHeight);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLocationRelativeTo(null);
        
        mainPanel = new JPanel(null); // TODO change layout manager; also no need to create a JPanel here.. use .getContentPane()
        
        mainWindow.add(mainPanel);
    }
    
    private void createAboutPanel()
    {
        aboutPane = new JPanel();
        aboutPane.setLayout(new BoxLayout(aboutPane, BoxLayout.PAGE_AXIS));
        
        JLabel applicationInfo = new JLabel("Created by " + author);
        applicationInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel versionInfo = new JLabel("Version " + version);
        versionInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel licensesText = new JLabel(licenseMenuText + ":");
        licensesText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // create a hyperlink to the Quasar License (TODO the license might need to be embedded into the application)
        JLabel quasarLicense = new JLabel(quasarLicenseText);
        quasarLicense.setForeground(Color.blue.darker());
        quasarLicense.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        quasarLicense.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mousePressed(MouseEvent e) {}
            
            @Override
            public void mouseExited(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {}
            
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(quasarLicenseUrl));
                } catch (IOException | URISyntaxException e1) {
                    JOptionPane.showMessageDialog(null, e1.getMessage(), urlErrorWindowTitle,
                            JOptionPane.ERROR_MESSAGE, null);
                }
            }
        });
    
        // create a hyperlink to the Quasar License (TODO the license might need to be embedded into the application)
        JLabel commons_ioLicense = new JLabel(commonsIoLicenseText);
        commons_ioLicense.setForeground(Color.blue.darker());
        commons_ioLicense.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        commons_ioLicense.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mousePressed(MouseEvent e) {}
            
            @Override
            public void mouseExited(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {}
            
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(commonsIoLicenseUrl));
                } catch (IOException | URISyntaxException e1) {
                    JOptionPane.showMessageDialog(null, e1.getMessage(), urlErrorWindowTitle,
                            JOptionPane.ERROR_MESSAGE, null);
                }
            }
        });
        
        aboutPane.add(licensesText);
        // put the license links side by side
        JPanel licenseLinks = new JPanel(new FlowLayout());
        licenseLinks.add(quasarLicense);
        licenseLinks.add(commons_ioLicense);
        aboutPane.add(licenseLinks);
        
        // add a space to start a new section of application info
        JLabel spacer = new JLabel("\n");
        spacer.setAlignmentX(Component.LEFT_ALIGNMENT);
        aboutPane.add(spacer);
        aboutPane.add(applicationInfo);
        aboutPane.add(versionInfo);
    }
    
    private void createAndAddMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        mainWindow.setJMenuBar(menuBar);
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        
        JMenuItem loadMenuItem = new JMenuItem("Load...");
        loadMenuItem.setMnemonic(KeyEvent.VK_L);
        loadMenuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // open a directory navigation window
                JFileChooser fileWindow = new JFileChooser();
                int returnVal = fileWindow.showOpenDialog(mainPanel);

                // if the user selects a file, attempt to load it
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    try {
                        databaseFilePath = fileWindow.getSelectedFile().getAbsolutePath();
                        Quasar.loadFile(databaseFilePath);
                    } catch (IOException e1) {
                        // TODO display an error window
                        e1.printStackTrace();
                    }
                    requestListDisplayUpdate();
                }
            }
        });
        fileMenu.add(loadMenuItem);
        
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setIcon(new ImageIcon(this.getClass().getResource(imagePath + "save.png")));
        saveMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // TODO save to file
                // TODO https://github.com/objectDisorientedProgrammer/Quasar2/issues/4
                // File -> Save
                try
                {
                    Quasar.saveToFile();
                } catch(UnsupportedOperationException ex)
                {
                    JOptionPane.showMessageDialog(null, "Save is not available yet.", "Save unsupported",
                            JOptionPane.ERROR_MESSAGE, null);
                }
            }
        });
        fileMenu.add(saveMenuItem);
        
        JMenuItem quitMenuItem = new JMenuItem("Quit", new ImageIcon(this.getClass().getResource(imagePath+"exit.png")));
        quitMenuItem.setMnemonic(KeyEvent.VK_Q);
        quitMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // TODO save to file then quit
                // save data and close program if user clicks: File -> Quit
                //writeToFile(filenameTextfield.getText());
                mainWindow.dispose();
            }
        });
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);
        
        JMenuItem helpMenuItem = new JMenuItem("Getting Started", new ImageIcon(this.getClass().getResource(imagePath+"help.png")));
        helpMenuItem.setMnemonic(KeyEvent.VK_G);
        helpMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // show basic use instructions if user clicks: Help -> Getting Started
                JOptionPane.showMessageDialog(null, "Something helpful...maybe a link to the wiki?", "Usage",
                        JOptionPane.PLAIN_MESSAGE, new ImageIcon(this.getClass().getResource(imagePath+"help64.png")));
            }
        });
        helpMenu.add(helpMenuItem);
        
        JMenuItem aboutMenuItem = new JMenuItem("About", new ImageIcon(this.getClass().getResource(imagePath+"about.png")));
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // show author and version if user clicks: Help -> About
                JOptionPane.showMessageDialog(null, aboutPane, "About",
                        JOptionPane.INFORMATION_MESSAGE, new ImageIcon(this.getClass().getResource(imagePath+"person.png")));
            }
        });
        helpMenu.add(aboutMenuItem);
    }
}
