package org.shtelo.sch.vending_project.vending_machine;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.shtelo.sch.vending_project.server.Server;
import org.shtelo.sch.vending_project.util.Josa;
import org.shtelo.sch.vending_project.util.Log;
import org.shtelo.sch.vending_project.util.sell_log.SellLogger;
import org.shtelo.sch.vending_project.util.sell_log.DailyLog;
import org.shtelo.sch.vending_project.vending_machine.data_type.Inventory;
import org.shtelo.sch.vending_project.vending_machine.data_type.Kind;
import org.shtelo.sch.vending_project.vending_machine.data_type.Product;
import org.shtelo.sch.vending_project.vending_machine.data_type.Wallet;
import org.shtelo.sch.vending_project.vending_machine.subwindow.AdminPrompt;
import org.shtelo.sch.vending_project.vending_machine.subwindow.CashInputPrompt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 가장 처음으로 보이는 자판기 화면입니다.
 */
public class VendingMachine {
    private final NumberFormat numberFormat;
    private final Wallet wallet;
    private final JLabel[] leftLabels = new JLabel[5];
    private final JButton[] buyButtons = new JButton[5];
    private final JLabel[] nameLabels = new JLabel[5];
    private JFrame frame;
    private JLabel cashAmountLabel;
    private Inventory inventory;
    private int cashThousands = 0;
    private final SellLogger sellLogger;
    private Server server;

    public VendingMachine() {
        this.numberFormat = NumberFormat.getInstance();
        this.wallet = Wallet.getWallet();
        this.sellLogger = SellLogger.getLogger();

        buildWindow(); // 화면 띄우기
    }

    /**
     * 자판기 화면을 만들고 띄웁니다.
     */
    private void buildWindow() {
        // Swing 테마 변경
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException ignored) {
        }

        // 자판기 화면 프레임 제작
        frame = new JFrame();
        frame.setTitle("20223519 - 자판기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 400));
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.stopServer();
            }
        });

        // 페이지 프레임 여백
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setLayout(new BorderLayout());

        // 페이지 내용 추가
        makeTitle(panel);
        makeMenu(panel);
        makeConsole(panel);

        frame.add(panel);
    }

    /**
     * 화면을 띄웁니다.
     */
    public void start() {
        frame.setVisible(true);
    }

    /**
     * 자판기 상단에 타이틀을 추가합니다.
     * @param panel 타이틀을 추가할 <code>JPanel</code> 객체
     */
    private void makeTitle(JPanel panel) {
        JLabel titleLabel = new JLabel("20223519 Java 자판기");
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
        panel.add(titleLabel, BorderLayout.PAGE_START);
    }

    /**
     * 자판기의 메뉴 선택 부분을 추가합니다.
     * @param panel 메뉴 선택 부분을 추가할 <code>JPanel</code> 객체
     */
    @SuppressWarnings("SpellCheckingInspection")
    private void makeMenu(JPanel panel) {
        // menuPanel의 내용이 화면에 꽉 차게 표시되지 않도록 메타패널을 만들어 menuPanel을 배치한다.
        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(6, 4));

        menuPanel.add(new JLabel("구매", JLabel.CENTER));
        menuPanel.add(new JLabel("메뉴", JLabel.CENTER));
        menuPanel.add(new JLabel("남은 수량", JLabel.CENTER));
        menuPanel.add(new JLabel("가격 [원]", JLabel.CENTER));

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
            nameLabels[i] = nameLabel;

            // 메뉴 남은 수량
            leftLabels[i] = new JLabel(leftString);
            menuPanel.add(leftLabels[i]);

            // 메뉴 가격
            String price = numberFormat.format(kind.getPrice());
            JLabel priceLabel = new JLabel(price, SwingConstants.RIGHT);
            menuPanel.add(priceLabel);
        }

        metaPanel.add(menuPanel, BorderLayout.PAGE_START);
        panel.add(metaPanel, BorderLayout.CENTER);
    }

    /**
     * 음료 구매를 처리합니다.
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

        if (wallet.getCash() < price) {
            JOptionPane.showMessageDialog(frame, "잔액이 부족합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cashThousands = 0;
        updateCash(wallet.getCash() - price);
        updateLeftProductAmount(juiceIndex, amount - 1);

        inventory.save();

        String message;

        message = String.format("%s 판매 (%d개 남음), 남은 현금 %d원", name, amount - 1, wallet.getCash());
        Log.writeLog(Log.SOLD, message);

        if (amount - 1 == 0) {
            message = String.format("%s 재고 소진됨", name);
            Log.writeLog(Log.SOLD_OUT, message);
        }

        DailyLog log = sellLogger.getLog(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()));
        log.setSells(name, log.getSells(name) + 1);
        log.setSales(log.getSales() + price);
        sellLogger.save();

        message = String.format(
                "%s%c 1개 구매했습니다.%n가격: %s원",
                name, Josa.eulReul(name), numberFormat.format(price));
        JOptionPane.showMessageDialog(frame, message, "정보", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 남은 상품 개수를 업데이트하도록 처리합니다.
     * <code>inventory</code>와 매대에 있는 남은 수량을 업데이트합니다.
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
    private void makeConsole(JPanel panel) {
        JPanel consolePanel = new JPanel();
        consolePanel.setBorder(new EmptyBorder(0, 100, 0, 0));
        consolePanel.setLayout(new BorderLayout());

        // 구매자 메뉴
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BorderLayout());

        { // 현금 투입 버튼
            JButton cashButton = new JButton("현금 투입");
            cashButton.addActionListener(e -> new CashInputPrompt(this));
            customerPanel.add(cashButton, BorderLayout.PAGE_START);
        }
        { // 거스름돈 버튼
            JButton changeButton = new JButton("거스름");
            changeButton.addActionListener(e -> changeCash());
            customerPanel.add(changeButton, BorderLayout.CENTER);
        }
        { // 투입금 표시 창
            JPanel cashPanel = new JPanel();
            cashPanel.setLayout(new GridLayout());

            JLabel cashLabel = new JLabel("투입금: ", SwingConstants.LEFT);
            cashPanel.add(cashLabel);

            String cashAmountText = numberFormat.format(wallet.getCash());
            cashAmountLabel = new JLabel(cashAmountText, SwingConstants.RIGHT);
            cashPanel.add(cashAmountLabel);

            customerPanel.add(cashPanel, BorderLayout.PAGE_END);
        }

        consolePanel.add(customerPanel, BorderLayout.PAGE_START);

        // 메타 패널
        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new BorderLayout());
        { // 관리자 콘솔 버튼
            JButton adminButton = new JButton("관리자 콘솔");
            adminButton.addActionListener(e -> new AdminPrompt(this));
            metaPanel.add(adminButton, BorderLayout.PAGE_START);
        }
        { // 프로그램 종료 버튼
            JButton quitButton = new JButton("종료");
            quitButton.addActionListener(e -> System.exit(0));
            metaPanel.add(quitButton, BorderLayout.PAGE_END);
        }

        consolePanel.add(metaPanel, BorderLayout.PAGE_END);

        panel.add(consolePanel, BorderLayout.EAST);
    }

    /**
     * 기계에 있는 거스름을 사용자에게 돌려주는 작업을 수행합니다.
     */
    private void changeCash() {
        int[] result = wallet.change(wallet.getCash());

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
                numberFormat.format(result[5]), numberFormat.format(wallet.getCash() - result[5]));
        JOptionPane.showMessageDialog(frame, text);

        updateCash(result[5]);
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * 현금을 자판기에 투입한 것으로 처리합니다.
     * @param amount 투입할 현금의 액수
     */
    public void insertCash(int amount) {
        updateCash(wallet.getCash() + amount);
    }

    /**
     * 투입한 금액의 표시 액수를 <code>cash</code> 변수에 따라서 변경합니다.
     * @param amount 투입된 금액으로 표시할 액수
     */
    private void updateCash(int amount) {
        wallet.setCash(amount);
        String text = numberFormat.format(wallet.getCash());
        cashAmountLabel.setText(text);
        wallet.save();
    }

    public void updateProductName(int index, String name) {
        Kind kind = inventory.getJuices().get(index).getKind();
        kind.setName(name);
        nameLabels[index].setText(name);
        inventory.save();
    }

    public Wallet getWallet() {
        return wallet;
    }

    public int getCashThousands() {
        return cashThousands;
    }

    public void setCashThousands(int cashThousands) {
        this.cashThousands = cashThousands;
    }

    public SellLogger getSellLogger() {
        return sellLogger;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}