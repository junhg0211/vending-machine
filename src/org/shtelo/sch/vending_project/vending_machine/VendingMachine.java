package org.shtelo.sch.vending_project.vending_machine;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;

public class VendingMachine {

    private final NumberFormat numberFormat;

    public VendingMachine() {
        numberFormat = NumberFormat.getInstance();

        buildWindow(); // 화면 띄우기
    }

    /**
     * 자판기 화면을 만들고 띄웁니다.
     */
    private void buildWindow() {
        // Swing 테마 변경
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException ignored) {}

        // 자판기 화면 프레임 제작
        JFrame frame = new JFrame();
        frame.setTitle("20223519 - 자판기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 400));
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setLocationRelativeTo(null);

        // 페이지 프레임 여백
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setLayout(new BorderLayout());

        // 페이지 내용 추가
        makeTitle(panel);
        makeMenu(panel);
        makeConsole(panel);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * 자판기 상단에 타이틀을 추가합니다.
     */
    private void makeTitle(JPanel panel) {
        JLabel titleLabel = new JLabel("20223519 Java 자판기");
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
        panel.add(titleLabel, BorderLayout.PAGE_START);
    }

    /**
     * 자판기의 메뉴 선택 부분을 추가합니다.
     */
    private void makeMenu(JPanel panel) {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(5, 4));

        Inventory inventory = getInventory();

        // 메뉴 목록 만들기
        EmptyBorder nameLabelBorder = new EmptyBorder(0, 8, 0, 0);
        for (int i = 0; i < 5; i++) {
            JButton button = new JButton("구매");
            menuPanel.add(button);

            Product juice = inventory.getJuices().get(i);
            Kind kind = juice.getKind();

            // 메뉴 이름
            String name = kind.getName();
            JLabel nameLabel = new JLabel(name);
            nameLabel.setBorder(nameLabelBorder);
            menuPanel.add(nameLabel);

            // 메뉴 남은 수량
            String left = Integer.toString(juice.getAmount());
            JLabel leftLabel = new JLabel(left);
            menuPanel.add(leftLabel);

            // 메뉴 가격
            String price = numberFormat.format(kind.getPrice());
            JLabel priceLabel = new JLabel(price, SwingConstants.RIGHT);
            menuPanel.add(priceLabel);
        }

        panel.add(menuPanel, BorderLayout.CENTER);
    }

    /**
     * 파일에 저장되어있는 인벤토리를 볼러옵니다.
     * 만약 파일이 존재하지 않는다면 코드에 설정된 기본값으로 파일을 생성하고 불러옵니다.
     */
    private Inventory getInventory() {
        Inventory inventory;
        String INVENTORY_PATH = "res/inventory.json";

        // res 폴더가 없으면 만든다.
        try {
            Files.createDirectories(Path.of("res"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            FileReader reader = new FileReader(INVENTORY_PATH);
            Gson gson = new Gson();
            inventory = gson.fromJson(reader, Inventory.class);
        } catch (FileNotFoundException e) {
            // 파일이 없으면 기본값으로 만든다
            inventory = Inventory.getDefault();
            try {
                FileWriter writer = new FileWriter(INVENTORY_PATH);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(inventory, writer);
                writer.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return inventory;
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
            customerPanel.add(cashButton, BorderLayout.PAGE_START);
        }
        { // 거스름돈 버튼
            JButton changeButton = new JButton("거스름");
            customerPanel.add(changeButton, BorderLayout.CENTER);
        }
        { // 투입금 표시 창
            JPanel cashPanel = new JPanel();
            cashPanel.setLayout(new GridLayout());

            JLabel cashLabel = new JLabel("투입금: ", SwingConstants.LEFT);
            cashPanel.add(cashLabel);

            JLabel cashAmountLabel = new JLabel("0", SwingConstants.RIGHT);
            cashPanel.add(cashAmountLabel);

            customerPanel.add(cashPanel, BorderLayout.PAGE_END);
        }

        consolePanel.add(customerPanel, BorderLayout.PAGE_START);

        // 메타 패널
        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new BorderLayout());
        { // 관리자 콘솔 버튼
            JButton adminButton = new JButton("관리자 콘솔");
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
}