package de.dbconsult.interceptor.workflow;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.dbconsult.interceptor.WorkflowResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class LogAndPassFrame {
    private JPanel mainpanel;
    private JTable hexData;
    private JButton button1;
    private JButton button2;
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
    DefaultTableModel stringTableModel;
    DefaultTableModel hexTableModel;

    public LogAndPassFrame(String port1, String port2) {
        stringTableModel = new DefaultTableModel(new String[]{"#", port2, port1}, 0);
        hexTableModel = new DefaultTableModel(new String[]{"#", port2, port1}, 0);
        $$$setupUI$$$();
        if (numOfOK.getText().isEmpty()) numOfOK.setText("0");
        if (numOfCR.getText().isEmpty()) numOfCR.setText("0");
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

    public void incrementCmd(int commandsFound) {
        if (numOfCR.getText().isEmpty()) numOfCR.setText("0");
        numOfCR.setText(Long.parseLong(numOfCR.getText()) + commandsFound + "");
        difference.setText(Long.parseLong(numOfCR.getText()) - Long.parseLong(numOfOK.getText()) + "");
    }

    public void incrementOk(int oksFound) {
        if (numOfOK.getText().isEmpty()) numOfOK.setText("0");
        numOfOK.setText(Long.parseLong(numOfOK.getText()) + oksFound + "");
        difference.setText(Long.parseLong(numOfCR.getText()) - Long.parseLong(numOfOK.getText()) + "");
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
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buffers.add(panel5, cc.xy(1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        label3.setText("Ok Count");
        panel5.add(label3);
        final Spacer spacer2 = new Spacer();
        panel5.add(spacer2);
        progressBar2 = new JProgressBar();
        panel5.add(progressBar2);
        numOfOK = new JTextField();
        numOfOK.setMinimumSize(new Dimension(256, 30));
        panel5.add(numOfOK);
        final JScrollPane scrollPane3 = new JScrollPane();
        buffers.add(scrollPane3, cc.xy(1, 10, CellConstraints.FILL, CellConstraints.FILL));
        unconfirmedCommands = new JTable();
        scrollPane3.setViewportView(unconfirmedCommands);
        difference = new JTextField();
        buffers.add(difference, cc.xy(5, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label4 = new JLabel();
        label4.setText("Difference");
        buffers.add(label4, cc.xy(3, 5));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        splitPane1.setRightComponent(panel6);
        disableGCheckBox = new JCheckBox();
        disableGCheckBox.setText("Disable G$");
        panel6.add(disableGCheckBox);
        disableCheckBox = new JCheckBox();
        disableCheckBox.setText("disable ?");
        panel6.add(disableCheckBox);
        disableAllCheckBox = new JCheckBox();
        disableAllCheckBox.setText("Disable All");
        panel6.add(disableAllCheckBox);
        final Spacer spacer3 = new Spacer();
        panel6.add(spacer3);
        button1 = new JButton();
        button1.setText("Button");
        panel6.add(button1);
        final Spacer spacer4 = new Spacer();
        panel6.add(spacer4);
        final Spacer spacer5 = new Spacer();
        panel6.add(spacer5);
        button2 = new JButton();
        button2.setText("Button");
        panel6.add(button2);
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
