/*
 * Copyright 2012 - 2018 Manuel Laggner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tinymediamanager.ui.tvshows;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.tvshow.TvShowList;
import org.tinymediamanager.core.tvshow.TvShowModuleManager;
import org.tinymediamanager.core.tvshow.entities.TvShow;
import org.tinymediamanager.core.tvshow.entities.TvShowEpisode;
import org.tinymediamanager.core.tvshow.entities.TvShowSeason;
import org.tinymediamanager.ui.IconManager;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.TreePopupListener;
import org.tinymediamanager.ui.TreeUI;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.actions.RequestFocusAction;
import org.tinymediamanager.ui.components.EnhancedTextField;
import org.tinymediamanager.ui.components.JSplitButton;
import org.tinymediamanager.ui.components.JSplitButton.SplitButtonActionListener;
import org.tinymediamanager.ui.components.ZebraJTree;
import org.tinymediamanager.ui.tvshows.TvShowExtendedMatcher.SearchOptions;
import org.tinymediamanager.ui.tvshows.actions.DebugDumpShow;
import org.tinymediamanager.ui.tvshows.actions.TvShowBulkEditAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowChangeSeasonPosterAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowChangeToAiredOrderAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowChangeToDvdOrderAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowClearImageCacheAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowDeleteAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowDownloadMissingArtworkAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowEditAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowExportAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowMediaInformationAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowRemoveAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowRenameAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowRewriteEpisodeNfoAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowRewriteNfoAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowScrapeEpisodesAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowScrapeNewItemsAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowSelectedScrapeAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowSetWatchedFlagAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowSingleScrapeAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowSubtitleDownloadAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowSubtitleSearchAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowSyncSelectedTraktTvAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowSyncTraktTvAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowSyncWatchedTraktTvAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowUpdateAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowUpdateDatasourcesAction;
import org.tinymediamanager.ui.tvshows.actions.TvShowUpdateSingleDatasourceAction;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * The Class TvShowPanel.
 * 
 * @author Manuel Laggner
 */
public class TvShowPanel extends JPanel {
  private static final long           serialVersionUID                 = -1923811385292825136L;
  /**
   * @wbp.nls.resourceBundle messages
   */
  private static final ResourceBundle BUNDLE                           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  TvShowSelectionModel                tvShowSelectionModel;
  TvShowSeasonSelectionModel          tvShowSeasonSelectionModel;
  TvShowEpisodeSelectionModel         tvShowEpisodeSelectionModel;
  private TvShowTreeModel             treeModel;
  private TvShowList                  tvShowList                       = TvShowList.getInstance();

  private JTree                       tree;
  private JPanel                      panelRight;
  private JMenu                       menu;
  private JLabel                      lblTvShows;
  private JLabel                      lblEpisodes;

  private final Action                actionUpdateDatasources          = new TvShowUpdateDatasourcesAction(false);
  private final Action                actionUpdateDatasources2         = new TvShowUpdateDatasourcesAction(true);
  private final Action                actionUpdateTvShow               = new TvShowUpdateAction();
  private final Action                actionScrape                     = new TvShowSingleScrapeAction(false);
  private final Action                actionScrape2                    = new TvShowSingleScrapeAction(true);
  private final Action                actionScrapeSelected             = new TvShowSelectedScrapeAction();
  private final Action                actionScrapeNewItems             = new TvShowScrapeNewItemsAction();
  private final Action                actionEdit                       = new TvShowEditAction(false);
  private final Action                actionEdit2                      = new TvShowEditAction(true);
  private final Action                actionRemove2                    = new TvShowRemoveAction(true);
  private final Action                actionDelete2                    = new TvShowDeleteAction(true);
  private final Action                actionChangeSeasonPoster2        = new TvShowChangeSeasonPosterAction(true);
  private final Action                actionBatchEdit                  = new TvShowBulkEditAction();
  private final Action                actionSetWatchedFlag             = new TvShowSetWatchedFlagAction();
  private final Action                actionScrapeEpisodes             = new TvShowScrapeEpisodesAction(true);
  private final Action                actionScrapeEpisodes2            = new TvShowScrapeEpisodesAction(false);
  private final Action                actionRewriteTvShowNfo           = new TvShowRewriteNfoAction();
  private final Action                actionRewriteTvShowEpisodeNfo    = new TvShowRewriteEpisodeNfoAction();
  private final Action                actionRename                     = new TvShowRenameAction(true);
  private final Action                actionRenameIcon                 = new TvShowRenameAction(false);
  private final Action                actionMediaInformation           = new TvShowMediaInformationAction(false);
  private final Action                actionMediaInformation2          = new TvShowMediaInformationAction(true);
  private final Action                actionClearImageCache            = new TvShowClearImageCacheAction();
  private final Action                actionExport                     = new TvShowExportAction();
  private final Action                actionSyncTrakt                  = new TvShowSyncTraktTvAction();
  private final Action                actionSyncWatchedTrakt           = new TvShowSyncWatchedTraktTvAction();
  private final Action                actionSyncSelectedTrakt          = new TvShowSyncSelectedTraktTvAction();
  private final Action                actionChangeToDvdOrder           = new TvShowChangeToDvdOrderAction();
  private final Action                actionChangeToAiredOrder         = new TvShowChangeToAiredOrderAction();
  private final Action                actionDownloadSubtitles          = new TvShowSubtitleDownloadAction();
  private final Action                actionDownloadMissingArtwork     = new TvShowDownloadMissingArtworkAction();
  private final Action                actionSearchAndDownloadSubtitles = new TvShowSubtitleSearchAction();
  private final Action                debugDumpShow                    = new DebugDumpShow();

  private int                         width                            = 0;
  private JTextField                  textField;

  /**
   * Instantiates a new tv show panel.
   */
  public TvShowPanel() {
    super();

    treeModel = new TvShowTreeModel(tvShowList.getTvShows());
    tvShowSeasonSelectionModel = new TvShowSeasonSelectionModel();
    tvShowEpisodeSelectionModel = new TvShowEpisodeSelectionModel();

    // build menu
    menu = new JMenu(BUNDLE.getString("tmm.tvshows")); //$NON-NLS-1$
    JFrame mainFrame = MainWindow.getFrame();
    JMenuBar menuBar = mainFrame.getJMenuBar();
    menuBar.add(menu);

    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("850px:grow"), FormFactory.RELATED_GAP_COLSPEC, },
        new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));

    JSplitPane splitPane = new JSplitPane();
    splitPane.setContinuousLayout(true);
    add(splitPane, "2, 2, fill, fill");

    JPanel panelTvShowTree = new JPanel();
    splitPane.setLeftComponent(panelTvShowTree);
    panelTvShowTree.setLayout(new FormLayout(
        new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.UNRELATED_GAP_COLSPEC,
            ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, },
        new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("3px:grow"), FormFactory.RELATED_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC, }));

    textField = EnhancedTextField.createSearchTextField();
    panelTvShowTree.add(textField, "4, 1, right, bottom");
    textField.setColumns(12);
    textField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(final DocumentEvent e) {
        applyFilter();
      }

      @Override
      public void removeUpdate(final DocumentEvent e) {
        applyFilter();
      }

      @Override
      public void changedUpdate(final DocumentEvent e) {
        applyFilter();
      }

      public void applyFilter() {
        TvShowTreeModel filteredModel = (TvShowTreeModel) tree.getModel();
        if (StringUtils.isNotBlank(textField.getText())) {
          filteredModel.setFilter(SearchOptions.TEXT, textField.getText());
        }
        else {
          filteredModel.removeFilter(SearchOptions.TEXT);
        }

        filteredModel.filter(tree);
      }
    });
    // register global short cut for the search field
    getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "search");
    getActionMap().put("search", new RequestFocusAction(textField));

    final JToggleButton btnFilter = new JToggleButton(IconManager.FILTER);
    btnFilter.setToolTipText(BUNDLE.getString("movieextendedsearch.options")); //$NON-NLS-1$
    panelTvShowTree.add(btnFilter, "6, 1, default, bottom");

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    panelTvShowTree.add(scrollPane, "2, 3, 5, 1, fill, fill");

    JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);
    toolBar.setFloatable(false);
    toolBar.setOpaque(false);
    panelTvShowTree.add(toolBar, "2, 1");

    // toolBar.add(actionUpdateDatasources);
    final JSplitButton buttonUpdateDatasource = new JSplitButton(IconManager.REFRESH);
    // temp fix for size of the button
    buttonUpdateDatasource.setText("   ");
    buttonUpdateDatasource.setHorizontalAlignment(JButton.LEFT);
    // buttonScrape.setMargin(new Insets(2, 2, 2, 24));
    buttonUpdateDatasource.setSplitWidth(18);
    buttonUpdateDatasource.setToolTipText(BUNDLE.getString("update.datasource")); //$NON-NLS-1$
    buttonUpdateDatasource.addSplitButtonActionListener(new SplitButtonActionListener() {
      public void buttonClicked(ActionEvent e) {
        actionUpdateDatasources.actionPerformed(e);
      }

      public void splitButtonClicked(ActionEvent e) {
        // build the popupmenu on the fly
        buttonUpdateDatasource.getPopupMenu().removeAll();
        buttonUpdateDatasource.getPopupMenu().add(new JMenuItem(actionUpdateDatasources2));
        buttonUpdateDatasource.getPopupMenu().addSeparator();
        for (String ds : TvShowModuleManager.SETTINGS.getTvShowDataSource()) {
          buttonUpdateDatasource.getPopupMenu().add(new JMenuItem(new TvShowUpdateSingleDatasourceAction(ds)));
        }
        buttonUpdateDatasource.getPopupMenu().addSeparator();
        buttonUpdateDatasource.getPopupMenu().add(new JMenuItem(actionUpdateTvShow));
        buttonUpdateDatasource.getPopupMenu().pack();
      }
    });

    JPopupMenu popup = new JPopupMenu("popup");
    buttonUpdateDatasource.setPopupMenu(popup);
    toolBar.add(buttonUpdateDatasource);

    JSplitButton buttonScrape = new JSplitButton(IconManager.SEARCH);
    // temp fix for size of the button
    buttonScrape.setText("   ");
    buttonScrape.setHorizontalAlignment(JButton.LEFT);
    buttonScrape.setSplitWidth(18);
    buttonScrape.setToolTipText(BUNDLE.getString("tvshow.scrape.selected")); //$NON-NLS-1$

    // register for listener
    buttonScrape.addSplitButtonActionListener(new SplitButtonActionListener() {
      @Override
      public void buttonClicked(ActionEvent e) {
        actionScrape.actionPerformed(e);
      }

      @Override
      public void splitButtonClicked(ActionEvent e) {
      }
    });

    popup = new JPopupMenu("popup");
    JMenuItem item = new JMenuItem(actionScrape2);
    popup.add(item);
    // item = new JMenuItem(actionScrapeUnscraped);
    // popup.add(item);
    item = new JMenuItem(actionScrapeSelected);
    popup.add(item);
    item = new JMenuItem(actionScrapeNewItems);
    popup.add(item);
    buttonScrape.setPopupMenu(popup);
    toolBar.add(buttonScrape);
    toolBar.add(actionEdit);

    JButton btnRen = new JButton();
    btnRen.setAction(actionRenameIcon);
    toolBar.add(btnRen);

    JButton btnMediaInformation = new JButton();
    btnMediaInformation.setAction(actionMediaInformation);
    toolBar.add(btnMediaInformation);

    // install drawing of full with
    tree = new ZebraJTree(treeModel) {
      private static final long serialVersionUID = 2422163883324014637L;

      @Override
      public void paintComponent(Graphics g) {
        width = this.getWidth();
        super.paintComponent(g);
      }
    };
    tvShowSelectionModel = new TvShowSelectionModel(tree);

    TreeUI ui = new TreeUI() {
      @Override
      protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded,
          boolean hasBeenExpanded, boolean isLeaf) {
        bounds.width = width - bounds.x;
        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
      }
    };
    tree.setUI(ui);

    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.setCellRenderer(new TvShowTreeCellRenderer());
    tree.setRowHeight(0);
    scrollPane.setViewportView(tree);

    JPanel panelHeader = new JPanel() {
      private static final long serialVersionUID = -6914183798172482157L;

      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getColHeaderColors(), 0, 0, getWidth(), getHeight());
      }
    };
    scrollPane.setColumnHeaderView(panelHeader);
    panelHeader.setLayout(new FormLayout(
        new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC,
            ColumnSpec.decode("center:20px"), ColumnSpec.decode("center:20px"), ColumnSpec.decode("center:20px") },
        new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, }));

    JLabel lblTvShowsColumn = new JLabel(BUNDLE.getString("metatag.tvshow")); //$NON-NLS-1$
    lblTvShowsColumn.setHorizontalAlignment(JLabel.CENTER);
    panelHeader.add(lblTvShowsColumn, "2, 1");

    JLabel lblNfoColumn = new JLabel("");
    lblNfoColumn.setHorizontalAlignment(JLabel.CENTER);
    lblNfoColumn.setIcon(IconManager.INFO);
    lblNfoColumn.setToolTipText(BUNDLE.getString("metatag.nfo"));//$NON-NLS-1$
    panelHeader.add(lblNfoColumn, "4, 1");

    JLabel lblImageColumn = new JLabel("");
    lblImageColumn.setHorizontalAlignment(JLabel.CENTER);
    lblImageColumn.setIcon(IconManager.IMAGE);
    lblImageColumn.setToolTipText(BUNDLE.getString("metatag.images"));//$NON-NLS-1$
    panelHeader.add(lblImageColumn, "5, 1");

    JLabel lblSubtitleColumn = new JLabel("");
    lblSubtitleColumn.setHorizontalAlignment(JLabel.CENTER);
    lblSubtitleColumn.setIcon(IconManager.SUBTITLE);
    lblSubtitleColumn.setToolTipText(BUNDLE.getString("metatag.subtitles"));//$NON-NLS-1$
    panelHeader.add(lblSubtitleColumn, "6, 1");

    JPanel panel = new JPanel();
    panelTvShowTree.add(panel, "2, 5, 3, 1, fill, fill");
    panel
        .setLayout(new FormLayout(
            new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, },
            new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JLabel lblTvShowsT = new JLabel(BUNDLE.getString("metatag.tvshows") + ":"); //$NON-NLS-1$
    panel.add(lblTvShowsT, "1, 2, fill, fill");

    lblTvShows = new JLabel("");
    panel.add(lblTvShows, "3, 2");

    JLabel labelSlash = new JLabel("/");
    panel.add(labelSlash, "5, 2");

    JLabel lblEpisodesT = new JLabel(BUNDLE.getString("metatag.episodes") + ":"); //$NON-NLS-1$
    panel.add(lblEpisodesT, "7, 2");

    lblEpisodes = new JLabel("");
    panel.add(lblEpisodes, "9, 2");

    JLayeredPane layeredPaneRight = new JLayeredPane();
    layeredPaneRight.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("default"), ColumnSpec.decode("default:grow") },
        new RowSpec[] { RowSpec.decode("default"), RowSpec.decode("default:grow") }));
    panelRight = new JPanel();
    layeredPaneRight.add(panelRight, "1, 1, 2, 2, fill, fill");
    layeredPaneRight.setLayer(panelRight, 0);

    // glass pane
    final TvShowExtendedSearchPanel panelExtendedSearch = new TvShowExtendedSearchPanel(treeModel, tree);
    panelExtendedSearch.setVisible(false);
    // panelMovieList.add(panelExtendedSearch, "2, 5, 2, 1, fill, fill");
    btnFilter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (panelExtendedSearch.isVisible() == true) {
          panelExtendedSearch.setVisible(false);
        }
        else {
          panelExtendedSearch.setVisible(true);
        }
      }
    });
    // add a propertychangelistener which reacts on setting a filter
    tree.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if ("filterChanged".equals(evt.getPropertyName())) {
          if (Boolean.TRUE.equals(evt.getNewValue())) {
            btnFilter.setIcon(IconManager.FILTER_ACTIVE);
            btnFilter.setToolTipText(BUNDLE.getString("movieextendedsearch.options.active")); //$NON-NLS-1$
          }
          else {
            btnFilter.setIcon(IconManager.FILTER);
            btnFilter.setToolTipText(BUNDLE.getString("movieextendedsearch.options")); //$NON-NLS-1$
          }
        }
      }
    });
    layeredPaneRight.add(panelExtendedSearch, "1, 1, fill, fill");
    layeredPaneRight.setLayer(panelExtendedSearch, 1);

    splitPane.setRightComponent(layeredPaneRight);
    panelRight.setLayout(new CardLayout(0, 0));

    JPanel panelTvShow = new TvShowInformationPanel(tvShowSelectionModel);
    panelRight.add(panelTvShow, "tvShow");

    JPanel panelTvShowSeason = new TvShowSeasonInformationPanel(tvShowSeasonSelectionModel);
    panelRight.add(panelTvShowSeason, "tvShowSeason");

    JPanel panelTvShowEpisode = new TvShowEpisodeInformationPanel(tvShowEpisodeSelectionModel);
    panelRight.add(panelTvShowEpisode, "tvShowEpisode");

    tree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
          // click on a tv show
          if (node.getUserObject() instanceof TvShow) {
            TvShow tvShow = (TvShow) node.getUserObject();
            tvShowSelectionModel.setSelectedTvShow(tvShow);
            CardLayout cl = (CardLayout) (panelRight.getLayout());
            cl.show(panelRight, "tvShow");
          }

          // click on a season
          if (node.getUserObject() instanceof TvShowSeason) {
            TvShowSeason tvShowSeason = (TvShowSeason) node.getUserObject();
            tvShowSeasonSelectionModel.setSelectedTvShowSeason(tvShowSeason);
            CardLayout cl = (CardLayout) (panelRight.getLayout());
            cl.show(panelRight, "tvShowSeason");
          }

          // click on an episode
          if (node.getUserObject() instanceof TvShowEpisode) {
            TvShowEpisode tvShowEpisode = (TvShowEpisode) node.getUserObject();
            tvShowEpisodeSelectionModel.setSelectedTvShowEpisode(tvShowEpisode);
            CardLayout cl = (CardLayout) (panelRight.getLayout());
            cl.show(panelRight, "tvShowEpisode");
          }
        }
        else {
          // check if there is at least one tv show in the model
          TvShowRootTreeNode root = (TvShowRootTreeNode) tree.getModel().getRoot();
          if (root.getChildCount() == 0) {
            // sets an inital show
            tvShowSelectionModel.setSelectedTvShow(null);
          }
        }
      }
    });

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentHidden(ComponentEvent e) {
        menu.setVisible(false);
        super.componentHidden(e);
      }

      @Override
      public void componentShown(ComponentEvent e) {
        menu.setVisible(true);
        super.componentHidden(e);
      }
    });

    // further initializations
    init();
    initDataBindings();

    // selecting first TV show at startup
    if (tvShowList.getTvShows() != null && tvShowList.getTvShows().size() > 0) {
      DefaultMutableTreeNode firstLeaf = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) tree.getModel().getRoot()).getFirstChild();
      tree.setSelectionPath(new TreePath(((DefaultMutableTreeNode) firstLeaf.getParent()).getPath()));
      tree.setSelectionPath(new TreePath(firstLeaf.getPath()));
    }
  }

  /**
   * Inits the.
   */
  private void init() {
    // build menu
    buildMenu();

    // add double click listener
    MouseListener mouseListener = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2 && !e.isConsumed() && e.getButton() == MouseEvent.BUTTON1) {
          actionEdit2.actionPerformed(new ActionEvent(e, 0, ""));
        }
      }
    };
    tree.addMouseListener(mouseListener);
  }

  /**
   * Builds the menu.
   */
  private void buildMenu() {
    menu.setMnemonic(KeyEvent.VK_V);

    // menu items
    JMenuItem menuItem = menu.add(actionUpdateDatasources2);
    menuItem.setMnemonic(KeyEvent.VK_U);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menu.addSeparator();

    JMenu menuScrape = new JMenu(BUNDLE.getString("Button.scrape")); //$NON-NLS-1$
    menuScrape.setMnemonic(KeyEvent.VK_S);
    menuItem = menuScrape.add(actionScrape2);
    menuItem.setMnemonic(KeyEvent.VK_S);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menuItem = menuScrape.add(actionScrapeSelected);
    menuItem.setMnemonic(KeyEvent.VK_F);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menuItem = menuScrape.add(actionScrapeNewItems);
    menuItem.setMnemonic(KeyEvent.VK_N);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menu.add(menuScrape);

    JMenu menuEdit = new JMenu(BUNDLE.getString("Button.edit")); //$NON-NLS-1$
    menuEdit.setMnemonic(KeyEvent.VK_E);
    menuItem = menuEdit.add(actionEdit2);
    menuItem.setMnemonic(KeyEvent.VK_E);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menuItem = menuEdit.add(actionBatchEdit);
    menuItem.setMnemonic(KeyEvent.VK_B);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menuItem = menuEdit.add(actionSetWatchedFlag);
    menuItem.setMnemonic(KeyEvent.VK_W);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menuItem = menuEdit.add(actionChangeSeasonPoster2);
    menuItem.setMnemonic(KeyEvent.VK_S);
    menuEdit.add(actionChangeToDvdOrder);
    menuEdit.add(actionChangeToAiredOrder);

    menu.add(menuEdit);
    menu.add(actionRewriteTvShowNfo);
    menu.add(actionRewriteTvShowEpisodeNfo);

    menuItem = menu.add(actionRename);
    menuItem.setMnemonic(KeyEvent.VK_R);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menuItem = menu.add(actionMediaInformation2);
    menuItem.setMnemonic(KeyEvent.VK_M);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menuItem = menu.add(actionExport);
    menuItem.setMnemonic(KeyEvent.VK_X);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menuItem = menu.add(actionClearImageCache);
    menuItem.setMnemonic(KeyEvent.VK_C);

    menu.addSeparator();
    menuItem = menu.add(actionRemove2);
    menuItem.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_DELETE));
    menuItem = menu.add(actionDelete2);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.SHIFT_MASK));

    menu.addSeparator();
    menuItem = menu.add(actionSyncTrakt);
    menuItem.setMnemonic(KeyEvent.VK_T);
    menuItem = menu.add(actionSyncWatchedTrakt);
    menuItem.setMnemonic(KeyEvent.VK_W);
    menuItem = menu.add(actionSyncSelectedTrakt);

    // popup menu
    JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.add(actionScrape2);
    popupMenu.add(actionScrapeSelected);
    popupMenu.add(actionScrapeEpisodes);
    popupMenu.add(actionScrapeEpisodes2);
    popupMenu.add(actionScrapeNewItems);
    // popupMenu.add(actionScrapeMetadataSelected);
    popupMenu.addSeparator();
    popupMenu.add(actionUpdateTvShow);
    popupMenu.addSeparator();
    popupMenu.add(actionEdit2);
    popupMenu.add(actionChangeSeasonPoster2);
    popupMenu.add(actionBatchEdit);
    popupMenu.add(actionSetWatchedFlag);
    popupMenu.add(actionChangeToDvdOrder);
    popupMenu.add(actionChangeToAiredOrder);
    popupMenu.add(actionRewriteTvShowNfo);
    popupMenu.add(actionRewriteTvShowEpisodeNfo);
    // popupMenu.add(actionBatchEdit);
    popupMenu.add(actionRename);
    popupMenu.add(actionMediaInformation2);
    popupMenu.add(actionExport);
    popupMenu.add(actionClearImageCache);
    popupMenu.addSeparator();
    popupMenu.add(actionDownloadMissingArtwork);
    popupMenu.add(actionDownloadSubtitles);
    popupMenu.add(actionSearchAndDownloadSubtitles);
    popupMenu.addSeparator();
    popupMenu.add(actionSyncTrakt);
    popupMenu.add(actionSyncWatchedTrakt);
    popupMenu.add(actionSyncSelectedTrakt);
    popupMenu.addSeparator();
    popupMenu.add(actionRemove2);
    popupMenu.add(actionDelete2);
    popupMenu.addSeparator();
    popupMenu.add(new ExpandAllAction());
    popupMenu.add(new CollapseAllAction());

    if (Globals.isDebug()) {
      JMenu menuDebug = new JMenu("Debug"); //$NON-NLS-1$
      menuDebug.add(debugDumpShow);
      popupMenu.addSeparator();
      popupMenu.add(menuDebug);
    }

    MouseListener popupListener = new TreePopupListener(popupMenu, tree);
    tree.addMouseListener(popupListener);
  }

  protected void initDataBindings() {
    BeanProperty<TvShowList, Integer> tvShowListBeanProperty = BeanProperty.create("tvShowCount");
    BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
    AutoBinding<TvShowList, Integer, JLabel, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, tvShowList, tvShowListBeanProperty,
        lblTvShows, jLabelBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<TvShowList, Integer> tvShowListBeanProperty_1 = BeanProperty.create("episodeCount");
    AutoBinding<TvShowList, Integer, JLabel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, tvShowList,
        tvShowListBeanProperty_1, lblEpisodes, jLabelBeanProperty);
    autoBinding_1.bind();
  }

  /**************************************************************************
   * local helper classes
   **************************************************************************/
  private class CollapseAllAction extends AbstractAction {
    private static final long serialVersionUID = -1444530142931061317L;

    public CollapseAllAction() {
      putValue(NAME, BUNDLE.getString("tree.collapseall")); //$NON-NLS-1$
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      for (int i = tree.getRowCount() - 1; i >= 0; i--) {
        tree.collapseRow(i);
      }
    }
  }

  private class ExpandAllAction extends AbstractAction {
    private static final long serialVersionUID = 6191727607109012198L;

    public ExpandAllAction() {
      putValue(NAME, BUNDLE.getString("tree.expandall")); //$NON-NLS-1$
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      int i = 0;
      do {
        tree.expandRow(i++);
      } while (i < tree.getRowCount());
    }
  }
}
