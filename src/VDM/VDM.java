package VDM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.stream.Stream;

public class VDM extends JFrame implements Observer {

    private boolean ready;
    private JTextField addTextField;
    private JComboBox<String> selectComboBox;
    private JTable table;
    private JButton pauseButton, resumeButton;
    private JButton clearButton, cancelButton;

    private DownloadsTableModel tableModel;
    private Download selectedDownload;
    private ArrayList<Serie> series;
    private boolean clearing;


    private VDM() {

        // Set application title.
        setTitle("VOD Download Manager");

        // Set window size.
        setSize(1060, 480);

        // Handle window closing events.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });

        // Set up file menu.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem = new JMenuItem("Exit",
                KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(e -> actionExit());
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Set up add panel.
        JPanel addPanel = new JPanel();
        addTextField = new JTextField(30);
        addPanel.add(addTextField);
        JButton addButton = new JButton("Add Download");
        addButton.addActionListener(e -> {
            try {
                actionAdd();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        addPanel.add(addButton);
        selectComboBox = new JComboBox<>();
        selectComboBox.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                addDownload();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addPanel.add(selectComboBox);

        // Set up Downloads table.
        tableModel = new DownloadsTableModel();
        table = new JTable(tableModel);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> tableSelectionChanged());
        // Allow only one row at a time to be selected.


        // Set up ProgressBar as renderer for progress column.
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true); // show progress text
        table.setDefaultRenderer(JProgressBar.class, renderer);

        // Set table's row height large enough to fit JProgressBar.
        table.setRowHeight(
                (int) renderer.getPreferredSize().getHeight());

        // Set up downloads panel.
        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setBorder(
                BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table),
                BorderLayout.CENTER);

        // Set up buttons panel.
        JPanel buttonsPanel = new JPanel();
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> actionPause());
        pauseButton.setEnabled(false);
        buttonsPanel.add(pauseButton);
        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(e -> actionResume());
        resumeButton.setEnabled(false);
        buttonsPanel.add(resumeButton);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> actionCancel());
        cancelButton.setEnabled(false);
        buttonsPanel.add(cancelButton);
        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> actionClear());
        clearButton.setEnabled(false);
        buttonsPanel.add(clearButton);

        // Add panels to display.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(addPanel, BorderLayout.NORTH);
        getContentPane().add(downloadsPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        VDM manager = new VDM();
       /* ImageIcon img = new ImageIcon("icon.png");
        manager.setIconImage(img.getImage());*/
        manager.setVisible(true);
    }

    private void actionExit() {
        System.exit(0);
    }

    private void actionAdd() throws IOException {

        ready = false;
        series = genData();
        if (verifyUrl(addTextField.getText()) != null) {

            URL url = verifyUrl(addTextField.getText());


            assert url != null;
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream("list.m3u");
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

        for (Serie serie : series) {
            int capitulos = 0;
            for (Temporada temporada : serie.getTemporadas()) {
                capitulos += temporada.getEpisodios().size();
            }

            selectComboBox.addItem(serie.getTitulo() + " (" + serie.getTemporadas().size() + " Temporadas, " + capitulos + " Capitulos)");
        }

        ready = true;
        addTextField.setText("");

    }


    private ArrayList<Serie> genData() throws IOException {


        File f = new File("list.m3u");
        BufferedReader b = new BufferedReader(new FileReader(f));

        String readLine;

        ArrayList<Element> elements = new ArrayList<>();
        int i = 0;


        while ((readLine = b.readLine()) != null) {

            if (readLine.startsWith("#EXTINF:-1")) {

                elements.add(setElement(readLine));
                i++;

            } else if ((verifyUrl(readLine)) != null) {

                elements.get(i - 1).setUrl(verifyUrl(readLine));
            }
        }



  /*if (n.getUrl() != null) {
                tableModel.addDownload(new Download(n.getUrl()));
            }*/

        ArrayList<Serie> series = new ArrayList<>();
        for (Element element : elements) {
            try {
                Serie serie;
                Temporada temporada;

                int temp = Integer.valueOf(element.getChannel_id().substring(element.getChannel_id().length() - 7,
                        element.getChannel_id().length() - 5).trim());


                if (indexSerie(series, element.getGroup_title()) < 0) {
                    series.add(new Serie(element.getGroup_title().trim()));
                }
                serie = series.get(indexSerie(series, element.getGroup_title()));


                if (indexTemporada(serie.getTemporadas(), temp) < 0) {

                    serie.getTemporadas().add(new Temporada(temp));
                }
                temporada = serie.getTemporadas().get(indexTemporada(serie.getTemporadas(), temp));


                Episodio episodio = new Episodio(element.getUrl(), element.getTvg_logo(), element.getChannel_id().trim(), serie, temporada);

                temporada.getEpisodios().add(episodio);


            } catch (Exception e) {
            }
        }

        series.sort(Comparator.comparing(p -> p.getTitulo()));
        return series;
    }

    private int indexSerie(ArrayList<Serie> series, String title) {
        try {
            for (Serie serie : series) {
                if (serie.getTitulo().equals(title)) {
                    return series.indexOf(serie);
                }
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int indexTemporada(ArrayList<Temporada> temporadas, int nro) {
        try {
            for (Temporada temporada : temporadas) {
                if (temporada.getNro() == nro) {
                    return temporadas.indexOf(temporada);
                }
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private URL verifyUrl(String url) {
        // Only allow HTTP URLs.
        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://"))
            return null;

        // Verify format of URL.
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }

        // Make sure URL specifies a file.
        if (verifiedUrl.getFile().length() < 2)
            return null;

        return verifiedUrl;
    }

    private void addDownload() {


        Serie serie = series.get(selectComboBox.getSelectedIndex());

        int limit = 1;
        int i = 0;
        for (Temporada temporada : serie.getTemporadas()) {
            for (Episodio episodio : temporada.getEpisodios()) {
                if (i < limit) {
                    tableModel.addDownload(new Download(episodio, true));
                    i++;
                } else {
                    tableModel.addDownload(new Download(episodio, false));
                }
            }
        }

    }

    private void tableSelectionChanged() {
    /* Unregister from receiving notifications
       from the last selected download. */
        if (selectedDownload != null)
            selectedDownload.deleteObserver(VDM.this);

    /* If not in the middle of clearing a download,
       set the selected download and register to
       receive notifications from it. */
        if (!clearing) {
            selectedDownload =
                    tableModel.getDownload(table.getSelectedRow());
            selectedDownload.addObserver(VDM.this);
            updateButtons();
        }
    }

    private void actionPause() {
        selectedDownload.pause();
        updateButtons();
    }

    private void actionResume() {
        selectedDownload.resume();
        updateButtons();
    }

    private void actionCancel() {
        selectedDownload.cancel();
        updateButtons();
    }

    private void actionClear() {
        clearing = true;
        tableModel.clearDownload(table.getSelectedRow());
        clearing = false;
        selectedDownload = null;
        updateButtons();
    }

    private void updateButtons() {
        if (selectedDownload != null) {
            int status = selectedDownload.getStatus();
            switch (status) {
                case Download.DOWNLOADING:
                    pauseButton.setEnabled(true);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.PAUSED:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.ERROR:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
                    break;
                case Download.WAITING:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
                    break;
                default: // COMPLETE or CANCELLED
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
            }
        } else {
            // No download is selected in table.
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            cancelButton.setEnabled(false);
            clearButton.setEnabled(false);
        }
    }

    private Element setElement(String tmp) {

        int init = tmp.indexOf("channel-id=\"");
        tmp = tmp.substring(init + 12);
        int fn = tmp.indexOf("\"");
        String channel_idN = tmp.substring(0, fn);

        init = tmp.indexOf("tvg-id=\"");
        tmp = tmp.substring(init + 8);
        fn = tmp.indexOf('"');
        String tvg_id = tmp.substring(0, fn);

        init = tmp.indexOf("tvg-logo=\"");
        tmp = tmp.substring(init + 10);
        fn = tmp.indexOf('"');
        String tvg_logo = tmp.substring(0, fn);

        init = tmp.indexOf("channel-id=\"");
        tmp = tmp.substring(init + 12);
        fn = tmp.indexOf('"');
        String channel_id = tmp.substring(0, fn);

        init = tmp.indexOf("group-title=\"");
        tmp = tmp.substring(init + 13);
        fn = tmp.indexOf('"');

        String group_title = tmp.substring(0, fn);


        return new Element(channel_idN, tvg_id, tvg_logo, channel_id, group_title);

    }

    @Override
    public void update(Observable o, Object arg) {
        if (selectedDownload != null && selectedDownload.equals(o)) {
            updateButtons();
        }

    }

}
