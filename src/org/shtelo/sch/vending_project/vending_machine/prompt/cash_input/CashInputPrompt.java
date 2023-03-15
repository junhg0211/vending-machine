package org.shtelo.sch.vending_project.vending_machine.prompt.cash_input;

import com.formdev.flatlaf.FlatClientProperties;
import org.shtelo.sch.vending_project.vending_machine.VendingMachine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;

public class CashInputPrompt {
    private final VendingMachine machine;
    private ArrayList<JSpinner> spinners;
    private JFrame frame;

    private final int[] cashKinds = new int[]{10, 50, 100, 500, 1000};

    public CashInputPrompt(VendingMachine machine) {
        this.machine = machine;

        buildWindow();
    }

    private void buildWindow() {
        // 자판기 화면 프레임 제작
        frame = new JFrame();
        frame.setTitle("20223519 - 자판기 - 현급 투입");
        frame.setMinimumSize(new Dimension(400, 200));
        frame.setLocationRelativeTo(machine.getFrame());

        // 페이지 프레임 여백
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setLayout(new BorderLayout());

        makeTitle(panel);
        makeCashSelect(panel);
        makeConsole(panel);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * 화면 가장 위에 타이틀을 표시합니다.
     */
    private void makeTitle(JPanel panel) {
        JLabel titleLabel = new JLabel("현금 투입");
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");
        panel.add(titleLabel, BorderLayout.PAGE_START);
    }

    /**
     * 확인, 취소 등의 메타 투입적 작업을 수행할 수 있는 부분
     */
    private void makeConsole(JPanel panel) {
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1));

        { // 취소 버튼
            JButton cancelButton = new JButton("취소");
            cancelButton.addActionListener(e -> closeWindow());
            buttonPanel.add(cancelButton);
        }
        { // 투입 버튼
            JButton insertButton = new JButton("투입");
            insertButton.addActionListener(e -> insertCash());
            buttonPanel.add(insertButton);
        }

        consolePanel.add(buttonPanel, BorderLayout.PAGE_END);
        panel.add(consolePanel, BorderLayout.EAST);
    }

    /**
     * 사용자가 입력한 액수의 현금을 자판기에 투입하고 창을 종료합니다.
     * 투입 버튼을 눌렀을 때에 실행됩니다.
     */
    private void insertCash() {
        int amount = 0;
        for (int i = 0; i < 5; i++) {
            amount += cashKinds[i] * (int) spinners.get(i).getValue();
        }

        machine.insertCash(amount);
        machine.getWallet().insertCash(
            (int) spinners.get(0).getValue(),
            (int) spinners.get(1).getValue(),
            (int) spinners.get(2).getValue(),
            (int) spinners.get(3).getValue(),
            (int) spinners.get(4).getValue()
        );
        closeWindow();
    }

    /**
     * 아무 작업을 수행하지 않고 창을 종료합니다.
     * 취소 버튼이 눌렸을 때에 실행됨.
     */
    private void closeWindow() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * 어떤 화폐를 얼마만큼 투입할지 결정할 수 있는 스피너 부분
     */
    private void makeCashSelect(JPanel panel) {
        // GridLayout 세로 늘어남 방지
        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new BorderLayout());

        JPanel cashSelectPanel = new JPanel();
        cashSelectPanel.setLayout(new GridLayout(5, 2));

        // 투입단위별 투입량을 조절하는 스피너를 제시
        spinners = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            spinners.add(makeCashSelectSpinner(cashSelectPanel, cashKinds[i]));
        }

        metaPanel.add(cashSelectPanel, BorderLayout.PAGE_START);
        panel.add(metaPanel, BorderLayout.CENTER);
    }

    /**
     * 스피너의 각각 행을 추가합니다.
     * @param amount 얼마짜리 화폐에 대한 행을 추가할지 결정합니다.
     */
    private JSpinner makeCashSelectSpinner(JPanel panel, int amount) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        JLabel label = new JLabel(numberFormat.format(amount));
        panel.add(label);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 5, 1);
        JSpinner spinner = new JSpinner(spinnerModel);
        panel.add(spinner);

        return spinner;
    }
}