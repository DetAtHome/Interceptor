package de.dbconsult.interceptor.workflow;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.dbconsult.interceptor.TargetDevices;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.exactheight.MaskZMainController;
import de.dbconsult.interceptor.exactposition.EdgeMonitor;
import de.dbconsult.interceptor.exactposition.ExtraReader;
import de.dbconsult.interceptor.internal.UIController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

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
    private JButton findLowerLeft;
    private JTextField txtXWorkpieceOffset;
    private JTextField txtYWorkpieceOffset;
    private JButton projectDirectoryButton;
    private JTextField txtXOffset;
    private JTextField txtYOffset;
    private JButton flipButton;
    private JButton cleanButton;
    private JButton btnTest;
    private JButton btnTestUpper;
    private JTextField txtAButtonOffsetX;
    private JTextField txtAButtonOffsetY;
    private JTextField txtBButtonOffsetX;
    private JTextField txtBButtonOffsetY;
    private JButton sveSettings;
    private JButton loadSettings;
    private JTextField txtExtraInput;
    private JTextArea txtLogOut;
    private JButton btnSendExtra;
    private JCheckBox disableHeartbeatCheckBox;
    private JFileChooser fc = new JFileChooser();
    private JFileChooser rotFc = new JFileChooser();

    DefaultTableModel stringTableModel;
    DefaultTableModel hexTableModel;

    private WorkflowDataStore workflowDataStore = null;
    private UIController uiController = null;
    private ExtraReader extraReader = null;

    public LogAndPassFrame(String port1, String port2, String port3, WorkflowDataStore workflowDataStore, UIController controller) {
        this.workflowDataStore = workflowDataStore;
        this.uiController = controller;
        extraReader = (ExtraReader) workflowDataStore.read("EXTRAREADER");
        stringTableModel = new DefaultTableModel(new String[]{"#", port2, port1, port3}, 0);
        hexTableModel = new DefaultTableModel(new String[]{"#", port2, port1, port3}, 0);
        $$$setupUI$$$();
        if (numOfOK.getText().isEmpty()) numOfOK.setText("0");
        if (numOfCR.getText().isEmpty()) numOfCR.setText("0");
        if (pcQSize.getText().isEmpty()) pcQSize.setText("0");
        if (millQSize.getText().isEmpty()) millQSize.setText("0");

        projectDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotFc.setCurrentDirectory(new File("D:/Projects/arduinoscetches/UebungsLayout/CNC"));
                int returnVal = rotFc.showOpenDialog(mainpanel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    workflowDataStore.update("RotationFileDirectory", rotFc.getSelectedFile().getAbsoluteFile());
                    workflowDataStore.update("RotationFileName", rotFc.getSelectedFile().getAbsoluteFile().getName());
                }
            }
        });

        txtExtraInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtraReader extraReader = new ExtraReader(workflowDataStore);
                String toSend = txtExtraInput.getText();
                if (!toSend.endsWith(";")) toSend = toSend +";";
                extraReader.send(toSend);
                txtExtraInput.setText("");
            }
        });
        btnSendExtra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtraReader extraReader = new ExtraReader(workflowDataStore);
                String toSend = txtExtraInput.getText();
                extraReader.send(toSend);
            }
        });
        sveSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                workflowDataStore.update("WorkpieceXOffset", Double.parseDouble(txtXWorkpieceOffset.getText()));
                workflowDataStore.update("WorkpieceYOffset", Double.parseDouble(txtYWorkpieceOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetX", Double.parseDouble(txtXOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetY", Double.parseDouble(txtYOffset.getText()));
                workflowDataStore.update("FwdButtonOffsetX", Double.parseDouble(txtAButtonOffsetX.getText()));
                workflowDataStore.update("FwdButtonOffsetY", Double.parseDouble(txtAButtonOffsetY.getText()));
                workflowDataStore.update("RvsButtonOffsetX", Double.parseDouble(txtBButtonOffsetX.getText()));
                workflowDataStore.update("RvsButtonOffsetY", Double.parseDouble(txtBButtonOffsetY.getText()));
                workflowDataStore.update("EdgeMonitorFunctionCalled", "saveSettings");
                EdgeMonitor edgeMonitor = new EdgeMonitor(workflowDataStore);
                edgeMonitor.start();
            }
        });

        loadSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap<String, Double> tempStore;
                EdgeMonitor edgeMonitor = new EdgeMonitor(workflowDataStore);
                tempStore = edgeMonitor.loadSettings();
                txtXWorkpieceOffset.setText(tempStore.get("WorkpieceXOffset").toString());
                txtYWorkpieceOffset.setText(tempStore.get("WorkpieceYOffset").toString());
                txtXOffset.setText(tempStore.get("CurrentWorkpieceOffsetX").toString());
                txtYOffset.setText(tempStore.get("CurrentWorkpieceOffsetY").toString());
                txtAButtonOffsetX.setText(tempStore.get("FwdButtonOffsetX").toString());
                txtAButtonOffsetY.setText(tempStore.get("FwdButtonOffsetY").toString());
                txtBButtonOffsetX.setText(tempStore.get("RvsButtonOffsetX").toString());
                txtBButtonOffsetY.setText(tempStore.get("RvsButtonOffsetY").toString());

            }
        });
        btnTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workflowDataStore.update("WorkpieceXOffset", Double.parseDouble(txtXWorkpieceOffset.getText()));
                workflowDataStore.update("WorkpieceYOffset", Double.parseDouble(txtYWorkpieceOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetX", Double.parseDouble(txtXOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetY", Double.parseDouble(txtYOffset.getText()));
                workflowDataStore.update("EdgeMonitorFunctionCalled", "TestLower");
                EdgeMonitor edgeMonitor = new EdgeMonitor(workflowDataStore);
                edgeMonitor.start();
            }
        });

        btnTestUpper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workflowDataStore.update("WorkpieceXOffset", Double.parseDouble(txtXWorkpieceOffset.getText()));
                workflowDataStore.update("WorkpieceYOffset", Double.parseDouble(txtYWorkpieceOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetX", Double.parseDouble(txtXOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetY", Double.parseDouble(txtYOffset.getText()));
                workflowDataStore.update("EdgeMonitorFunctionCalled", "TestUpper");
                EdgeMonitor edgeMonitor = new EdgeMonitor(workflowDataStore);
                edgeMonitor.start();
            }
        });
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

        findLowerLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                workflowDataStore.update("WorkpieceXOffset", Double.parseDouble(txtXWorkpieceOffset.getText()));
                workflowDataStore.update("WorkpieceYOffset", Double.parseDouble(txtYWorkpieceOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetX", Double.parseDouble(txtXOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetY", Double.parseDouble(txtYOffset.getText()));
                workflowDataStore.update("FwdButtonOffsetX", Double.parseDouble(txtAButtonOffsetX.getText()));
                workflowDataStore.update("FwdButtonOffsetY", Double.parseDouble(txtAButtonOffsetY.getText()));
                workflowDataStore.update("RvsButtonOffsetX", Double.parseDouble(txtBButtonOffsetX.getText()));
                workflowDataStore.update("RvsButtonOffsetY", Double.parseDouble(txtBButtonOffsetX.getText()));
                workflowDataStore.update("EdgeMonitorFunctionCalled", "FindLowerLeft");
                EdgeMonitor edgeMonitor = new EdgeMonitor(workflowDataStore);
                edgeMonitor.start();
            }
        });

        flipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workflowDataStore.update("WorkpieceXOffset", Double.parseDouble(txtXWorkpieceOffset.getText()));
                workflowDataStore.update("WorkpieceYOffset", Double.parseDouble(txtYWorkpieceOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetX", Double.parseDouble(txtXOffset.getText()));
                workflowDataStore.update("CurrentWorkpieceOffsetY", Double.parseDouble(txtYOffset.getText()));
                workflowDataStore.update("EdgeMonitorFunctionCalled", "FindUpperLeft");
                EdgeMonitor edgeMonitor = new EdgeMonitor(workflowDataStore);
                edgeMonitor.start();

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
                uiController.testSomething();
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

    public void extraLog(String log) {
        txtLogOut.append(log);
    }

    public boolean isHeartbeatEnabled() {
        return disableHeartbeatCheckBox.isSelected();
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
                    if (wfData.getFormSource()== TargetDevices.CANDLE) {
                        stringTableModel.setValueAt(new String(wfData.getOutput()), stringTableModel.getRowCount() - 1, 1);
                        String hexView = toHexString(wfData.getOutput());
                        hexTableModel.setValueAt(hexView, stringTableModel.getRowCount() - 1, 1);
                        return;
                    }
                }
                if (((String) stringTableModel.getValueAt(stringTableModel.getRowCount() - 1, 2)).isEmpty()) {
                    if (wfData.getFormSource()==TargetDevices.CNC) {
                        stringTableModel.setValueAt(new String(wfData.getOutput()), stringTableModel.getRowCount() - 1, 2);
                        String hexView = toHexString(wfData.getOutput());
                        hexTableModel.setValueAt(hexView, stringTableModel.getRowCount() - 1, 2);
                        return;
                    }
                }
            }
        }
        if (wfData.getFormSource()==TargetDevices.CANDLE) {
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
        panel6.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        stringOrHex.addTab("ExtraComm", panel6);
        txtExtraInput = new JTextField();
        panel6.add(txtExtraInput, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel6.add(spacer7, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnSendExtra = new JButton();
        btnSendExtra.setText("Send To Extra");
        panel6.add(btnSendExtra, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        panel6.add(scrollPane4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        txtLogOut = new JTextArea();
        scrollPane4.setViewportView(txtLogOut);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        stringOrHex.addTab("Single Sided PCB", panel7);
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
        panel7.add(label7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(spacer8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel7.add(spacer9, gbc);
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$(null, Font.BOLD, 14, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        label8.setText("Step 0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label8, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Fasten the single sided PBC raw material to your mill.");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label9, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("OK, now Hand's off your mill");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label10, gbc);
        didThatTakeMeButton = new JButton();
        didThatTakeMeButton.setText("Did that, take me to step 1");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(didThatTakeMeButton, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Put a V-shaped carving bit into the mill");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label11, gbc);
        final JLabel label12 = new JLabel();
        Font label12Font = this.$$$getFont$$$(null, Font.BOLD, 14, label12.getFont());
        if (label12Font != null) label12.setFont(label12Font);
        label12.setText("Step 1: Initing");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label12, gbc);
        final JLabel label13 = new JLabel();
        label13.setText("Homing");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label13, gbc);
        OKDoneCheckBox1 = new JCheckBox();
        OKDoneCheckBox1.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox1, gbc);
        final JLabel label14 = new JLabel();
        label14.setText("Zero x y");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label14, gbc);
        OKDoneCheckBox2 = new JCheckBox();
        OKDoneCheckBox2.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox2, gbc);
        final JLabel label15 = new JLabel();
        label15.setText("Jog the mill to where the workpiece is");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label15, gbc);
        didThatMoveOnButton = new JButton();
        didThatMoveOnButton.setText("Did that, move on");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(didThatMoveOnButton, gbc);
        final JLabel label16 = new JLabel();
        label16.setText("Saving restore coords");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label16, gbc);
        OKDoneCheckBox5 = new JCheckBox();
        OKDoneCheckBox5.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox5, gbc);
        OKDoneCheckBox4 = new JCheckBox();
        OKDoneCheckBox4.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox4, gbc);
        final JLabel label17 = new JLabel();
        label17.setText("Zero X Y");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label17, gbc);
        final JLabel label18 = new JLabel();
        label18.setText("Doing a Z-probe (make sure it can be done autmatically)");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label18, gbc);
        OKDoneCheckBox6 = new JCheckBox();
        OKDoneCheckBox6.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox6, gbc);
        OKDoneCheckBox7 = new JCheckBox();
        OKDoneCheckBox7.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox7, gbc);
        final JLabel label19 = new JLabel();
        label19.setText("Move slightly abouve the PCB");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label19, gbc);
        final JLabel label20 = new JLabel();
        label20.setText("Createing adjusted Mask file");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label20, gbc);
        OKDoneCheckBox8 = new JCheckBox();
        OKDoneCheckBox8.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox8, gbc);
        loadTheCopperTopButton = new JButton();
        loadTheCopperTopButton.setText("Load the copper-top nc file to candle");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(loadTheCopperTopButton, gbc);
        final JLabel label21 = new JLabel();
        Font label21Font = this.$$$getFont$$$(null, Font.BOLD, 14, label21.getFont());
        if (label21Font != null) label21.setFont(label21Font);
        label21.setText("Step 2: Isolation routing");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label21, gbc);
        final JLabel label22 = new JLabel();
        label22.setText("Your turn, most of the time");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label22, gbc);
        createAHightmapButton = new JButton();
        createAHightmapButton.setText("Create a hightmap");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(createAHightmapButton, gbc);
        useTheHightmapButton = new JButton();
        useTheHightmapButton.setText("Use the hightmap");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(useTheHightmapButton, gbc);
        startIsolationRoutingHitButton = new JButton();
        startIsolationRoutingHitButton.setText("Start isolation routing, hit when done");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(startIsolationRoutingHitButton, gbc);
        doAToolchangeToButton = new JButton();
        doAToolchangeToButton.setText("Do a toolchange to a 1mm flatend mill");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 17;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(doAToolchangeToButton, gbc);
        final JLabel label23 = new JLabel();
        label23.setText("Z Probe and reset Z");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 18;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label23, gbc);
        OKDoneCheckBox14 = new JCheckBox();
        OKDoneCheckBox14.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox14, gbc);
        final JLabel label24 = new JLabel();
        Font label24Font = this.$$$getFont$$$(null, Font.BOLD, 14, label24.getFont());
        if (label24Font != null) label24.setFont(label24Font);
        label24.setText("Step 3: Apply solder mask");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label24, gbc);
        final JLabel label25 = new JLabel();
        label25.setText("Hold on, presenting the workplace");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label25, gbc);
        final JLabel label26 = new JLabel();
        label26.setText("Move drill aside");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 19;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label26, gbc);
        OKDoneCheckBox15 = new JCheckBox();
        OKDoneCheckBox15.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox15, gbc);
        final JLabel label27 = new JLabel();
        label27.setText("Apply solder resist fluid");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label27, gbc);
        didThatGoOnButton = new JButton();
        didThatGoOnButton.setText("Did that, go on");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 20;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(didThatGoOnButton, gbc);
        shineSomeUVLightButton = new JButton();
        shineSomeUVLightButton.setText("Shine some UV light to it (cure solder resist)");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 21;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(shineSomeUVLightButton, gbc);
        runAdjustedMaskNcButton = new JButton();
        runAdjustedMaskNcButton.setText("Run adjusted mask.nc file");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 22;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(runAdjustedMaskNcButton, gbc);
        final JLabel label28 = new JLabel();
        Font label28Font = this.$$$getFont$$$(null, Font.BOLD, 14, label28.getFont());
        if (label28Font != null) label28.setFont(label28Font);
        label28.setText("Step 4: Drill the holes");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 23;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label28, gbc);
        final JLabel label29 = new JLabel();
        label29.setText("Drilling time, repeat for all drill widths");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 23;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label29, gbc);
        toolchangeToYourDrillButton = new JButton();
        toolchangeToYourDrillButton.setText("Toolchange to your drill bit");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 23;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(toolchangeToYourDrillButton, gbc);
        OKDoneCheckBox = new JCheckBox();
        OKDoneCheckBox.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox, gbc);
        OKDoneCheckBox3 = new JCheckBox();
        OKDoneCheckBox3.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox3, gbc);
        OKDoneCheckBox9 = new JCheckBox();
        OKDoneCheckBox9.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox9, gbc);
        OKDoneCheckBox10 = new JCheckBox();
        OKDoneCheckBox10.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox10, gbc);
        OKDoneCheckBox11 = new JCheckBox();
        OKDoneCheckBox11.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox11, gbc);
        OKDoneCheckBox12 = new JCheckBox();
        OKDoneCheckBox12.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox12, gbc);
        OKDoneCheckBox13 = new JCheckBox();
        OKDoneCheckBox13.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox13, gbc);
        OKDoneCheckBox16 = new JCheckBox();
        OKDoneCheckBox16.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox16, gbc);
        OKDoneCheckBox17 = new JCheckBox();
        OKDoneCheckBox17.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 21;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox17, gbc);
        OKDoneCheckBox18 = new JCheckBox();
        OKDoneCheckBox18.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 22;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox18, gbc);
        OKDoneCheckBox19 = new JCheckBox();
        OKDoneCheckBox19.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 23;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox19, gbc);
        final JLabel label30 = new JLabel();
        label30.setText("Z Probing again");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 24;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label30, gbc);
        OKDoneCheckBox20 = new JCheckBox();
        OKDoneCheckBox20.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 24;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox20, gbc);
        runDrillFileButton = new JButton();
        runDrillFileButton.setText("Run Drill file");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 25;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(runDrillFileButton, gbc);
        OKDoneCheckBox21 = new JCheckBox();
        OKDoneCheckBox21.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 25;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox21, gbc);
        final JLabel label31 = new JLabel();
        Font label31Font = this.$$$getFont$$$(null, Font.BOLD, 14, label31.getFont());
        if (label31Font != null) label31.setFont(label31Font);
        label31.setText("Step 5: Cutout");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 26;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label31, gbc);
        final JLabel label32 = new JLabel();
        label32.setText("Final step");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 26;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label32, gbc);
        toolchangeToTheCutoutButton = new JButton();
        toolchangeToTheCutoutButton.setText("Toolchange to the cutout tool ");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 26;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(toolchangeToTheCutoutButton, gbc);
        OKDoneCheckBox22 = new JCheckBox();
        OKDoneCheckBox22.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 26;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox22, gbc);
        final JLabel label33 = new JLabel();
        label33.setText("ZProbe for the last time");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 27;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label33, gbc);
        runTheCutoutNcButton = new JButton();
        runTheCutoutNcButton.setText("Run the cutout nc");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 28;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(runTheCutoutNcButton, gbc);
        OKDoneCheckBox23 = new JCheckBox();
        OKDoneCheckBox23.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 28;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(OKDoneCheckBox23, gbc);
        final JLabel label34 = new JLabel();
        Font label34Font = this.$$$getFont$$$(null, Font.BOLD, 16, label34.getFont());
        if (label34Font != null) label34.setFont(label34Font);
        label34.setHorizontalTextPosition(0);
        label34.setText("You made it!");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 29;
        gbc.gridwidth = 6;
        panel7.add(label34, gbc);
        final JLabel label35 = new JLabel();
        label35.setText("LAST CHANCE TO DO A TOOLCHANGE");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        panel7.add(label35, gbc);
        final JScrollPane scrollPane5 = new JScrollPane();
        stringOrHex.addTab("2Sided PCB", scrollPane5);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane5.setViewportView(panel8);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        panel8.add(panel9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label36 = new JLabel();
        label36.setEnabled(false);
        Font label36Font = this.$$$getFont$$$(null, Font.BOLD, 16, label36.getFont());
        if (label36Font != null) label36.setFont(label36Font);
        label36.setText("Lets create a double sided PCB");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label36, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel9.add(spacer10, gbc);
        final JLabel label37 = new JLabel();
        Font label37Font = this.$$$getFont$$$(null, Font.BOLD, 14, label37.getFont());
        if (label37Font != null) label37.setFont(label37Font);
        label37.setText("Step 0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label37, gbc);
        final JLabel label38 = new JLabel();
        label38.setText("Fasten the single sided PBC raw material to your mill.");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label38, gbc);
        final JLabel label39 = new JLabel();
        label39.setText("OK, now Hand's off your mill");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label39, gbc);
        final JButton button1 = new JButton();
        button1.setText("Did that, take me to step 1");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button1, gbc);
        final JLabel label40 = new JLabel();
        label40.setText("Put a V-shaped carving bit into the mill");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label40, gbc);
        final JLabel label41 = new JLabel();
        Font label41Font = this.$$$getFont$$$(null, Font.BOLD, 14, label41.getFont());
        if (label41Font != null) label41.setFont(label41Font);
        label41.setText("Step 1: Initing");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label41, gbc);
        final JLabel label42 = new JLabel();
        label42.setText("Homing");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label42, gbc);
        final JCheckBox checkBox1 = new JCheckBox();
        checkBox1.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox1, gbc);
        final JLabel label43 = new JLabel();
        label43.setText("Zero x y");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 9;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label43, gbc);
        final JCheckBox checkBox2 = new JCheckBox();
        checkBox2.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox2, gbc);
        final JLabel label44 = new JLabel();
        label44.setText("Jog the mill to where the workpiece is");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label44, gbc);
        final JButton button2 = new JButton();
        button2.setText("Did that, move on");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 10;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button2, gbc);
        final JLabel label45 = new JLabel();
        label45.setText("Saving restore coords");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 11;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label45, gbc);
        final JCheckBox checkBox3 = new JCheckBox();
        checkBox3.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox3, gbc);
        final JCheckBox checkBox4 = new JCheckBox();
        checkBox4.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox4, gbc);
        final JLabel label46 = new JLabel();
        label46.setText("Zero X Y");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 12;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label46, gbc);
        final JLabel label47 = new JLabel();
        label47.setText("Doing a Z-probe (make sure it can be done autmatically)");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 13;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label47, gbc);
        final JCheckBox checkBox5 = new JCheckBox();
        checkBox5.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox5, gbc);
        final JCheckBox checkBox6 = new JCheckBox();
        checkBox6.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox6, gbc);
        final JLabel label48 = new JLabel();
        label48.setText("Move slightly abouve the PCB");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 14;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label48, gbc);
        final JLabel label49 = new JLabel();
        label49.setText("Createing adjusted Mask file");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 15;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label49, gbc);
        final JCheckBox checkBox7 = new JCheckBox();
        checkBox7.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox7, gbc);
        final JButton button3 = new JButton();
        button3.setText("Load the copper-top nc file to candle");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 16;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button3, gbc);
        final JLabel label50 = new JLabel();
        Font label50Font = this.$$$getFont$$$(null, Font.BOLD, 14, label50.getFont());
        if (label50Font != null) label50.setFont(label50Font);
        label50.setText("Step 2: Isolation routing");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label50, gbc);
        final JLabel label51 = new JLabel();
        label51.setText("Your turn, most of the time");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 16;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label51, gbc);
        final JButton button4 = new JButton();
        button4.setText("Create a hightmap");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 17;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button4, gbc);
        final JButton button5 = new JButton();
        button5.setText("Use the hightmap");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 19;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button5, gbc);
        final JButton button6 = new JButton();
        button6.setText("Start isolation routing, hit when done");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 20;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button6, gbc);
        final JButton button7 = new JButton();
        button7.setText("Do a toolchange to a 1mm flatend mill");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 21;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button7, gbc);
        final JLabel label52 = new JLabel();
        label52.setText("Z Probe and reset Z");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 22;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label52, gbc);
        final JCheckBox checkBox8 = new JCheckBox();
        checkBox8.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 22;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox8, gbc);
        final JLabel label53 = new JLabel();
        Font label53Font = this.$$$getFont$$$(null, Font.BOLD, 14, label53.getFont());
        if (label53Font != null) label53.setFont(label53Font);
        label53.setText("Step 3: Apply solder mask");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 23;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label53, gbc);
        final JLabel label54 = new JLabel();
        label54.setText("Hold on, presenting the workplace");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 23;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label54, gbc);
        final JLabel label55 = new JLabel();
        label55.setText("Move drill aside");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 23;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label55, gbc);
        final JCheckBox checkBox9 = new JCheckBox();
        checkBox9.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 23;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox9, gbc);
        final JLabel label56 = new JLabel();
        label56.setText("Apply solder resist fluid");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 24;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label56, gbc);
        final JButton button8 = new JButton();
        button8.setText("Did that, go on");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 24;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button8, gbc);
        final JButton button9 = new JButton();
        button9.setText("Shine some UV light to it (cure solder resist)");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 25;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button9, gbc);
        final JButton button10 = new JButton();
        button10.setText("Run adjusted mask.nc file");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 26;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button10, gbc);
        final JLabel label57 = new JLabel();
        Font label57Font = this.$$$getFont$$$(null, Font.BOLD, 14, label57.getFont());
        if (label57Font != null) label57.setFont(label57Font);
        label57.setText("Step 4: Drill the holes");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 27;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label57, gbc);
        final JLabel label58 = new JLabel();
        label58.setText("Drilling time, repeat for all drill widths");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 27;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label58, gbc);
        final JButton button11 = new JButton();
        button11.setText("Toolchange to your drill bit");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 27;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button11, gbc);
        final JCheckBox checkBox10 = new JCheckBox();
        checkBox10.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox10, gbc);
        final JCheckBox checkBox11 = new JCheckBox();
        checkBox11.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox11, gbc);
        final JCheckBox checkBox12 = new JCheckBox();
        checkBox12.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox12, gbc);
        final JCheckBox checkBox13 = new JCheckBox();
        checkBox13.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox13, gbc);
        final JCheckBox checkBox14 = new JCheckBox();
        checkBox14.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox14, gbc);
        final JCheckBox checkBox15 = new JCheckBox();
        checkBox15.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox15, gbc);
        final JCheckBox checkBox16 = new JCheckBox();
        checkBox16.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 21;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox16, gbc);
        final JCheckBox checkBox17 = new JCheckBox();
        checkBox17.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 24;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox17, gbc);
        final JCheckBox checkBox18 = new JCheckBox();
        checkBox18.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 25;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox18, gbc);
        final JCheckBox checkBox19 = new JCheckBox();
        checkBox19.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 26;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox19, gbc);
        final JCheckBox checkBox20 = new JCheckBox();
        checkBox20.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 27;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox20, gbc);
        final JLabel label59 = new JLabel();
        label59.setText("Z Probing again");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 28;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label59, gbc);
        final JCheckBox checkBox21 = new JCheckBox();
        checkBox21.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 28;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox21, gbc);
        final JButton button12 = new JButton();
        button12.setText("Run Drill file");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 29;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button12, gbc);
        final JCheckBox checkBox22 = new JCheckBox();
        checkBox22.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 29;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox22, gbc);
        final JLabel label60 = new JLabel();
        Font label60Font = this.$$$getFont$$$(null, Font.BOLD, 14, label60.getFont());
        if (label60Font != null) label60.setFont(label60Font);
        label60.setText("Step 5: Cutout");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 30;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label60, gbc);
        final JLabel label61 = new JLabel();
        label61.setText("Final step");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 30;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label61, gbc);
        final JButton button13 = new JButton();
        button13.setText("Toolchange to the cutout tool ");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 30;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button13, gbc);
        final JCheckBox checkBox23 = new JCheckBox();
        checkBox23.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 30;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox23, gbc);
        final JLabel label62 = new JLabel();
        label62.setText("ZProbe for the last time");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 31;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label62, gbc);
        final JButton button14 = new JButton();
        button14.setText("Run the cutout nc");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 32;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(button14, gbc);
        final JCheckBox checkBox24 = new JCheckBox();
        checkBox24.setText("OK, done");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 32;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(checkBox24, gbc);
        final JLabel label63 = new JLabel();
        Font label63Font = this.$$$getFont$$$(null, Font.BOLD, 16, label63.getFont());
        if (label63Font != null) label63.setFont(label63Font);
        label63.setHorizontalTextPosition(0);
        label63.setText("You made it!");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 33;
        gbc.gridwidth = 10;
        panel9.add(label63, gbc);
        final JLabel label64 = new JLabel();
        label64.setText("LAST CHANCE TO DO A TOOLCHANGE");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 18;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label64, gbc);
        txtXWorkpieceOffset = new JTextField();
        txtXWorkpieceOffset.setText("5");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(txtXWorkpieceOffset, gbc);
        final JLabel label65 = new JLabel();
        label65.setText("X/Y Workpiece offsets:");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel9.add(label65, gbc);
        final JLabel label66 = new JLabel();
        label66.setText("Homeposition on Workpiece");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel9.add(label66, gbc);
        findLowerLeft = new JButton();
        findLowerLeft.setText("Find lower left");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(findLowerLeft, gbc);
        txtXOffset = new JTextField();
        txtXOffset.setText("5");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(txtXOffset, gbc);
        projectDirectoryButton = new JButton();
        projectDirectoryButton.setText("Project Directory");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(projectDirectoryButton, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(spacer11, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(spacer12, gbc);
        txtYWorkpieceOffset = new JTextField();
        txtYWorkpieceOffset.setText("5");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(txtYWorkpieceOffset, gbc);
        txtYOffset = new JTextField();
        txtYOffset.setText("5");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(txtYOffset, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        panel9.add(spacer13, gbc);
        final JPanel spacer14 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        panel9.add(spacer14, gbc);
        final JPanel spacer15 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        panel9.add(spacer15, gbc);
        final JPanel spacer16 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 10;
        panel9.add(spacer16, gbc);
        btnTest = new JButton();
        btnTest.setText("TestLower");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(btnTest, gbc);
        btnTestUpper = new JButton();
        btnTestUpper.setText("TestUpper");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(btnTestUpper, gbc);
        final JLabel label67 = new JLabel();
        label67.setText("Button Offset from center X/Y");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel9.add(label67, gbc);
        txtAButtonOffsetX = new JTextField();
        txtAButtonOffsetX.setText("0.2");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(txtAButtonOffsetX, gbc);
        txtAButtonOffsetY = new JTextField();
        txtAButtonOffsetY.setText("0.3");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(txtAButtonOffsetY, gbc);
        cleanButton = new JButton();
        cleanButton.setText("Clean");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(cleanButton, gbc);
        txtBButtonOffsetX = new JTextField();
        txtBButtonOffsetX.setText("-0.4");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(txtBButtonOffsetX, gbc);
        flipButton = new JButton();
        flipButton.setText("Flip");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(flipButton, gbc);
        txtBButtonOffsetY = new JTextField();
        txtBButtonOffsetY.setText("-0.2");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(txtBButtonOffsetY, gbc);
        final JLabel label68 = new JLabel();
        label68.setText("Button Offset from center X/Y when flipped");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel9.add(label68, gbc);
        sveSettings = new JButton();
        sveSettings.setText("Save Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(sveSettings, gbc);
        loadSettings = new JButton();
        loadSettings.setText("Load Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(loadSettings, gbc);
        final Spacer spacer17 = new Spacer();
        panel8.add(spacer17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer18 = new Spacer();
        panel8.add(spacer18, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(panel10);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel10.add(panel11, BorderLayout.NORTH);
        disableGCheckBox = new JCheckBox();
        disableGCheckBox.setText("Disable G$");
        panel11.add(disableGCheckBox);
        disableCheckBox = new JCheckBox();
        disableCheckBox.setText("disable ?");
        panel11.add(disableCheckBox);
        disableAllCheckBox = new JCheckBox();
        disableAllCheckBox.setText("Disable All");
        panel11.add(disableAllCheckBox);
        final Spacer spacer19 = new Spacer();
        panel11.add(spacer19);
        disableHeartbeatCheckBox = new JCheckBox();
        disableHeartbeatCheckBox.setText("Disable Heartbeat");
        panel11.add(disableHeartbeatCheckBox);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel10.add(panel12, BorderLayout.CENTER);
        final Spacer spacer20 = new Spacer();
        panel12.add(spacer20);
        maskHeights = new JButton();
        maskHeights.setText("StoreMask Z");
        panel12.add(maskHeights);
        openFile = new JButton();
        openFile.setText("...");
        panel12.add(openFile);
        final Spacer spacer21 = new Spacer();
        panel12.add(spacer21);
        saveEjectPosButton = new JButton();
        saveEjectPosButton.setText("Save Eject Pos");
        panel12.add(saveEjectPosButton);
        btnEject = new JButton();
        btnEject.setText("Eject");
        panel12.add(btnEject);
        final Spacer spacer22 = new Spacer();
        panel12.add(spacer22);
        gracefulResumeButton = new JButton();
        gracefulResumeButton.setText("GracefulResume");
        panel12.add(gracefulResumeButton);
        final Spacer spacer23 = new Spacer();
        panel12.add(spacer23);
        final Spacer spacer24 = new Spacer();
        panel12.add(spacer24);
        storeWorkItemPosition = new JButton();
        storeWorkItemPosition.setText("Save Work Item Home");
        panel12.add(storeWorkItemPosition);
        final Spacer spacer25 = new Spacer();
        panel12.add(spacer25);
        restoreWorkItemHome = new JButton();
        restoreWorkItemHome.setText("Restore Work Home");
        panel12.add(restoreWorkItemHome);
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
