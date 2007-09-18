package ja.lingo.application.gui.main.search;

import info.clearthought.layout.TableLayout;
import ja.centre.gui.util.BaseGui;
import ja.centre.gui.util.CardPanel;
import ja.lingo.application.model.Model;
import ja.lingo.application.util.articlelist.ArticleList;
import ja.lingo.application.util.misc.Strings;
import ja.lingo.application.util.Buttons;

import javax.swing.*;

public class SearchResultsPanel extends BaseGui {
    private JPanel gui;

    private JLabel searchingLabel;

    private JButton stopButton;
    private JButton newSearchButton;

    private JProgressBar progressBar;
    private ArticleList articleList;

    private volatile boolean running;

    private CardPanel cards;

    public SearchResultsPanel( Model model ) {
        searchingLabel = new JLabel();

        stopButton = Buttons.stop();
        newSearchButton = Buttons.searchNew();

        cards = new CardPanel();
        cards.add( stopButton );
        cards.add( newSearchButton );
        cards.show( stopButton );

        progressBar = new JProgressBar();

        articleList = new ArticleList( model, false, true );

        gui = new JPanel( new TableLayout( new double[][] { {
                TableLayout.FILL
        }, {
                TableLayout.PREFERRED, // 0: Searching ... / Found ...
                10,
                TableLayout.PREFERRED, // 2: progress
                10,
                TableLayout.PREFERRED, // 4: Stop / New Search
                10,
                TableLayout.FILL// 6: list
        } } ) );
        gui.add( searchingLabel, "0, 0" );

        gui.add( progressBar, "0,  2" );

        gui.add( cards.getGui(), "0,  4, right, center" );
        gui.add( articleList.getGui(), "0,  6" );

        switchToClean();
    }

    public void switchToClean() {
        running = false;
        articleList.getList().setModel( new DefaultListModel() );
    }
    public void switchToStarted( String text ) {
        running = true;
        cards.show( stopButton );
        //progressBar.setVisible( true );
        searchingLabel.setText( resources.text( "searching", Strings.escapeHtml( text ) ) );

        articleList.setHighlight( text );
    }
    public void switchToStopped( String text, int size ) {
        running = false;
        cards.show( newSearchButton );
        //progressBar.setVisible( false );
        setFoundMessage( text, size );
    }
    public void switchToFinished( String text, int size ) {
        running = false;
        cards.show( newSearchButton );
        //progressBar.setVisible( false );
        setFoundMessage( text, size );
    }

    private void setFoundMessage( String text, int size ) {
        searchingLabel.setText( resources.text( "found", size, Strings.escapeHtml( text ) ) );
    }

    public JComponent getGui() {
        return gui;
    }


    public JList getList() {
        return articleList.getList();
    }
    public JButton getStopButton() {
        return stopButton;
    }
    public JButton getNewSearchButton() {
        return newSearchButton;
    }
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public boolean isRunning() {
        return running;
    }
}
