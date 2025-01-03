package edu.uiuc.ncsa.sas.webclient;

import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.storage.XMLMap;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Configure the SAS web client. This reads/writes the configuration file (very simple XML).
 * It takes an optional existing file as the argument. This is the configuration that the client
 * loads in order to connect to the server. If you are looking for the (server) admin CLI, that is in
 * {@link edu.uiuc.ncsa.sas.admin.SASCommands} and is text only.
 * <p>Created by Jeff Gaynor<br>
 * on 8/26/22 at  6:15 AM
 */
public class WebClientConfig {
    private JPanel panel1;
    private JTabbedPane ConnectionNotebook;
    private JTextField hostField;
    private JTextField clientIDField;
    private JTextArea privateKeyText;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField trFile;
    private JTextField trPassword;
    private JTextField trDN;
    private JRadioButton JKSRadioButton;
    private JRadioButton PKCS12RadioButton;

    public static final String CONFIG_CLIENT_ID = "client_id";
    public static final String CONFIG_PRIVATE_KEY = "private_key";
    public static final String CONFIG_TR_FILE = "trust_root_path";
    public static final String CONFIG_TR_PASSWORD = "trust_root_password";
    public static final String CONFIG_TR_TYPE = "trust_root_type";
    public static final String CONFIG_TR_DN = "trust_root_dn";
    public static final String CONFIG_HOST = "host";

    protected void setValue(XMLMap map, String key, String value) {
        if (!StringUtils.isTrivial(value)) {
            map.put(key, value);
        }
    }

    public WebClientConfig() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (file == null) {
                    file = chooseFile();
                }
                if (file != null) {
                    try {
                        XMLMap map = new XMLMap();
                        setValue(map, CONFIG_CLIENT_ID, clientIDField.getText());
                        setValue(map, CONFIG_HOST, hostField.getText());
                        setValue(map, CONFIG_PRIVATE_KEY, privateKeyText.getText());
                        setValue(map, CONFIG_TR_FILE, trFile.getText());
                        setValue(map, CONFIG_TR_PASSWORD, trPassword.getText());
                        setValue(map, CONFIG_TR_DN, trDN.getText());
                        if (JKSRadioButton.isSelected()) {
                            setValue(map, CONFIG_TR_TYPE, "JKS");
                        }
                        if (PKCS12RadioButton.isSelected()) {
                            setValue(map, CONFIG_TR_TYPE, "PKCS");
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        map.toXML(fos);
                        fos.flush();
                        fos.close();
                    } catch (Throwable t) {
                        JOptionPane.showMessageDialog(null, "could not save file \"" + file.getAbsolutePath() + "\": " + t.getMessage());
                    }
                }
                System.out.println("yo!");
            }
        });
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.exit(0);
            }
        });
    }

    File file = null;

    public static void main(String[] args) throws Throwable {
        JFrame frame = new JFrame("SAS Client configuration utility");
        WebClientConfig webClientConfig = new WebClientConfig();
        if (0 < args.length) {
            webClientConfig.file = new File(args[0]);
        }

        frame.setContentPane(webClientConfig.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int w = (int) dimension.getWidth() / 2;
        int h = (int) dimension.getHeight() / 2;
        frame.setSize(w, h);
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        String laf = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(laf);
        webClientConfig.init();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        final JScrollPane scrollPane1 = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(scrollPane1, gbc);
        ConnectionNotebook = new JTabbedPane();
        scrollPane1.setViewportView(ConnectionNotebook);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        ConnectionNotebook.addTab("Connection", panel2);
        final JLabel label1 = new JLabel();
        label1.setText("host");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel2.add(label1, gbc);
        hostField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 10.0;
        gbc.weighty = 0.25;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(hostField, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("client Id");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel2.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("private key");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel2.add(label3, gbc);
        clientIDField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 10.0;
        gbc.weighty = 0.25;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(clientIDField, gbc);
        privateKeyText = new JTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 10.0;
        gbc.weighty = 4.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(privateKeyText, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        ConnectionNotebook.addTab("SSL trustroot", panel3);
        final JLabel label4 = new JLabel();
        label4.setText("file");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel3.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("password");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label5, gbc);
        trFile = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 10.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(trFile, gbc);
        trPassword = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(trPassword, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("DN");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel3.add(label6, gbc);
        trDN = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(trDN, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer3, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("type");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel3.add(label7, gbc);
        JKSRadioButton = new JRadioButton();
        JKSRadioButton.setText("JKS");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(JKSRadioButton, gbc);
        PKCS12RadioButton = new JRadioButton();
        PKCS12RadioButton.setText("PKCS 12");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(PKCS12RadioButton, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("You only need this page if you are talking to a service with a self-signed certificate.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel3.add(label8, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer4, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel4, gbc);
        saveButton = new JButton();
        saveButton.setText("save");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(saveButton, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("                                              ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(label9, gbc);
        cancelButton = new JButton();
        cancelButton.setText("cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(cancelButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    protected void init() throws Throwable {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(JKSRadioButton);
        buttonGroup.add(PKCS12RadioButton);

        if (file == null) {
            file = chooseFile();
        }
        if (file != null) {
            XMLMap map = new XMLMap();
            map.fromXML(new FileInputStream(file));
            // now populate it.
            hostField.setText(map.getString(CONFIG_HOST));
            clientIDField.setText(map.getString(CONFIG_CLIENT_ID));
            privateKeyText.setText(map.getString(CONFIG_PRIVATE_KEY));
            trFile.setText(map.getString(CONFIG_TR_FILE));
            trPassword.setText(map.getString(CONFIG_TR_PASSWORD));
            trDN.setText(map.getString(CONFIG_TR_DN));
            switch (map.getString(CONFIG_TR_TYPE)) {
                case "JKS":
                    JKSRadioButton.setSelected(true);
                    break;
                case "PKCS12":
                    PKCS12RadioButton.setSelected(true);
                    break;
            }
        }
    }

    protected File chooseFile() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        int returnValue = jfc.showOpenDialog(null);
        // int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile();
        }
        return null;
    }
}
