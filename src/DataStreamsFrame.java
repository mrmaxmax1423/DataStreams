import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;

public class DataStreamsFrame extends JFrame
{
    JPanel mainPanel;

    JPanel controlPanel;
    JFileChooser originalFileChooser;
    JButton chooseFileButton;
    JButton searchFileButton;
    JButton quitButton;

    JPanel displayPanel;
    JLabel originalTextLabel;
    JLabel filteredTextLabel;
    JTextArea originalTextArea;
    JTextArea filteredTextArea;
    JScrollPane originalTextScroll;
    JScrollPane filteredTextScroll;

    JPanel searchPanel;
    JTextField searchTermField;

    File selectedFile;
    String searchTerm;

    public DataStreamsFrame()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        createDisplayPanel();
        mainPanel.add(displayPanel, BorderLayout.NORTH);

        createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.CENTER);

        createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setSize(1000,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void createControlPanel()
    {
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1,2));

        chooseFileButton = new JButton("Pick Text file To Search From");
        chooseFileButton.addActionListener((ActionEvent ae) -> selectFile());
        quitButton = new JButton("Quit");
        quitButton.addActionListener((ActionEvent ae) -> System.exit(0));

        chooseFileButton.setFont(new java.awt.Font("Serif", 0, 20));
        searchFileButton.setFont(new java.awt.Font("Serif", 0, 20));
        quitButton.setFont(new java.awt.Font("Serif", 0, 20));

        controlPanel.add(chooseFileButton);
        controlPanel.add(quitButton);
    }

    public void createDisplayPanel()
    {
        displayPanel = new JPanel();
        originalTextArea = new JTextArea(15,25);
        filteredTextArea = new JTextArea(15, 25);
        originalTextLabel = new JLabel("Original Text:");
        filteredTextLabel = new JLabel("Filtered Text Results:");

        originalTextArea.setFont(new java.awt.Font("Serif", 0, 20));
        filteredTextArea.setFont(new java.awt.Font("Serif", 0, 20));

        originalTextScroll = new JScrollPane(originalTextArea);
        filteredTextScroll = new JScrollPane(filteredTextArea);

        displayPanel.add(originalTextLabel);
        displayPanel.add(originalTextScroll);
        displayPanel.add(filteredTextLabel);
        displayPanel.add(filteredTextScroll);
    }

    public void createSearchPanel()
    {
        searchPanel = new JPanel();

        searchFileButton = new JButton("Search File For Matches");
        searchFileButton.addActionListener((ActionEvent ae) -> filterFile(searchTermField.getText()));
        searchFileButton.setFont(new java.awt.Font("Serif", 0, 20));

        searchTermField = new JTextField(15);
        searchTermField.setFont(new java.awt.Font("Serif", 0, 20));

        searchPanel.add(searchFileButton);
        searchPanel.add(searchTermField);
    }

    public void selectFile() //chooses a file to filter, and displays it
    {
        String rec = "";

        File chosenFile;
        originalFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = originalFileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = originalFileChooser.getSelectedFile();
        }

        originalTextArea.setText("");
        try
        {
            InputStream in = new BufferedInputStream(Files.newInputStream(selectedFile.toPath(), CREATE));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while (reader.ready()) {
                rec = reader.readLine();

                originalTextArea.append(rec + "\n");
            }
            reader.close();
            }
            catch (FileNotFoundException e)
            {
                System.out.println(("File not found"));
                e.printStackTrace();
            }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void filterFile(String searchTerm) {  //checks file for specified term and displays matching lines
        if(selectedFile != null)
        {
            filteredTextArea.setText(""); //lets you change and filter different files without messing up display
            try (Stream<String> lines = Files.lines( selectedFile.toPath() ))
            {
                List<String> filteredLines = lines.filter(s -> s.toLowerCase()
                        .contains(searchTerm.toLowerCase(Locale.ROOT)))
                        .collect(Collectors.toList()); //included capitalized results

                for(String line : filteredLines)
                {
                    filteredTextArea.append(line+ "\n");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
