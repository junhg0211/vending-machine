package org.shtelo.sch.vending_project.vending_machine.subwindow;

import com.formdev.flatlaf.FlatClientProperties;
import org.shtelo.sch.vending_project.util.Log;
import org.shtelo.sch.vending_project.vending_machine.VendingMachine;
import org.shtelo.sch.vending_project.vending_machine.data_type.Inventory;
import org.shtelo.sch.vending_project.vending_machine.data_type.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 관리자 콘솔
 */
public class AdminConsole {
    private final VendingMachine machine;
    private JFrame frame;
    private final ArrayList<JSpinner> spinners = new ArrayList<>();
    private final ArrayList<JTextField> names = new ArrayList<>();

    AdminConsole(VendingMachine machine) {
        this.machine = machine;

        makeWindow();
    }

    private void makeWindow() {
        frame = new JFrame();
        frame.setMinimumSize(new Dimension(500, 600));
        frame.setLocationRelativeTo(machine.getFrame());
        frame.setTitle("20223519 - 자판기 - 관리자 콘솔");
        frame.addWindowListener(new AdminCloseListener());

        // 가장자리 margin을 위한 패널
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // 탭으로 작업 구분
        JTabbedPane pane = new JTabbedPane();

        // 내용 추가
        makeSalesInfo(pane);
        makeInventoryManager(pane);
        makeCashInventoryManager(pane);
        makeLogInfo(pane);
        makeMetaManager(pane);

        panel.add(pane);

        // 화면 닫기 버튼 추가
        makeConsole(panel);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * 매출 정보 패널을 제작합니다.
     * @param pane 매출 정보 패널이 제작될 <code>JTabbedPane</code> 객체
     */
    private void makeSalesInfo(JTabbedPane pane) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(new JLabel("매출 정보 패널"));

        pane.addTab("매출", panel);
    }

    /**
     * 재고·상품 관리 패널을 제작합니다.
     * @param pane 재고관리 패널이 제작될 <code>JTabbedPane</code> 객체
     */
    private void makeInventoryManager(JTabbedPane pane) {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setLayout(new BorderLayout());

        // 저장 버튼
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new BorderLayout());

        JButton saveButton = new JButton("저장");
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> {
            List<Product> juices = machine.getInventory().getJuices();
            // 재고 목록 수정
            for (int i = 0; i < 5; i++) {
                int amount = (int) spinners.get(i).getValue();
                if (machine.updateLeftProductAmount(i, amount) == 0) {
                    String message = String.format(
                            "%s의 재고를 %d개로 설정하였습니다", juices.get(i).getKind().getName(), amount);
                    Log.writeLog(Log.REFILL_PRODUCT, message);
                }

                String previousName = juices.get(i).getKind().getName();
                String name = names.get(i).getText();
                if (!previousName.equals(name)) {
                    machine.updateProductName(i, name);

                    String message = String.format("상품 '%s'의 이름을 '%s'로 변경했습니다.", previousName, name);
                    Log.writeLog(Log.ADMIN_INFO, message);
                }
            }

            saveButton.setEnabled(false);
        });

        consolePanel.add(saveButton, BorderLayout.EAST);
        panel.add(consolePanel, BorderLayout.PAGE_END);

        // 재고 표
        JPanel table = new JPanel();
        table.setLayout(new GridLayout(5, 3));

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) { saveButton.setEnabled(true); }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) { saveButton.setEnabled(true); }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) { saveButton.setEnabled(true); }
        };
        ChangeListener changeListener = e -> saveButton.setEnabled(true);

        Inventory inventory = machine.getInventory();
        List<Product> juices = inventory.getJuices();
        for (int i = 0; i < 5; i++) {
            Product product = juices.get(i);

            String name = product.getKind().getName();
            int amount = product.getAmount();

            JTextField nameField = new JTextField();
            nameField.getDocument().addDocumentListener(documentListener);
            nameField.setText(name);
            table.add(nameField);
            names.add(nameField);

            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, null, 1);
            JSpinner countSpinner = new JSpinner(spinnerModel);
            countSpinner.addChangeListener(changeListener);
            countSpinner.setValue(amount);
            table.add(countSpinner);
            spinners.add(countSpinner);

            table.add(new JLabel("개 남음", JLabel.LEFT));
        }

        panel.add(table, BorderLayout.PAGE_START);

        pane.addTab("상품", panel);
    }

    /**
     * 현금 통 관리 패널을 제작합니다.
     * @param pane 현금 통 관리 패널이 만들어질 <code>JTabbedPane</code> 객체
     */
    private void makeCashInventoryManager(JTabbedPane pane) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(new JLabel("현금"));

        pane.addTab("현금", panel);
    }

    /**
     * 관리자 로그를 확인하는 패널을 제작합니다.
     * @param pane 관리자 로그 패널이 만들어질 <code>JTabbedPane</code> 객체
     */
    private void makeLogInfo(JTabbedPane pane) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(new JLabel("로그"));

        pane.addTab("로그", panel);
    }

    /**
     * 비밀번호 변경이나 서버 주소 변경 등 메타 관리적인 요소를 관리하는 조작판을 제작합니다.
     * @param panel 메타 관리 조작판을 제작할 <code>JPanel</code> 객체
     */
    private void makeMetaManager(JTabbedPane panel) {
        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new BoxLayout(metaPanel, BoxLayout.Y_AXIS));
        metaPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        { // 관리자 비밀번호 변경 패널
            JLabel header = new JLabel("관리자 비밀번호 변경");
            header.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");
            metaPanel.add(header);

            JButton button = new JButton("비밀번호 변경...");
            button.addActionListener(e -> {
                Log.writeLog(Log.ADMIN_INFO, "관리자가 비밀번호 변경을 시도했습니다.");
                new ChangePasswordPrompt(frame);
            });
            metaPanel.add(button);
        }

        panel.addTab("메타", metaPanel);
    }

    /**
     * 관리자 패널 메타 조작을 위한 조작판을 제작합니다.
     * @param panel 메타 조작 패널을 제작할 <code>JPanel</code> 객체
     */
    private void makeConsole(JPanel panel) {
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        { // 닫기 버튼
            JButton closeButton = new JButton("닫기");
            closeButton.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));
            consolePanel.add(closeButton);
        }

        panel.add(consolePanel, BorderLayout.PAGE_END);
    }

    private static class AdminCloseListener implements WindowListener {
        @Override public void windowOpened(WindowEvent windowEvent) { }
        @Override public void windowClosing(WindowEvent windowEvent) {
            Log.writeLog(Log.ADMIN_LOGOUT, "관리자가 콘솔을 닫았습니다.");
        }
        @Override public void windowClosed(WindowEvent windowEvent) { }
        @Override public void windowIconified(WindowEvent windowEvent) { }
        @Override public void windowDeiconified(WindowEvent windowEvent) { }
        @Override public void windowActivated(WindowEvent windowEvent) { }
        @Override public void windowDeactivated(WindowEvent windowEvent) { }
    }
}
