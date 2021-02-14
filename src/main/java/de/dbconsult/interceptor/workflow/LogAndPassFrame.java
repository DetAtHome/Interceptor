package de.dbconsult.interceptor.workflow;

import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.exactheight.MaskZMainController;
import de.dbconsult.interceptor.internal.UIController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class LogAndPassFrame {
    private JPanel mainpanel;
    private JTable hexData;
    private JButton gracefulHoldButton;
    private JButton gracefulResumeButton;
    private JTabbedPane stringOrHex;
    private JTable stringData;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JPanel buffers;
    private JTextField numOfCR;
    private JTextField numOfOK;
    private JTable unconfirmedCommands;
    private JTextField difference;
    private JCheckBox disableAllCheckBox;
    private JCheckBox disableCheckBox;
    private JCheckBox disableGCheckBox;
    private JTextField pcQSize;
    private JTextField millQSize;
    private JButton storeWorkItemPosition;
    private JButton restoreWorkItemHome;
    private JButton maskHeights;
    private JButton openFile;
    private JButton didThatTakeMeButton;
    private JCheckBox OKDoneCheckBox1;
    private JCheckBox OKDoneCheckBox2;
    private JButton didThatMoveOnButton;
    private JCheckBox OKDoneCheckBox5;
    private JCheckBox OKDoneCheckBox4;
    private JCheckBox OKDoneCheckBox6;
    private JCheckBox OKDoneCheckBox7;
    private JButton loadTheCopperTopButton;
    private JCheckBox OKDoneCheckBox8;
    private JButton createAHightmapButton;
    private JButton useTheHightmapButton;
    private JButton startIsolationRoutingHitButton;
    private JButton doAToolchangeToButton;
    private JCheckBox OKDoneCheckBox14;
    private JCheckBox OKDoneCheckBox15;
    private JButton didThatGoOnButton;
    private JButton shineSomeUVLightButton;
    private JButton runAdjustedMaskNcButton;
    private JButton toolchangeToYourDrillButton;
    private JCheckBox OKDoneCheckBox;
    private JCheckBox OKDoneCheckBox3;
    private JCheckBox OKDoneCheckBox9;
    private JCheckBox OKDoneCheckBox10;
    private JCheckBox OKDoneCheckBox11;
    private JCheckBox OKDoneCheckBox12;
    private JCheckBox OKDoneCheckBox13;
    private JCheckBox OKDoneCheckBox16;
    private JCheckBox OKDoneCheckBox17;
    private JCheckBox OKDoneCheckBox18;
    private JCheckBox OKDoneCheckBox19;
    private JCheckBox OKDoneCheckBox20;
    private JButton runDrillFileButton;
    private JCheckBox OKDoneCheckBox21;
    private JButton toolchangeToTheCutoutButton;
    private JCheckBox OKDoneCheckBox22;
    private JButton runTheCutoutNcButton;
    private JCheckBox OKDoneCheckBox23;
    private JButton saveEjectPosButton;
    private JButton btnEject;
    private JFileChooser fc = new JFileChooser();
    DefaultTableModel stringTableModel;
    DefaultTableModel hexTableModel;

    private WorkflowDataStore workflowDataStore = null;
    private UIController uiController = null;

    public LogAndPassFrame(String port1, String port2, String port3, WorkflowDataStore workflowDataStore, UIController controller) {
        this.workflowDataStore = workflowDataStore;
        this.uiController = controller;

        stringTableModel = new DefaultTableModel(new String[]{"#", port2, port1, port3}, 0);
        hexTableModel = new DefaultTableModel(new String[]{"#", port2, port1, port3}, 0);
        $$$setupUI$$$();
        if (numOfOK.getText().isEmpty()) numOfOK.setText("0");
        if (numOfCR.getText().isEmpty()) numOfCR.setText("0");
        if (pcQSize.getText().isEmpty()) pcQSize.setText("0");
        if (millQSize.getText().isEmpty()) millQSize.setText("0");

        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setCurrentDirectory(new File("d:/Projects/arduinoscetches/SeriousLego/PCBLayouts"));
                int returnVal = fc.showOpenDialog(mainpanel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    workflowDataStore.update("MaskFile", fc.getSelectedFile().getAbsoluteFile());
                    workflowDataStore.update("MaskFileName", fc.getSelectedFile().getAbsoluteFile().getName());
                }
            }
        });

        saveEjectPosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uiController.storeEjectPosButton();
            }
        });

        btnEject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uiController.eject();
            }
        });
        maskHeights.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MaskZMainController maskZMainController = new MaskZMainController(workflowDataStore, "G01 Z-0.0200");
                maskZMainController.start();
            }
        });
//        gracefulHoldButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                uiController.gracefulHoldButton();
//            }
//        });
        gracefulResumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uiController.gracefulResumeButton();
            }
        });
        storeWorkItemPosition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uiController.storeWorkItemPositionButton();
            }
        });

        restoreWorkItemHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uiController.restoreWorkItemHomeButton();
            }
        });
    }

    public boolean getDisableAll() {
        return disableAllCheckBox.isSelected();
    }

    public boolean getDisableQuestion() {
        return disableCheckBox.isSelected();
    }

    public boolean getDisableG() {
        return disableGCheckBox.isSelected();
    }


    public void show() {
        JFrame frame = new JFrame("LogAndPassFrame");
        frame.setContentPane(mainpanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        stringData.setModel(stringTableModel);
        hexData.setModel(hexTableModel);
        frame.setVisible(true);

    }

    public void showAbsCmdNumber(long commandsFound) {
        if (numOfCR.getText().isEmpty()) numOfCR.setText("0");
        numOfCR.setText(commandsFound + "");
        difference.setText(Long.parseLong(numOfCR.getText()) - Long.parseLong(numOfOK.getText()) + "");
    }

    public void showAbsOkNumber(long oksFound) {
        if (numOfOK.getText().isEmpty()) numOfOK.setText("0");
        numOfOK.setText(oksFound + "");
        difference.setText(Long.parseLong(numOfCR.getText()) - Long.parseLong(numOfOK.getText()) + "");
    }

    public void showPCQSize(long pcQSze) {
        pcQSize.setText(pcQSze + "");
    }

    public void showMillQSize(long millQSze) {
        millQSize.setText(millQSze + "");
    }

    public void addData(WorkflowResult wfData) {
        String[] stringRow;
        String[] hexRow;
        if (stringTableModel.getRowCount() > 0) {
            if (Long.parseLong((String) stringTableModel.getValueAt(stringTableModel.getRowCount() - 1, 0)) == wfData.getIndex()) {
                if (((String) stringTableModel.getValueAt(stringTableModel.getRowCount() - 1, 1)).isEmpty()) {
                    if (wfData.getFormSource().getName().toLowerCase().contains("pc")) {
                        stringTableModel.setValueAt(new String(wfData.getOutput()), stringTableModel.getRowCount() - 1, 1);
                        String hexView = toHexString(wfData.getOutput());
                        hexTableModel.setValueAt(hexView, stringTableModel.getRowCount() - 1, 1);
                        return;
                    }
                }
                if (((String) stringTableModel.getValueAt(stringTableModel.getRowCount() - 1, 2)).isEmpty()) {
                    if (wfData.getFormSource().getName().toLowerCase().contains("mill")) {
                        stringTableModel.setValueAt(new String(wfData.getOutput()), stringTableModel.getRowCount() - 1, 2);
                        String hexView = toHexString(wfData.getOutput());
                        hexTableModel.setValueAt(hexView, stringTableModel.getRowCount() - 1, 2);
                        return;
                    }
                }
            }
        }
        if (wfData.getFormSource().getName().contains("pc")) {
            stringRow = new String[]{"" + wfData.getIndex(), new String(wfData.getOutput()), ""};
            hexRow = new String[]{"" + wfData.getIndex(), toHexString(wfData.getOutput()), ""};

        } else {
            stringRow = new String[]{"" + wfData.getIndex(), "", new String(wfData.getOutput())};
            hexRow = new String[]{"" + wfData.getIndex(), "", toHexString(wfData.getOutput())};
        }
        stringTableModel.addRow(stringRow);
        if (stringTableModel.getRowCount() > 100) {
            for (int i = 0; i < 10; i++)
                stringTableModel.removeRow(i);
        }
        hexTableModel.addRow(hexRow);
        if (hexTableModel.getRowCount() > 100) {
            for (int i = 0; i < 10; i++)
                hexTableModel.removeRow(i);
        }
    }

    private String toHexString(byte[] data) {

        String out = new String();
        for (byte d : data) {
            if (d == 0) break;
            out = out + String.format("%02x", d) + ", ";
        }

        if (out.length() < 2) return "";
        out = out.substring(0, out.length() - 2);
        out = out + " | " + new String(data, StandardCharsets.US_ASCII);
        return out;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainpanel = new JPanel();
        mainpanel.setLayout(new BorderLayout(0, 0));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setOrientation(0);
        mainpanel.add(splitPane1, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        splitPane1.setLeftComponent(panel1);
        stringOrHex = new JTabbedPane();
        panel1.add(stringOrHex, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        stringOrHex.addTab("StringView", panel2);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, BorderLayout.CENTER);
        stringData = new JTable();
        scrollPane1.setViewportView(stringData);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        stringOrHex.addTab("HexView", panel3);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel3.add(scrollPane2, BorderLayout.CENTER);
        hexData = new JTable();
        scrollPane2.setViewportView(hexData);
        buffers = new JPanel();
        buffers.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,center:d:grow"));
        stringOrHex.addTab("Buffers", buffers);
        final JLabel label1 = new JLabel();
        label1.setText("Label");
        CellConstraints cc = new CellConstraints();
        buffers.add(label1, cc.xy(1, 3));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buffers.add(panel4, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("CR Counter");
        panel4.add(label2);
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1);
        progressBar1 = new JProgressBar();
        panel4.add(progressBar1);
        numOfCR = new JTextField();
        numOfCR.setMinimumSize(new Dimension(256, 30));
        panel4.add(numOfCR);
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2);
        final JLabel label3 = new JLabel();
        label3.setText("PC Queue Size");
        panel4.add(label3);
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3);
        pcQSize = new JTextField();
        panel4.add(pcQSize);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buffers.add(panel5, cc.xy(1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label4 = new JLabel();
        label4.setText("Ok Count");
        panel5.add(label4);
        final Spacer spacer4 = new Spacer();
        panel5.add(spacer4);
        progressBar2 = new JProgressBar();
        panel5.add(progressBar2);
        numOfOK = new JTextField();
        numOfOK.setMinimumSize(new Dimension(256, 30));
        panel5.add(numOfOK);
        final Spacer spacer5 = new Spacer();
        panel5.add(spacer5);
        final JLabel label5 = new JLabel();
        label5.setText("Mill Queue Size");
        panel5.add(label5);
        final Spacer spacer6 = new Spacer();
        panel5.add(spacer6);
        millQSize = new JTextField();
        panel5.add(millQSize);
        final JScrollPane scrollPane3 = new JScrollPane();
        buffers.add(scrollPane3, cc.xy(1, 10, CellConstraints.FILL, CellConstraints.FILL));
        unconfirmedCommands = new JTable();
        scrollPane3.setViewportView(unconfirmedCommands);
        difference = new JTextField();
        buffers.add(difference, cc.xy(5, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label6 = new JLabel();
        label6.setText("Difference");
        buffers.add(label6, cc.xy(3, 5));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        stringOrHex.addTab("Single Sided PCB", panel6);
        final JLabel label7 = new JLabel();
        label7.setEnabled(false);
        Font label7Font = this.$$$getFont$$$(null, Font.BOLD, 16, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("Lets create a single sided PCB");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label7, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel6.add(spacer8, gbc);
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$(null, Font.BOLD, 14, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        label8.setText("Step 0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label8, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Fasten the single sided PBC raw material to your mill.");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label9, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("OK, now Hand's off your mill");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label10, gbc);
        didThatTakeMeButton = new JButton();
        didThatTakeMeButton.setText("Did that, take me to step 1");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(didThatTakeMeButton, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Put a V-shaped carving bit into the mill");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label11, gbc);
        final JLabel label12 = new JLabel();
        Font label12Font = this.$$$getFont$$$(null, Font.BOLD, 14, label12.getFont());
        if (label12Font != null) label12.setFont(label12Font);
        label12.setText("Step 1: Initing");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label12, gbc);
        final JLabel label13 = new JLabel();
        label13.setText("Homing");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label13, gbc);
        OKDoneCheckBox1 = new JCheckBox();
        OKDoneCheckBox1.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox1, gbc);
        final JLabel label14 = new JLabel();
        label14.setText("Zero x y");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label14, gbc);
        OKDoneCheckBox2 = new JCheckBox();
        OKDoneCheckBox2.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox2, gbc);
        final JLabel label15 = new JLabel();
        label15.setText("Jog the mill to where the workpiece is");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label15, gbc);
        didThatMoveOnButton = new JButton();
        didThatMoveOnButton.setText("Did that, move on");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(didThatMoveOnButton, gbc);
        final JLabel label16 = new JLabel();
        label16.setText("Saving restore coords");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label16, gbc);
        OKDoneCheckBox5 = new JCheckBox();
        OKDoneCheckBox5.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox5, gbc);
        OKDoneCheckBox4 = new JCheckBox();
        OKDoneCheckBox4.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox4, gbc);
        final JLabel label17 = new JLabel();
        label17.setText("Zero X Y");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label17, gbc);
        final JLabel label18 = new JLabel();
        label18.setText("Doing a Z-probe (make sure it can be done autmatically)");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label18, gbc);
        OKDoneCheckBox6 = new JCheckBox();
        OKDoneCheckBox6.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox6, gbc);
        OKDoneCheckBox7 = new JCheckBox();
        OKDoneCheckBox7.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox7, gbc);
        final JLabel label19 = new JLabel();
        label19.setText("Move slightly abouve the PCB");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label19, gbc);
        final JLabel label20 = new JLabel();
        label20.setText("Createing adjusted Mask file");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label20, gbc);
        OKDoneCheckBox8 = new JCheckBox();
        OKDoneCheckBox8.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox8, gbc);
        loadTheCopperTopButton = new JButton();
        loadTheCopperTopButton.setText("Load the copper-top nc file to candle");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(loadTheCopperTopButton, gbc);
        final JLabel label21 = new JLabel();
        Font label21Font = this.$$$getFont$$$(null, Font.BOLD, 14, label21.getFont());
        if (label21Font != null) label21.setFont(label21Font);
        label21.setText("Step 2: Isolation routing");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label21, gbc);
        final JLabel label22 = new JLabel();
        label22.setText("Your turn, most of the time");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label22, gbc);
        createAHightmapButton = new JButton();
        createAHightmapButton.setText("Create a hightmap");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(createAHightmapButton, gbc);
        useTheHightmapButton = new JButton();
        useTheHightmapButton.setText("Use the hightmap");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(useTheHightmapButton, gbc);
        startIsolationRoutingHitButton = new JButton();
        startIsolationRoutingHitButton.setText("Start isolation routing, hit when done");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(startIsolationRoutingHitButton, gbc);
        doAToolchangeToButton = new JButton();
        doAToolchangeToButton.setText("Do a toolchange to a 1mm flatend mill");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 17;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(doAToolchangeToButton, gbc);
        final JLabel label23 = new JLabel();
        label23.setText("Z Probe and reset Z");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 18;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label23, gbc);
        OKDoneCheckBox14 = new JCheckBox();
        OKDoneCheckBox14.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox14, gbc);
        final JLabel label24 = new JLabel();
        Font label24Font = this.$$$getFont$$$(null, Font.BOLD, 14, label24.getFont());
        if (label24Font != null) label24.setFont(label24Font);
        label24.setText("Step 3: Apply solder mask");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label24, gbc);
        final JLabel label25 = new JLabel();
        label25.setText("Hold on, presenting the workplace");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label25, gbc);
        final JLabel label26 = new JLabel();
        label26.setText("Move drill aside");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 19;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label26, gbc);
        OKDoneCheckBox15 = new JCheckBox();
        OKDoneCheckBox15.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox15, gbc);
        final JLabel label27 = new JLabel();
        label27.setText("Apply solder resist fluid");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label27, gbc);
        didThatGoOnButton = new JButton();
        didThatGoOnButton.setText("Did that, go on");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 20;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(didThatGoOnButton, gbc);
        shineSomeUVLightButton = new JButton();
        shineSomeUVLightButton.setText("Shine some UV light to it (cure solder resist)");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 21;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(shineSomeUVLightButton, gbc);
        runAdjustedMaskNcButton = new JButton();
        runAdjustedMaskNcButton.setText("Run adjusted mask.nc file");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 22;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(runAdjustedMaskNcButton, gbc);
        final JLabel label28 = new JLabel();
        Font label28Font = this.$$$getFont$$$(null, Font.BOLD, 14, label28.getFont());
        if (label28Font != null) label28.setFont(label28Font);
        label28.setText("Step 4: Drill the holes");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 23;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label28, gbc);
        final JLabel label29 = new JLabel();
        label29.setText("Drilling time, repeat for all drill widths");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 23;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label29, gbc);
        toolchangeToYourDrillButton = new JButton();
        toolchangeToYourDrillButton.setText("Toolchange to your drill bit");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 23;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(toolchangeToYourDrillButton, gbc);
        OKDoneCheckBox = new JCheckBox();
        OKDoneCheckBox.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox, gbc);
        OKDoneCheckBox3 = new JCheckBox();
        OKDoneCheckBox3.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox3, gbc);
        OKDoneCheckBox9 = new JCheckBox();
        OKDoneCheckBox9.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox9, gbc);
        OKDoneCheckBox10 = new JCheckBox();
        OKDoneCheckBox10.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox10, gbc);
        OKDoneCheckBox11 = new JCheckBox();
        OKDoneCheckBox11.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox11, gbc);
        OKDoneCheckBox12 = new JCheckBox();
        OKDoneCheckBox12.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox12, gbc);
        OKDoneCheckBox13 = new JCheckBox();
        OKDoneCheckBox13.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox13, gbc);
        OKDoneCheckBox16 = new JCheckBox();
        OKDoneCheckBox16.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox16, gbc);
        OKDoneCheckBox17 = new JCheckBox();
        OKDoneCheckBox17.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 21;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox17, gbc);
        OKDoneCheckBox18 = new JCheckBox();
        OKDoneCheckBox18.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 22;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox18, gbc);
        OKDoneCheckBox19 = new JCheckBox();
        OKDoneCheckBox19.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 23;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox19, gbc);
        final JLabel label30 = new JLabel();
        label30.setText("Z Probing again");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 24;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label30, gbc);
        OKDoneCheckBox20 = new JCheckBox();
        OKDoneCheckBox20.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 24;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox20, gbc);
        runDrillFileButton = new JButton();
        runDrillFileButton.setText("Run Drill file");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 25;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(runDrillFileButton, gbc);
        OKDoneCheckBox21 = new JCheckBox();
        OKDoneCheckBox21.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 25;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox21, gbc);
        final JLabel label31 = new JLabel();
        Font label31Font = this.$$$getFont$$$(null, Font.BOLD, 14, label31.getFont());
        if (label31Font != null) label31.setFont(label31Font);
        label31.setText("Step 5: Cutout");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 26;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label31, gbc);
        final JLabel label32 = new JLabel();
        label32.setText("Final step");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 26;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label32, gbc);
        toolchangeToTheCutoutButton = new JButton();
        toolchangeToTheCutoutButton.setText("Toolchange to the cutout tool ");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 26;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(toolchangeToTheCutoutButton, gbc);
        OKDoneCheckBox22 = new JCheckBox();
        OKDoneCheckBox22.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 26;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox22, gbc);
        final JLabel label33 = new JLabel();
        label33.setText("ZProbe for the last time");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 27;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label33, gbc);
        runTheCutoutNcButton = new JButton();
        runTheCutoutNcButton.setText("Run the cutout nc");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 28;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(runTheCutoutNcButton, gbc);
        OKDoneCheckBox23 = new JCheckBox();
        OKDoneCheckBox23.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 28;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(OKDoneCheckBox23, gbc);
        final JLabel label34 = new JLabel();
        Font label34Font = this.$$$getFont$$$(null, Font.BOLD, 16, label34.getFont());
        if (label34Font != null) label34.setFont(label34Font);
        label34.setHorizontalTextPosition(0);
        label34.setText("You made it!");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 29;
        gbc.gridwidth = 6;
        panel6.add(label34, gbc);
        final JLabel label35 = new JLabel();
        label35.setText("LAST CHANCE TO DO A TOOLCHANGE");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(label35, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel7.add(panel8, BorderLayout.NORTH);
        disableGCheckBox = new JCheckBox();
        disableGCheckBox.setText("Disable G$");
        panel8.add(disableGCheckBox);
        disableCheckBox = new JCheckBox();
        disableCheckBox.setText("disable ?");
        panel8.add(disableCheckBox);
        disableAllCheckBox = new JCheckBox();
        disableAllCheckBox.setText("Disable All");
        panel8.add(disableAllCheckBox);
        final Spacer spacer9 = new Spacer();
        panel8.add(spacer9);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel7.add(panel9, BorderLayout.CENTER);
        final Spacer spacer10 = new Spacer();
        panel9.add(spacer10);
        maskHeights = new JButton();
        maskHeights.setText("StoreMask Z");
        panel9.add(maskHeights);
        openFile = new JButton();
        openFile.setText("...");
        panel9.add(openFile);
        final Spacer spacer11 = new Spacer();
        panel9.add(spacer11);
        saveEjectPosButton = new JButton();
        saveEjectPosButton.setText("Save Eject Pos");
        panel9.add(saveEjectPosButton);
        btnEject = new JButton();
        btnEject.setText("Eject");
        panel9.add(btnEject);
        final Spacer spacer12 = new Spacer();
        panel9.add(spacer12);
        gracefulResumeButton = new JButton();
        gracefulResumeButton.setText("GracefulResume");
        panel9.add(gracefulResumeButton);
        final Spacer spacer13 = new Spacer();
        panel9.add(spacer13);
        final Spacer spacer14 = new Spacer();
        panel9.add(spacer14);
        storeWorkItemPosition = new JButton();
        storeWorkItemPosition.setText("Save Work Item Home");
        panel9.add(storeWorkItemPosition);
        final Spacer spacer15 = new Spacer();
        panel9.add(spacer15);
        restoreWorkItemHome = new JButton();
        restoreWorkItemHome.setText("Restore Work Home");
        panel9.add(restoreWorkItemHome);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainpanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
