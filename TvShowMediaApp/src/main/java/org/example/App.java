package org.example;

import com.toedter.calendar.JDateChooser;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
    private static List<TVShow> tvShowList = new ArrayList<TVShow>();

    public static void main(String[] args) {
        loadTVShows();

        // Create and show GUI
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });

    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("TV Show App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.getContentPane().setBackground(Color.blue);
        //frame.setResizable(false);


        // Create components
        JTable tvShowTable = createTVShowTable();
        JScrollPane tableScrollPane = new JScrollPane(tvShowTable);
        JPanel buttonPanel = createButtonPanel(tvShowTable);

        // Set layout
        frame.setLayout(new BorderLayout());
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        JButton pdfButton = new JButton("Generate PDF");
        pdfButton.addActionListener(e -> {
            PDFGenerator.generatePDF(tvShowList, "tv_show_list.pdf");
        });

        buttonPanel.add(pdfButton);

        JButton emailButton = new JButton("Send Email");
        emailButton.addActionListener(e -> {
            String recipientEmail = JOptionPane.showInputDialog("Enter recipient's email:");
            String subject = "TV Show List";
            String body = generateEmailBody(tvShowList);

            EmailSender.sendEmail(recipientEmail, subject, body);
        });

        buttonPanel.add(emailButton);



// Add action listener to the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                filterTable(tvShowTable, searchTerm);
            }
        });

// Add components to a panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

// Add search panel to the frame
        frame.add(searchPanel, BorderLayout.NORTH);

// ...


        // Display the frame
        frame.setVisible(true);
    }

    private static JTable createTVShowTable() {
        String[] columnNames = {"Title", "Season", "Rating","Release Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (TVShow tvShow : tvShowList) {
            model.addRow(new Object[]{tvShow.getTitle(), tvShow.getSeason(), tvShow.getRating(), tvShow.getReleaseDate()});
        }

        return new JTable(model);
    }

    private static JPanel createButtonPanel(JTable tvShowTable) {
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add TV Show");
        JButton sortButton = new JButton("Sort by Rating");
        JButton chartButton = new JButton("Show Ratings Chart");

        buttonPanel.add(addButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(chartButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTVShow();
                refreshTable(tvShowTable);
            }
        });

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortTVShows();
                refreshTable(tvShowTable);
            }
        });

        chartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRatingsChart();
            }
        });

        return buttonPanel;
    }

    private static void addTVShow() {
        String title = JOptionPane.showInputDialog("Enter TV Show Title:");
        int season = Integer.parseInt(JOptionPane.showInputDialog("Enter Season Number:"));
        double rating = Double.parseDouble(JOptionPane.showInputDialog("Enter Rating:"));
        Date releaseDate = showDatePickerDialog();

        TVShow tvShow = new TVShow(title, season, rating, releaseDate);
        tvShowList.add(tvShow);

        // Save TV shows to file
        saveTVShows();

        JOptionPane.showMessageDialog(null, "TV Show added successfully!");
    }

    private static void refreshTable(JTable tvShowTable) {
        DefaultTableModel model = (DefaultTableModel) tvShowTable.getModel();
        model.setRowCount(0);

        for (TVShow tvShow : tvShowList) {
            model.addRow(new Object[]{tvShow.getTitle(), tvShow.getSeason(), tvShow.getRating(), tvShow.getReleaseDate()});
        }
    }

    private static void sortTVShows() {
        Collections.sort(tvShowList, Comparator.comparingDouble(TVShow::getRating).reversed());
    }

    private static void showRatingsChart2() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (TVShow tvShow : tvShowList) {
            model.addElement(tvShow.getTitle() + " (Season " + tvShow.getSeason() + "): " + tvShow.getRating());
        }

        JList<String> list = new JList<>(model);

        JOptionPane.showMessageDialog(null, new JScrollPane(list), "TV Show Ratings Chart",
                JOptionPane.PLAIN_MESSAGE);
    }

    private static void loadTVShows() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tvshows.dat"))) {
            tvShowList = (List<TVShow>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Handle exceptions or create the file if it doesn't exist
        }
    }

    private static void saveTVShows() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tvshows.dat"))) {
            oos.writeObject(tvShowList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showRatingsChart() {
        // Create dataset
        CategoryDataset dataset = createDataset();

        // Create chart
        JFreeChart chart = createChart(dataset);

        // Display chart in a frame
        JFrame chartFrame = new JFrame("TV Show Ratings Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(800, 600);

        // Add chart to the frame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartFrame.getContentPane().add(chartPanel, BorderLayout.CENTER);

        // Display the frame
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    private static CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (TVShow tvShow : tvShowList) {
            dataset.addValue(tvShow.getRating(), "Ratings", tvShow.getTitle() + " (Season " + tvShow.getSeason() + ", " + tvShow.getReleaseDate() + ")");

        }

        return dataset;
    }
    private static String generateEmailBody(List<TVShow> tvShowList) {
        StringBuilder body = new StringBuilder("TV Show List:\n\n");

        for (TVShow tvShow : tvShowList) {
            body.append("Title: ").append(tvShow.getTitle()).append("\n");
            body.append("Season: ").append(tvShow.getSeason()).append("\n");
            body.append("Rating: ").append(tvShow.getRating()).append("\n");
            body.append("Release Date: ").append(tvShow.getReleaseDate()).append("\n\n");
        }

        return body.toString();
    }


    private static JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "TV Show Ratings",
                "TV Show",
                "Rating",
                dataset, PlotOrientation.VERTICAL, true, true, false
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainAxis(new CategoryAxis("TV Shows"));
        plot.setRangeAxis(new NumberAxis("Rating"));

        return chart;
    }
    private static Date showDatePickerDialog() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("MM/dd/yyyy");

        int option = JOptionPane.showConfirmDialog(null, dateChooser, "Select Release Date", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return dateChooser.getDate();
        } else {
            return null; // Handle cancel or closed dialog
        }
    }
    private static void filterTable(JTable table, String searchTerm) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) table.getModel());
        table.setRowSorter(sorter);

        if (searchTerm.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                // Case-insensitive search for any column
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
            } catch (java.util.regex.PatternSyntaxException e) {
                System.out.println("Invalid search pattern");
            }
        }


    }
}