import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Converter extends JFrame{
    private JPanel wrapper;
    private JLabel lblFilePaths;
    private JLabel lblQuarter;
    private JLabel lblComponent;
    private JLabel lblLabel;
    private JLabel lblAssignee;
    private JLabel lblRepoFolder;
    private JComboBox cbQuarter;
    private JComboBox cbComponent;
    private JComboBox cbLabel;
    private JComboBox cbAssignee;
    private JTextField inptFilePath;
    private JTextField inptRepoFolder;
    private JButton btnConvert;
    private JButton btnClose;
    private JLabel lblImageContainer;
    private JLabel lblCompanyName;
    private JLabel lblDeveloper;
    private JLabel lblVersion;
    private JLabel lblAppName;
    public List<String> files;

    public Converter() throws InterruptedException {

//        JFrame frame = new JFrame();
        Arrays.asList(UIManager.getInstalledLookAndFeels())
                .stream()
                .filter(info -> info.getName().equals("Mac OS X"))
                .forEach(info -> {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });

        ImageIcon imageIcon = new ImageIcon(getClass().getResource("pf.png"));
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(100, 60, Image.SCALE_DEFAULT);
        imageIcon = new ImageIcon(newImage);
        lblImageContainer.setIcon(imageIcon);

        setContentPane(wrapper);

        setSize(600, 600);
        setTitle("Feature To Excel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2;

        setLocation(x, y);
        Thread.sleep(2000);
        setVisible(true);

        inptFilePath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(true);

                // Show open dialog
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFiles = fileChooser.getSelectedFiles();
                    StringBuilder filePaths = new StringBuilder();
                    for (File selectedFile : selectedFiles) {
                        filePaths.append(selectedFile.getAbsolutePath()).append("; "); // Add file paths
                    }
                    String filePathsString = filePaths.toString();
                    if (filePathsString.length() > 2) {
                        filePathsString = filePathsString.substring(0, filePathsString.length() - 2); // Remove the last "; "
                    }
                    inptFilePath.setText(filePathsString);
                }
            }
        });

        btnConvert.addActionListener(e -> {
            boolean hasError = false;
            String filePaths = inptFilePath.getText();
            String quarter = cbQuarter.getSelectedItem().toString();
            String component = cbComponent.getSelectedItem().toString();
            String label = cbLabel.getSelectedItem().toString();
            String assignee = cbAssignee.getSelectedItem().toString();
            String repoFolder = inptRepoFolder.getText();

            files = Arrays.asList(filePaths.split("; "));

            for (String file : files) {
                if (!file.endsWith(".feature")) {
                    JOptionPane.showConfirmDialog(null, "Please Select feature file", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                    hasError = true;
                }
            }

            if (assignee.startsWith("---")) {
                JOptionPane.showConfirmDialog(null, "Please Select Assignee", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                hasError = true;
            }

            if (!hasError) {
                TextConverter.quarter = quarter.startsWith("---")?"":quarter;
                TextConverter.component = component.startsWith("---")?"":component;
                TextConverter.label = label.startsWith("---")?"":label;
                TextConverter.assignee = assignee;
                TextConverter.repoFolder = repoFolder.startsWith("---")?"":repoFolder;

                FileOperations.createCSV(files);

                if (FileOperations.getMessage(files.size())) {
                    JOptionPane.showMessageDialog(null, "All Feature Files Successfully Converted!!\nScenarios folder created in Downloads directory", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Unfortunately There is an error on the convert process", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        btnClose.addActionListener(e -> {
            System.exit(0);
        });
    }
}
