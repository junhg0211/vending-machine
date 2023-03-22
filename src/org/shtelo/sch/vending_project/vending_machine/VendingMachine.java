package org.shtelo.sch.vending_project.vending_machine;

import org.shtelo.sch.vending_project.util.Josa;
import org.shtelo.sch.vending_project.util.Log;
import org.shtelo.sch.vending_project.vending_machine.data_type.Inventory;
import org.shtelo.sch.vending_project.vending_machine.data_type.Kind;
import org.shtelo.sch.vending_project.vending_machine.data_type.Product;
import org.shtelo.sch.vending_project.vending_machine.data_type.Wallet;
import org.shtelo.sch.vending_project.vending_machine.subwindow.AdminPrompt;
import org.shtelo.sch.vending_project.vending_machine.subwindow.CashInputPrompt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;

public class VendingMachine {
    private final NumberFormat numberFormat;
    private final Wallet wallet;
    private final JLabel[] leftLabels = new JLabel[5];
    private final JButton[] buyButtons = new JButton[5];
    private JButton cashButton;
    private JButton changeButton;
    private JButton adminButton;
    private JPanel menuPanel;
    private JButton quitButton;
    private JPanel contentPanel;
    private JLabel cashAmountLabel;
    private JFrame frame;
    private int cash;
    private Inventory inventory;
    private int cashThousands = 0;

    public VendingMachine() {
        numberFormat = NumberFormat.getInstance();

        wallet = Wallet.getWallet();
        cash = 0;

        buildWindow(); // 화면 띄우기
    }

    /**
     * 자판기 화면을 만들고 띄웁니다.
     */
    private void buildWindow() {
        // 자판기 화면 프레임 제작
        frame = new JFrame();
        frame.setTitle("20223519 - 자판기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 400));
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setLocationRelativeTo(null);

        makeMenu();
        makeConsole();

        frame.add(contentPanel);
        frame.setVisible(true);
    }

    /**
     * 자판기의 메뉴 선택 부분을 추가합니다.
     */
    private void makeMenu() {
        menuPanel.setLayout(new GridLayout(6, 4));

        inventory = Inventory.getInventory();
        // 메뉴 목록 만들기
        EmptyBorder nameLabelBorder = new EmptyBorder(0, 8, 0, 0);
        for (int i = 0; i < 5; i++) {
            Product juice = inventory.getJuices().get(i);
            Kind kind = juice.getKind();
            int left = juice.getAmount();
            String leftString = Integer.toString(left);

            // 구매 버튼
            buyButtons[i] = new JButton("구매");
            int finalI = i;
            buyButtons[i].addActionListener(e -> processBuy(finalI));
            buyButtons[i].setEnabled(left > 0);
            menuPanel.add(buyButtons[i]);

            // 메뉴 이름
            String name = kind.getName();
            JLabel nameLabel = new JLabel(name);
            nameLabel.setBorder(nameLabelBorder);
            menuPanel.add(nameLabel);

            // 메뉴 남은 수량
            leftLabels[i] = new JLabel(leftString);
            menuPanel.add(leftLabels[i]);

            // 메뉴 가격
            String price = numberFormat.format(kind.getPrice());
            JLabel priceLabel = new JLabel(price, SwingConstants.RIGHT);
            menuPanel.add(priceLabel);
        }
    }

    /**
     * 음료 구매를 처리합니다.
     *
     * @param juiceIndex 구매할 음료수의 인덱스 번호
     */
    private void processBuy(int juiceIndex) {
        Product product = inventory.getJuices().get(juiceIndex);
        Kind kind = product.getKind();
        String name = kind.getName();
        int price = kind.getPrice();
        int amount = product.getAmount();

        if (amount <= 0) {
            JOptionPane.showMessageDialog(frame, "해당 상품은 매진되었습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cash < price) {
            JOptionPane.showMessageDialog(frame, "잔액이 부족합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cashThousands = 0;
        updateCash(cash - price);
        updateLeftProductAmount(juiceIndex, amount - 1);

        inventory.save();

        String message;

        message = String.format("%s 판매 (%d개 남음), 남은 현금 %d원", name, amount - 1, cash);
        Log.writeLog(Log.SOLD, message);

        message = String.format(
                "%s%c 1개 구매했습니다.%n가격: %s원",
                name, Josa.eulReul(name), numberFormat.format(price));
        JOptionPane.showMessageDialog(frame, message, "정보", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 남은 상품 개수를 업데이트하도록 처리합니다.
     * <code>inventory</code>와 매대에 있는 남은 수량을 업데이트합니다.
     *
     * @param index  업데이트할 상품의 인덱스
     * @param amount 업데이트된 상품의 남은 수량
     */
    public int updateLeftProductAmount(int index, int amount) {
        Product product = inventory.getJuices().get(index);

        if (product.getAmount() == amount) {
            return 1;
        }

        product.setAmount(amount);
        leftLabels[index].setText(String.format("%d", amount));
        buyButtons[index].setEnabled(amount > 0);

        return 0;
    }

    /**
     * 자판기의 사용자 조작 메뉴를 추가합니다.
     */
    private void makeConsole() {
        JPanel consolePanel = new JPanel();
        consolePanel.setBorder(new EmptyBorder(0, 100, 0, 0));
        consolePanel.setLayout(new BorderLayout());

        // 구매자 메뉴
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BorderLayout());

        cashButton.addActionListener(e -> new CashInputPrompt(this));
        changeButton.addActionListener(e -> changeCash());

        // 메타 패널
        adminButton.addActionListener(e -> new AdminPrompt(this));
        quitButton.addActionListener(e -> System.exit(0));
    }

    /**
     * 기계에 있는 거스름을 사용자에게 돌려주는 작업을 수행합니다.
     */
    private void changeCash() {
        int[] result = wallet.change(cash);

        String message = String.format(
                "거스름돈을 처리했습니다. " +
                        "10*%d, 50*%d, 100*%d, 500*%d, 1000*%d, 거스름돈 버퍼에 %d원 남음. " +
                        "현금 버퍼에는 각각 %d, %d, %d, %d, %d개 남음",
                result[0], result[1], result[2], result[3], result[4], result[5],
                wallet.getTens(), wallet.getFifties(), wallet.getHundreds(),
                wallet.getFiveHundreds(), wallet.getThousands());
        Log.writeLog(Log.CHANGE_CASH, message);
        String text = String.format(
                "거스름을 처리했습니다.%n" +
                        "1,000원: %d장%n" +
                        "500원: %d개%n" +
                        "100원: %d개%n" +
                        "50원: %d개%n" +
                        "10원: %d개%n%n" +
                        "거스르지 못한 돈: %s원%n" +
                        "거스른 돈: %s원",
                result[4], result[3], result[2], result[1], result[0],
                numberFormat.format(result[5]), numberFormat.format(cash - result[5]));
        JOptionPane.showMessageDialog(frame, text);

        updateCash(result[5]);
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * 현금을 자판기에 투입한 것으로 처리합니다.
     *
     * @param amount 투입할 현금의 액수
     */
    public void insertCash(int amount) {
        updateCash(cash + amount);
    }

    /**
     * 투입한 금액의 표시 액수를 <code>cash</code> 변수에 따라서 변경합니다.
     *
     * @param amount 투입된 금액으로 표시할 액수
     */
    private void updateCash(int amount) {
        cash = amount;
        String text = numberFormat.format(cash);
        cashAmountLabel.setText(text);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public int getCash() {
        return this.cash;
    }

    public int getCashThousands() {
        return cashThousands;
    }

    public void setCashThousands(int cashThousands) {
        this.cashThousands = cashThousands;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
